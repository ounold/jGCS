package grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    private static final Logger LOGGER = LogManager.getLogger(Rule.class);

    private Symbol left;

    private Symbol right1;

    private Symbol right2;

    private double probability;

    private double count;

    private double countInNeighbourhoods;

    private double countInPositives;

    private double positiveCount;

    private double negativeCount;

    private double positiveSumInsideOutsideUsages;

    private double negativeSumInsideOutsideUsages;

    private int countInsideOutsideUsageProbability;

    private int countUsageInValidSentencesParsing;

    private int countUsageInNotValidSentencesParsing;

    private double profit;

    private double debt;

    private double fertility;

    private double fitness;

    @Override
    public Rule clone() {
        return new Rule(this.left, this.right1, this.right2, this.probability, this.count, this.countInNeighbourhoods,
                this.countInPositives, this.positiveCount, this.negativeCount, this.positiveSumInsideOutsideUsages,
                this.negativeSumInsideOutsideUsages, this.countInsideOutsideUsageProbability, this.countUsageInValidSentencesParsing,
                this.countUsageInNotValidSentencesParsing, this.profit, this.debt, this.fertility, this.fitness);
    }

    public Rule(Symbol left, Symbol right1, Symbol right2, double probability) {
        this.left = left;
        this.right1 = right1;
        this.right2 = right2;
        this.probability = probability;
    }

    public boolean isStart() {
        return left.isStart();
    }

    @Override
    public String toString() {
        return left.toString() + " -> " + right1.toString() + (right2 != null ? " " + right2.toString() : "") + ": " + probability +
        ", fitness.: " + fitness;
    }

    public String getDefinition() {
        return toString();
    }

    public void addInsideOutsideUsages(double value, boolean positive) {
        if (positive)
            positiveSumInsideOutsideUsages += value;
        else
            negativeSumInsideOutsideUsages += value;
        countInsideOutsideUsageProbability++;
    }

    public double getSumInsideOutsideUsages() {
        return positiveSumInsideOutsideUsages + negativeSumInsideOutsideUsages;
    }

    public void addCount(double count) {
        this.count += count;
    }

    public void addPositiveCount(double count) {
        this.positiveCount += count;
    }

    public void addNegativeCount(double count) {
        this.negativeCount += count;
    }

    public void addCountInNeighbourhood(double countInNeighbourhood) {
        this.countInNeighbourhoods += countInNeighbourhood;
    }

    public void addCountInPositives(double countInPositives) {
        this.countInPositives += countInPositives;
    }

    public void incrementUsageInValidSentencesParsing() {
        this.countUsageInValidSentencesParsing++;
    }

    public void incrementUsageInNotValidSentencesParsing() {
        this.countUsageInNotValidSentencesParsing++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(left, rule.left) &&
                Objects.equals(right1, rule.right1) &&
                Objects.equals(right2, rule.right2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right1, right2);
    }

    public String toFullString() {
        return "Rule{" +
                "definition=" + getDefinition() +
                ", probability=" + probability +
                ", count=" + count +
                ", countInNeighbourhoods=" + countInNeighbourhoods +
                ", countInPositives=" + countInPositives +
                ", positiveCount=" + positiveCount +
                ", negativeCount=" + negativeCount +
                ", positiveSumInsideOutsideUsages=" + positiveSumInsideOutsideUsages +
                ", negativeSumInsideOutsideUsages=" + negativeSumInsideOutsideUsages +
                ", countInsideOutsideUsageProbability=" + countInsideOutsideUsageProbability +
                '}';
    }
}
