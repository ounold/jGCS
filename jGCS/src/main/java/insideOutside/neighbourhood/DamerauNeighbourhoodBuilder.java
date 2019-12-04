package insideOutside.neighbourhood;

import dataset.Sequence;
import distance.DistanceService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DamerauNeighbourhoodBuilder extends NeighbourhoodBuilder {

    private final DistanceService distanceService = DistanceService.getInstance();

    private final Map<Integer, List<Sequence>> negatives;

    private final int distanceLimit;

    private final boolean random;

    public DamerauNeighbourhoodBuilder(List<Sequence> negatives, int distanceLimit, boolean random) {
        this.negatives = negatives.stream()
                .collect(Collectors.groupingBy(Sequence::length));
        this.distanceLimit = distanceLimit;
        this.random = random;
    }

    @Override
    public List<Sequence> build(Sequence positive, int maxSize) {
        int length = positive.length();
        List<Sequence> potentialNeighbours = negatives.keySet().stream()
                .filter(l -> Math.abs(l - length) <= distanceLimit)
                .flatMap(l -> negatives.get(l).stream())
                .filter(s -> distanceService.damerau(positive.symbolList(), s.symbolList()) <= distanceLimit)
                .collect(Collectors.toList());
        if (potentialNeighbours.size() > maxSize) {
            if (random)
                Collections.shuffle(potentialNeighbours);
            return potentialNeighbours.subList(0, maxSize);
        }
        return potentialNeighbours;
    }

}
