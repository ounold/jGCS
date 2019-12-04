package cyk;

import configuration.Configuration;
import configuration.ConfigurationService;
import covering.CoveringService;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import probabilityArray.ProbabilityArray;
import probabilityArray.ProbabilityCell;
import rulesTable.CellRule;
import rulesTable.RulesTable;
import rulesTable.TableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public abstract class CykProcessor {

    private static final Logger LOGGER = LogManager.getLogger(CykProcessor.class);

    private static final String NUM_OF_THREADS = "cyk.numOfThreads";
    private static final String PARSING_THRESHOLD = "cyk.parsingThreshold";
    private static final String PROBABILITY_BASED_EVALUATION = "cyk.probabilityBasedParsingEvaluation";

    private ConcurrentLinkedQueue<TableCell> jobs = new ConcurrentLinkedQueue<>();

    protected RulesTable rulesTable;
    protected ProbabilityArray probabilityArray;
    protected Grammar grammar = new Grammar();

    private static ExecutorService executors;

    private Configuration configuration = ConfigurationService.getConfiguration();
    private CoveringService coveringService = new CoveringService();

    private boolean enableCovering;
    private double parsingThreshold  = configuration.getDouble(PARSING_THRESHOLD);

    public CykProcessor(boolean enableCovering) {
        this.enableCovering = enableCovering;
    }

    private void prepareJobs(int sentenceLength) {
        for (int i = 1; i < sentenceLength; i++) {
            for (int j = 0; j < sentenceLength - i; j++) {
                jobs.add(rulesTable.get(i, j));
            }
        }
    }

    private void initFirstRow(Sequence sentence) {
        List<String> symbols = sentence.symbolList();
        for (int i = 0; i < sentence.length(); i++) {
            for (Rule rule : grammar.getTerminalRules()) {
                if (rule.getRight1().getValue().equals(symbols.get(i))) {
                    ProbabilityCell probabilityCell = probabilityArray.add(0, i, rule.getLeft(), rule.getProbability());
                    rulesTable.get(0, i).getCellRules().add(createCellRule(rule, probabilityCell, i));
                }
            }
            rulesTable.get(0, i).getEvaluated().compareAndSet(false, true);
        }

    }

    private void waitUntilEvaluated(int parentOneI, int parentOneJ, int parentTwoI, int parentTwoJ) {
        while (!rulesTable.get(parentOneI, parentOneJ).isEvaluated()
                || !rulesTable.get(parentTwoI, parentTwoJ).isEvaluated());
    }

    private boolean parseSentence() {
        while (true) {
            TableCell cellToAnalyse = jobs.poll();

            if (cellToAnalyse == null) {
                return true;
            }

            int i = cellToAnalyse.getXCor();
            int j = cellToAnalyse.getYCor();
            boolean isLastCell = jobs.size() == 0;

            fillCell(i, j, isLastCell);

            rulesTable.get(i, j).getEvaluated().compareAndSet(false, true);
        }

    }

    private void fillCell(int i, int j, boolean islastCell) {
        for (int k = 0; k < i; k++) {

            int parentOneI = k;
            int parentOneJ = j;

            int parentTwoI = i - k - 1;
            int parentTwoJ = j + k + 1;

            boolean ruleFound = false;
            boolean hasStartSymbol = false;

            waitUntilEvaluated(parentOneI, parentOneJ, parentTwoI, parentTwoJ);

            synchronized (grammar.getNonTerminalRules()) {
                for (Rule rule : grammar.getNonTerminalRules()) {

                    if (probabilityArray.get(parentOneI, parentOneJ, rule.getRight1()) != ProbabilityCell.EMPTY_CELL
                            && probabilityArray.get(parentTwoI, parentTwoJ, rule.getRight2()) != ProbabilityCell.EMPTY_CELL) {

                        ProbabilityCell probabilityCell = fillProbabilityCell(i, j, parentOneI, parentOneJ, parentTwoI, parentTwoJ, rule);
                        rulesTable.get(i, j).getCellRules().add(createCellRule(i, j, k, rule, probabilityCell));

                        ruleFound = true;
                        hasStartSymbol = rule.getLeft().getSymbolType().equals(SymbolType.START) && rule.getProbability() > parsingThreshold;
                    }
                }

                if (enableCovering && (!ruleFound || (islastCell && !hasStartSymbol))) {
                    Symbol rightSymbol1 = getSymbolByParents(parentOneI, parentOneJ);
                    Symbol rightSymbol2 = getSymbolByParents(parentTwoI, parentTwoJ);

                    List<Rule> coveringRules = coveringService.run(grammar, rightSymbol1, rightSymbol2, islastCell, hasStartSymbol);

                    for (Rule coveringRule : coveringRules) {
                        ProbabilityCell probabilityCell = fillProbabilityCell(i, j, parentOneI, parentOneJ, parentTwoI, parentTwoJ, coveringRule);
                        rulesTable.get(i, j).getCellRules().add(createCellRule(i, j, k, coveringRule, probabilityCell));
                        grammar.addNonTerminalRule(coveringRule);
                    }
                }
            }
        }
    }

    private Symbol getSymbolByParents(int parentI, int parentJ) {
        Symbol symbol = null;
        for (Symbol nonTerminalSymbol : grammar.getNonTerminalSymbols()) {
            ProbabilityCell probabilityCell = probabilityArray.get(parentI, parentJ, nonTerminalSymbol);
            if (probabilityCell != ProbabilityCell.EMPTY_CELL) {
                symbol = nonTerminalSymbol;
            }
        }
        return symbol;
    }


    private ProbabilityCell fillProbabilityCell(int i, int j, int parentOneI, int parentOneJ, int parentTwoI, int parentTwoJ, Rule rule) {
        ProbabilityCell parentCellProbability = probabilityArray.get(parentOneI, parentOneJ, rule.getRight1());
        ProbabilityCell parent2CellProbability = probabilityArray.get(parentTwoI, parentTwoJ, rule.getRight2());
        return probabilityArray.add(i, j, rule.getLeft(), calculateProbability(parentCellProbability, parent2CellProbability, rule));
    }

    protected abstract CellRule createCellRule(int i, int j, int k, Rule rule, ProbabilityCell probabilityCell);

    protected abstract CellRule createCellRule(Rule rule, ProbabilityCell probabilityCell, int i);

    private double calculateProbability(ProbabilityCell parent1, ProbabilityCell parent2, Rule rule) {
        double result = rule.getProbability() * parent1.getProbability() * parent2.getProbability();
        /*if (result == 0.0 && rule.getProbability() > 0 && parent1.getProbability() >  0 && parent2.getProbability() > 0) {
            result = Math.pow(10, -13); // workaround for very small probabilities that exceed range of double
        }*/
        return result;
    }

    CykResult runCyk(Sequence testSentence, Grammar grammar) {
        this.grammar = grammar;

        // Grammar values generation
        Integer numOfThreads = configuration.getInteger(NUM_OF_THREADS);
        executors = Executors.newFixedThreadPool(numOfThreads);

        // CYK parsing
        rulesTable = new RulesTable(testSentence.length());
        probabilityArray = new ProbabilityArray(testSentence.length(), grammar.getNonTerminalSymbols().size());
        initFirstRow(testSentence);
        prepareJobs(testSentence.length());

        List<Callable<Object>> todo = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            todo.add(this::parseSentence);
        }
        try {
            List<Future<Object>> status = executors.invokeAll(todo);

            for (Future<Object> s : status) {
                s.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
        executors.shutdown();
        double sentenceProbability = calculateSentenceProbability();

        return new CykResult(rulesTable, probabilityArray, sentenceProbability, isParsed(sentenceProbability));
    }

    private boolean isParsed(double sentenceProbability) {
        if (configuration.getBoolean(PROBABILITY_BASED_EVALUATION)) {
            return sentenceProbability > parsingThreshold;
        } else {
            for (CellRule cellRule : rulesTable.getStartTableCell().getCellRules()) {
                if (cellRule.getRule().getLeft().getSymbolType().equals(SymbolType.START)) {
                    return true;
                }
            }
            return false;
        }
    }

    private double calculateSentenceProbability() {
        double result = 0;
        for (Symbol symbol : grammar.getNonTerminalSymbols())
            if (symbol.isStart())
                result += probabilityArray.getStartCell(symbol).getProbability();
        return result;
    }

}