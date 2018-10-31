package dependenceAnalysis.util.cfg;

/**
 * This class is added to manage edge information, since DefaultEdge class in
 * JGraphT framework cannot give source and target nodes.
 * @author Shashidar Ette : se146
 *
 */
public class GraphEdge {
	// Source node
	private Node source;
	
	// Destination node
	private Node destination;
	
	public GraphEdge(Node src, Node dest) {
		source = src;
		destination = dest;
	}
	
	public Node getSource() {
		return source;
	}
	
	public Node getDestination() {
		return destination;
	}

	// Return the edge information as string Source -> Destination
	@Override 
	public String toString() {
		return  source + "->" + destination;
	}
}
