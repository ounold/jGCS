package common.mock;

import java.util.List;
import java.util.Random;

public class RandomMock extends Random {

    private List<Integer> generatedInts;
    private int generatedIntsCounter;

    public RandomMock(List<Integer> generatedInts) {
        this.generatedInts = generatedInts;
        generatedIntsCounter = 0;
    }

    @Override
    public int nextInt(int bound) {
        int result = generatedInts.get(generatedIntsCounter);
        generatedIntsCounter++;

        return result;
    }

}
