package cyk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import probabilityArray.ProbabilityArray;
import rulesTable.RulesTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CykResult {

    RulesTable rulesTable;

    ProbabilityArray probabilityArray;

    double sentenceProbability;

    boolean parsed;

}
