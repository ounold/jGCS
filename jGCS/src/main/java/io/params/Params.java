package io.params;

import lombok.Data;

@Data
public class Params {

    private boolean serial;
    private String grammarFilename;
    private String datasetFilename;
    private String testDatasetFilename;
    private String configFilename;
    private String outputFilename;
    private String evaluationOutput;
    private String timesOutput;
    private int repeats;

}
