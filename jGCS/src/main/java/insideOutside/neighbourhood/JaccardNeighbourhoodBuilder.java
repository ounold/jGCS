package insideOutside.neighbourhood;

import dataset.Sequence;
import distance.DistanceService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JaccardNeighbourhoodBuilder extends NeighbourhoodBuilder {

    private final DistanceService distanceService = DistanceService.getInstance();

    private final List<Sequence> negatives;

    private final double distanceLimit;

    private final boolean random;

    public JaccardNeighbourhoodBuilder(List<Sequence> negatives, double distanceLimit, boolean random) {
        this.negatives = negatives;
        this.distanceLimit = distanceLimit;
        this.random = random;
    }

    @Override
    public List<Sequence> build(Sequence positive, int maxSize) {
        List<Sequence> potentialNeighbours = negatives.stream()
                .filter(s -> distanceService.jaccard(positive.symbolSet(), s.symbolSet()) <= distanceLimit)
                .collect(Collectors.toList());
        if (potentialNeighbours.size() > maxSize) {
            if (random)
                Collections.shuffle(potentialNeighbours);
            return potentialNeighbours.subList(0, maxSize);
        }
        return potentialNeighbours;
    }
}
