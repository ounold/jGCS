package common.grammar;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleTestGrammar implements TestGrammar {

    public final Symbol a = new Symbol("a", 0, SymbolType.TERMINAL);
    public final Symbol b = new Symbol("b", 0, SymbolType.TERMINAL);
    public final Symbol S = new Symbol("$", 0, SymbolType.START);
    public final Symbol A = new Symbol("A", 1, SymbolType.NON_TERMINAL);
    public final Symbol B = new Symbol("B", 2, SymbolType.NON_TERMINAL);
    public final Symbol C = new Symbol("C", 3, SymbolType.NON_TERMINAL);
    public final Symbol D = new Symbol("D", 4, SymbolType.NON_TERMINAL);
    public final Symbol E = new Symbol("E", 5, SymbolType.NON_TERMINAL);
    public final Symbol F = new Symbol("F", 6, SymbolType.NON_TERMINAL);
    public final Symbol G = new Symbol("G", 7, SymbolType.NON_TERMINAL);
    public final Symbol H = new Symbol("H", 8, SymbolType.NON_TERMINAL);

    public final Rule A_a = new Rule(A, a, null, 1);
    public final Rule B_b = new Rule(B, b, null, 1);
    public final Rule C_AB = new Rule(C, A, B, 1);
    public final Rule D_CA = new Rule(D, C, A, 1);
    public final Rule E_BC = new Rule(E, B, C, 1);
    public final Rule F_BD = new Rule(F, B, D, 1);
    public final Rule S_AF = new Rule(S, A, F, 1);

    @Override
    public Grammar getGrammar() {
        Grammar result = new Grammar();
        result.setTerminalSymbols(Arrays.asList(a, b));
        result.setNonTerminalSymbols(Arrays.asList(S, A, B, C, D, E, F, G, H));
        List<Rule> terminalRules = new ArrayList<>();
        terminalRules.add(A_a);
        terminalRules.add(B_b);
        result.setTerminalRules(terminalRules);
        List<Rule> nonTerminalRules = new ArrayList<>();
        nonTerminalRules.add(C_AB);
        nonTerminalRules.add(D_CA);
        nonTerminalRules.add(E_BC);
        nonTerminalRules.add(F_BD);
        nonTerminalRules.add(S_AF);
        result.setNonTerminalRules(nonTerminalRules);
        return result;
    }

}
