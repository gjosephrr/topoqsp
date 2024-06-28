package util;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.index.strtree.AbstractNode;
import org.locationtech.jts.index.strtree.Boundable;
import org.locationtech.jts.index.strtree.ItemBoundable;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.io.InStream;
import org.locationtech.jts.io.WKBReader;
import util.file.CSVReader;
import util.quadtree.ANode;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class IRTreeBuilder {

    private HashMap<Integer, double[][]> polygonMap;
    private HashMap<Integer, String[]> keyMap;
    private int leafCounter = Integer.MIN_VALUE;
    public static int treeHeight = -1;

    // In-memory version
    public IRTreeBuilder(String state) {

	// Load polygons
	// Load keywords 
    }

    public Node build() {
	// Step 1: Build R-tree using API
	AbstractNode rtreeRoot = buildRtree();

	// Step 2: Build IR-tree structure by traversing R-tree
	ANode irTreeRoot = buildIRTreeStruct(rtreeRoot);

	// Step 3: Build keyword inverted list of IR-tree
	buildIRTreeInvert(irTreeRoot);

	if (irTreeRoot instanceof Leaf) {
	    System.out.println("IRTreeBuilder: Root node is a leaf node");
	    System.out.println("Ignoring case with IR-tree height of 1");
	    System.exit(0);
	}

	// Step 4: Compute height
	this.treeHeight = getTreeHeight(irTreeRoot);

	return (Node) irTreeRoot;
    }

    // Step 1: Build R-tree
    private AbstractNode buildRtree() {
	STRtree tree = new STRtree(12);

	int count = 0;
	int totalCount = 0;
	HashMap<Integer, String[]> keyData = new HashMap<>();

	// Database connection and data loading

	setKeywordMap(keyData);
	System.out.println("Polygons loaded correctly: " + count);
	System.out.println("Total polygons with errors: " + totalCount);
	System.out.println("IRTree built successfully!");

	return tree.getRoot();
    }

    // Step 2: Build IR-tree structure by traversing R-tree
    private ANode buildIRTreeStruct(Object rtreeRoot) {
	AbstractNode rootNode = (AbstractNode) rtreeRoot;
	Boundable boundable = (Boundable) rootNode;
	Envelope envelope = (Envelope) boundable.getBounds();
	MBR mbr = new MBR(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());

	// Consider first child node
	Object firstChild = rootNode.getChildBoundables().get(0);
	if (firstChild instanceof AbstractNode) { // Internal nodes
	    List<ANode> childNodeList = new ArrayList<>();
	    for (Iterator iterator = rootNode.getChildBoundables().iterator(); iterator.hasNext();) {
	        Object childObject = iterator.next();
	        ANode childNode = buildIRTreeStruct(childObject);
	        childNodeList.add(childNode);
	    }
	    return new Node(mbr, childNodeList);
	} else if (firstChild instanceof ItemBoundable) { // Leaf nodes
	    List<PolygonItem> objectList = new ArrayList<>();
	    for (Iterator iterator = rootNode.getChildBoundables().iterator(); iterator.hasNext();) {
	        Object object = iterator.next();
	        ItemBoundable item = (ItemBoundable) object;
	        PolygonItem itemObject = (PolygonItem) item.getItem();
	        int id = itemObject.getId();
	        objectList.add(new PolygonItem(id, itemObject.getPolygon()));
	    }
	    return new Leaf(leafCounter++, mbr, objectList);
	} else {
	    System.out.println("IRTreeBuilder.buildIRTreeStruct: Error encountered");
	    return null;
	}
    }

    public HashMap<Integer, double[][]> getPolygonMap() {
	return polygonMap;
    }

    public void setPolygonMap(HashMap<Integer, double[][]> polygonMap) {
	this.polygonMap = polygonMap;
    }

    public HashMap<Integer, String[]> getKeywordMap() {
	return keyMap;
    }

    public void setKeywordMap(HashMap<Integer, String[]> keyMap) {
	this.keyMap = keyMap;
    }
	private void buildInvertedList(AbstractNode rootNode) {
	    if (rootNode instanceof LeafNode) {
	      // Initialize inverted list for leaf nodes
	      Map<String, List<PolygonItem>> invertedMap = new HashMap<>();
	      for (PolygonItem item : ((LeafNode) rootNode).getObjectList()) {
		int id = item.getId();

		String[] keywords = ((String[]) keyArray.get(id)).split(" ");

		for (String keyword : keywords) {
		  if (!invertedMap.containsKey(keyword)) {
		    invertedMap.put(keyword, new ArrayList<>());
		  }
		  invertedMap.get(keyword).add(item);
		}
	      }
	      ((LeafNode) rootNode).setInvertedList(invertedMap);
	    } else { // Initialize inverted list for internal nodes
	      List<AbstractNode> childList = ((InternalNode)rootNode).getChildList();
	      AbstractNode firstChild = childList.get(0);

	      Map<String, List<AbstractNode>> invertedMap = new HashMap<>();
	      if (firstChild instanceof LeafNode) {
		if (((LeafNode)firstChild).getInvertedList() == null) {
		  for (AbstractNode node : childList) {
		    buildInvertedList(node);
		  }
		}

		for (AbstractNode node : childList) {
		  LeafNode leaf = (LeafNode) node;
		  Map<String, List<PolygonItem>> childInvertedMap = leaf.getInvertedList();
		  for (String keyword : childInvertedMap.keySet()) {
		    if (!invertedMap.containsKey(keyword)) {
		      invertedMap.put(keyword, new ArrayList<>());
		    }
		    invertedMap.get(keyword).add(node);
		  }
		}
	      } else {
		if (((InternalNode)firstChild).getInvertedList() == null) {
		  for (AbstractNode node : childList) {
		    buildInvertedList(node);
		  }
		}
		for (AbstractNode node : childList) {
		  Map<String, List<AbstractNode>> childInvertedMap = ((InternalNode)node).getInvertedList();
		  for (String keyword : childInvertedMap.keySet()) {
		    if (!invertedMap.containsKey(keyword)) {
		      invertedMap.put(keyword, new ArrayList<>());
		    }
		    invertedMap.get(keyword).add(node);
		  }
		}
	      }
	      ((InternalNode)rootNode).setInvertedList(invertedMap); // Set inverted list
	    }
	  }

	  // Step 4: Compute the height (logic remains the same)

	  public Coordinate[] generateCoordinateArray(Double[][] coordinates) {
	    int arraySize = coordinates.length;
	    Coordinate[] coordArray = new Coordinate[arraySize];
	    for (int i = 0; i < arraySize; i++) {
	      Coordinate coord2 = new Coordinate(coordinates[i][0], coordinates[i][1]);
	      coordArray[i] = coord2;
	    }

	    return coordArray;
	  }

	  public void loadPolygon(String state) {
	    CSVReader csvReader = new CSVReader();
	    HashMap<Integer, Double[][]> polygonArray = csvReader.readPolygon(state);
	    setPolygonArray(polygonArray);
	  }

	  public void loadKeywords(String state) {
	    CSVReader csvReader = new CSVReader();
	    HashMap<Integer, String[]> keyArray = csvReader.readKeyword(state);
	    setKeyArray(keyArray);
	  }

	}


}
