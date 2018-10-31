package dependenceAnalysis.util.cfg;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class Graph {

	/**
	 * A facade class to store graphs as DirectedMultiGraphs using the JGraphT framework.
	 */

	protected DirectedMultigraph<Node, DefaultEdge> graph;
	
	public Graph(){
        Node.sNextId = 1;
		graph = new DirectedMultigraph<Node, DefaultEdge>(new ClassBasedEdgeFactory<Node, DefaultEdge>(DefaultEdge.class));
	}
	
	public void addNode(Node n){
		graph.addVertex(n);
	}

	public void addEdge(Node a, Node b) {
		graph.addEdge(a,b);
	}

	/**
	 * Returns the immediate predecessors of a node.
	 * @param a
	 * @return
	 */
	public Set<Node> getPredecessors(Node a){
		Set<Node> preds = new HashSet<Node>();
		for(DefaultEdge de : graph.incomingEdgesOf(a)){
			preds.add(graph.getEdgeSource(de));
		}
		return preds;
	}

	/**
	 * Returns the immediate successors of a node.
	 * @param a
	 * @return
	 */
	public Set<Node> getSuccessors(Node a){

		Set<Node> succs = new HashSet<Node>();
		for(DefaultEdge de : graph.outgoingEdgesOf(a)){
			succs.add(graph.getEdgeTarget(de));
		}
		return succs;
	}

	/**
	 * Returns all of the nodes in the graph.
	 * @return
	 */
	public Set<Node> getNodes(){
		return graph.vertexSet();
	}

	/**
	 * Returns the entry node - the node with no predecessors.
	 * Assumes that there is only one such node in the graph.
	 * @return
	 */
	public Node getEntry(){
		for(Node n : getNodes()){
			if(graph.incomingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}

	/**
	 * Returns the exit node - the node with no successors.
	 * Assumes that there is only one such node in the graph.
	 * @return
	 */
	public Node getExit(){
		for(Node n : getNodes()){
			if(graph.outgoingEdgesOf(n).isEmpty())
				return n;
		}
		return null;
	}

	/**
	 * Returns a representation of the graph in the GraphViz dot format. This can be written to a file and visualised using GraphViz.
	 * @return
	 */
	public String toString(){
		String dotString = "digraph cfg{\n";
		for (Node node : getNodes()) {
			for (Node succ: getSuccessors(node)) {
				dotString+=node.toString()+"->"+succ.toString()+"\n";
			}
		}
		dotString+="}";
		return dotString;
	}


	/**
	 * Return all transitive successors of m - i.e. any instructions
	 * that could eventually be reached from m.
	 * @param m
	 * @return
     */
	public Collection<Node> getTransitiveSuccessors(Node m){
		return transitiveSuccessors(m, new HashSet<Node>());
	}

	private Collection<Node> transitiveSuccessors(Node m, Set<Node> done){
		Collection<Node> successors = new HashSet<Node>();
		for(Node n : getSuccessors(m)){
			if(!done.contains(n)) {
				successors.add(n);
				done.add(n);
				successors.addAll(transitiveSuccessors(n, done));
			}
		}
		return successors;
	}
	
	/**
	 * For a given pair of nodes in a DAG, return the ancestor that is common to both nodes.
	 *
	 * Important: This operation presumes that the graph contains no cycles.
	 * @param x
	 * @param y
	 * @return
	 */
	public Node getLeastCommonAncestor(Node x, Node y) {
        Node current = x;
        while(!containsTransitiveSuccessors(current,x,y)){
        	Set<Node> predes = getPredecessors(current);
        	if (!predes.isEmpty()) {
        		current = predes.iterator().next();
        	}
        	else {
        		System.out.println("LCA empty");
        		break;
        	}
        }
        return current;
    }

	private boolean containsTransitiveSuccessors(Node x, Node x2, Node y) {
		Collection<Node> transitiveSuccessors = getTransitiveSuccessors(x);
        if(transitiveSuccessors.contains(x2) && transitiveSuccessors.contains(y))
        	return true;
        else
        	return false;
	}	
}
