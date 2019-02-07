package common.testCase;

import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import probabilityArray.ProbabilityArray;
import rulesTable.CellRule;
import rulesTable.RulesTable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GrammarTestCase {

    public abstract Sequence getSequence();

    public abstract Grammar getGrammar();

    public abstract void assertResult(CykResult cykResult);

    protected List<Rule> extractRules(RulesTable rulesTable, int i, int j) {
        return rulesTable.getCellRules(i, j).stream().map(CellRule::getRule).collect(Collectors.toList());
    }

    protected double extractProbability(ProbabilityArray probabilityArray, int i, int j, Symbol symbol) {
        return probabilityArray.get(i, j, symbol).getProbability();
    }

    protected double extractProbability(ProbabilityArray probabilityArray, int i, int j, int symbol) {
        return probabilityArray.get(i, j, symbol).getProbability();
    }

}
