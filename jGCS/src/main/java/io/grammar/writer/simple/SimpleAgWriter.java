package io.grammar.writer.simple;

import grammar.Grammar;
import grammar.Rule;
import io.grammar.rule.simple.SimpleAgNonTerminalRule;
import io.grammar.rule.simple.SimpleAgTerminalRule;
import io.grammar.token.AgToken;
import io.grammar.token.simple.SimpleAgSeparator;
import io.grammar.writer.AgWriter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SimpleAgWriter extends AgWriter {

    public SimpleAgWriter(Grammar grammar) {
        super(grammar);
    }

    @Override
    public String writeGrammar() {
        LinkedList<AgToken> result = new LinkedList<>();
        grammar.getTerminalRules().stream()
                .sorted(Comparator.comparing(Rule::getDefinition))
                .map(SimpleAgTerminalRule::new)
                .forEach(r -> {
                    result.addAll(r.write());
                    result.add(new SimpleAgSeparator());
                });
        grammar.getNonTerminalRules().stream()
                .sorted(Comparator.comparing(Rule::getDefinition))
                .map(SimpleAgNonTerminalRule::new)
                .forEach(r -> {
                    result.addAll(r.write());
                    result.add(new SimpleAgSeparator());
                });
        result.pollLast();
        return result.stream().map(AgToken::write).collect(Collectors.joining());
    }
}
