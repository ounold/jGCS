package io.grammar.token;

import io.grammar.parser.AgContext;
import io.grammar.parser.AgInterpretable;

import java.util.Random;

import static io.grammar.tokenType.AgTokenType.PROBABILITY;

public class AgProbability extends AgToken implements AgInterpretable<Double> {

    public AgProbability(String value) {
        super(value, PROBABILITY);
    }

    public AgProbability(double probability) {
        super("(" + probability + ")", PROBABILITY);
    }

    public Double interpret(AgContext context) {
        if (value == null) {
            if (context.isRandomProbabilities())
                return new Random().nextDouble();
            return 1d;
        }
        return Double.parseDouble(value.substring(1, value.length() - 1));
    }
}
