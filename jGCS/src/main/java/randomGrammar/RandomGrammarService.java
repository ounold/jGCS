package randomGrammar;

import application.ApplicationException;
import configuration.Configuration;
import configuration.ConfigurationService;
import dataset.Dataset;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomGrammarService {

    private static final String RANDOM_TERMINALS = "grammar.maxRandomTerminals";
    private static final String RANDOM_NON_TERMINALS = "grammar.randomNonTerminals";
    private static final String RANDOM_PROBABILITIES = "grammar.randomProbabilities";

    private Configuration configuration = ConfigurationService.getConfiguration();

    private static RandomGrammarService instance;

    private RandomGrammarService() {
    }

    public static RandomGrammarService getInstance() {
        if (instance == null)
            instance = new RandomGrammarService();
        return instance;
    }

    public Grammar createRandomGrammar(Dataset dataset) {
        int nonTerminalIndex = 0;
        Grammar result = new Grammar();
        nonTerminalIndex = generateTerminals(result, dataset, nonTerminalIndex);
        generateNonTerminals(result, nonTerminalIndex);
        return result;
    }

    public int generateTerminals(Grammar grammar, Dataset dataset, int nonTerminalIndex) {
        int ntIndex = nonTerminalIndex;
        for (String terminal : extractTerminals(dataset)) {
            Symbol terminalSymbol = new Symbol(terminal, 0, SymbolType.TERMINAL);
            Symbol nonTerminalSymbol = new Symbol(terminal.toUpperCase(), ntIndex++, SymbolType.NON_TERMINAL);
            Rule rule = new Rule(nonTerminalSymbol, terminalSymbol, null, 1);
            if (configuration.getBoolean(RANDOM_PROBABILITIES))
                rule.setProbability(new Random().nextDouble());
            grammar.getTerminalSymbols().add(terminalSymbol);
            grammar.getNonTerminalSymbols().add(nonTerminalSymbol);
            grammar.getTerminalRules().add(rule);
        }
        return ntIndex;
    }

    public void generateNonTerminals(Grammar grammar, int nonTerminalIndex) {
        grammar.getNonTerminalSymbols().add(new Symbol("$", nonTerminalIndex, SymbolType.START));
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        Integer randomNonTerminals = configuration.getInteger(RANDOM_NON_TERMINALS);
        if(randomNonTerminals > possibleVariations(nonTerminalSymbols.size()))
            throw new ApplicationException("There is no way to generate so many non terminal rules");
        for (int i = 0; i < randomNonTerminals; i++) {
            Rule rule = new Rule(
                    nonTerminalSymbols.get(randomInt(nonTerminalSymbols.size())),
                    nonTerminalSymbols.get(randomInt(nonTerminalSymbols.size())),
                    nonTerminalSymbols.get(randomInt(nonTerminalSymbols.size())),
                    1
            );
            if(grammar.getNonTerminalRules().stream().anyMatch(r -> r.equals(rule))){
                i--;
                continue;
            }
            if (configuration.getBoolean(RANDOM_PROBABILITIES))
                rule.setProbability(new Random().nextDouble());
            grammar.getNonTerminalRules().add(rule);
        }
    }

    private double possibleVariations(int size) {
        return Math.pow(3, size);
    }

    private int randomInt(int bound) {
        return new Random().nextInt(bound);
    }

    private List<String> extractTerminals(Dataset dataset) {
        List<String> terminalSymbolsFromDataset = dataset.getSequences().stream()
                .flatMap(sequence -> sequence.symbolSet().stream())
                .distinct()
                .collect(Collectors.toList());
        int randomTerminals = configuration.getInteger(RANDOM_TERMINALS);
        if (randomTerminals < terminalSymbolsFromDataset.size()) {
            Collections.shuffle(terminalSymbolsFromDataset);
            return terminalSymbolsFromDataset.subList(0, randomTerminals);
        } else {
            return terminalSymbolsFromDataset;
        }
    }

}
