package grammar;

import application.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grammar {

    private List<Rule> terminalRules = new ArrayList<>();
    private List<Rule> nonTerminalRules = new ArrayList<>();
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

}
