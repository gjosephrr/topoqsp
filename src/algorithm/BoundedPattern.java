package util;

public class BoundedPattern {

    private DistancePattern distancePattern; 
    private double[][] minBounds; 
    private double[][] maxBounds; 

    public BoundedDistance(DistancePattern distancePattern) {
        this.distancePattern = distancePattern;
    }

    public void calculateBounds() { 
        int numDimensions = distancePattern.getNumDimensions(); 
        double[][] lowerBounds = distancePattern.getLowerBounds(); 
        double[][] upperBounds = distancePattern.getUpperBounds(); 
        minBounds = new double[numDimensions][numDimensions];
        maxBounds = new double[numDimensions][numDimensions];

        for (int i = 0; i < numDimensions; i++) {
            for (int j = 0; j < numDimensions; j++) {
                minBounds[i][j] = 0;
                if (j != i) {
                    maxBounds[i][j] = Double.POSITIVE_INFINITY; 
                } else {
                    maxBounds[i][j] = 0;
                }
            }
        }

        // Step 1: Build the undirected graph
        for (int i = 0; i < numDimensions; i++) {
            for (int j = 0; j < numDimensions; j++) {
                if (lowerBounds[i][j] >= 0.0 && lowerBounds[j][i] >= 0.0) {
                    minBounds[i][j] = minBounds[j][i] = Math.max(lowerBounds[i][j], lowerBounds[j][i]);
                    maxBounds[i][j] = maxBounds[j][i] = Math.min(upperBounds[i][j], upperBounds[j][i]);
                } else {
                    if (lowerBounds[i][j] >= 0) {
                        minBounds[i][j] = minBounds[j][i] = lowerBounds[i][j];
                    }
                    if (upperBounds[i][j] >= 0) {
                        maxBounds[i][j] = maxBounds[j][i] = upperBounds[i][j];
                    }
                }
            }
        }

        // Step 2: Compute the upper bound
        for (int k = 0; k < numDimensions; k++) {
            for (int i = 0; i < numDimensions; i++) {
                for (int j = 0; j < numDimensions; j++) {
                    double distance = maxBounds[i][k] + maxBounds[k][j];
                    if (distance < maxBounds[i][j]) {
                        maxBounds[i][j] = distance;
                    }
                }
            }
        }

        // Step 3: Compute the lower bound
        for (int k = 0; k < numDimensions; k++) {
            for (int i = 0; i < numDimensions; i++) {
                for (int j = 0; j < numDimensions; j++) {
                    if (i != j) {
                        double distance = 0;
                        if (maxBounds[i][k] < minBounds[k][j]) { // Updated condition
                            distance = minBounds[k][j] - maxBounds[i][k];
                        } else if (lowerBounds[i][k] > upperBounds[k][j]) {
                            distance = lowerBounds[i][k] - upperBounds[k][j];
                        } else {
                            distance = 0;
                        }
                        if (distance > minBounds[i][j]) {
                            minBounds[i][j] = distance;
                        }
                    }
                }
            }
        }
    }

    public double[][] getMinBounds() {
        return minBounds;
    }

    public double[][] getMaxBounds() {
        return maxBounds;
    }
}
