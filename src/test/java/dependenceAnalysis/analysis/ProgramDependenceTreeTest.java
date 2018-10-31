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

public class ProgramDependenceTreeTest {
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
	        	if(mn.name.equals("equals")) {//let's pick out the "equals" method as our subject
	                target = mn;
	            }
	        }
		ProgramDependenceGraph pdg = new ProgramDependenceGraph(cn, target);
		
		System.out.println("=== PDG ===");
		System.out.println(pdg.computeResult());
		System.out.println("=== Tightness ===");
		System.out.println(pdg.computeTightness());
		System.out.println("=== Overlap ===");
		System.out.println(pdg.computeOverlap());
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
			ProgramDependenceGraph pdg = new ProgramDependenceGraph(cn, mn);
			System.out.println("=== PDG : " + mn.name + " ===");
			System.out.println(pdg.computeResult());
			System.out.println("=== Tightness ===");
			System.out.println(pdg.computeTightness());
			System.out.println("=== Overlap ===");
			System.out.println(pdg.computeOverlap());
		}
	}
}
