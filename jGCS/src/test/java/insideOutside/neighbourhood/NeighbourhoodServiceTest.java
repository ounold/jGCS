package insideOutside.neighbourhood;

import application.ApplicationException;
import common.AbstractServiceTest;
import configuration.ConfigurationService;
import dataset.Dataset;
import dataset.Sequence;
import induction.InductionMode;
import insideOutside.IoDataset;
import insideOutside.IoSequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class NeighbourhoodServiceTest extends AbstractServiceTest {

    private static final String MAX_SIZE_PROP = "ce.maxNeighbourhoodSize";
    private static final String CE_MODE_PROP = "ce.mode";
    private static final String CE_DISTANCE_LIMIT = "ce.distanceLimit";
    private static final String CE_RADNOM_NEIGHBOURHOODS = "ce.randomNeighbours";
    private static final String CE_REQUIRE_NEIGHBOURS = "ce.requireNeighbours";

    private static final int MAX_SIZE = 10;
    private static final int DISTANCE_LIMIT = 3;
    private static final double JACCARD_DISTANCE_LIMIT = .35;

    private NeighbourhoodService neighbourhoodService = NeighbourhoodService.getInstance();
    private ConfigurationService configurationService = ConfigurationService.getInstance();

    @Test
    public void testBuildNoNeighbourhoods() {
        // given
        Dataset dataset = buildSimpleDataset();
        configureIo();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        assertEquals(dataset.getSequences().size(), result.getSequences().size());
        assertTrue(result.getSequences().stream().allMatch(s -> s.getTimesAsNeighbour() == 0));
    }

    private void configureIo() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.IO.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
    }

    @Test
    public void testBuildFullNeighbourhoods() {
        // given
        Dataset dataset = buildSimpleDataset();
        configureCeAll();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        assertEquals(dataset.getSequences().size(), result.getSequences().size());
        assertTrue(result.getSequences().stream().filter(IoSequence::isPositive).allMatch(s -> s.getTimesAsNeighbour() == 0));
        assertEquals(result.getSequences().stream().mapToInt(IoSequence::getTimesAsNeighbour).sum(), MAX_SIZE * 20);
    }

    private void configureCeAll() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.CE_ALL.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
    }

    @Test
    public void testBuildFullNeighbourhoodsWithoutRandom() {
        Dataset dataset = buildSimpleDataset();
        configureCeAll();
        testWithoutRandom(dataset);
    }

    @Test
    public void testBuildSameLengthNeighbourhoods() {
        // given
        Dataset dataset = buildSimpleDataset();
        configureCeSameLength();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        assertEquals(dataset.getSequences().size(), result.getSequences().size());
        assertTrue(result.getSequences().stream().filter(IoSequence::isPositive).allMatch(s -> s.getTimesAsNeighbour() == 0));
        assertEquals(result.getSequences().stream().mapToInt(IoSequence::getTimesAsNeighbour).sum(), MAX_SIZE * 20);
        assertLessOrEqualWithLength(result, 1, 2 * MAX_SIZE);
        assertLessOrEqualWithLength(result, 20, 2 * MAX_SIZE);
        IntStream.range(2, 20)
                .forEach(i -> assertLessOrEqualWithLength(result, i, 3 * MAX_SIZE));
    }

    private void configureCeSameLength() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.CE_SAME_LENGTH.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
    }

    @Test
    public void testBuildSameLengthNeighbourhoodsWithoutRandom() {
        Dataset dataset = buildSimpleDataset();
        configureCeSameLength();
        testWithoutRandom(dataset);
    }

    @Test
    public void testBuildLevenshteinNeighbourhoods() {
        // given
        Dataset dataset = buildDistanceDataset();
        configureCeLevenshtein();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        Map<Integer, List<IoSequence>> byTimesAsNeighbour = result.getSequences()
                .stream()
                .collect(Collectors.groupingBy(IoSequence::getTimesAsNeighbour));
        assertIterableEquals(Arrays.asList(
                "a b b b b b g h i j k",
                "a b c d e f",
                "a b c d e f g h i j",
                "a b c d e f g h i j b b b b",
                "a b c d f e g h i j b b"
                ),
                byTimesAsNeighbour.get(0).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));

        assertIterableEquals(Arrays.asList(
                "a b c d e f g a",
                "a b c d e f g b h",
                "a b c d e f g h b b b",
                "a b c d e f g h i",
                "a b c d e f g h i b",
                "a b c d e f g h i j b",
                "a b c d e f g h i j b b"
                ),
                byTimesAsNeighbour.get(1).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));
    }

    private void configureCeLevenshtein() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.CE_LEVENSHTEIN.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
        configurationService.overrideProperty(CE_DISTANCE_LIMIT, "" + DISTANCE_LIMIT);
    }

    @Test
    public void testBuildLevenshteinNeighbourhoodsWithoutRandom() {
        Dataset dataset = buildSimpleDataset();
        configureCeLevenshtein();
        testWithoutRandom(dataset);
    }

    @Test
    public void testBuildDamerauNeighbourhoods() {
        // given
        Dataset dataset = buildDistanceDataset();
        configureCeDamerau();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        Map<Integer, List<IoSequence>> byTimesAsNeighbour = result.getSequences()
                .stream()
                .collect(Collectors.groupingBy(IoSequence::getTimesAsNeighbour));
        assertIterableEquals(Arrays.asList(
                "a b b b b b g h i j k",
                "a b c d e f",
                "a b c d e f g h i j",
                "a b c d e f g h i j b b b b"
                ),
                byTimesAsNeighbour.get(0).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));

        assertIterableEquals(Arrays.asList(
                "a b c d e f g a",
                "a b c d e f g b h",
                "a b c d e f g h b b b",
                "a b c d e f g h i",
                "a b c d e f g h i b",
                "a b c d e f g h i j b",
                "a b c d e f g h i j b b",
                "a b c d f e g h i j b b"
                ),
                byTimesAsNeighbour.get(1).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));
    }

    private void configureCeDamerau() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.CE_DAMERAU.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
        configurationService.overrideProperty(CE_DISTANCE_LIMIT, "" + DISTANCE_LIMIT);
    }

    @Test
    public void testBuildDamerauNeighbourhoodsWithoutRandom() {
        Dataset dataset = buildSimpleDataset();
        configureCeDamerau();
        testWithoutRandom(dataset);
    }

    @Test
    public void testBuildJaccardNeighbourhoods() {
        // given
        Dataset dataset = buildDistanceDataset();
        configureCeJaccard();

        // when
        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);

        // then
        Map<Integer, List<IoSequence>> byTimesAsNeighbour = result.getSequences()
                .stream()
                .collect(Collectors.groupingBy(IoSequence::getTimesAsNeighbour));
        assertIterableEquals(Arrays.asList(
                "a b b b b b g h i j k",
                "a b c d e f",
                "a b c d e f g h i j"
                ),
                byTimesAsNeighbour.get(0).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));

        assertIterableEquals(Arrays.asList(
                "a b c d e f g a",
                "a b c d e f g b h",
                "a b c d e f g h b b b",
                "a b c d e f g h i",
                "a b c d e f g h i b",
                "a b c d e f g h i j b",
                "a b c d e f g h i j b b",
                "a b c d e f g h i j b b b b",
                "a b c d f e g h i j b b"
                ),
                byTimesAsNeighbour.get(1).stream().map(is -> String.join(" ", is.getSequence().symbolList())).sorted().collect(Collectors.toList()));
    }

    private void configureCeJaccard() {
        configurationService.overrideProperty(CE_MODE_PROP, InductionMode.CE_JACCARD.name());
        configurationService.overrideProperty(MAX_SIZE_PROP, "" + MAX_SIZE);
        configurationService.overrideProperty(CE_DISTANCE_LIMIT, "" + JACCARD_DISTANCE_LIMIT);
    }

    @Test
    public void testBuildJaccardNeighbourhoodsWithoutRandom() {
        Dataset dataset = buildSimpleDataset();
        configureCeJaccard();
        testWithoutRandom(dataset);
    }

    @Test
    public void testBuildCeNeighbourhoodsFailsOnNoNeighbours(){
        Dataset dataset = buildOnlyPositivesDataset();
        configurationService.overrideProperty(CE_REQUIRE_NEIGHBOURS, "" + true);

        configureCeAll();
        Assertions.assertThrows(ApplicationException.class, () -> neighbourhoodService.buildNeighbourhoods(dataset));

        configureCeSameLength();
        Assertions.assertThrows(ApplicationException.class, () -> neighbourhoodService.buildNeighbourhoods(dataset));

        configureCeLevenshtein();
        Assertions.assertThrows(ApplicationException.class, () -> neighbourhoodService.buildNeighbourhoods(dataset));

        configureCeDamerau();
        Assertions.assertThrows(ApplicationException.class, () -> neighbourhoodService.buildNeighbourhoods(dataset));

        configureCeJaccard();
        Assertions.assertThrows(ApplicationException.class, () -> neighbourhoodService.buildNeighbourhoods(dataset));
    }

    @Test
    public void testBuildIoNeighbourhoodsWorksOnNoNeighbours(){
        Dataset dataset = buildOnlyPositivesDataset();
        configurationService.overrideProperty(CE_REQUIRE_NEIGHBOURS, "" + true);

        configureIo();
        neighbourhoodService.buildNeighbourhoods(dataset);
    }


    private void testWithoutRandom(Dataset dataset) {
        configurationService.overrideProperty(CE_RADNOM_NEIGHBOURHOODS, "" + false);

        IoDataset result = neighbourhoodService.buildNeighbourhoods(dataset);
        IoDataset result2 = neighbourhoodService.buildNeighbourhoods(dataset);

        for (int i = 0; i < result.getSequences().size(); i++) {
            IoSequence element = result.getSequences().get(i);
            IoSequence element2 = result2.getSequences().get(i);
            assertIterableEquals(element.getSequence().symbolList(), element2.getSequence().symbolList());
            assertEquals(element.getTimesAsNeighbour(), element2.getTimesAsNeighbour());
        }
    }

    private Dataset buildDistanceDataset() {
        Dataset dataset = new Dataset();
        dataset.setSequences(Arrays.asList(
                new Sequence("a b c d e f g h i j", true),
                new Sequence("a b c d e f g h i", false),
                new Sequence("a b c d e f g h i b", false),
                new Sequence("a b c d e f g h i j b", false),
                new Sequence("a b c d e f g b h", false),
                new Sequence("a b c d e f g a", false),
                new Sequence("a b c d e f g h i j b b", false),
                new Sequence("a b c d e f g h b b b", false),
                new Sequence("a b c d e f g h i j b b b b", false),
                new Sequence("a b b b b b g h i j k", false),
                new Sequence("a b c d e f", false),
                new Sequence("a b c d f e g h i j b b", false)
        ));
        return dataset;
    }

    private void assertLessOrEqualWithLength(IoDataset result, int length, int value) {
        assertTrue(result.getSequences().stream().filter(s -> s.getSequence().length() == length).mapToDouble(IoSequence::getTimesAsNeighbour).sum() <= value);
    }

    private Dataset buildOnlyPositivesDataset(){
        return new Dataset(Arrays.asList(new Sequence("A", true), new Sequence("B", true)));
    }

    private Dataset buildSimpleDataset() {
        Dataset dataset = new Dataset();
        IntStream.range(1, 21)
                .forEach(i -> dataset.getSequences().add(new Sequence(buildSequence(i), true)));
        IntStream.range(0, 20)
                .forEach(i ->
                        IntStream.range(0, 20)
                                .forEach(j -> dataset.getSequences().add(
                                        new Sequence(buildSequence(i), false)
                                ))
                );
        return dataset;
    }

    private String buildSequence(int length) {
        StringBuilder result = new StringBuilder();
        IntStream.range(0, length).forEach(i -> result.append("a"));
        return String.join(" ", result.toString().split(""));
    }

}