package rulesTable;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class TableCell {

    private AtomicBoolean evaluated = new AtomicBoolean(false);
    private AtomicBoolean proceeded = new AtomicBoolean(false);

    private List<CellRule> cellRules = Collections.synchronizedList(new ArrayList<>());

    private volatile int xCor;
    private volatile int yCor;

    private boolean isNull;

    public TableCell(boolean isNull) {
        this.isNull = isNull;
    }

    public boolean isEvaluated(){
        return evaluated.get();
    }

    public boolean isProceeded(){
        return proceeded.get();
    }
}
