package util;

import java.util.ArrayList;
import java.util.List;

public class SpatialDataRecord {

    private SearchPattern pattern;
    private SpatialNode rootNode;

    public SpatialDataRecord(SearchPattern pattern, SpatialNode rootNode) {
        this.pattern = pattern;
        this.rootNode = rootNode;
    }

    // Collect all matching records
    public List<int[]> getMatchingRecords() {
        return getMatchingRecords(rootNode, 0, pattern.getNumDimensions(), null);
    }

    private List<int[]> getMatchingRecords(SpatialNode currentNode, int currentDimension, int totalDimensions, int[] currentRecord) {
        List<int[]> resultList = new ArrayList<>();
        if (currentDimension == 0) {
            for (SpatialNode childNode : currentNode.getChildren()) {
                int[] record = {childNode.getSpatialObject().getId()};
                List<int[]> tempList = getMatchingRecords(childNode, currentDimension + 1, totalDimensions, record);
                resultList.addAll(tempList);
            }
        } else {
            if (currentNode.getChildren() != null) {
                for (SpatialNode childNode : currentNode.getChildren()) {
                    int[] record = new int[currentRecord.length + 1];
                    for (int i = 0; i < currentRecord.length; i++) {
                        record[i] = currentRecord[i];
                    }
                    record[currentRecord.length] = childNode.getSpatialObject().getId();

                    if (currentDimension < totalDimensions - 1) {
                        List<int[]> tempList = getMatchingRecords(childNode, currentDimension + 1, totalDimensions, record);
                        resultList.addAll(tempList);
                    } else {
                        resultList.add(record);
                    }
                }
            }
        }
        return resultList;
    }

    public int countMatchingRecords(SpatialNode currentNode, int currentDimension, int totalDimensions) {
        if (currentDimension < totalDimensions) {
            int sum = 0;
            if (currentNode.getChildren() != null) {
                for (SpatialNode childNode : currentNode.getChildren()) {
                    int count = countMatchingRecords(childNode, currentDimension + 1, totalDimensions);
                    sum += count;
                }
            }
            return sum;
        } else {
            return 1;
        }
    }
}
}
