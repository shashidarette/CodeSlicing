package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.GraphEdge;
import dependenceAnalysis.util.cfg.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class ControlDependenceTree extends Analysis {

    public ControlDependenceTree(ClassNode cn, MethodNode mn) {
        super(cn, mn);
    }

    /**
	 * Produce a new Graph object, representing the reverse of the Graph given
	 * in the cfg parameter.
	 * 
	 * @param cfg
	 * @return
	 */
	protected Graph reverseGraph(Graph cfg) {
		Graph reverseCFG = new Graph();
		Iterator<Node> cfgIt = cfg.getNodes().iterator();
		while (cfgIt.hasNext()) {
			reverseCFG.addNode(cfgIt.next());
		}
		cfgIt = cfg.getNodes().iterator();
		while (cfgIt.hasNext()) {
			Node n = cfgIt.next();
			Set<Node> successors = cfg.getSuccessors(n);
			for (Node succ : successors) {
				reverseCFG.addEdge(succ, n);
			}
		}
		return reverseCFG;
	}
	
	/**
	 * Utility function to create an augmented graph with Start node connected to Entry and Exit
	 * @param graph
	 * @return
	 */
	private Graph createAugmentedCFG(Graph graph) {
		Node start = new Node("Start");
		
    	graph.addNode(start);
    	graph.addEdge(start, graph.getEntry());
    	graph.addEdge(start, graph.getExit());
   	
    	return graph;
	}
	
	/**
	 * Utility function to get the nodes in the graph from source node to destination node
	 * @param g
	 * @param source
	 * @param destination
	 * @return
	 */
	public Set<Node> getNodesOnPath(Graph g, Node source, Node destination, ArrayList<String> traversedNodes) {
		Set<Node> nodes = new HashSet<Node>();
		
		if (traversedNodes.contains(source.toString())) {
			return nodes;
		}
		Collection<Node> sucessors = g.getTransitiveSuccessors(source);
		if (sucessors.contains(destination)) {
			nodes.add(source);
		}
		for (Node n : sucessors) {
			Set<Node> pathNodes = getNodesOnPath(g, n, destination, traversedNodes);
			traversedNodes.add(n.toString());
			nodes.addAll(pathNodes);
		}		
		traversedNodes.add(source.toString());
		return nodes;
	}
	
    /**
     * Return a graph representing the control dependence tree of the control
     * flow graph, which is stored in the controlFlowGraph class attribute
     * (this is inherited from the Analysis class).
     *
     * You may wish to use the post dominator tree code you implement to support
     * computing the Control Dependence Graph.
     *
     * @return
     */
    public Graph computeResult() {    	
        // Create an augmented control flow graph
    	Graph augCfg = createAugmentedCFG(controlFlowGraph);
    	
    	// Create a post dominant tree of it
    	PostDominatorTree pdt = new PostDominatorTree(cn, mn);
    	Graph cpdt = pdt.computePostDominanceTree(reverseGraph(augCfg));
    	    	
    	// Selection of edges that are branches
    	Set<GraphEdge> selectedEdges = new HashSet<GraphEdge>();
    	for (Node node : controlFlowGraph.getNodes()) {
    		Set<Node> successors = controlFlowGraph.getSuccessors(node);
    		
    		// node with at-least 2 outgoing edges
    		if (successors.size() >= 2) {
    			for (Node successor : successors) {
    				if (!cpdt.getTransitiveSuccessors(successor).contains(node)) {
    					selectedEdges.add(new GraphEdge(node, successor));
    				}
    			}
    		}
    	}
    	
    	// Computation of control dependence graph
    	Graph cdGraph = new Graph();
    	for (GraphEdge edge : selectedEdges) {
    		Node lcaNode = cpdt.getLeastCommonAncestor(edge.getSource(), edge.getDestination());
    		Set<Node> pathNodes = getNodesOnPath(cpdt, lcaNode, edge.getDestination(), new ArrayList<String>());
    		
    		// path nodes contains lca nodes as well as main node 'a'
    		// check if lca node is 'a' else remove the both the nodes
    		if (lcaNode != edge.getSource()) {
    			pathNodes.remove(edge.getSource());
        		pathNodes.remove(lcaNode);
    		}
    		
    		// add 'b' node
    		pathNodes.add(edge.getDestination());
    		
    		// Get all nodes from post dominance tree from destination to lcaNode
    		cdGraph.addNode(edge.getSource());
    		for (Node n: pathNodes) {
    			cdGraph.addNode(n);
    			cdGraph.addEdge(edge.getSource(), n);
    		}
    	}
    	
        return cdGraph;
    }
}