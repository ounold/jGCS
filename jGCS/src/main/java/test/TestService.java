/*
package test;

import application.InputSet;
import cyk.CykResult;
import cyk.CykService;
import dataset.Dataset;
import dataset.Sequence;
import grammar.Grammar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static sun.misc.Version.println;

public class TestService {

    private CykService cykService = CykService.getInstance();
    private static TestService instance;

    private TestService() {
    }

    public static TestService getInstance() {
        if (instance == null)
            instance = new TestService();
        return instance;
    }

    public void run(Dataset trainDataSet, Grammar grammar) {
        int TP = 0, TN = 0, FN = 0, FP = 0;

        double P = 0.0;
        List<Sequence> sequences = trainDataSet.getSequences();

        for(Sequence sequence : sequences) {
            //parsing

            CykResult cykResult = cykService.runCyk(sequence, grammar);

            if (sequence.isPositive() && cykResult.getSentenceProbability() > P) {
                TP++;
            }
            if (sequence.isPositive() && cykResult.getSentenceProbability() < P) {
                FN++;
            }
            if (!sequence.isPositive() && cykResult.getSentenceProbability() > P) {
                FP++;
            }
            if (!sequence.isPositive() && cykResult.getSentenceProbability() < P) {
                TN++;
            }
        }

        BigDecimal sensitivity = BigDecimal.valueOf(TP).divide(BigDecimal.valueOf(TP + FN), 4, RoundingMode.HALF_UP);
        BigDecimal specifity = BigDecimal.valueOf(TN).divide(BigDecimal.valueOf((TN + FP)), 4, RoundingMode.HALF_UP);
        BigDecimal precision = BigDecimal.valueOf(TP).divide(BigDecimal.valueOf((TP + FP)), 4, RoundingMode.HALF_UP);
        BigDecimal F1 = BigDecimal.valueOf(2).multiply((sensitivity.multiply(precision)).divide(sensitivity.add(precision), 4, RoundingMode.HALF_UP));

        System.out.println("Sensivity: " + sensitivity);
        System.out.println("Specifity: " + specifity);
        System.out.println("precision: " + precision);
        System.out.println("F1: " + F1);
        System.out.println("TP: " + TP);
        System.out.println("TN: " + TN);
        System.out.println("FP: " + FP);
        System.out.println("FN: " + FN);

    }

}
*/
