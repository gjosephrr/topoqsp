package util;

import java.util.*;

public class Pattern {

	private int m = -1;//the number of vertices in the pattern
	private HashSet<String> label[] = null;//vertex i has a keyword label[i]
	private double lb[][] = null;//lbDist[i][j] = lbDist[j][i] = l_{i,j}, if vertex i has an out-edge to vertex j. Default: -1
	private double ub[][] = null;//ubDist[i][j] = ubDist[j][i] = u_{i,j}, if vertex i has an out-edge to vertex j. Default: -1
	private int graph[][] = null;//keep the neighbors of each vertex
	private int topo[][] = null;
	public Pattern(){}

	//construct an object using a list of links
	public Pattern(List<Link> pairList){
		Map<HashSet<String>, Integer> map = new HashMap<HashSet<String>, Integer>();//keyword -> vertexId
		int index = 0;
		for(Link pair:pairList){
			HashSet<String> [] s = new HashSet[2];
			s[0] = pair.keyword1;
			s[1] = pair.keyword2;
			for(HashSet<String> word:s){
				if(!map.containsKey(word)){
					map.put(word, index);
					index ++;
				}
			}
		}

		this.m = map.size();
		this.label = new HashSet[m];
		this.lb = new double[m][m];
		this.ub = new double[m][m];
		this.topo = new int[m][m];

		for(Map.Entry<HashSet<String>, Integer> entry:map.entrySet()){
			label[entry.getValue()] = entry.getKey();
		}
		//lowerbound
		for(int i = 0;i < m;i ++){
			for(int j = 0;j < m;j ++){
				if(i == j)   lb[i][j] = ub[i][j] = 0.0;
				else         lb[i][j] = ub[i][j] = -1.0;
			}
		}
		for(Link pair:pairList){
			int id1 = map.get(pair.keyword1);
			int id2 = map.get(pair.keyword2);
			lb[id1][id2] = lb[id2][id1] = pair.lower;//symmetric
			ub[id1][id2] = ub[id2][id1] = pair.upper;//symmetric
			if(pair.topo == "equal"){
				topo[id1][id2]  = 2;
			}
			if(pair.topo == "overlap"){
				topo[id1][id2]  = 1;
			}
			if(pair.topo == "touches"){
				topo[id1][id2]  = 3;
			}
			if(pair.topo == "disjoint"){
				topo[id1][id2]  = 4;
			}
		}

		//build the graph
		this.graph = new int[m][];
		for(int i = 0;i < m;i ++){
			List<Integer> list = new ArrayList<Integer>();
			for(int j = 0;j < m;j ++){
				if(i != j && ub[i][j] > 0){
					list.add(j);
				}
			}
			graph[i] = new int[list.size()];
			for(int j = 0;j < list.size();j ++)   graph[i][j] = list.get(j);
		}
	}

	public HashSet<String>[] getLabel() {
		return label;
	}

	public void setLabel(HashSet<String>[] label) {
		this.label = label;
	}

	public double[][] getLb() {
		return lb;
	}

	public void setLb(double[][] lb) {
		this.lb = lb;
	}

	public double[][] getUb() {
		return ub;
	}

	public void setUb(double[][] ub) {
		this.ub = ub;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int[][] getGraph() {
		return graph;
	}

	public int[][] getTopo() {
		return topo;
	}

	public void setTopo(int[][] topo) {
		this.topo = topo;
	}

	//will be updated by local-refining and global-refining
	public void setGraph(int[][] graph) {
		this.graph = graph;
	}

	public Pattern clone(){
		Pattern p = new Pattern();
		double clb[][] = new double[m][m];
		double cub[][] = new double[m][m];
		HashSet<String> clabel[] = new HashSet[m];
		boolean cmark[][] = new boolean[m][m];
		int cgraph[][] = new int[m][];

		for(int i = 0;i < m;i ++){
			clabel[i] = label[i];
			for(int j = 0;j < m;j ++){
				clb[i][j] = lb[i][j];
				cub[i][j] = ub[i][j];
			}

			cgraph[i] = new int[graph[i].length];
			for(int j = 0;j < graph[i].length;j ++){
				cgraph[i][j] = graph[i][j];
			}
		}

		p.setM(m);
		p.setLb(clb);
		p.setUb(cub);
		p.setLabel(clabel);
		p.setGraph(cgraph);

		return p;
	}
}
