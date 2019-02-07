package executionTime;

import configuration.Configuration;
import configuration.ConfigurationService;
import io.file.FileService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExecutionTimeService {

    private static final double MILLION = 1000. * 1000.;

    private ExecutionTimeRepository executionTimeRepository = ExecutionTimeRepository.getInstance();

    private FileService fileService = FileService.getInstance();

    private Configuration configuration = ConfigurationService.getConfiguration();

    private static ExecutionTimeService instance;

    private ExecutionTimeService() {
    }

    public static ExecutionTimeService getInstance() {
        if (instance == null)
            instance = new ExecutionTimeService();
        return instance;
    }

    public <T> T saveExecutionTime(EtMarkersChain methodId, Supplier<T> action) {
        long startTime = getCurrentTime();
        T result = action.get();
        long endTime = getCurrentTime();
        executionTimeRepository.insert(methodId, endTime - startTime);
        return result;
    }

    public void saveExecutionTime(EtMarkersChain methodId, Runnable action) {
        long startTime = getCurrentTime();
        action.run();
        long endTime = getCurrentTime();
        executionTimeRepository.insert(methodId, endTime - startTime);
    }

    private long getCurrentTime() {
        return System.nanoTime();
    }

    public void writeAveragesToCSV(String filename) {
        List<List<Object>> records = new ArrayList<>();
        records.add(Arrays.asList("Configuration", "Id", "Avg. time in millis", "Avg. time", "Executions"));
        records.addAll(executionTimeRepository.getResult().entrySet().stream()
                .sorted(Comparator.comparing(t -> t.getKey().toString()))
                .map(e -> {
                    List<Object> result = new ArrayList<>();
                    result.add(configuration.getName());
                    result.add(e.getKey().toString());
                    double averageTime = e.getValue().stream().mapToDouble(v -> v).average().orElse(0);
                    result.add(averageTime / MILLION);
                    result.add(humanReadableFormat(Duration.ofNanos((long) averageTime)));
                    result.add(e.getValue().size());
                    return result;
                }).collect(Collectors.toList()));
        fileService.writeToCSV(
                filename,
                records
        );
    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d+(\\.\\d+)?\\w)", "$1 ")
                .trim()
                .toLowerCase();
    }
}
