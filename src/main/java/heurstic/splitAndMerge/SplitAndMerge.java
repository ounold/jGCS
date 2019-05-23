package heurstic.splitAndMerge;

import configuration.Configuration;
import configuration.ConfigurationService;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;
import heurstic.Heuristic;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
public class SplitAndMerge implements Heuristic {

    private static final String MAX_RULES = "grammar.maxRules";

    private final Random random = new Random();
    private final Configuration configuration = ConfigurationService.getConfiguration();

    @Override
    public void process(Grammar grammar, int execution) {
        if (grammar.getRules().size() > configuration.getInteger(MAX_RULES)) {
            merge(grammar);
        } else {
            if (execution % 2 == 0) {
                merge(grammar);
            } else {
                split(grammar);
            }
        }
   }

    private void split(Grammar grammar) {
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        final Symbol splitSymbol = nonTerminalSymbols.get(random.nextInt(nonTerminalSymbols.size()));
        List<Symbol> unusedSymbolsPair = findUnusedNonTerminalSymbols(grammar);
        if (!unusedSymbolsPair.isEmpty()) {

            Symbol newSymbol1 = unusedSymbolsPair.get(0);
            Symbol newSymbol2 = unusedSymbolsPair.get(1);

            grammar.getRules().stream().forEach(new Consumer<Rule>() {
                @Override
                public void accept(Rule rule) {
                    if (rule.getLeft().equals(splitSymbol)) {
                        grammar.removeRule(rule);
                        Rule newRule1 = new Rule(newSymbol1, rule.getRight1(), rule.getRight2(), 0.0);
                        Rule newRule2 = new Rule(newSymbol2, rule.getRight1(), rule.getRight2(), 0.0);
                        grammar.addNonTerminalRule(newRule1);
                        grammar.addNonTerminalRule(newRule2);
                    } else if (rule.getRight1().equals(splitSymbol)) {
                        grammar.removeRule(rule);
                        Rule newRule1 = new Rule(rule.getLeft(), newSymbol1, rule.getRight2(), 0.0);
                        Rule newRule2 = new Rule(rule.getLeft(), newSymbol2, rule.getRight2(), 0.0);
                        grammar.addNonTerminalRule(newRule1);
                        grammar.addNonTerminalRule(newRule2);
                    } else if (rule.getRight2() != null && rule.getRight2().equals(splitSymbol)) {
                        grammar.removeRule(rule);
                        Rule newRule1 = new Rule(rule.getLeft(), rule.getRight1(), newSymbol1, 0.0);
                        Rule newRule2 = new Rule(rule.getLeft(), rule.getRight1(), newSymbol2, 0.0);
                        grammar.addNonTerminalRule(newRule1);
                        grammar.addNonTerminalRule(newRule2);
                    }
                }
            });
        } else {
            System.out.println("Not enough unused non terminal symbols to execute split phase");
        }
    }

    private void merge(Grammar grammar) {
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        int replacedIndex = getRandomWithoutDuplicate(null, nonTerminalSymbols.size());
        int replacementIndex = getRandomWithoutDuplicate(replacedIndex, nonTerminalSymbols.size());
        final Symbol replaced = nonTerminalSymbols.get(replacedIndex);
        final Symbol replacement = nonTerminalSymbols.get(replacementIndex);

        grammar.getRules().stream().forEach(new Consumer<Rule>() {
            @Override
            public void accept(Rule rule) {
                switchLeft(rule, replaced, replacement);
                switchRight1(rule, replaced, replacement);
                switchRight2(rule, replaced, replacement);
            }
        });
    }

    private void switchLeft(Rule rule, Symbol replaced, Symbol replacement) {
        if (rule.getLeft().equals(replaced)) {
            rule.setLeft(replacement);
        }
    }

    private void switchRight1(Rule rule, Symbol replaced, Symbol replacement) {
        if (rule.getRight1().equals(replaced)) {
            rule.setRight1(replacement);
        }
    }

    private void switchRight2(Rule rule, Symbol replaced, Symbol replacement) {
        if (rule.getRight2() != null && rule.getRight2().equals(replaced)) {
            rule.setRight2(replacement);
        }
    }

    private List<Symbol> findUnusedNonTerminalSymbols(Grammar grammar) {
        Set<Symbol> allSymbols = new HashSet<>(grammar.getNonTerminalSymbols());
        Set<Symbol> usedSymbols = getUsedSymbols(grammar);

        allSymbols.removeAll(usedSymbols);

        if (allSymbols.size() < 2) {
            return Collections.EMPTY_LIST;
        }
        return getTwoRandomSymbols(allSymbols);
    }

    private List<Symbol> getTwoRandomSymbols(Set<Symbol> allSymbols) {
        int randomInt1 = getRandomWithoutDuplicate(null,allSymbols.size() - 1);
        int randomInt2 = getRandomWithoutDuplicate(randomInt1, allSymbols.size() - 1);
        int loopCounter = 0;
        int foundCounter = 0;
        List<Symbol> resultPair = new ArrayList<>();
        Iterator<Symbol> iterator = allSymbols.iterator();

        while (foundCounter != 2 && iterator.hasNext()){
            Symbol symbol = iterator.next();
            if (loopCounter == randomInt1 || loopCounter == randomInt2 ) {
                resultPair.add(symbol);
                foundCounter++;
            }
            loopCounter++;
        }
        return resultPair;
    }

    private Set<Symbol> getUsedSymbols(Grammar grammar) {
        Set<Symbol> usedSymbols = new HashSet<>();
        List<Rule> rules = grammar.getRules();

        for (Rule rule : rules) {
            Symbol symbol = rule.getLeft();
            if ((symbol.getSymbolType().equals(SymbolType.NON_TERMINAL)) || (symbol.getSymbolType().equals(SymbolType.START))) {
                usedSymbols.add(symbol);
            }

            symbol = rule.getRight1();
            if (symbol.getSymbolType().equals(SymbolType.NON_TERMINAL)) {
                usedSymbols.add(symbol);
            }

            symbol = rule.getRight2();
            if (symbol != null && symbol.getSymbolType().equals(SymbolType.NON_TERMINAL)) {
                usedSymbols.add(symbol);
            }
        }
        return usedSymbols;
    }

    private int getRandomWithoutDuplicate(Integer notToDuplicate, int bound) {
        int result = random.nextInt(bound);

        if (notToDuplicate == null) {
            return result;
        }

        while (result == notToDuplicate) {
            result = random.nextInt(bound);
        }

        return result;
    }
}
