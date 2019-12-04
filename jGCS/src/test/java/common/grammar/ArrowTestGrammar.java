package common.grammar;

import grammar.Grammar;
import io.grammar.GrammarIoService;

public class ArrowTestGrammar implements TestGrammar {

    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    private final Grammar GRAMMAR;

    public ArrowTestGrammar(String grammar) {
        GRAMMAR = grammarIoService.parseGrammar(grammar, false, false);
    }

    @Override
    public Grammar getGrammar() {
        return GRAMMAR;
    }

}
