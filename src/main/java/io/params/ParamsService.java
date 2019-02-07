package io.params;

import application.ApplicationException;
import org.apache.commons.cli.*;

public class ParamsService {

    public static final String GRAMMAR = "g";
    public static final String CONFIG = "c";
    public static final String DATASET = "d";
    public static final String EVALUATION_DATASET = "v";
    public static final String OUTPUT = "o";
    public static final String EVALUATION = "e";
    public static final String REPEATS = "r";
    public static final String SERIAL = "s";
    public static final String TIMES = "t";
    private final Options options;

    private static ParamsService instance;

    private ParamsService() {
        options = new Options();
        options.addOption(createOptionWithParam(GRAMMAR, "File containing initial grammar", false));
        options.addOption(createOptionWithParam(CONFIG, "File containing configuration", true));
        options.addOption(createOptionWithParam(DATASET, "File containing dataset", true));
        options.addOption(createOptionWithParam(EVALUATION_DATASET, "File containing test dataset", false));
        options.addOption(createOptionWithParam(OUTPUT, "Output filename for grammar", false));
        options.addOption(createOptionWithParam(EVALUATION, "Output filename for evaluation", true));
        options.addOption(createOptionWithParam(TIMES, "Output filename for execution times", false));
        options.addOption(createOptionWithParam(REPEATS, "Number of repeats", false));
        options.addOption(createOptionWithoutParam(SERIAL, "Serial mode", false));
    }

    private Option createOptionWithParam(String name, String desc, boolean required) {
        return Option.builder(name)
                .required(required)
                .hasArg()
                .desc(desc)
                .build();
    }

    private Option createOptionWithoutParam(String name, String desc, boolean required) {
        return Option.builder(name)
                .required(required)
                .desc(desc)
                .build();
    }

    public static ParamsService getInstance() {
        if (instance == null)
            instance = new ParamsService();
        return instance;
    }

    public Params readParams(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine params = parser.parse(options, args);
            Params result = new Params();
            result.setGrammarFilename(params.getOptionValue(GRAMMAR));
            result.setConfigFilename(params.getOptionValue(CONFIG));
            result.setDatasetFilename(params.getOptionValue(DATASET));
            result.setTestDatasetFilename(params.getOptionValue(EVALUATION_DATASET));
            result.setOutputFilename(params.getOptionValue(OUTPUT));
            result.setEvaluationOutput(params.getOptionValue(EVALUATION));
            result.setTimesOutput(params.getOptionValue(TIMES));
            if (params.hasOption(REPEATS))
                result.setRepeats(Integer.parseInt(params.getOptionValue(REPEATS)));
            else
                result.setRepeats(1);
            result.setSerial(params.hasOption(SERIAL));
            return result;
        } catch (ParseException e) {
            throw new ApplicationException("Paramters could not be parsed", e);
        }
    }

}
