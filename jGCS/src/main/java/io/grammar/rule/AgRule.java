package io.grammar.rule;

import grammar.Rule;
import io.grammar.parser.AgInterpretable;
import io.grammar.token.AgToken;

import java.util.LinkedList;

public interface AgRule extends AgInterpretable<Rule> {

    LinkedList<AgToken> write();

}
