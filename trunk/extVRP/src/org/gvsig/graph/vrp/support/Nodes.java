package org.gvsig.graph.vrp.support;

import java.util.ArrayList;

import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.vrp.gui.VRPControlPanel;
import org.metavrp.GA.Gene;
import org.metavrp.GA.GeneList;
import org.metavrp.VRP.Customer;
import org.metavrp.VRP.Vehicle;

import com.iver.cit.gvsig.fmap.core.IGeometry;

/*
 * This class represents the set of nodes of the graph that represents this VRP.
 */
public class Nodes {

	/*
	 * This class represents a node of a graph. 
	 * The VRP can be represented by a graph, where the customers and depots are nodes.
	 * This node can be seen as a gene in Evolutionary Computation.
	 * It has a geometry in GvSIG (a point).
	 * For topological constraints, it is a flag in GvSIG's Network Extension.
	 */
	public class Node {
		private Gene gene;
		private IGeometry geom;
		private GvFlag flag;
		
		/*
		 * Getters and Setters
		 */
		public Gene getGene() {
			return gene;
		}
		public void setGene(Gene gene) {
			this.gene = gene;
		}
		public IGeometry getGeom() {
			return geom;
		}
		public void setGeom(IGeometry geom) {
			this.geom = geom;
		}
		public GvFlag getFlag() {
			return flag;
		}
		public void setFlag(GvFlag flag) {
			this.flag = flag;
		}
	}
	
	// The VRP Control Panel that called this object
	private VRPControlPanel controlPanel;				
	
	/*
	 * Constructor. Needs the Control Panel.
	 */
	public Nodes(VRPControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	private ArrayList<Node> nodes = new ArrayList<Node>();

	// Add a node
	public void addNode(Node node){
		nodes.add(node);
	}
	
	// Get the node by index
	public Node getNode(int i){
		return nodes.get(i);
	}
	
	// Get the node by gene
	// TODO: Analyze the case of the depot that has more than one vehicle
	public Node getNode(Gene gene){
		for (Node node:nodes){
			if (node.getGene().getNode()==gene.getNode()){
				return node;
			}
		}
		return null;
	}
	
	// Get the node by IGeometry
	public Node getNode(IGeometry geom){
		for (Node node:nodes){
			if (node.getGeom()==geom) {
				return node;
			}
		}
		return null;
	}
		
	// Get the node by flag
	public Node getNode(GvFlag flag){
		for (Node node:nodes){
			if (node.getFlag()==flag) {
				return node;
			}
		}
		return null;
	}

	/*
	 * Getters and setters
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
}
