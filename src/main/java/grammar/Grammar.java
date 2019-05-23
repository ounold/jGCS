package grammar;

import application.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Predicate;
import java.util.prefs.PreferenceChangeEvent;
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
        return terminalSymbols.stream()
                .filter(s -> s.getValue().equals(name))
                .findFirst()
                .orElseGet(() -> nonTerminalSymbols.stream()
                        .filter(s -> s.getValue().equals(name))
                        .findFirst().orElseThrow(() -> new ApplicationException(String.format("Symbol %s not found", name)))
                );
    }

    public Rule getRule(String definition) {
        return terminalRules.stream()
                .filter(r -> r.toString().equals(definition))
                .findFirst()
                .orElseGet(() -> nonTerminalRules.stream()
                        .filter(r -> r.toString().equals(definition))
                        .findFirst().orElseThrow(() -> new ApplicationException(String.format("Rule %s not found", definition)))
                );
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

    public void addNonTerminalRule(Rule rule) {
        nonTerminalRules.add(rule);
    }

    public void removeRule(Rule ruleToRemove) {
        //todo: check what kind of rule is removed, don't check both lists
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
