package util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.distance.DistanceOp;

public class Euclidean {
	  public static double calculateDistance(double x1, double y1, double x2, double y2) {
	    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	  }

	  public static double calculateDistance(double[][] locations, int point1, int point2) {
	    return Math.sqrt((locations[point1][0] - locations[point2][0]) * (locations[point1][0] - locations[point2][0]) 
		    + (locations[point1][1] - locations[point2][1]) * (locations[point1][1] - locations[point2][1]));
	  }

	  public static double calculateDistance(Geometry point1, Geometry point2) {
	    DistanceOp distance = new DistanceOp(point1, point2);
	    Coordinate[] coordinates = distance.nearestPoints(point1, point2);
	    double earthRadius = 6371; // miles (or 6371.0 kilometers)
	    double latitudeDifference = Math.toRadians(coordinates[1].x - coordinates[0].x);
	    double longitudeDifference = Math.toRadians(coordinates[1].y - coordinates[0].y);
	    double sineLatitudeDifference = Math.sin(latitudeDifference / 2);
	    double sineLongitudeDifference = Math.sin(longitudeDifference / 2);
	    double a = Math.pow(sineLatitudeDifference, 2) + Math.pow(sineLongitudeDifference, 2)
		    * Math.cos(Math.toRadians(coordinates[0].x)) * Math.cos(Math.toRadians(coordinates[1].x));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = earthRadius * c;
	    return distance;
	  }

	  public static double calculateDistance(double[] point1, double[] point2) {
	    return Math.sqrt((point1[0] - point2[0]) * (point1[0] - point2[0]) + (point1[1] - point2[1]) * (point1[1] - point2[1]));
	  }

	  public static double calculateMinimumDistance(MBR rectangle1, MBR rectangle2) {
	    double minX1 = rectangle1.getMinX(), minY1 = rectangle1.getMinY(), maxX1 = rectangle1.getMaxX(), maxY1 = rectangle1.getMaxY(),
		       minX2 = rectangle2.getMinX(), minY2 = rectangle2.getMinY(), maxX2 = rectangle2.getMaxX(), maxY2 = rectangle2.getMaxY();

	    if (maxX1 < minX2) {
	      if (maxY1 < minY2) { // case 1
		return calculateDistance(maxX1, maxY1, minX2, minY2);
	      } else if (minY1 > maxY2) { // case 3
		return calculateDistance(maxX1, minY1, minX2, maxY2);
	      } else {
		return minX2 - maxX1; // case 2
	      }
	    } else if (minX1 > maxX2) {
	      if (maxY1 < minY2) { // case 4
		return calculateDistance(minX1, maxY1, maxX2, minY2);
	      } else if (minY1 > maxY2) { // case 6
		return calculateDistance(minX1, minY1, maxX2, maxY2);
	      } else {
		return minX1 - maxX2; // case 5
	      }
	    } else {
	      if (maxY1 < minY2) {
		return minY2 - maxY1; // case 7
	      } else if (minY1 > maxY2) {
		return minY1 - maxY2; // case 8
	      } else {
		return 0;
	      }
	    }
	  }

		
public static double calculateMaximumDistance(Rectangle box1, Rectangle box2) {

    long startTime = System.nanoTime();
    double minX1 = box1.getMinX(), minY1 = box1.getMinY(), maxX1 = box1.getMaxX(), maxY1 = box1.getMaxY(),
           minX2 = box2.getMinX(), minY2 = box2.getMinY(), maxX2 = box2.getMaxX(), maxY2 = box2.getMaxY();

    if (maxX1 < minX2) {
        if (maxY1 < minY2) { // Case 1
            return distance(minX1, minY1, maxX2, maxY2);
        } else if (minY1 > maxY2) { // Case 3
            return distance(minX1, maxY1, maxX2, minY2);
        } else {
            long intermediateTime = System.nanoTime();
            double[][] pointSetA = {{minX1, minY1}, {minX1, maxY1}};
            double[][] pointSetB = {{maxX2, minY2}, {maxX2, maxY2}};
            long loopStartTime = System.nanoTime();

            double maxDistance = -1;
            for (int i = 0; i < pointSetA.length; i++) {
                for (int j = 0; j < pointSetB.length; j++) {
                    double currentDistance = distance(pointSetA[i][0], pointSetA[i][1], pointSetB[j][0], pointSetB[j][1]);
                    if (currentDistance > maxDistance) {
                        maxDistance = currentDistance;
                    }
                }
            }

            return maxDistance; // Case 2
        }
    } else if (minX1 > maxX2) {
        if (maxY1 < minY2) { // Case 4
            return distance(maxX1, minY1, minX2, maxY2);
        } else if (minY1 > maxY2) { // Case 6 (fixed bug)
            return distance(maxX1, maxY1, minX2, minY2);
        } else {
            long intermediateTime = System.nanoTime();
            double[][] pointSetA = {{maxX1, minY1}, {maxX1, maxY1}};
            double[][] pointSetB = {{minX2, minY2}, {minX2, maxY2}};
            long loopStartTime = System.nanoTime();

            double maxDistance = -1;
            for (int i = 0; i < pointSetA.length; i++) {
                for (int j = 0; j < pointSetB.length; j++) {
                    double currentDistance = distance(distance(pointSetA[i][0], pointSetA[i][1], pointSetB[j][0], pointSetB[j][1]));
                    if (currentDistance > maxDistance) {
                        maxDistance = currentDistance;
                    }
                }
            }

            return maxDistance; // Case 5
        }
    } else {
        if (maxY1 < minY2) {
            long intermediateTime = System.nanoTime();
            double[][] pointSetA = {{minX1, minY1}, {maxX1, minY1}};
            double[][] pointSetB = {{minX2, maxY2}, {maxX2, maxY2}};
            long loopStartTime = System.nanoTime();

            double maxDistance = -1;
            for (int i = 0; i < pointSetA.length; i++) {
                for (int j = 0; j < pointSetB.length; j++) {
                    double currentDistance = distance(pointSetA[i][0], pointSetA[i][1], pointSetB[j][0], pointSetB[j][1]);
                    if (currentDistance > maxDistance) {
                        maxDistance = currentDistance;
                    }
                }
            }

            return maxDistance; // Case 7
        } else if (minY1 > maxY2) {
            long intermediateTime = System.nanoTime();
            double[][] pointSetA = {{minX1, maxY1}, {maxX1, maxY1}};
            double[][] pointSetB = {{minX2, minY2}, {maxX2, minY2}};
            long loopStartTime = System.nanoTime();


public static double calculateMinimumDistance(double pointX, double pointY, MBR rectangle) {
    if (pointX >= rectangle.getMinX() && pointX <= rectangle.getMaxX() && pointY >= rectangle.getMinY() && pointY <= rectangle.getMaxY()) {
        return 0;
    }

    // Calculate distances for each case
    double distance = 0;
    if (pointX > rectangle.getMaxX()) {
        if (pointY > rectangle.getMaxY()) { // Case 1
            distance = Math.sqrt((pointX - rectangle.getMaxX()) * (pointX - rectangle.getMaxX()) + (pointY - rectangle.getMaxY()) * (pointY - rectangle.getMaxY()));
        } else if (pointY < rectangle.getMinY()) { // Case 3
            distance = Math.sqrt((pointX - rectangle.getMaxX()) * (pointX - rectangle.getMaxX()) + (pointY - rectangle.getMinY()) * (pointY - rectangle.getMinY()));
        } else { // Case 2
            distance = Math.abs(pointX - rectangle.getMaxX());
        }
    } else if (pointX < rectangle.getMinX()) {
        if (pointY > rectangle.getMaxY()) { // Case 4
            distance = Math.sqrt((pointX - rectangle.getMinX()) * (pointX - rectangle.getMinX()) + (pointY - rectangle.getMaxY()) * (pointY - rectangle.getMaxY()));
        } else if (pointY < rectangle.getMinY()) { // Case 6
            distance = Math.sqrt((pointX - rectangle.getMinX()) * (pointX - rectangle.getMinX()) + (pointY - rectangle.getMinY()) * (pointY - rectangle.getMinY()));
        } else { // Case 5
            distance = Math.abs(pointX - rectangle.getMinX());
        }
    } else {
        if (pointY > rectangle.getMaxY()) { // Case 7
            distance = Math.abs(pointY - rectangle.getMaxY());
        } else { // Case 8
            distance = Math.abs(pointY - rectangle.getMinY());
        }
    }

    return distance;
}

public static double calculateMaximumDistance(double pointX, double pointY, MBR rectangle) {
    double maxDistance = 0;
    double currentDistance;

    // Calculate distances for each corner and store the maximum
    currentDistance = Math.sqrt((pointX - rectangle.getMinX()) * (pointX - rectangle.getMinX()) + (pointY - rectangle.getMinY()) * (pointY - rectangle.getMinY()));
    maxDistance = Math.max(maxDistance, currentDistance);

    currentDistance = Math.sqrt((pointX - rectangle.getMinX()) * (pointX - rectangle.getMinX()) + (pointY - rectangle.getMaxY()) * (pointY - rectangle.getMaxY()));
    maxDistance = Math.max(maxDistance, currentDistance);

    currentDistance = Math.sqrt((pointX - rectangle.getMaxX()) * (pointX - rectangle.getMaxX()) + (pointY - rectangle.getMinY()) * (pointY - rectangle.getMinY()));
    maxDistance = Math.max(maxDistance, currentDistance);

    currentDistance = Math.sqrt((pointX - rectangle.getMaxX()) * (pointX - rectangle.getMaxX()) + (pointY - rectangle.getMaxY()) * (pointY - rectangle.getMaxY()));
    maxDistance = Math.max(maxDistance, currentDistance);

    return maxDistance;
}

	
}
