package grammar;

import application.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grammar {

    private List<Rule> terminalRules = new ArrayList<>();
    private List<Rule> nonTerminalRules = Collections.synchronizedList(new ArrayList<>());
    private List<Symbol> terminalSymbols = new ArrayList<>();
    private List<Symbol> nonTerminalSymbols = new ArrayList<>();

    public List<Rule> getRules() {
        List<Rule> result = new ArrayList<>();
        result.addAll(terminalRules);
        result.addAll(nonTerminalRules);
        return result;
    }

    public Symbol getSymbol(String name) {
        Symbol terminalSymbol = findTerminalSymbol(name);
        if (terminalSymbol != null) {
            return terminalSymbol;
        }

        Symbol nonTerminalSymbol = findNonTerminalSymbol(name);
        if (nonTerminalSymbol != null) {
            return nonTerminalSymbol;
        }

        throw new ApplicationException(String.format("Symbol %s not found", name));
    }

    private Symbol findTerminalSymbol(String name) {
        for (Symbol symbol : terminalSymbols) {
            if (symbol.getValue().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    private Symbol findNonTerminalSymbol(String name) {
        for (Symbol symbol : nonTerminalSymbols) {
            if (symbol.getValue().equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public Rule getRule(String definition) {
        Rule terminalRule = findTerminalRule(definition);
        if (terminalRule != null) {
            return terminalRule;
        }

        Rule nonTerminalRule = findNonTerminalRule(definition);
        if (nonTerminalRule != null) {
            return nonTerminalRule;
        }

        throw new ApplicationException(String.format("Rule %s not found", definition));
    }

    private Rule findTerminalRule(String definition) {
        for (Rule rule : terminalRules) {
            if (rule.toString().equals(definition)) {
                return rule;
            }
        }
        return null;
    }

    private Rule findNonTerminalRule(String definition) {
        for (Rule rule : nonTerminalRules) {
            if (rule.toString().equals(definition)) {
                return rule;
            }
        }
        return null;
    }

    public void removeNonTerminalRules(Collection<Rule> rules) {
        nonTerminalRules.removeAll(rules);
    }

    public void removeNonTerminalSymbols(Collection<Symbol> symbols) {
        nonTerminalSymbols.removeAll(symbols);
        IntStream.range(0, nonTerminalSymbols.size()).forEach(i -> {
            Symbol symbol = nonTerminalSymbols.get(i);
            symbol.setIndex(i);
        });
    }

    public void addRule(Rule rule) {
        if (rule.getRight1().getSymbolType().equals(SymbolType.TERMINAL)) {
            terminalRules.add(rule);
        } else {
            nonTerminalRules.add(rule);
        }
    }


    public void addNonTerminalRules(List<Rule> rules) {
        nonTerminalRules.addAll(rules);
    }

    public void addNonTerminalRule(Rule rule) {
        nonTerminalRules.add(rule);
    }

    public void removeRule(Rule ruleToRemove) {
        Optional<Rule> nonTerminal = nonTerminalRules.stream()
                .filter(new EqualRules(ruleToRemove))
                .findFirst();
        nonTerminal.ifPresent(rule -> nonTerminalRules.remove(rule));

        Optional<Rule> terminal = terminalRules.stream()
                .filter(new EqualRules(ruleToRemove))
                .findFirst();
        terminal.ifPresent(rule -> terminalRules.remove(rule));
    }

    private static class EqualRules implements Predicate<Rule> {
        private Rule comparedRule;

        public EqualRules(Rule comparedRule) {
            this.comparedRule = comparedRule;
        }

        @Override
        public boolean test(Rule rule) {
            return rule.equals(comparedRule);
        }
    }
}
