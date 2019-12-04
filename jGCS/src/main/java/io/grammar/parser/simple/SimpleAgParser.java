package io.grammar.parser.simple;

import application.ApplicationException;
import grammar.Grammar;
import io.grammar.parser.AgParser;
import io.grammar.rule.simple.SimpleAgNonTerminalRule;
import io.grammar.rule.simple.SimpleAgTerminalRule;
import io.grammar.token.AgArrow;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;
import io.grammar.token.AgTerminalSymbol;
import io.grammar.token.simple.SimpleAgNonTerminalSymbol;
import io.grammar.token.simple.SimpleAgSeparator;
import io.grammar.token.simple.SimpleAgTerminalSymbol;
import io.grammar.tokenType.AgTokenType;
import io.grammar.tokenType.AgTokenTypeDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.grammar.tokenType.AgTokenType.*;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;

public class SimpleAgParser extends AgParser {

    private static final AgTokenTypeDefinition NON_TERMINAL_SYMBOL_DEF = new AgTokenTypeDefinition(NON_TERMINAL_SYMBOL, "[A-Z$]", SimpleAgNonTerminalSymbol::new);
    private static final AgTokenTypeDefinition TERMINAL_SYMBOL_DEF = new AgTokenTypeDefinition(TERMINAL_SYMBOL, "[a-z]", SimpleAgTerminalSymbol::new);
    private static final AgTokenTypeDefinition ARROW_DEF = new AgTokenTypeDefinition(ARROW, "->", AgArrow::new);
    private static final AgTokenTypeDefinition SEPARATOR_DEF = new AgTokenTypeDefinition(SEPARATOR, ";", SimpleAgSeparator::new);
    private static final AgTokenTypeDefinition PROBABILITY_DEF = new AgTokenTypeDefinition(PROBABILITY, "\\(\\d(?:\\.\\d+)?(?:[eE][-+]?\\d+)?\\)", AgProbability::new);

    private static final Map<AgTokenType, AgTokenTypeDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put(NON_TERMINAL_SYMBOL, NON_TERMINAL_SYMBOL_DEF);
        DEFINITIONS.put(TERMINAL_SYMBOL, TERMINAL_SYMBOL_DEF);
        DEFINITIONS.put(ARROW, ARROW_DEF);
        DEFINITIONS.put(SEPARATOR, SEPARATOR_DEF);
        DEFINITIONS.put(PROBABILITY, PROBABILITY_DEF);
    }

    public SimpleAgParser(String line, boolean skipDuplicates, boolean randomProbabilities) {
        super(line, skipDuplicates, randomProbabilities, DEFINITIONS);
    }

    @Override
    public Grammar parseGrammar() {
        while (!stack.isEmpty()) {
            AgProbability probability = null;
            if (isTypeOnTop(PROBABILITY))
                probability = (AgProbability) stack.pop();
            else
                probability = new AgProbability(null);
            if (isTypeOnTop(TERMINAL_SYMBOL)) {
                AgTerminalSymbol right = (AgTerminalSymbol) stack.pop();
                removeFromTop(ARROW);
                AgNonTerminalSymbol left = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                rules.add(new SimpleAgTerminalRule(left, right, probability));
            } else {
                AgNonTerminalSymbol right2 = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                AgNonTerminalSymbol right1 = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                removeFromTop(ARROW);
                AgNonTerminalSymbol left = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                rules.add(new SimpleAgNonTerminalRule(left, right1, right2, probability));
            }
            if (!stack.empty())
                removeFromTop(SEPARATOR);
        }
        return interpretGrammar();
    }

    @Override
    protected void buildStack(String line) {
        if (line.isEmpty())
            return;
        for (AgTokenTypeDefinition tokenType : definitions.values()) {
            Pattern pattern = Pattern.compile("\\s*(" + tokenType.getDefinition() + ")\\s*(.*)", MULTILINE | DOTALL);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                stack.push(tokenType.createInstance(matcher.group(1)));
                buildStack(matcher.group(2));
                return;
            }
        }
        throw new ApplicationException("Grammar could not be parsed.");
    }
}

