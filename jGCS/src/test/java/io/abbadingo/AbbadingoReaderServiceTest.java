package io.abbadingo;

import common.AbstractServiceTest;
import dataset.Dataset;
import dataset.Sequence;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class AbbadingoReaderServiceTest extends AbstractServiceTest {

    private AbbadingoReaderService abbadingoReaderService = AbbadingoReaderService.getInstance();

    @Test
    public void testLoadFile() {
        // when
        Dataset dataset = abbadingoReaderService.loadFile("src/test/resources/sequenceSets/tomita_1.txt");

        //then
        List<List<String>> positives = dataset.getSequences()
                .stream().filter(Sequence::isPositive)
                .map(Sequence::symbolList)
                .collect(Collectors.toList());
        assertIterableEquals(positives.get(0), Arrays.asList("a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(1), Arrays.asList("a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(2), Arrays.asList("a a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(3), Arrays.asList("a a a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(4), Arrays.asList("a a a a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(5), Arrays.asList("a a a a a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(6), Arrays.asList("a a a a a a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(positives.get(7), Arrays.asList("a a a a a a a a".split(Sequence.WORD_SEPARATOR)));

        //then
        List<List<String>> negatives = dataset.getSequences()
                .stream().filter(s -> !s.isPositive())
                .map(Sequence::symbolList)
                .collect(Collectors.toList());
        assertIterableEquals(negatives.get(0), Arrays.asList("b".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(1), Arrays.asList("a b".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(2), Arrays.asList("b a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(3), Arrays.asList("b b".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(4), Arrays.asList("b a a".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(5), Arrays.asList("a a b".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(6), Arrays.asList("a a a a a a a b".split(Sequence.WORD_SEPARATOR)));
        assertIterableEquals(negatives.get(7), Arrays.asList("a b a a a a a a".split(Sequence.WORD_SEPARATOR)));
    }

}