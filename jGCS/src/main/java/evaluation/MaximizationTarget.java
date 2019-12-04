package evaluation;

import lombok.Getter;

import java.util.function.Function;

public enum MaximizationTarget {
    SPECIFICITY(ConfusionMatrix::getSpecificity),
    SENSITIVITY(ConfusionMatrix::getSensitivity),
    PRECISION(ConfusionMatrix::getPrecision),
    F1(ConfusionMatrix::getF1);

    @Getter
    final Function<ConfusionMatrix, Double> extractor;

    MaximizationTarget(Function<ConfusionMatrix, Double> extractor) {
        this.extractor = extractor;
    }
}
