package distance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DistanceService {

    private static DistanceService instance;

    private DistanceService() {
    }

    public static DistanceService getInstance() {
        if (instance == null)
            instance = new DistanceService();
        return instance;
    }

    public int levenshtein(List<?> a, List<?> b) {
        int n = a.size();
        int m = b.size();
        int[][] distances = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            distances[i][0] = i;
        }
        for (int i = 0; i < m + 1; i++) {
            distances[0][i] = i;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                if (a.get(i - 1).equals(b.get(j - 1))) {
                    distances[i][j] = distances[i - 1][j - 1];
                } else {
                    distances[i][j] = Math.min(
                            Math.min(distances[i - 1][j] + 1,
                                    distances[i][j - 1] + 1),
                            distances[i - 1][j - 1] + 1
                    );
                }
            }
        }
        return distances[n][m];
    }

    public int damerau(List<?> a, List<?> b) {
        int n = a.size();
        int m = b.size();
        int[][] distances = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            distances[i][0] = i;
        }
        for (int i = 0; i < m + 1; i++) {
            distances[0][i] = i;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                if (a.get(i - 1).equals(b.get(j - 1))) {
                    distances[i][j] = distances[i - 1][j - 1];
                } else {
                    distances[i][j] = Math.min(
                            Math.min(distances[i - 1][j] + 1,
                                    distances[i][j - 1] + 1),
                            distances[i - 1][j - 1] + 1
                    );
                    if (i > 1 && j > 1 && a.get(i - 1).equals(b.get(j - 2)) && a.get(i - 2).equals(b.get(j - 1))) {
                        distances[i][j] = Math.min(distances[i][j], distances[i - 2][j - 2] + 1);
                    }
                }
            }
        }
        return distances[n][m];
    }

    public double jaccard(Set<?> a, Set<?> b) {
        final Set<?> union = Stream.concat(a.stream(), b.stream()).collect(Collectors.toSet());
        final Set<?> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        return 1. - (double) intersection.size() / (double) union.size();
    }

}
