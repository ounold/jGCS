package insideOutside.neighbourhood;

import dataset.Sequence;

import java.util.Collections;
import java.util.List;

public class NoNeighbourhoodBuilder extends NeighbourhoodBuilder {

    @Override
    public List<Sequence> build(Sequence positive, int maxSize) {
        return Collections.emptyList();
    }

}
