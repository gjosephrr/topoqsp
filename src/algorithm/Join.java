package util;
import org.locationtech.jts.geom.Polygon;
import java.util.*;

public class Join {
	
	public List<int[]> join(Pattern pattern, List<CPair> orderList, Map<String, Map<PolygonItem, List<PolygonItem>>> linkMap){
	    double[][] lowerBounds = pattern.getLb();
	    double[][] upperBounds = pattern.getUb();

	    // Test codes
	    HashSet<String>[] labels = pattern.getLabel();
	    for (CPair cpair : orderList) {
		Map<PolygonItem, List<PolygonItem>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
		System.out.println("[" + labels[cpair.id1] + ", " + labels[cpair.id2] + "]" + " |map|:" + map.size());
		for (Map.Entry<PolygonItem, List<PolygonItem>> entry : map.entrySet()) {
		    int firstId = entry.getKey().id;
		    for (PolygonItem p : entry.getValue()) {
		        int secondId = p.id;
		        System.out.println("[" + firstId + ", " + secondId + "]");
		    }
		}
		System.out.println();
	    }

	    // Step 0: Early stop
	    for (CPair cpair : orderList) {
		Map<PolygonItem, List<PolygonItem>> map = linkMap.get(cpair.id1 + ":" + cpair.id2);
		if (cpair.linkOut) {
		    if (map == null || map.size() == 0) return null;
		}
	    }

	    // Step 1: Determine column order
	    int rankIndex = 0;
	    Map<Integer, Integer> rankMap = new HashMap<>(); // vertexId -> rank
	    for (CPair cpair : orderList) {
		if (!rankMap.containsKey(cpair.id1)) rankMap.put(cpair.id1, rankIndex++);
		if (!rankMap.containsKey(cpair.id2)) rankMap.put(cpair.id2, rankIndex++);
	    }

	    // Step 2: Link edges
	    LinkBox linkBox = new LinkBox();
	    Box rootBox = new Box(new PolygonItem(-1, new Polygon(null, null, null))); // Root of the result tree
	    for (int i = 0; i < orderList.size(); i++) {
		CPair cpair = orderList.get(i);
		int id1 = cpair.id1, id2 = cpair.id2;
		int rank1 = rankMap.get(id1), rank2 = rankMap.get(id2);
		Map<PolygonItem, List<PolygonItem>> candidateMap = linkMap.get(id1 + ":" + id2);
		if (rank1 > rank2) candidateMap = linkBox.reverse(candidateMap); // Reverse key-value pairs

		int leftRank = Math.min(rank1, rank2);
		int rightRank = Math.max(rank1, rank2);

		if (cpair.linkOut) { // Outer edge
		    if (i == 0) linkBox.buildFirstLevel(rootBox, candidateMap);
		    else {
		        boolean hasNextLevel = linkBox.buildLeaf(rootBox, candidateMap, leftRank, rightRank);
		        if (!hasNextLevel) return null;
		    }
		} else { // Inner edge
		    double lowerDistance = lowerBounds[id1][id2];
		    double upperDistance = upperBounds[id1][id2];
		    linkBox.backInclude(rootBox, leftRank, rightRank, lowerDistance, upperDistance);
		}
	    }

	    // Step 3: Collect records
	    BoxRecord boxRecord = new BoxRecord(pattern, rootBox);
	    List<int[]> resultList = boxRecord.collectRecord();

	    return resultList;
	}
	

	public List<int[]> join(Pattern queryPattern, BoundedPattern boundaryPattern, List<JoinPair> orderList, 
                          Map<String, Map<PolygonItem, List<PolygonItem>>> connectionMap, 
                          Map<Integer, List<Integer>> anchorMap, MTopoJoin caller) {

	    HashSet<String>[] labels = queryPattern.getLabel();
	    double[][] lb = queryPattern.getLb(), ub = queryPattern.getUb();

	    // Step 0: Early termination
	    for (JoinPair joinPair : orderList) {
		Map<PolygonItem, List<PolygonItem>> map = connectionMap.get(joinPair.id1 + ":" + joinPair.id2);
		if (joinPair.linkOut) {
		    if (map == null || map.isEmpty()) {
		        return null;
		    }
		}
	    }

	    // Step 1: Determine column order
	    int rankIndex = 0;
	    HashMap<Integer, Integer> rankMap = new HashMap<>(); // vertexId -> rank
	    for (JoinPair joinPair : orderList) {
		if (!rankMap.containsKey(joinPair.id1)) {
		    rankMap.put(joinPair.id1, rankIndex++);
		}
		if (!rankMap.containsKey(joinPair.id2)) {
		    rankMap.put(joinPair.id2, rankIndex++);
		}
	    }

	    // Step 2: Link edges
	    LinkBox linkBox = new LinkBox();
	    Box rootBox = new Box(new PolygonItem(-1, null)); // Root of the result tree
	    BoxRecord boxRecord = new BoxRecord(queryPattern, rootBox);
	    Set<Integer> visitedSet = new HashSet<>();
	    for (int i = 0; i < orderList.size(); i++) {
		JoinPair joinPair = orderList.get(i);
		int id1 = joinPair.id1, id2 = joinPair.id2;
		int rank1 = rankMap.get(id1), rank2 = rankMap.get(id2);
		Map<PolygonItem, List<PolygonItem>> candidateMap = connectionMap.get(id1 + ":" + id2);

		if (rank1 > rank2) {
		    candidateMap = linkBox.reverse(candidateMap); // Reverse key-value pairs
		}

		int leftRank = rank1, rightRank = rank2, newId = id2;
		if (rank1 > rank2) {
		    leftRank = rank2;
		    rightRank = rank1;
		    newId = id1;
		}

		if (joinPair.linkOut) { // Outer edge
		    if (i == 0) {
		        linkBox.buildFirstLevel(rootBox, candidateMap);
		    } else {
		        boolean hasNextLevel = linkBox.buildLeaf(rootBox, candidateMap, leftRank, rightRank);
		        if (!hasNextLevel) {
		            return null;
		        }

		        // Anchor pruning
		        if (anchorMap != null && anchorMap.containsKey(newId)) {
		            linkBox.achPrune(boundaryPattern, rootBox, rankMap, newId, anchorMap.get(newId));
		        }
		    }
		} else { // Inner edge
		    double lbDist = lb[id1][id2], ubDist = ub[id1][id2];
		    linkBox.backInclude(rootBox, leftRank, rightRank, lbDist, ubDist);
		}
	    }

	    // Step 3: Collect records
	    List<int[]> resultList = boxRecord.collectRecord();

	    int maxRank = 0;
	    for (Map.Entry<Integer, Integer> entry : rankMap.entrySet()) {
		int rank = entry.getValue();
		if (rank > maxRank) {
		    maxRank = rank;
		}
	    }
	    caller.keywordMap = new HashSet[maxRank + 1];
	    for (Map.Entry<Integer, Integer> entry : rankMap.entrySet()) {
		int rank = entry.getValue();
		HashSet<String> word = labels[entry.getKey()];
		caller.keywordMap[rank] = word;
	    }
	    return resultList;
	}
}
