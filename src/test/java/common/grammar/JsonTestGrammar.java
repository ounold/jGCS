package common.grammar;

import com.fasterxml.jackson.databind.ObjectMapper;
import grammar.Grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonTestGrammar implements TestGrammar {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Grammar GRAMMAR;

    public JsonTestGrammar(String path){
        try {
            GRAMMAR = MAPPER.readValue(new String(Files.readAllBytes(Paths.get(path))), Grammar.class);
        } catch (IOException e) {
            throw new IllegalStateException("Grammar not imported", e);
        }
    }

    @Override
    public Grammar getGrammar() {
        return GRAMMAR;
    }

}
