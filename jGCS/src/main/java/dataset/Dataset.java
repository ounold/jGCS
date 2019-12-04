package dataset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

    private List<Sequence> sequences = new ArrayList<>();

    public int countPositives(){
        return (int) sequences.stream().filter(Sequence::isPositive).count();
    }

    public int countNegatives(){
        return sequences.size() - countPositives();
    }

}
