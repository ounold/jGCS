package evaluation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Evaluation {

    private final String grammar;

    private final ConfusionMatrix evaluation;

}
