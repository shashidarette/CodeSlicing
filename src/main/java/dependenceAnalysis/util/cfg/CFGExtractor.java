package dependenceAnalysis.util.cfg;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFGExtractor {

	/**
	 * Builds the control flow graph for mn.
	 * @param owner
	 * @param mn
	 * @return
	 * @throws AnalyzerException
     */
	public static Graph getCFG(String owner, MethodNode mn)throws AnalyzerException {
		final Graph g = buildGraph(owner, mn);
		Node entry = new Node("Entry");
		Node exit = new Node("Exit");
		g.addNode(entry);
		g.addNode(exit);
		for(Node n: g.getNodes()){
			if(n.toString().equals("\"Exit\"") || n.toString().equals("\"Entry\""))
				continue;
			if(g.getSuccessors(n).isEmpty())
				g.addEdge(n, exit);
			if(g.getPredecessors(n).isEmpty())
				g.addEdge(entry, n);
		}
	return g;
	}

	protected static Graph buildGraph(String owner,
			MethodNode mn) throws AnalyzerException {
		final InsnList instructions = mn.instructions;
		final Map<AbstractInsnNode,Node> nodes = new HashMap<AbstractInsnNode,Node>();
		final Graph g = new Graph();
		Analyzer a =new Analyzer(new BasicInterpreter()) {
			
			
			protected void newControlFlowEdge(int src, int dst) {
				AbstractInsnNode from = instructions.get(src);
                AbstractInsnNode to = instructions.get(dst);
                Node srcNode = nodes.get(from);
                if(srcNode == null){
                	srcNode =  new Node(from);
                	nodes.put(from,srcNode);
                	g.addNode(srcNode);
                }
                Node tgtNode = nodes.get(to);
                if(tgtNode == null){
                	tgtNode = new Node(to);
                	nodes.put(to,tgtNode);
                	g.addNode(tgtNode);
                }
                g.addEdge(srcNode, tgtNode);
			}
		};
		
		a.analyze(owner, mn);
		
		return g;
	}
	
	public static void main(String[] args) throws IOException{
		ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);
        for(MethodNode mn : (List<MethodNode>)cn.methods){
        	try {
        		System.out.println("================CFG FOR: "+cn.name+"."+mn.name+mn.desc+" =================");
        		System.out.println(CFGExtractor.getCFG(cn.name, mn));
			} catch (AnalyzerException e) {
				e.printStackTrace();
			}
        }
	}
}
