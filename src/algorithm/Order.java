package util;
import algorithm.PriQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class Order {
	
	public List<JoinPair> determineJoinOrder(Schema pattern, Map<StringPair, Integer> costMap) {
    int numNodes = pattern.getNumNodes();
    int[][] adjacencyMatrix = pattern.getAdjacencyMatrix();
    Set<String>[] labels = pattern.getLabels();

    // Step 1: Select the starting edge
    int startI = -1, startJ = -1, minCost = Integer.MAX_VALUE;
    for (Map.Entry<StringPair, Integer> entry : costMap.entrySet()) {
        if (entry.getValue() < minCost) {
            minCost = entry.getValue();
            String[] s = entry.getKey().getValues(); 
            startI = Integer.parseInt(s[0]);
            startJ = Integer.parseInt(s[1]);
        }
    }

    // Step 2: Initialize the list
    List<JoinPair> orderList = new ArrayList<>();
    boolean[] visited = new boolean[numNodes];
    orderList.add(new JoinPair(startI, startJ, true));
    visited[startI] = visited[startJ] = true;

    // Step 3: Explore the order incrementally
    PriorityQueue<JoinPair> queue = new PriorityQueue<>();
    for (int neighbor : adjacencyMatrix[startI]) {
        if (startI < neighbor)
            queue.add(new JoinPair(startI, neighbor, costMap.get(new StringPair(startI + ":" + neighbor))));
        else
            queue.add(new JoinPair(neighbor, startI, costMap.get(new StringPair(neighbor + ":" + startI))));
    }
    for (int neighbor : adjacencyMatrix[startJ]) {
        if (startJ < neighbor)
            queue.add(new JoinPair(startJ, neighbor, costMap.get(new StringPair(startJ + ":" + neighbor))));
        else
            queue.add(new JoinPair(neighbor, startJ, costMap.get(new StringPair(neighbor + ":" + startJ))));
    }

    while (!queue.isEmpty()) {
        JoinPair currentPair = queue.poll();
        int id1 = currentPair.getId1();
        int id2 = currentPair.getId2();
        if (visited[id1] && visited[id2])
            continue;

        int newId = visited[id2] ? id1 : id2;

        orderList.add(new JoinPair(id1, id2, true));

        // Handle inner-edges
        for (int neighbor : adjacencyMatrix[newId]) {
            if (visited[neighbor] && neighbor != id1 && neighbor != id2) {
                if (neighbor < newId)
                    orderList.add(new JoinPair(neighbor, newId, false));
                else
                    orderList.add(new JoinPair(newId, neighbor, false));
            }
        }

        // Handle out-edges
        for (int neighbor : adjacencyMatrix[newId]) {
            if (!visited[neighbor]) {
                if (neighbor < newId)
                    queue.add(new JoinPair(neighbor, newId, costMap.get(new StringPair(neighbor + ":" + newId))));
                else
                    queue.add(new JoinPair(newId, neighbor, costMap.get(new StringPair(newId + ":" + neighbor))));
            }
        }

        visited[newId] = true;
    }

    return orderList;
}
}
