package insideOutside;

import dataset.Sequence;
import lombok.Data;

@Data
public class IoSequence {

    private final Sequence sequence;

    private final int timesAsNeighbour;

    public IoSequence(Sequence sequence, int timesAsNeighbour) {
        this.sequence = sequence;
        this.timesAsNeighbour = timesAsNeighbour;
    }

    public boolean isPositive() {
        return sequence.isPositive();
    }

    public int length() {
        return sequence.length();
    }
}
