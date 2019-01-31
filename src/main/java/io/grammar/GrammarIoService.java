package io.grammar;

import application.ApplicationException;
import configuration.Configuration;
import configuration.ConfigurationService;
import grammar.Grammar;
import io.file.FileService;
import io.grammar.parser.AgParser;
import io.grammar.parser.extended.ExtendedAgParser;
import io.grammar.parser.simple.SimpleAgParser;
import io.grammar.writer.AgWriter;
import io.grammar.writer.extended.ExtendedAgWriter;
import io.grammar.writer.simple.SimpleAgWriter;

public class GrammarIoService {

    private static final String AG_FORMAT = "grammar.format";

    private FileService fileService = FileService.getInstance();

    private Configuration configuration = ConfigurationService.getConfiguration();

    private static GrammarIoService instance;

    private GrammarIoService() {
    }

    public static GrammarIoService getInstance() {
        if (instance == null)
            instance = new GrammarIoService();
        return instance;
    }

    public Grammar parseGrammar(String line, boolean skipDuplicates, boolean randomProbabilities) {
        return getParser(line, skipDuplicates, randomProbabilities).parseGrammar();
    }

    private AgParser getParser(String line, boolean skipDuplicates, boolean randomProbabilities) {
        switch (configuration.getEnum(AgFormat::valueOf, AG_FORMAT)) {
            case SIMPLE:
                return new SimpleAgParser(line, skipDuplicates, randomProbabilities);
            case EXTENDED:
                return new ExtendedAgParser(line, skipDuplicates, randomProbabilities);
            default:
                throw new ApplicationException("Grammar format unknown");
        }
    }

    public Grammar parseGrammarFromFile(String path, boolean skipDuplicates, boolean randomProbabilities) {
        return parseGrammar(fileService.readFileToString(path), skipDuplicates, randomProbabilities);
    }

    public String writeGrammar(Grammar grammar) {
        return getWriter(grammar).writeGrammar();
    }

    private AgWriter getWriter(Grammar grammar) {
        switch (configuration.getEnum(AgFormat::valueOf, AG_FORMAT)) {
            case SIMPLE:
                return new SimpleAgWriter(grammar);
            case EXTENDED:
                return new ExtendedAgWriter(grammar);
            default:
                throw new ApplicationException("Grammar format unknown");
        }
    }

}
