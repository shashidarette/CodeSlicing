package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class PostDominatorTree extends Analysis {

    public PostDominatorTree(ClassNode cn, MethodNode mn) {
        super(cn, mn);
    }

    /**
     * Return a graph representing the post-dominator tree of the control
     * flow graph, which is stored in the controlFlowGraph class attribute
     * (this is inherited from the Analysis class).
     * @return
     */
    public Graph computeResult() {
        Graph cfg = reverseGraph(controlFlowGraph);
        Graph dominanceTree = computePostDominanceTree(cfg);
        return dominanceTree;
    }

    protected Graph computePostDominanceTree(Graph cfg) {
        Map<Node,Collection<Node>> pDom = calculatePostDominance(cfg,new HashMap<Node,Collection<Node>>());

        Graph dominanceTree = new Graph();
        dominanceTree.addNode(cfg.getEntry());
        Map<Node,Collection<Node>> mapCopy = new HashMap<Node,Collection<Node>>();
        mapCopy.putAll(pDom);
        Iterator<Node> keyIt = mapCopy.keySet().iterator();
        while(keyIt.hasNext()){
            Node next = keyIt.next();
            mapCopy.get(next).remove(next);
        }
        Queue<Node> nodeQueue = new LinkedList<Node>();
        nodeQueue.add(cfg.getEntry());
        while(!nodeQueue.isEmpty()) {
            Node m = nodeQueue.remove();
            Iterator<Node> nodeIterator = mapCopy.keySet().iterator();
            while (nodeIterator.hasNext()) {
                Node n = nodeIterator.next();
                Collection<Node> doms = mapCopy.get(n);
                if (doms.contains(m)) {
                    doms.remove(m);
                    if (doms.isEmpty()) {
                        dominanceTree.addNode(n);
                        dominanceTree.addEdge(m, n);
                        nodeQueue.add(n);
                    }
                }

            }
        }
        return dominanceTree;
    }

    /**
     * The dominance computation function.
     *
     * @param map
     * @return
     */
    private Map<Node, Collection<Node>> calculatePostDominance(Graph cfg, Map<Node, Collection<Node>> map){
        Node entry = cfg.getEntry();
        HashSet<Node> entryDom = new HashSet<Node>();
        entryDom.add(entry);
        map.put(entry, entryDom);
        for(Node n: cfg.getNodes()){
            if(n.equals(entry))
                continue;
            HashSet<Node> allNodes = new HashSet<Node>();
            allNodes.addAll(cfg.getNodes());
            map.put(n, allNodes);
        }
        boolean changed = true;
        while(changed){
            changed = false;
            for(Node n: cfg.getNodes()){
                if(n.equals(entry))
                    continue;
                Collection<Node> currentDominators = map.get(n);
                Collection<Node> newDominators = calculateDominators(cfg, map,n);

                if(!currentDominators.equals(newDominators)){
                    changed = true;
                    map.put(n, newDominators);
                    break;
                }
            }
        }
        return map;
    }

    /**
     * Computes the intersection for a given set of sets of nodes (representing
     * the sets of dominators).
     * @param cfg
     * @param dominate
     * @param n
     * @return
     */
    private static Set<Node> calculateDominators(Graph cfg, Map<Node,Collection<Node>> dominate, Node n) {
        Set<Node> doms = new HashSet<Node>();
        doms.add(n);
        Iterator<Node> predIt = cfg.getPredecessors(n).iterator();
        Set<Node> intersection = new HashSet<Node>();
        if(!predIt.hasNext())
            return new HashSet<Node>();
        boolean firstTime = true;
        while(predIt.hasNext()){
            Node pred = predIt.next();
            Collection<Node> pDoms = dominate.get(pred);
            if(firstTime){
                intersection.addAll(pDoms);
                firstTime = false;
            }
            else{
                intersection.retainAll(pDoms);
            }
        }
        intersection.addAll(doms);
        return intersection;
    }

        /**
         * Produce a new Graph object, representing the reverse of the
         * Graph given in the cfg parameter.
         * @param cfg
         * @return
         */
        protected Graph reverseGraph(Graph cfg){
            Graph reverseCFG = new Graph();
            Iterator<Node> cfgIt = cfg.getNodes().iterator();
            while(cfgIt.hasNext()){
                reverseCFG.addNode(cfgIt.next());
            }
            cfgIt = cfg.getNodes().iterator();
            while(cfgIt.hasNext()){
                Node n = cfgIt.next();
                Set<Node> successors = cfg.getSuccessors(n);
                for (Node succ : successors) {
                    reverseCFG.addEdge(succ, n);
                }
            }
            return reverseCFG;
        }
}
