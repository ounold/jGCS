package application;

import configuration.Configuration;
import configuration.ConfigurationService;
import covering.CoveringService;
import dataset.Dataset;
import evaluation.EvaluationService;
import executionTime.EtMarkers;
import executionTime.EtMarkersChain;
import executionTime.ExecutionTimeService;
import grammar.Grammar;
import heurstic.HeuristicService;
import induction.GrammarInductionService;
import induction.InductionMode;
import io.abbadingo.AbbadingoReaderService;
import io.file.FileService;
import io.grammar.GrammarIoService;
import io.params.Params;
import io.params.ParamsService;
import io.ui.UiService;
import randomGrammar.RandomGrammarService;
import stopCondition.StopConditionService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static final EtMarkersChain ETMC_APPLICATION = EtMarkersChain.init(EtMarkers.ET_APPLICATION);
    public static final EtMarkersChain ETMC_DATASET = ETMC_APPLICATION.get(EtMarkers.ET_DATASET);
    public static final EtMarkersChain ETMC_EXECUTION = ETMC_DATASET.get(EtMarkers.ET_EXECUTION);
    public static final EtMarkersChain ETMC_INDUCTION = ETMC_EXECUTION.get(EtMarkers.ET_INDUCTION);
    public static final EtMarkersChain ETM_HEURISTIC = ETMC_EXECUTION.get(EtMarkers.ET_HEURISTIC);

    private static final String SKIP_DUPLICATES = "grammar.skipDuplicates";
    private static final String RANDOM_PROBABILITIES = "grammar.randomProbabilities";

    private static final String CE_MODE = "ce.mode";
    private static final String CE_ITERATIONS = "ce.iterations";
    private static final String CYK_COVERING_ENABLED = "cyk.coveringEnabled";

    private ParamsService paramsService = ParamsService.getInstance();
    private ConfigurationService configurationService = ConfigurationService.getInstance();
    private AbbadingoReaderService abbadingoReaderService = AbbadingoReaderService.getInstance();
    private UiService uiService = UiService.getInstance();

    private RandomGrammarService randomGrammarService;
    private GrammarInductionService grammarInductionService;
    private EvaluationService evaluationService;
    private FileService fileService;
    private ExecutionTimeService executionTimeService;
    private StopConditionService stopConditionService;
    private GrammarIoService grammarIoService;
    private HeuristicService heuristicService;
    private final Configuration configuration;
    private final Params params;

    public Application(String[] args) {
        uiService.info("Application started");
        params = paramsService.readParams(args);
        configurationService.loadConfigurationFile(params.getConfigFilename());
        configuration = ConfigurationService.getConfiguration();
        uiService.info("Configuration loaded successfully");
        randomGrammarService = RandomGrammarService.getInstance();
        grammarInductionService = GrammarInductionService.getInstance();
        evaluationService = EvaluationService.getInstance();
        fileService = FileService.getInstance();
        executionTimeService = ExecutionTimeService.getInstance();
        stopConditionService = StopConditionService.getInstance();
        stopConditionService.initializeStopConditions();
        grammarIoService = GrammarIoService.getInstance();
        heuristicService = HeuristicService.getInstance();
    }

    public void run() {
        try {
            executionTimeService.saveExecutionTime(ETMC_APPLICATION, () -> {
                Integer iterations = configuration.getInteger(CE_ITERATIONS);
                InductionMode mode = configuration.getEnum(InductionMode::valueOf, CE_MODE);
                List<InputSet> inputSets = createInputSets();
                String evaluationOutput = params.getEvaluationOutput();
                truncateEvaluationOutput(evaluationOutput);
                inputSets.stream().sorted(Comparator.comparing(InputSet::getDataset)).forEach(inputSet -> {
                    try {
                        executionTimeService.saveExecutionTime(ETMC_DATASET, () -> {
                            Path datasetPath = inputSet.getDataset();
                            Path testDatasetPath = inputSet.getTestDataset();
                            Optional<Path> grammarPath = inputSet.getGrammar();
                            printPaths(datasetPath, testDatasetPath, grammarPath);
                            Dataset dataset = loadDataset(datasetPath);
                            Dataset testDataset = loadTestDataset(testDatasetPath);
                            BestResultWrapper bestResultWrapper = new BestResultWrapper();
                            for (int execution = 1; execution < params.getRepeats() + 1; execution++) {
                                final int currentExecution = execution;
                                executionTimeService.saveExecutionTime(ETMC_EXECUTION, () -> {
                                    uiService.info("----------Starting execution: %d/%d----------", currentExecution, params.getRepeats());
                                    Grammar grammar = loadGrammar(grammarPath, dataset);
                                    runHeuristic(grammar, currentExecution);
                                    runInduction(iterations, mode, dataset, testDataset, grammar);
                                    saveEvaluation(evaluationOutput, datasetPath, currentExecution);
                                    updateBestResult(bestResultWrapper);
                                    evaluationService.clearEvaluation();
                                });
                            }
                            saveBestGrammar(bestResultWrapper.getEvaluation().getGrammar(), datasetPath.getFileName().toString());
                        });
                    } catch (ApplicationException exc) {
                        uiService.error(exc.getMessage());
                    }
                });
            });
            saveAverageExecutionTimes();
        } catch (ApplicationException exc) {
            uiService.fatal(exc.getMessage());
        }
    }

    private void printPaths(Path datasetPath, Path testDatasetPath, Optional<Path> grammarPath) {
        uiService.info("---------------------------------------------");
        uiService.info("Dataset: %s", datasetPath.toString());
        uiService.info("Test dataset: %s", testDatasetPath.toString());
        if (grammarPath.isPresent())
            uiService.info("Grammar: %s", grammarPath.get().toString());
        else
            uiService.info("Generated grammar");
        uiService.info("---------------------------------------------");
    }

    private void saveAverageExecutionTimes() {
        String timesOutput = params.getTimesOutput();
        if (timesOutput != null) {
            uiService.info("Saving average execution times to file: %s", timesOutput);
            executionTimeService.writeAveragesToCSV(timesOutput);
        }
    }

    private void saveEvaluation(String evaluationOutput, Path datasetPath, int execution) {
        uiService.info("Saving evaluation to file: %s", evaluationOutput);
        evaluationService.appendEvaluationToCSV(datasetPath.getFileName().toString(), execution, evaluationOutput);
    }

    private void runInduction(Integer iterations, InductionMode mode, Dataset dataset, Dataset testDataset, Grammar grammar) {
        uiService.info("Starting induction. Mode: %s. Iterations: %d.", mode, iterations);
        executionTimeService.saveExecutionTime(ETMC_INDUCTION, () -> grammarInductionService.run(grammar, dataset, testDataset, iterations, configuration.getBoolean(CYK_COVERING_ENABLED)));
    }

    private void runHeuristic(Grammar grammar, int execution) {
        uiService.info("Starting heuristic. Algorithm: %s.", heuristicService.getAlgorithmType());
        executionTimeService.saveExecutionTime(ETM_HEURISTIC, () -> heuristicService.run(grammar, execution));
    }

    private Grammar loadGrammar(Optional<Path> grammarPath, Dataset dataset) {
        if (grammarPath.isPresent()) {
            Grammar grammar = grammarIoService.parseGrammarFromFile(grammarPath.get().toString(), configuration.getBoolean(SKIP_DUPLICATES), configuration.getBoolean(RANDOM_PROBABILITIES));
            uiService.info("Grammar loaded successfully");
            return grammar;
        } else {
            Grammar grammar = randomGrammarService.createRandomGrammar(dataset);
            uiService.info("Grammar generated successfully");
            return grammar;
        }
    }

    private void updateBestResult(BestResultWrapper bestResultWrapper) {
        bestResultWrapper.setEvaluation(evaluationService.getBestResult(bestResultWrapper.getEvaluation()));
    }

    private void truncateEvaluationOutput(String evaluationOutput) {
        uiService.info("Truncating file: %s", evaluationOutput);
        evaluationService.truncateEvaluationOutput(evaluationOutput);
    }

    private Dataset loadDataset(Path datasetPath) {
        Dataset dataset = abbadingoReaderService.loadFile(datasetPath.toString());
        uiService.info("Dataset loaded successfully");
        return dataset;
    }

    private Dataset loadTestDataset(Path datasetPath) {
        Dataset dataset = abbadingoReaderService.loadFile(datasetPath.toString());
        uiService.info("Test dataset loaded successfully");
        return dataset;
    }

    private void saveBestGrammar(String bestGrammar, String datasetFilename) {
        String outputFilename = params.getOutputFilename();
        if (outputFilename != null) {
            String path = params.isSerial() ?
                    Paths.get(outputFilename, datasetFilename).toString()
                    : outputFilename;
            uiService.info("Saving the best grammar to file: %s", path);
            fileService.writeToFile(path, bestGrammar);
        }
    }

    private List<InputSet> createInputSets() {
        boolean serial = params.isSerial();
        List<Path> datasets = fileService.toFileList(serial, params.getDatasetFilename());
        List<Path> testDatasets = Optional.ofNullable(params.getTestDatasetFilename())
                .map(param -> fileService.toFileList(serial, param))
                .orElse(datasets);
        if (serial) {
            Function<Path, Optional<Path>> grammarGetter = Optional.ofNullable(params.getGrammarFilename())
                    .map(param -> fileService.toFileList(serial, param))
                    .map(fileList -> (Function<Path, Optional<Path>>) (Path filename) -> Optional.of(findByFilename(fileList, filename)))
                    .orElseGet(() -> (Path filename) -> Optional.empty());
            return datasets.stream()
                    .map(dataset -> new InputSet(
                            dataset,
                            findByFilename(testDatasets, dataset.getFileName()),
                            grammarGetter.apply(dataset.getFileName())
                    ))
                    .collect(Collectors.toList());
        } else {
            Supplier<Optional<Path>> grammarSupplier = Optional.ofNullable(params.getGrammarFilename())
                    .map(param -> fileService.toFileList(serial, param))
                    .map(fileList -> (Supplier<Optional<Path>>) () -> Optional.of(fileList.get(0)))
                    .orElseGet(() -> Optional::empty);
            return Collections.singletonList(new InputSet(
                    datasets.get(0),
                    testDatasets.get(0),
                    grammarSupplier.get()
            ));
        }
    }

    private Path findByFilename(List<Path> fileList, Path filename) {
        return fileList.stream().filter(td -> td.getFileName().equals(filename)).findFirst()
                .orElseThrow(() -> new ApplicationException("Lists do not contain same filenames"));
    }

}
