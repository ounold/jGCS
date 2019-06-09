package evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfusionMatrix {

    private int truePositives;

    private int falsePositives;

    private int trueNegatives;

    private int falseNegatives;

    public int countAll() {
        return truePositives + trueNegatives + falsePositives + falseNegatives;
    }

    public double getSensitivity() {
        if(truePositives == 0)
            return 0;
        return ((double) truePositives) / (truePositives + falseNegatives);
    }

    public double getPrecision() {
        if(truePositives == 0)
            return 0;
        return ((double) truePositives) / (truePositives + falsePositives);
    }

    public double getSpecificity() {
        if(trueNegatives == 0)
            return 0;
        return ((double) trueNegatives) / (trueNegatives + falsePositives);
    }

    public double getF1() {
        if(getSensitivity() + getPrecision() == 0)
            return 0;
        return 2 * (getSensitivity() * getPrecision()) / (getSensitivity() + getPrecision());
    }

    public void update(boolean expected, boolean actual) {
        if (expected && actual) {
            truePositives++;
        }
        if (expected && !actual) {
            falseNegatives++;
        }
        if (!expected && actual) {
            falsePositives++;
        }
        if (!expected && !actual) {
            trueNegatives++;
        }
    }

}
