package util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import util.quadtree.ANode;

import java.util.*;
import java.util.List;

public class LeafJoin {
	public class PatternProcessor { 

    private int maxIndex = -1;
    private String[][] labels = null;
    private boolean[][] visited = null;
    private double[][] lowerBounds, upperBounds = null;
    private int[][] topology = null;

    public PatternProcessor(Pattern pattern) {
        this.maxIndex = pattern.getMaxIndex();

        this.labels = pattern.getLabels();
        this.lowerBounds = pattern.getLowerBounds();
        this.upperBounds = pattern.getUpperBounds();
        this.topology = pattern.getTopology();
    }

    public Map<String, Map<PolygonItem, List<PolygonItem>>> processLeaves(List<JoinPair> orderedList, List<Map<AbstractNode, List<AbstractNode>>> candidateList, List<JoinPair> initialOrderList) {
        // Step 1: Map join pairs to integer indices
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < initialOrderList.size(); i++) {
            JoinPair pair = initialOrderList.get(i);
            String key = pair.getFirstId() + ":" + pair.getSecondId();
            indexMap.put(key, i);
        }

        // Step 2: Join each edge
        Map<String, Map<PolygonItem, List<PolygonItem>>> linkMap = new HashMap<>();
        for (JoinPair pair : orderedList) {
            if (pair.shouldProcess() || visited[pair.getFirstId()][pair.getSecondId()]) {
                String edge = pair.getFirstId() + ":" + pair.getSecondId();
                int index = indexMap.get(edge);
                // Candidate map holds nodes with mapped keywords
                Map<AbstractNode, List<AbstractNode>> candidateMap = candidateList.get(index);
                // Join operation (potentially the core logic)
                Map<PolygonItem, List<PolygonItem>> map = join(candidateMap, pair);
                linkMap.put(edge, map);
            }
        }

        return linkMap;
    }

public Map<DataPoint, List<DataPoint>> performJoin(Map<TreeNode, List<TreeNode>> candidateMap, JoinCondition condition) {
    int featureId1 = condition.getFirstFeatureId();
    int featureId2 = condition.getSecondFeatureId();
    Map<DataPoint, List<DataPoint>> nextCandidateMap = new HashMap<>(); // Stores results

    for (TreeNode node : candidateMap.keySet()) {
        LeafNode leaf = (LeafNode) node; // Assuming node is a LeafNode

        // Find points matching keywords in feature set 1
        List<DataPoint> points1 = new ArrayList<>();
        for (String keyword : labels[featureId1]) {
            List<DataPoint> temp = leaf.getInvertedIndex().get(keyword);
            if (points1.isEmpty()) {
                points1.addAll(temp);
            } else {
                points1.retainAll(temp); // Intersection
            }
        }

        // Process points from feature set 1
        for (DataPoint point1 : points1) {
            List<DataPoint> candidateList = new ArrayList<>();
            boolean pruned = false;

            for (TreeNode candidateNode : candidateMap.get(node)) {
                LeafNode candidateLeaf = (LeafNode) candidateNode; // Assuming candidateNode is a LeafNode

                // Find points matching keywords in feature set 2
                List<DataPoint> points2 = new ArrayList<>();
                for (String keyword : labels[featureId2]) {
                    List<DataPoint> temp = candidateLeaf.getInvertedIndex().get(keyword);
                    if (points2.isEmpty()) {
                        points2.addAll(temp);
                    } else {
                        points2.retainAll(temp); // Intersection
                    }
                }

                // Process points from feature set 2 based on condition
                for (DataPoint point2 : points2) {
                    double distance;
                    switch (topology[featureId1][featureId2]) {
                        case 4: // Disjoint with small tolerance
                            distance = EuclideanDistance.calculate(point1.getGeometry(), point2.getGeometry());
                            if (distance > 0.001 && distance < 0.5) {
                                candidateList.add(point2);
                            }
                            break;
                        case 1: // Overlap but not equal
                            if (checkOverlap(point1.getGeometry(), point2.getGeometry()) && !checkEqual(point1.getGeometry(), point2.getGeometry())) {
                                candidateList.add(point2);
                            }
                            break;
                        case 2: // Equal
                            if (checkEqual(point1.getGeometry(), point2.getGeometry())) {
                                candidateList.add(point2);
                            }
                            break;
                        case 3: // Disjoint with very small tolerance
                            distance = EuclideanDistance.calculate(point1.getGeometry(), point2.getGeometry());
                            if (distance > 0 && distance <= 0.00001) {
                                candidateList.add(point2);
                            }
                            break;
                    }
                    if (pruned) {
                        break; // Prune if all candidates in this node are filtered
                    }
                }
            }

            if (!pruned && !candidateList.isEmpty()) {
                nextCandidateMap.put(point1, candidateList);
            }
        }
    }

    return nextCandidateMap;
}

	public boolean isEquivalent(Geometry geom1, Geometry geom2) {
	    return geom1.equalsTopo(geom2);
	}

	public boolean isTouching(Geometry geom1, Geometry geom2) {
	    return geom1.touches(geom2);
	}

	public boolean isOverlapping(Geometry geom1, Geometry geom2) {
	    return geom1.intersects(geom2);
	}

	public boolean isWithinDistance(Geometry geom1, Geometry geom2, double distance) {
	    return geom1.isWithinDistance(geom2, distance);
	}
}
