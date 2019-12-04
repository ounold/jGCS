package distance;

import common.AbstractServiceTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceServiceTest extends AbstractServiceTest {

    private DistanceService distanceService = DistanceService.getInstance();

    @Test
    public void testLevenshteinForLetters(){
        List<String> a = Arrays.asList("kitten".split(""));
        List<String> b = Arrays.asList("sitting".split(""));

        int result = distanceService.levenshtein(a, b);

        assertEquals(3, result);
    }

    @Test
    public void testLevenshteinForWords(){
        List<String> a = Arrays.asList("I am your father".split(" "));
        List<String> b = Arrays.asList("I am not his mother".split(" "));

        int result = distanceService.levenshtein(a, b);

        assertEquals(3, result);
    }

    @Test
    public void testDamerauForLetters(){
        List<String> a = Arrays.asList("kitten".split(""));
        List<String> b = Arrays.asList("stiting".split(""));

        int result = distanceService.damerau(a, b);

        assertEquals(4, result);
    }

    @Test
    public void testDamerauForWords(){
        List<String> a = Arrays.asList("What are you doing here boy".split(" "));
        List<String> b = Arrays.asList("What you are doing here boy".split(" "));

        int result = distanceService.damerau(a, b);

        assertEquals(1, result);
    }

    @Test
    public void testJaccardForLetters(){
        List<String> a = Arrays.asList("kitten".split(""));
        List<String> b = Arrays.asList("sitting".split(""));

        double result = distanceService.jaccard(new HashSet<>(a), new HashSet<>(b));

        assertEquals(1 - .4285714, result, .0000001);
    }

    @Test
    public void testJaccardForWords(){
        List<String> a = Arrays.asList("I am your father".split(" "));
        List<String> b = Arrays.asList("I am not his mother".split(" "));

        double result = distanceService.jaccard(new HashSet<>(a), new HashSet<>(b));

        assertEquals(1 - .2857143, result, .0000001);
    }

}