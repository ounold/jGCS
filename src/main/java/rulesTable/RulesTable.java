package rulesTable;

import grammar.Rule;
import grammar.Symbol;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class RulesTable {

    private TableCell[][] table;

    public RulesTable(int size) {
        table = new TableCell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                table[i][j] = new TableCell(false);
                table[i][j].setXCor(i);
                table[i][j].setYCor(j);
            }
        }

    }

    public TableCell get(int i, int j) {
        return table[i][j];
    }

    public Optional<TableCell> getNullable(int i, int j) {
        if(i < 0 || j < 0 || i >= table.length || j >= table.length)
            return Optional.empty();
        return Optional.ofNullable(get(i, j));
    }

    public List<CellRule> getCellRules(int i, int j) {
        return table[i][j].getCellRules();
    }

    public CellRule getCellRule(int i, int j, Rule rule) {
        return table[i][j].getCellRules().stream().filter(cr -> cr.getRule().equals(rule)).findAny().orElse(null);
    }

    public int getLength(){
        return table.length;
    }

    public TableCell getStartTableCell(){
        return table[table.length - 1][0];
    }

    public List<CellRule> getStartCellRules() {
        List<CellRule> startCellRules = new ArrayList<>();
        for (CellRule cellRule : getStartTableCell().getCellRules()) {
            if (cellRule.isStart()) {
                startCellRules.add(cellRule);
            }
        }
        return startCellRules;
    }

    @Deprecated
    /*
        Deprecated due to performance issues. Use getStartCellRules() instead and iterate through it with standard for loop.
     */
    public Stream<CellRule> getStartCellRulesStream(){
        return getStartTableCell().getCellRules().stream().filter(CellRule::isStart);
    }

    public List<CellRule> findMatchingRules(int i, int j, Symbol symbol){
        return getCellRules(i, j).stream()
                .filter(childRule -> childRule.getRule().getLeft().equals(symbol))
                .collect(Collectors.toList());
    }

    public List<CellRule> findMatchingRules(Coordinate coordinate, Symbol symbol){
        if(coordinate == null)
            return Collections.emptyList();
        return findMatchingRules(coordinate.getX(), coordinate.getY(), symbol);
    }

}
