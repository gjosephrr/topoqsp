package util;
import java.util.*;

public class ArrowDel {

	//this function will be called by MSJ
	public List<ConnectedPair> remove(RemovalPattern pattern, Map<String, Map<KeyItem, List<KeyItem>>> connectionMap, 
		                          List<ConnectedPair> orderList) {
	    ConnectedPair previousPair = orderList.get(0);
	    List<ConnectedPair> updatedOrderList = new ArrayList<>();
	    updatedOrderList.add(previousPair);

	    for (int index = 1; index < orderList.size(); index++) {
		ConnectedPair currentPair = orderList.get(index);
		if (previousPair.key1 == currentPair.key2 && previousPair.key2 == currentPair.key1) {
		    int key1 = previousPair.key1;
		    int key2 = previousPair.key2;
		    Map<Integer, Set<Integer>> tempMap = new HashMap<>();
		    for (Map.Entry<KeyItem, List<KeyItem>> entry : connectionMap.get(key2 + ":" + key1).entrySet()) {
		        Set<Integer> connectedKeys = new HashSet<>();
		        for (KeyItem item : entry.getValue()) {
		            connectedKeys.add(item.id);
		        }
		        tempMap.put(entry.getKey().id, connectedKeys);
		    }

		    Map<KeyItem, List<KeyItem>> remainingConnections = connectionMap.get(key1 + ":" + key2);
		    Map<KeyItem, List<KeyItem>> updatedConnections = new HashMap<>();
		    for (Map.Entry<KeyItem, List<KeyItem>> entry : remainingConnections.entrySet()) {
		        KeyItem point = entry.getKey();
		        List<KeyItem> remainingConnectedItems = new ArrayList<>();
		        for (KeyItem item : entry.getValue()) {
		            Set<Integer> connectedKeys = tempMap.get(item.id);
		            if (connectedKeys != null && connectedKeys.contains(point.id)) {
		                remainingConnectedItems.add(item);
		            }
		        }
		        if (!remainingConnectedItems.isEmpty()) {
		            updatedConnections.put(point, remainingConnectedItems);
		        }
		    }

		    connectionMap.remove(key2 + ":" + key1);
		    connectionMap.put(key1 + ":" + key2, updatedConnections);
		} else {
		    updatedOrderList.add(currentPair);
		    previousPair = currentPair;
		}
	    }

	    return updatedOrderList;
	}
}
