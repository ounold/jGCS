package io.grammar.parser;

import application.ApplicationException;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AgContext {

    private Map<String, Symbol> terminalSymbols = new HashMap<>();

    private Map<String, Symbol> nonTerminalSymbols = new HashMap<>();

    private Map<String, Rule> terminalRules = new HashMap<>();

    private Map<String, Rule> nonTerminalRules = new HashMap<>();

    @Getter(AccessLevel.NONE)
    private int symbolIndex = 0;

    private boolean skipDuplicates;

    private boolean randomProbabilities;

    public AgContext(boolean skipDuplicates, boolean randomProbabilities) {
        this.skipDuplicates = skipDuplicates;
        this.randomProbabilities = randomProbabilities;
    }

    public int nextSymbolIndex() {
        return symbolIndex++;
    }

    public Rule saveTerminalRule(Rule rule) {
        return saveRule(rule, terminalRules);
    }

    public Rule saveNonTerminalRule(Rule rule) {
        return saveRule(rule, nonTerminalRules);
    }

    public Grammar buildGrammar() {
        if(nonTerminalSymbols.values().stream().noneMatch(Symbol::isStart))
            throw new ApplicationException("Grammar without start symbol");
        return new Grammar(new ArrayList<>(terminalRules.values()), new ArrayList<>(nonTerminalRules.values()), new ArrayList<>(terminalSymbols.values()), new ArrayList<>(nonTerminalSymbols.values()));

    }

    private Rule saveRule(Rule rule, Map<String, Rule> definedRules) {
        String ruleDefinition = rule.getDefinition();
        if (!skipDuplicates && definedRules.containsKey(ruleDefinition))
            throw new ApplicationException(String.format("Rule %s already defined", ruleDefinition));
        return definedRules.putIfAbsent(ruleDefinition, rule);
    }
}
