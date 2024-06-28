package util;

import java.util.ArrayList;
import java.util.List;

public class GlobalRef {
	 private Pattern originalPattern;
    private BoundedPattern boundedPattern;

    public RefinedGlobalRef(Pattern originalPattern, BoundedPattern boundedPattern) {
        this.originalPattern = originalPattern;
        this.boundedPattern = boundedPattern;
    }

    public Pattern refinePattern() {  // More descriptive method name
        int dimension = originalPattern.getDimension();
        double[][] lowerBounds = originalPattern.getLowerBounds();
        double[][] upperBounds = originalPattern.getUpperBounds();
        double[][] lower = boundedPattern.getLower();
        double[][] upper = boundedPattern.getUpper();

        boolean edgeRemoved = false;
        for (int i = 0; i < dimension; i++) {
            for (int j = i + 1; j < dimension; j++) {
                if (upperBounds[i][j] > 0) {
                    boolean overlap = isOverlap(lowerBounds[i][j], upperBounds[i][j], lower[i][j], upper[j][i]);
                    if (overlap) {
                        if (lower[i][j] > lowerBounds[i][j] && upper[j][i] < upperBounds[i][j]) {
                            // Delete edge
                            lowerBounds[i][j] = upperBounds[i][j] = lowerBounds[j][i] = upperBounds[j][i] = -1.0;
                            edgeRemoved = true;
                        } else {
                            upperBounds[i][j] = upperBounds[j][i] = Math.min(upperBounds[i][j], upper[j][i]);
                        }
                    } else {
                        // Wrong pattern detected (exclusion-ship)
                        return null;
                    }
                }
            }
        }

        if (edgeRemoved) {
            int[][] neighborGraph = new int[dimension][];
            for (int i = 0; i < dimension; i++) {
                List<Integer> neighbors = new ArrayList<>();
                for (int j = 0; j < dimension; j++) {
                    if (i != j && upperBounds[i][j] > 0) {
                        neighbors.add(j);
                    }
                }

                neighborGraph[i] = new int[neighbors.size()];
                for (int j = 0; j < neighbors.size(); j++) {
                    neighborGraph[i][j] = neighbors.get(j);
                }
            }
            originalPattern.setNeighborGraph(neighborGraph);
        }

        return originalPattern;
    }

    private boolean isOverlap(double lowerBound1, double upperBound1, double lowerBound2, double upperBound2) {
        return !(upperBound1 < lowerBound2 || upperBound2 < lowerBound1);
    }
}
