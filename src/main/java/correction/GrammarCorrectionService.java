package correction;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GrammarCorrectionService {

    private static GrammarCorrectionService instance;

    private GrammarCorrectionService() {
    }

    public static GrammarCorrectionService getInstance() {
        if (instance == null)
            instance = new GrammarCorrectionService();
        return instance;
    }

    public void correctGrammar(Grammar grammar) {
        removeImproductiveRulesAndSymbols(grammar);
        removeUnreachableRulesAndSymbols(grammar);
    }

    public void normalizeRules(Grammar grammar) {
        grammar.getNonTerminalSymbols().forEach(s -> {
            List<Rule> rules = grammar.getRules().stream()
                    .filter(r -> r.getLeft().equals(s))
                    .collect(Collectors.toList());
            double sum = rules.stream().mapToDouble(Rule::getProbability).sum();
            rules.forEach(rule -> {
                if (sum == 0)
                    rule.setProbability(1d / rules.size());
                else
                    rule.setProbability(rule.getProbability() / sum);
            });
        });
    }

    private void removeImproductiveRulesAndSymbols(Grammar grammar) {
        Set<Symbol> usedSymbols = new HashSet<>();
        Set<Rule> usedRules = new HashSet<>();
        Set<Rule> unusedRules = new HashSet<>(grammar.getNonTerminalRules());
        grammar.getTerminalRules().forEach(rule -> {
            usedRules.add(rule);
            usedSymbols.add(rule.getLeft());
        });

        while (true) {
            List<Rule> addedRules = new ArrayList<>();

            usedSymbols.forEach(symbol ->
                    unusedRules.stream()
                            .filter(r -> r.getRight2() == symbol || r.getRight1() == symbol)
                            .forEach(addedRules::add)
            );

            if (addedRules.isEmpty())
                break;

            addedRules.stream()
                    .flatMap(r -> Stream.of(r.getLeft(), r.getRight1(), r.getRight2()))
                    .forEach(usedSymbols::add);

            usedRules.addAll(addedRules);
            unusedRules.removeAll(addedRules);
        }

        grammar.removeNonTerminalRules(unusedRules);
        grammar.removeNonTerminalSymbols(grammar.getNonTerminalSymbols().stream()
                .filter(s -> !usedSymbols.contains(s))
                .collect(Collectors.toList()));
    }

    private void removeUnreachableRulesAndSymbols(Grammar grammar) {
        Set<Rule> usedRules = grammar.getRules().stream()
                .filter(Rule::isStart)
                .collect(Collectors.toSet());
        Set<Rule> unusedRules = grammar.getRules().stream()
                .filter(r -> !r.isStart())
                .collect(Collectors.toSet());
        Set<Symbol> usedSymbols = usedRules.stream()
                .flatMap(r -> Stream.of(r.getLeft(), r.getRight1(), r.getRight2()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        while (true) {
            List<Rule> addedRules = new ArrayList<>();

            usedSymbols.forEach(symbol ->
                    unusedRules.stream()
                            .filter(r -> r.getLeft() == symbol)
                            .forEach(addedRules::add)
            );

            if (addedRules.isEmpty())
                break;

            addedRules.stream()
                    .flatMap(r -> Stream.of(r.getLeft(), r.getRight1(), r.getRight2()))
                    .filter(Objects::nonNull)
                    .forEach(usedSymbols::add);

            usedRules.addAll(addedRules);
            unusedRules.removeAll(addedRules);
        }

        grammar.removeNonTerminalRules(unusedRules);
        grammar.removeNonTerminalSymbols(grammar.getNonTerminalSymbols().stream()
                .filter(s -> !usedSymbols.contains(s))
                .collect(Collectors.toList()));
    }

    public void removeZeroProbabilitiesRules(Grammar grammar) { //todo: write some tests
        grammar.getNonTerminalRules().removeIf(rule -> rule.getProbability() <= 0);
    }
}
