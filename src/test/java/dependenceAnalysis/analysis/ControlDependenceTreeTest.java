package dependenceAnalysis.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;

public class ControlDependenceTreeTest {
	/**
	 * Basic test with equals method
	 * @throws IOException
	 */
	@Test
	public void basicTest() throws IOException {
		 ClassNode cn = new ClassNode(Opcodes.ASM4);
	        InputStream in=CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
	        ClassReader classReader= new ClassReader(in);
	        classReader.accept(cn, 0);

	        MethodNode target = null;
	        for(MethodNode mn : (List<MethodNode>)cn.methods){
	        	System.out.println(mn.name);
	            if(mn.name.equals("equals")) {//let's pick out the "equals" method as our subject
	                target = mn;
	            }
	        }
		ControlDependenceTree cdt = new ControlDependenceTree(cn, target);
		System.out.println(cdt.computeResult());
	}
	
	/**
	 * Custom tests with CFG created through CodeTrees class.
	 * NOTE: For these tests, one manual code change is required in Analysis abstract class.
	 * i.e. to change the controlFlowGraph from final to non-final member variable
	 * protected final Graph controlFlowGraph; => protected Graph controlFlowGraph;
	 * @throws IOException
	 */
	@Test
	public void bounceTest() throws IOException {
		ControlDependenceTree cdt = new ControlDependenceTree(null, null);
		
		// NOTE: please read the function description
        //cdt.controlFlowGraph = CodeTrees.getLecturePptTree();
        //cdt.controlFlowGraph = CodeTrees.getBounceBASTree();
        //cdt.controlFlowGraph = CodeTrees.getCodeTree4();
		
		Graph cdGraph = cdt.computeResult();
		System.out.println("=== CD Graph ===");
		System.out.println(cdGraph);
	}
	
	/**
	 * Tests for all the nodes in Area.class
	 * Basically to find out any errors during the load test and fix them
	 * @throws IOException
	 */
	@Test
	public void testAllInArea() throws IOException {
		ClassNode cn = new ClassNode(Opcodes.ASM4);
		InputStream in = CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
		ClassReader classReader = new ClassReader(in);
		classReader.accept(cn, 0);

		for (MethodNode mn : (List<MethodNode>) cn.methods) {
			ControlDependenceTree cdt = new ControlDependenceTree(cn, mn);
			System.out.println("=== CDG : " + mn.name + " ===");
			System.out.println(cdt.computeResult());
		}
	}
}
