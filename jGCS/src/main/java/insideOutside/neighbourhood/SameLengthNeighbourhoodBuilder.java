package insideOutside.neighbourhood;

import dataset.Sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SameLengthNeighbourhoodBuilder extends NeighbourhoodBuilder {

    private final Map<Integer, List<Sequence>> negatives;

    private final boolean random;

    public SameLengthNeighbourhoodBuilder(List<Sequence> negatives, boolean random) {
        this.negatives = negatives.stream()
                .collect(Collectors.groupingBy(Sequence::length));
        this.random = random;
    }

    @Override
    public List<Sequence> build(Sequence positive, int maxSize) {
        int length = positive.length();
        List<Sequence> potentialNeighbours = new ArrayList<>();
        potentialNeighbours.addAll(negatives.getOrDefault(length - 1, Collections.emptyList()));
        potentialNeighbours.addAll(negatives.getOrDefault(length, Collections.emptyList()));
        potentialNeighbours.addAll(negatives.getOrDefault(length + 1, Collections.emptyList()));
        if (potentialNeighbours.size() > maxSize) {
            if (random)
                Collections.shuffle(potentialNeighbours);
            return potentialNeighbours.subList(0, maxSize);
        }
        return potentialNeighbours;
    }
}
