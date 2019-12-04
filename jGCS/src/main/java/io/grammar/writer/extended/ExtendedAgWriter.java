package io.grammar.writer.extended;

import grammar.Grammar;
import grammar.Rule;
import io.grammar.rule.extended.ExtendedAgNonTerminalRule;
import io.grammar.rule.extended.ExtendedAgTerminalRule;
import io.grammar.token.AgToken;
import io.grammar.token.extended.ExtendedAgSeparator;
import io.grammar.writer.AgWriter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ExtendedAgWriter extends AgWriter {

    public ExtendedAgWriter(Grammar grammar) {
        super(grammar);
    }

    @Override
    public String writeGrammar() {
        LinkedList<AgToken> result = new LinkedList<>();
        grammar.getTerminalRules().stream()
                .sorted(Comparator.comparing(Rule::getDefinition))
                .map(ExtendedAgTerminalRule::new)
                .forEach(r -> {
                    result.addAll(r.write());
                    result.add(new ExtendedAgSeparator());
                });
        grammar.getNonTerminalRules().stream()
                .sorted(Comparator.comparing(Rule::getDefinition))
                .map(ExtendedAgNonTerminalRule::new)
                .forEach(r -> {
                    result.addAll(r.write());
                    result.add(new ExtendedAgSeparator());
                });
        result.pollLast();
        return result.stream().map(AgToken::write).collect(Collectors.joining());
    }
}
