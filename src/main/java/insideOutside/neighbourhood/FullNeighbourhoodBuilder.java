package insideOutside.neighbourhood;

import dataset.Sequence;

import java.util.Collections;
import java.util.List;

public class FullNeighbourhoodBuilder extends NeighbourhoodBuilder {

    private final List<Sequence> negatives;

    private final boolean random;

    public FullNeighbourhoodBuilder(List<Sequence> negatives, boolean random) {
        this.negatives = negatives;
        this.random = random;
    }

    @Override
    public List<Sequence> build(Sequence positive, int maxSize) {
        if (negatives.size() > maxSize) {
            if (random)
                Collections.shuffle(negatives);
            return negatives.subList(0, maxSize);
        }
        return negatives;
    }
}
