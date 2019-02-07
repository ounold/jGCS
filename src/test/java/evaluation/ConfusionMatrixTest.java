package evaluation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfusionMatrixTest {

    private static final double DELTA = .0000001;

    @Test
    public void testSensitivity() {
        assertEquals(new ConfusionMatrix(7, 12, 3, 8).getSensitivity(), .4666667, DELTA);
    }

    @Test
    public void testPrecision() {
        assertEquals(new ConfusionMatrix(7, 12, 3, 8).getPrecision(), .36842105, DELTA);
    }

    @Test
    public void testSpecificity() {
        assertEquals(new ConfusionMatrix(7, 12, 3, 8).getSpecificity(), .2, DELTA);
    }

    @Test
    public void testF1() {
        assertEquals(new ConfusionMatrix(7, 12, 3, 8).getF1(), .4117647, DELTA);
    }

    @Test
    public void testSensitivityIsZero() {
        assertEquals(new ConfusionMatrix(0, 12, 3, 0).getSensitivity(), 0d, DELTA);
    }

    @Test
    public void testPrecisionIsZero() {
        assertEquals(new ConfusionMatrix(0, 0, 3, 8).getPrecision(), 0d, DELTA);
    }

    @Test
    public void testSpecificityIsZero() {
        assertEquals(new ConfusionMatrix(7, 0, 0, 8).getSpecificity(), 0d, DELTA);
    }

    @Test
    public void testF1IsZero() {
        assertEquals(new ConfusionMatrix(0, 0, 3, 0).getF1(), 0d, DELTA);
    }

    @Test
    public void testUpdateTruePositives() {
        testUpdate(true, true, 1, 0, 0, 0, 1);
    }

    @Test
    public void testUpdateFalsePositives() {
        testUpdate(false, true, 0, 1, 0, 0, 1);
    }

    @Test
    public void testUpdateTrueNegatives() {
        testUpdate(false, false, 0, 0, 1, 0, 1);
    }

    @Test
    public void testUpdateFalseNegatives() {
        testUpdate(true, false, 0, 0, 0, 1, 1);
    }

    private void testUpdate(boolean expected, boolean actual, int tp, int fp, int tn, int fn, int all) {
        // given
        ConfusionMatrix matrix = new ConfusionMatrix();

        // when
        matrix.update(expected, actual);

        // then
        assertEquals(matrix.getTruePositives(), tp);
        assertEquals(matrix.getFalsePositives(), fp);
        assertEquals(matrix.getTrueNegatives(), tn);
        assertEquals(matrix.getFalseNegatives(), fn);
        assertEquals(matrix.countAll(), all);
    }

}