package probabilityArray;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProbabilityCell {

    public static final ProbabilityCell EMPTY_CELL = new ProbabilityCell(-1.0);

    private double probability;

    public synchronized void add(double value){
        this.probability += value;
    }

}
