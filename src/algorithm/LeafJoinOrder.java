package util;
import util.quadtree.ANode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeafJoinOrder {
	public List<JoinPair> getOptimalJoinOrder(JoinPattern pattern, List<JoinPair> preOrderList, List<Map<AbstractNode, List<AbstractNode>>> candidateLists) {
		Map<String, Integer> edgeCosts = new HashMap<>(); // Cost of each join edge

		for (int index = 0; index < preOrderList.size(); index++) {
		    JoinPair currentPair = preOrderList.get(index);
		    int nodeId1 = currentPair.getNodeId1();
		    int nodeId2 = currentPair.getNodeId2();
		    Map<AbstractNode, List<AbstractNode>> candidateMap = candidateLists.get(index);
		    Set<AbstractNode> candidateKeys = candidateMap.keySet();

		    int cost = 0;
		    for (AbstractNode node : candidateKeys) {
		        cost += candidateMap.get(node).size();
		    }
		    edgeCosts.put(nodeId1 + ":" + nodeId2, cost);
		}

		JoinOrderOptimizer optimizer = new JoinOrderOptimizer();
		return optimizer.getOptimalJoinOrder(pattern, edgeCosts);
	    }
}
