package evaluation;

import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykResult;
import cyk.CykService;
import dataset.Dataset;
import grammar.Grammar;
import io.file.FileService;
import io.grammar.GrammarIoService;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EvaluationService {

    private static final String MAXIMIZATION_TARGET = "ev.maximizationTarget";
    private static final String EVALUATION_STEP = "ev.step";

    private CykService cykService = CykService.getInstance();
    private Configuration configuration = ConfigurationService.getConfiguration();
    private FileService fileService = FileService.getInstance();
    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    private static EvaluationService instance;

    private EvaluationRepository evaluationRepository = EvaluationRepository.getInstance();

    private EvaluationService() {
    }

    public static EvaluationService getInstance() {
        if (instance == null)
            instance = new EvaluationService();
        return instance;
    }

    public void truncateEvaluationOutput(String evaluationOutput) {
        fileService.deleteFile(evaluationOutput);
        writeEvaluationHeaderToCSV(evaluationOutput);
    }

    public Evaluation evaluateDataset(Dataset dataset, Grammar grammar) {
        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        dataset.getSequences().forEach(sequence -> {
            CykResult cykResult = cykService.runCyk(sequence, grammar, false);
            confusionMatrix.update(sequence.isPositive(), cykResult.isParsed());
        });
        return new Evaluation(grammarIoService.writeGrammar(grammar), grammar.getRules().size(), confusionMatrix);
    }

    public void saveEvaluationConditionally(Integer iteration, Supplier<Evaluation> evaluationSupplier) {
        int evaluationStep = configuration.getInteger(EVALUATION_STEP);
        if (evaluationStep != 0 && iteration % evaluationStep == 0)
            evaluationRepository.insert(iteration, evaluationSupplier.get());
    }

    public void saveEvaluation(Integer iteration, Supplier<Evaluation> evaluationSupplier) {
        evaluationRepository.insert(iteration, evaluationSupplier.get());
    }

    public void clearEvaluation() {
        evaluationRepository.clear();
    }

    public void writeEvaluationHeaderToCSV(String evaluationOutput) {
        fileService.appendToCSV(
                evaluationOutput,
                Collections.singletonList(Arrays.asList("Configuration", "Dataset", "Execution", "Iteration", "TP", "FP", "TN", "FN", "Sensitivity", "Precision", "Specificity", "F1", "Grammar size"))
        );
    }

    public void appendEvaluationToCSV(String dataset, int execution, String evaluationOutput) {
        fileService.appendToCSV(
                evaluationOutput,
                evaluationRepository.getResult().entrySet().stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .map(e -> {
                            List<Object> result = new ArrayList<>();
                            result.add(configuration.getName());
                            result.add(dataset);
                            result.add(execution);
                            result.add(e.getKey());
                            ConfusionMatrix value = e.getValue().getEvaluation();
                            result.add(value.getTruePositives());
                            result.add(value.getFalsePositives());
                            result.add(value.getTrueNegatives());
                            result.add(value.getFalseNegatives());
                            result.add(value.getSensitivity());
                            result.add(value.getPrecision());
                            result.add(value.getSpecificity());
                            result.add(value.getF1());
                            result.add(e.getValue().getGrammarSize());
                            return result;
                        }).collect(Collectors.toList())
        );
    }

    public Evaluation getLastResult() {
        return evaluationRepository.getLastResult();
    }

    public List<Evaluation> getLastResultsDesc(int limit) {
        return evaluationRepository.getLastResultsDesc(limit);
    }

    public Map<Integer, Evaluation> getResult() {
        return evaluationRepository.getResult();
    }

    public Evaluation getBestResult(Evaluation bestTillNow) {
        MaximizationTarget maximizationTarget = configuration.getEnum(MaximizationTarget::valueOf, MAXIMIZATION_TARGET);
        Function<ConfusionMatrix, Double> extractor = maximizationTarget.getExtractor();
        Evaluation result = bestTillNow;
        for(Evaluation evaluation: evaluationRepository.getResult().values()){
            if(result == null || extractor.apply(result.getEvaluation()) < extractor.apply(evaluation.getEvaluation())){
                result = evaluation;
            }
        }
        return result;
    }

}
