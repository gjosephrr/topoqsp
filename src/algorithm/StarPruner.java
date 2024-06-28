package util;

import java.util.*;


public class StarPruner {

  public void prune(Pattern queryPattern, List<CandidatePair> orderedList, Map<String, Map<DataObject, List<DataObject>>> connectionMap) {
    int numVertices = queryPattern.getNumVertices();

    // Step 1: Count neighbor count for each vertex in the query pattern
    int[] neighborCount = new int[numVertices];
    for (CandidatePair candidatePair : orderedList) {
      boolean considered = candidatePair.hasOutgoingLink();

      if (considered) {
        neighborCount[candidatePair.getFirstVertexId()]++;
        neighborCount[candidatePair.getSecondVertexId()]++;
      }
    }

    // Step 2: Count neighbor count for each object
    List<Map<Integer, Integer>> objectCountList = new ArrayList<>();
    for (int i = 0; i < numVertices; i++) {
      objectCountList.add(new HashMap<>());
    }
    for (CandidatePair candidatePair : orderedList) {
      int vertexId1 = candidatePair.getFirstVertexId();
      int vertexId2 = candidatePair.getSecondVertexId();
      Map<Integer, Integer> objectCountMap1 = objectCountList.get(vertexId1);
      Map<Integer, Integer> objectCountMap2 = objectCountList.get(vertexId2);
      Map<DataObject, List<DataObject>> candidateMap = connectionMap.get(vertexId1 + ":" + vertexId2);

      if (candidateMap != null) {
        Set<Integer> objectSet1 = new HashSet<>();
        for (DataObject dataPoint : candidateMap.keySet()) {
          objectSet1.add(dataPoint.getId());
        }
        for (int objectId : objectSet1) {
          objectCountMap1.put(objectId, objectCountMap1.getOrDefault(objectId, 0) + 1);
        }

        Set<Integer> objectSet2 = new HashSet<>();
        for (Map.Entry<DataObject, List<DataObject>> entry : candidateMap.entrySet()) {
          for (DataObject dataPoint : entry.getValue()) {
            objectSet2.add(dataPoint.getId());
          }
        }
        for (int candidateObjectId : objectSet2) {
          objectCountMap2.put(candidateObjectId, objectCountMap2.getOrDefault(candidateObjectId, 0) + 1);
        }
      }
    }

    // Step 3: Perform neighbor pruning
    for (CandidatePair candidatePair : orderedList) {
      Map<DataObject, List<DataObject>> candidateMap = connectionMap.get(candidatePair.getFirstVertexId() + ":" + candidatePair.getSecondVertexId());
      if (candidateMap != null) {
        int vertexId1 = candidatePair.getFirstVertexId();
        int vertexId2 = candidatePair.getSecondVertexId();
        Map<Integer, Integer> objectCountMap1 = objectCountList.get(vertexId1);
        Map<Integer, Integer> objectCountMap2 = objectCountList.get(vertexId2);

        Iterator<Map.Entry<DataObject, List<DataObject>>> candidateMapIterator = candidateMap.entrySet().iterator();
        while (candidateMapIterator.hasNext()) {
          Map.Entry<DataObject, List<DataObject>> entry = candidateMapIterator.next();
          int objectId = entry.getKey().getId();
          if (objectCountMap1.get(objectId) < neighborCount[vertexId1]) {
            candidateMapIterator.remove(); // Prune
          } else {
            List<DataObject> candidateList = entry.getValue();
            List<DataObject> filteredList = new ArrayList<>();
            for (DataObject candidateDataObject : candidateList) {
              if (objectCountMap2.get(candidateDataObject.getId()) >= neighborCount[vertexId2]) {
                filteredList.add(candidateDataObject);
              }
            }
            if (filteredList.isEmpty()) {
              candidateMapIterator.remove(); // Prune
            } else {
              entry.setValue(filteredList);
            }
          }
        }
      }
    }
  }
}
}
