package grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Symbol {

    private String value;

    private int index;

    private SymbolType symbolType;

    @Override
    public String toString() {
        if (symbolType == SymbolType.TERMINAL)
            if(value.contains("'"))
                return "\"" + value + "\"";
            else
                return "'" + value + "'";
        return value;
    }

    public boolean isStart() {
        return symbolType == SymbolType.START;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(value, symbol.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
