package heurstic.ga;

import configuration.Configuration;
import configuration.ConfigurationService;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import heurstic.Heuristic;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

public class GeneticAlgorithm implements Heuristic {

    private static final String RUN_PROBABILITY = "heuristic.ga.runProbability";
    private static final String NUM_OF_NEW_RULES = "heuristic.ga.numOfNewRules";
    private static final String POPULATION_SIZE = "heuristic.ga.populationSize";
    private static final String SYMBOL_MUTATION_PROBABILITY = "heuristic.ga.symbolMutationProbability";
    private static final String INVERSION_PROBABILITY = "heuristic.ga.inversionProbability";
    private static final String CROSSOVER_PROBABILITY = "heuristic.ga.crossoverProbability";

    private Random random = new Random();
    private Configuration configuration = ConfigurationService.getConfiguration();

    @Override
    public void process(Grammar grammar, int execution) {
        if (random.nextDouble() <= configuration.getDouble(RUN_PROBABILITY)) {//probability of getting value less or equal to n equals n
            for (int i = 1; i < getNumberOfNewRules(); i++) {
                List<Rule> rules = select(grammar);
                if (rules.size() == 2) {
                    List<Rule> newRules = crossover(rules.get(0), rules.get(1));
                    mutate(newRules, grammar);
                    invert(newRules);
                    grammar.addNonTerminalRules(newRules);
                }
            }
        }
    }

    private List<Rule> select(Grammar grammar) {
        Set<Rule> selectedRules = new HashSet<>();
        Set<Integer> selectedRulesIndexes = new HashSet<>();
        int populationSize = getPopulationSize(grammar);

        while (selectedRules.size() != populationSize) {
            int ruleIndex = getRandomWithoutDuplicates(selectedRulesIndexes, grammar.getNonTerminalRules().size());
            Rule parentRule =  grammar.getNonTerminalRules().get(ruleIndex);
            Rule rule = new Rule(parentRule);
            selectedRulesIndexes.add(ruleIndex);
            selectedRules.add(rule);
        }

        return selectedRules.stream()
                .sorted(Comparator.comparingDouble(Rule::getFitness).reversed())
                .limit(2)
                .collect(Collectors.toList());
    }

    private int getPopulationSize(Grammar grammar) {
        int populationSize = configuration.getInteger(POPULATION_SIZE);
        if (populationSize > grammar.getNonTerminalRules().size()) {
            return grammar.getNonTerminalRules().size();
        }
        return populationSize;
    }

    private int getNumberOfNewRules() {
        int populationSize = configuration.getInteger(NUM_OF_NEW_RULES);
        if (populationSize % 2 != 0) {
            throw new InvalidParameterException(NUM_OF_NEW_RULES + " has to be even number");
        }
        return populationSize;
    }

    private void invert(List<Rule> rules) {
        rules.forEach(rule -> {
            if (random.nextDouble() <= configuration.getDouble(INVERSION_PROBABILITY)) {
                Symbol symbol1 = rule.getRight1();
                Symbol symbol2 = rule.getRight2();
                rule.setRight1(symbol2);
                rule.setRight2(symbol1);
                rule.setProbability(1.0 - rule.getProbability());
            }
        });
    }

    private void mutate(List<Rule> rules, Grammar grammar) {
        rules.forEach(rule -> {
            if (random.nextDouble() <= configuration.getDouble(SYMBOL_MUTATION_PROBABILITY)) {
                rule.setLeft(getRandomSymbol(grammar.getNonTerminalSymbols()));
            }

            if (random.nextDouble() <= configuration.getDouble(SYMBOL_MUTATION_PROBABILITY)) {
                rule.setRight1(getRandomSymbol(grammar.getNonTerminalSymbols()));
            }

            if (random.nextDouble() <= configuration.getDouble(SYMBOL_MUTATION_PROBABILITY)) {
                rule.setRight2(getRandomSymbol(grammar.getNonTerminalSymbols()));
            }
        });
    }

    private Symbol getRandomSymbol(List<Symbol> symbols) {
        int index = random.nextInt(symbols.size());
        return symbols.get(index);
    }

    private List<Rule> crossover (Rule rule1, Rule rule2) {
        if (random.nextDouble() <= configuration.getDouble(CROSSOVER_PROBABILITY)) {
            int switchedSymbolIndex = random.nextInt(2) + 1;

            Rule newRule1 = null;
            Rule newRule2 = null;

            if (switchedSymbolIndex == 1) {
                Symbol right11 = rule1.getRight1();
                Symbol right12 = rule2.getRight1();
                newRule1 = new Rule(rule1.getLeft(), right12, rule1.getRight2(), 1.0);
                newRule2 = new Rule(rule2.getLeft(), right11, rule2.getRight2(), 1.0);
            }
            if (switchedSymbolIndex == 2) {
                Symbol right21 = rule1.getRight2();
                Symbol right22 = rule2.getRight2();
                newRule1 = new Rule(rule1.getLeft(), rule1.getRight1(), right22, 1.0);
                newRule2 = new Rule(rule2.getLeft(), rule2.getRight2(), right21, 1.0);
            }
            return Arrays.asList(newRule1, newRule2);
        }
        return Arrays.asList(rule1, rule2);
    }

    private int getRandomWithoutDuplicates(Set notToDuplicate, int bound) {
        int result = random.nextInt(bound);

        if (notToDuplicate == null || notToDuplicate.isEmpty()) {
            return result;
        }

        while (notToDuplicate.contains(result)) {
            result = random.nextInt(bound);
        }

        return result;
    }
}
