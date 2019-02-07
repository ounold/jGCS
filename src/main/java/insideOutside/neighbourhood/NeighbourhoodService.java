package insideOutside.neighbourhood;

import application.ApplicationException;
import configuration.Configuration;
import configuration.ConfigurationService;
import dataset.Dataset;
import dataset.Sequence;
import induction.InductionMode;
import insideOutside.IoDataset;
import insideOutside.IoSequence;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static induction.InductionMode.IO;

public class NeighbourhoodService {

    private static final String MAX_SIZE = "ce.maxNeighbourhoodSize";
    private static final String CE_MODE = "ce.mode";
    private static final String CE_DISTANCE_LIMIT = "ce.distanceLimit";
    private static final String CE_RADNOM_NEIGHBOURHOODS = "ce.randomNeighbours";
    private static final String CE_REQUIRE_NEIGHBOURS = "ce.requireNeighbours";

    private Configuration configuration = ConfigurationService.getConfiguration();

    private static NeighbourhoodService instance;

    private NeighbourhoodService() {
    }

    public static NeighbourhoodService getInstance() {
        if (instance == null)
            instance = new NeighbourhoodService();
        return instance;
    }

    public IoDataset buildNeighbourhoods(Dataset dataset) {
        IoDataset result = new IoDataset(
                fillTimesAsNeighbour(
                        dataset,
                        buildNeighboursStream(dataset, getNeighbourhoodBuilder(dataset))
                )
        );
        if(configuration.getEnum(InductionMode::valueOf, CE_MODE) != IO && configuration.getBoolean(CE_REQUIRE_NEIGHBOURS) && result.getSequences().stream().mapToInt(IoSequence::getTimesAsNeighbour).sum() == 0)
            throw new ApplicationException("No neighbours found. Use IO mode instead.");
        return result;
    }

    private Stream<Sequence> buildNeighboursStream(Dataset dataset, NeighbourhoodBuilder neighbourhoodBuilder) {
        return dataset.getSequences().stream()
                .filter(Sequence::isPositive)
                .flatMap(s -> neighbourhoodBuilder.build(s, configuration.getInteger(MAX_SIZE)).stream());
    }

    private List<IoSequence> fillTimesAsNeighbour(Dataset dataset, Stream<Sequence> result) {
        Map<Sequence, List<Sequence>> grouped = result
                .collect(Collectors.groupingBy(Function.identity()));
        dataset.getSequences().forEach(s -> grouped.putIfAbsent(s, Collections.emptyList()));
        return grouped
                .entrySet()
                .stream()
                .map(e -> new IoSequence(e.getKey(), e.getValue().size()))
                .collect(Collectors.toList());
    }

    private NeighbourhoodBuilder getNeighbourhoodBuilder(Dataset dataset) {
        List<Sequence> negatives = dataset.getSequences().stream()
                .filter(sequence -> !sequence.isPositive())
                .collect(Collectors.toList());
        return getBuilder(negatives, configuration.getEnum(InductionMode::valueOf, CE_MODE));
    }

    private NeighbourhoodBuilder getBuilder(List<Sequence> negatives, InductionMode mode) {
        switch (mode) {
            case IO:
                return new NoNeighbourhoodBuilder();
            case CE_SAME_LENGTH:
                return new SameLengthNeighbourhoodBuilder(negatives, configuration.getBoolean(CE_RADNOM_NEIGHBOURHOODS));
            case CE_ALL:
                return new FullNeighbourhoodBuilder(negatives, configuration.getBoolean(CE_RADNOM_NEIGHBOURHOODS));
            case CE_LEVENSHTEIN:
                return new LevenshteinNeighbourhoodBuilder(negatives, configuration.getInteger(CE_DISTANCE_LIMIT), configuration.getBoolean(CE_RADNOM_NEIGHBOURHOODS));
            case CE_DAMERAU:
                return new DamerauNeighbourhoodBuilder(negatives, configuration.getInteger(CE_DISTANCE_LIMIT), configuration.getBoolean(CE_RADNOM_NEIGHBOURHOODS));
            case CE_JACCARD:
                return new JaccardNeighbourhoodBuilder(negatives, configuration.getDouble(CE_DISTANCE_LIMIT), configuration.getBoolean(CE_RADNOM_NEIGHBOURHOODS));
            default:
                throw new ApplicationException(String.format("Unkown mode: %s", mode));
        }
    }

}
