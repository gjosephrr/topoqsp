package util;

import util.quadtree.ANode;

import java.util.List;
import java.util.Map;

public class Leaf extends ANode {
    private int nodeId = -1;
    private List<DataItem> dataList = null;
    private Map<String, List<DataItem>> invertedMap = null; // Inverted list

    public DataNode(int nodeId, MBR mbr, List<DataItem> dataList) {
        this.nodeId = nodeId;
        this.mbr = mbr;
        this.dataList = dataList;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public List<DataItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataItem> dataList) {
        this.dataList = dataList;
    }

    public Map<String, List<DataItem>> getInvertedMap() {
        return invertedMap;
    }

    public void setInvertedMap(Map<String, List<DataItem>> invertedMap) {
        this.invertedMap = invertedMap;
   
