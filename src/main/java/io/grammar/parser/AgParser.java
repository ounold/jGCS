package io.grammar.parser;

import application.ApplicationException;
import grammar.Grammar;
import io.grammar.rule.AgRule;
import io.grammar.token.AgToken;
import io.grammar.tokenType.AgTokenType;
import io.grammar.tokenType.AgTokenTypeDefinition;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class AgParser {

    private static final Logger LOGGER = LogManager.getLogger(AgParser.class);

    protected final Map<AgTokenType, AgTokenTypeDefinition> definitions;

    @Getter
    protected Stack<AgToken> stack;

    protected List<AgRule> rules;

    protected AgContext context;

    public AgParser(String line, boolean skipDuplicates, boolean randomProbabilities, Map<AgTokenType, AgTokenTypeDefinition> definitions) {
        this.definitions = definitions;
        this.stack = new Stack<>();
        this.rules = new ArrayList<>();
        this.context = new AgContext(skipDuplicates, randomProbabilities);
        buildStack(line);
    }

    public abstract Grammar parseGrammar();

    protected Grammar interpretGrammar() {
        rules.forEach(r -> r.interpret(context));
        return context.buildGrammar();
    }

    protected AgToken getFromTop(AgTokenType tokenType) {
        AgToken result = stack.pop();
//        LOGGER.error("Getting from top {}: {}", tokenType, result.getValue());
        if (result.getType() != tokenType)
            throw new ApplicationException(String.format("Grammar could not be parsed. Expected: %s. Found: %s.", tokenType.name(), result.getType().name()));
        return result;
    }

    protected boolean isTypeOnTop(AgTokenType tokenType) {
        return stack.peek().getType() == tokenType;
    }

    protected void removeFromTop(AgTokenType tokenType) {
        AgToken result = stack.pop();
//        LOGGER.error("Removing from top {}: {}", tokenType, result.getValue());
        if (result.getType() != tokenType)
            throw new ApplicationException(String.format("Grammar could not be parsed. Expected: %s. Found: %s.", tokenType.name(), result.getType().name()));
    }

    protected abstract void buildStack(String line);

}
