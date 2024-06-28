package util;

import util.quadtree.ANode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node extends ANode {
	private List<DataNode> entries = null; 
	private Map<String, List<DataNode>> invertedIndex = null;  

	public DataNode() {

	}

	public DataNode(MBR boundingBox) {
	    this.mbr = boundingBox;
	    this.entries = new ArrayList<DataNode>();
	}

	public DataNode(MBR boundingBox, List<DataNode> entries) {
	    this.mbr = boundingBox;
	    this.entries = entries;
	}

	public MBR getMbr() {
	    return mbr;
	}

	public void setMbr(MBR mbr) {
	    this.mbr = mbr;
	}

	public List<DataNode> getEntries() {
	    return entries;
	}

	public void setEntries(List<DataNode> entries) {
	    this.entries = entries;
	}

	public Map<String, List<DataNode>> getInvertedIndex() {
	    return invertedIndex;
	}

	public void setInvertedIndex(Map<String, List<DataNode>> invertedIndex) {
	    this.invertedIndex = invertedIndex;
	}
}
