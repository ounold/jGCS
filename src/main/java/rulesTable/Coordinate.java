package rulesTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {

    private int x;

    private int y;

    @Override
    public String toString(){
        return String.format("(%d, %d)", x, y);
    }
}
