package io.grammar.parser.extended;

import application.ApplicationException;
import grammar.Grammar;
import io.grammar.parser.AgParser;
import io.grammar.rule.extended.ExtendedAgNonTerminalRule;
import io.grammar.rule.extended.ExtendedAgTerminalRule;
import io.grammar.token.AgArrow;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;
import io.grammar.token.AgTerminalSymbol;
import io.grammar.token.extended.ExtendedAgNonTerminalSymbol;
import io.grammar.token.extended.ExtendedAgSeparator;
import io.grammar.token.extended.ExtendedAgTerminalSymbol;
import io.grammar.token.extended.ExtendedAgTokenSeparator;
import io.grammar.tokenType.AgTokenType;
import io.grammar.tokenType.AgTokenTypeDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.grammar.tokenType.AgTokenType.*;
import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;

public class ExtendedAgParser extends AgParser {
    private static final AgTokenTypeDefinition NON_TERMINAL_SYMBOL_DEF = new AgTokenTypeDefinition(AgTokenType.NON_TERMINAL_SYMBOL, "\\w+", ExtendedAgNonTerminalSymbol::new);
    private static final AgTokenTypeDefinition TERMINAL_SYMBOL_DEF = new AgTokenTypeDefinition(AgTokenType.TERMINAL_SYMBOL, "'[^']+'|\"[^\"]+\"", ExtendedAgTerminalSymbol::new);
    private static final AgTokenTypeDefinition ARROW_DEF = new AgTokenTypeDefinition(AgTokenType.ARROW, "->", AgArrow::new);
    private static final AgTokenTypeDefinition SEPARATOR_DEF = new AgTokenTypeDefinition(AgTokenType.SEPARATOR, "\\n", ExtendedAgSeparator::new);
    private static final AgTokenTypeDefinition PROBABILITY_DEF = new AgTokenTypeDefinition(AgTokenType.PROBABILITY, "\\(\\d(?:\\.\\d+)?(?:[eE][-+]?\\d+)?\\)", AgProbability::new);
    private static final AgTokenTypeDefinition TOKEN_SEPARATOR_DEF = new AgTokenTypeDefinition(AgTokenType.TOKEN_SEPARATOR, " ", ExtendedAgTokenSeparator::new);

    private static final Map<AgTokenType, AgTokenTypeDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put(AgTokenType.NON_TERMINAL_SYMBOL, NON_TERMINAL_SYMBOL_DEF);
        DEFINITIONS.put(AgTokenType.TERMINAL_SYMBOL, TERMINAL_SYMBOL_DEF);
        DEFINITIONS.put(AgTokenType.ARROW, ARROW_DEF);
        DEFINITIONS.put(AgTokenType.SEPARATOR, SEPARATOR_DEF);
        DEFINITIONS.put(AgTokenType.PROBABILITY, PROBABILITY_DEF);
        DEFINITIONS.put(TOKEN_SEPARATOR, TOKEN_SEPARATOR_DEF);
    }

    public ExtendedAgParser(String line, boolean skipDuplicates, boolean randomProbabilities) {
        super(line, skipDuplicates, randomProbabilities, DEFINITIONS);
    }

    @Override
    public Grammar parseGrammar() {
        while (!stack.isEmpty()) {
            AgProbability probability = null;
            if (isTypeOnTop(PROBABILITY)) {
                probability = (AgProbability) stack.pop();
                removeFromTop(TOKEN_SEPARATOR);
            } else {
                probability = new AgProbability(null);
            }
            if (isTypeOnTop(TERMINAL_SYMBOL)) {
                AgTerminalSymbol right = (AgTerminalSymbol) stack.pop();
                removeFromTop(TOKEN_SEPARATOR);
                removeFromTop(ARROW);
                removeFromTop(TOKEN_SEPARATOR);
                AgNonTerminalSymbol left = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                rules.add(new ExtendedAgTerminalRule(left, right, probability));
            } else {
                AgNonTerminalSymbol right2 = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                removeFromTop(TOKEN_SEPARATOR);
                AgNonTerminalSymbol right1 = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                removeFromTop(TOKEN_SEPARATOR);
                removeFromTop(ARROW);
                removeFromTop(TOKEN_SEPARATOR);
                AgNonTerminalSymbol left = (AgNonTerminalSymbol) getFromTop(NON_TERMINAL_SYMBOL);
                rules.add(new ExtendedAgNonTerminalRule(left, right1, right2, probability));
            }
            if (!stack.empty())
                removeFromTop(SEPARATOR);
        }
        return interpretGrammar();
    }

    @Override
    protected void buildStack(String input) {
        String line = input;
        while (!line.isEmpty()) {
            boolean found = false;
            for (AgTokenTypeDefinition tokenType : definitions.values()) {
                Pattern pattern = Pattern.compile("(" + tokenType.getDefinition() + ")(.*)", MULTILINE | DOTALL);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    stack.push(tokenType.createInstance(matcher.group(1)));
                    line = matcher.group(2);
                    found = true;
                    break;
                }
            }
            if (!found)
                throw new ApplicationException("Grammar could not be parsed.");
        }
    }
}
