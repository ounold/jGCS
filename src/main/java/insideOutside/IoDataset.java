package insideOutside;

import lombok.Data;

import java.util.List;

@Data
public class IoDataset {

    private List<IoSequence> sequences;

    public IoDataset(List<IoSequence> sequences) {
        this.sequences = sequences;
    }

}
