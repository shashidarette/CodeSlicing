package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.Variable;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class ProgramDependenceGraph extends Analysis {
	// Data dependence graph - for computation of overlap
	private Graph dataDependenceGraph;
	// Program dependence graph - (cannot use controlFlowGraph in Analysis since its final)
	private Graph programDependenceGraph;
    public ProgramDependenceGraph(ClassNode cn, MethodNode mn) {
        super(cn, mn);
    }

    /**
     * Return a graph representing the Program Dependence Graph of the control
     * flow graph, which is stored in the controlFlowGraph class attribute
     * (this is inherited from the Analysis class).
     *
     * You may wish to use the class that computes the Control Dependence Tree to
     * obtain the control dependences, and may wish to use the code in the
     * dependenceAnalysis.analysis.DataFlowAnalysis class to obtain the data dependences.
     * 
     * @return
     */
    public Graph computeResult() {
    	ControlDependenceTree cdt = new ControlDependenceTree(cn, mn);
    	programDependenceGraph = cdt.computeResult();
    	
    	// Prepare a map of node and variables used by the node
    	// this is to avoid multiple calls later in the processing
    	Map<String, Collection<Variable>> nodeUsedByVarsMap = new HashMap<String, Collection<Variable>>();
    	for (Node node : programDependenceGraph.getNodes()) {
    		// skip slicing for non-instruction node
    		if (isSkipNode(node)) {
    			continue;
    		}
    		try {
				Collection<Variable> usedVars = DataFlowAnalysis.usedBy(cn.name, mn, node.getInstruction());
				nodeUsedByVarsMap.put(node.toString(), usedVars);
			} catch (AnalyzerException e) {
				System.out.println("Analyser exception occured while computing program dependence");
				e.printStackTrace();
			}
    	}
    	
    	// in addition to the program dependence graph, data dependence graph is also computed
    	dataDependenceGraph = new Graph();
    	
    	// for each node get defined variables and check with all the node's usedBy variables
    	// if any variable defined is user by a node add data dependence and add edge in 
    	// control flow graph i.e. program dependence graph
    	for (Node node : programDependenceGraph.getNodes()) {
    		// skip slicing for non-instruction node
    		if (isSkipNode(node)) {
    			continue;
    		}
    		try {
	    		Collection<Variable> definedVars = DataFlowAnalysis.definedBy(cn.name, mn, node.getInstruction());
	    		for (Node n : programDependenceGraph.getNodes()) {
	    			// skip slicing for non-instruction node
	        		if (isSkipNode(n)) {
	        			continue;
	        		}
	        		
	        		// get user by variables of the node from the map
	        		Collection<Variable> usedByVars = nodeUsedByVarsMap.get(n.toString());
	        		for (Variable variable : definedVars){
	                    if(usedByVars.contains(variable)){
	                    	// update data dependence graph : add nodes and edge
	                    	dataDependenceGraph.addNode(node);
	                    	dataDependenceGraph.addNode(n);
	                    	if (node != n) {
	                    		dataDependenceGraph.addEdge(node, n);
	                    		// Add an edge to between definedBy node and usedBy node
		                    	programDependenceGraph.addEdge(node, n);
	                    	}
	                    }
	                }
	    		}
	    	} catch (AnalyzerException e) {
	    		System.out.println("Analyser exception occured while computing program dependence");
				e.printStackTrace();
			}
    	}
        return programDependenceGraph;
    }

    /**
     * Utility function to find if the node is one of the non-instruction node
     * @param node
     * @return true if the node is an non-instruction else false
     */
    private boolean isSkipNode(Node node) {
    	String name = node.toString();
    	return (name.equalsIgnoreCase("\"Start\"") 
    			|| name.equalsIgnoreCase("\"Entry\"") 
    			|| name.equalsIgnoreCase("\"Exit\""));
    }
    /**
     * Compute the set of nodes that belong to a backward slice, computed from a given
     * node in the program dependence graph.
     *
     * @param node
     * @return
     */
    public Set<Node> backwardSlice(Node node){
        Set<Node> sliceNodes = new HashSet<Node>();
        
        // check if the program dependence graph is computed, else compute it
        if (programDependenceGraph == null) {
        	computeResult();
        }
        
        // add the original node to the slice
        //sliceNodes.add(node);
        
        // traverse through node and find if any of the nodes contain the original node as part
        // of transitive successors add it to the slice
        for (Node n : programDependenceGraph.getNodes()) {
        	if (n != node && programDependenceGraph.getTransitiveSuccessors(n).contains(node)) {
        		sliceNodes.add(n);
        	}
        }
        return sliceNodes;
    }

    /**
     * Compute the Tightness slice-based metric. The proportion of nodes in a control flow graph that occur
     * in every possible slice of that control flow graph.
     * @return
     */
    public double computeTightness(){
    	ArrayList<Set<Node>> sliceNodeSet = new ArrayList<Set<Node>>();
    	
    	// check if the program dependence graph is computed, else compute it
    	if (programDependenceGraph == null) {
        	computeResult();
        }
    	
    	// traverse through the program dependence graph
    	for (Node node : programDependenceGraph.getNodes()) {
    		// skip slicing for non-instruction node
    		if (isSkipNode(node)) {
    			continue;
    		}
    		
    		// generate backward slice and add it to the Slices set
    		Set<Node> slice = backwardSlice(node);
    		sliceNodeSet.add(slice);
        }
    	
    	// find common node set which intersection all the slices computed
    	double tightness = 0.0;    	
    	if (sliceNodeSet.size() > 0) {
	    	Set<Node> commonNodeSet = sliceNodeSet.get(0);
	    	for (int index = 1; index < sliceNodeSet.size(); index++) {
	    		commonNodeSet.retainAll(sliceNodeSet.get(index));
	    	}
	    	tightness = commonNodeSet.size() / (double) (programDependenceGraph.getNodes().size());
    	}
    	
        return tightness;
    }

    /**
     * Compute the Overlap slice-based metric: How many statements per output-slice are unique to that slice?
     * Output slices are computed by computing backward slices from the set of nodes with incoming data dependencies,
     * but with no outgoing data dependencies.
     * @return
     */
    public double computeOverlap(){    	
    	ArrayList<Set<Node>> slices = new ArrayList<Set<Node>>();
    	
    	// check if the data dependence graph is computed, else compute it
    	if (programDependenceGraph == null) {
        	computeResult();
        }
    	
    	// traverse through the data dependence graph
        for (Node node : dataDependenceGraph.getNodes()) {
        	// skip slicing for non-instruction node
        	if (isSkipNode(node)) {
        		continue;
        	}
        	
        	// check if the node with input edges but no output edges
        	if (dataDependenceGraph.getPredecessors(node).size() > 0
        			&& dataDependenceGraph.getSuccessors(node).size() == 0) {
        		// generate backward slice and add it to the Slices set
        		slices.add(backwardSlice(node));
        	}
        }
        
        // find common node set which intersection all the slices computed
        double overlap = 0.0;
        if (slices.size() > 0) {
	        Set<Node> commonNodeSet = slices.get(0);
	    	for (int index = 1; index < slices.size(); index++) {
	    		commonNodeSet.retainAll(slices.get(index));
	    	}
	    	overlap = commonNodeSet.size()/(double) slices.size();
        }        
        
    	return overlap;
    }
}
