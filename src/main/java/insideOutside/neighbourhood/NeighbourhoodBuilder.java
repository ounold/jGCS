package insideOutside.neighbourhood;

import dataset.Sequence;

import java.util.List;

public abstract class NeighbourhoodBuilder {

    public abstract List<Sequence> build(Sequence positive, int maxSize);

}
