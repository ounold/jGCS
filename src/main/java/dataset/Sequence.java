package dataset;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sequence {

    public static final String WORD_SEPARATOR = " ";

    @Getter(AccessLevel.NONE)
    private String value;

    private boolean positive;

    public Set<String> symbolSet() {
        return Stream.of(value.split(WORD_SEPARATOR)).collect(Collectors.toSet());
    }

    public List<String> symbolList() {
        return Arrays.asList(value.split(WORD_SEPARATOR));
    }

    public int length() {
        return symbolList().size();
    }

}
