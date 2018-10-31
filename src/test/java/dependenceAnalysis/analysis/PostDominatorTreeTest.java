package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.InputStream;
import java.util.List;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class PostDominatorTreeTest {

    @Test
    public void computePostDom_AreaEquals() throws Exception {

        //Pick suitable ClassNode and MethodNode as test subjects.
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        InputStream in=CFGExtractor.class.getResourceAsStream("/java/awt/geom/Area.class");
        ClassReader classReader=new ClassReader(in);
        classReader.accept(cn, 0);

        MethodNode target = null;
        for(MethodNode mn : (List<MethodNode>)cn.methods){
            if(mn.name.equals("equals")) {//let's pick out the "equals" method as our subject
                target = mn;
            }
        }

        //Run the post dominator tree generation code.
        PostDominatorTree pdt = new PostDominatorTree(cn,target);
        Graph tree = pdt.computeResult();

        //Print results for inspection (best visualised using GraphViz).
        System.out.println("ORIGINAL CFG: \n"+pdt.getControlFlowGraph()
                +"\n\nPOST-DOMINATOR TREE:\n"+tree);
    }

    /**
     * Original testing challenge as given on Blackboard:
     *
     * 1:  Pass = 0 ;
     * 2:  Fail = 0 ;
     * 3:  Count = 0 ;
     * 4:  while (!eof()) {
     * 5:  TotalMarks=0;
     * 6:  scanf("%d",Marks);
     * 7:  if (Marks >= 40)
     * 8:  Pass = Pass + 1;
     * 9:  if (Marks < 40)
     * 10: Fail = Fail + 1;
     * 11: Count = Count + 1;
     * 12: TotalMarks = TotalMarks+Marks ;
     * 13: }
     * 14: printf("Out of %d, %d passed and %d failed\n",Count,Pass,Fail) ;
     * 15: average = TotalMarks/Count;
     * 16: // This is the point of interest
     * 17: printf("The average was %d\n",average) ;
     * 18: PassRate = Pass/Count*100 ;
     * 19: printf("This is a pass rate of %d\n",PassRate) ;
     *
     * @throws Exception
     */
    @Test
    public void computePostDom_Slicing_Challenge() throws Exception {

        Graph cfg = slicingChallengeCFG();


        //Run the post dominator tree generation code.
        PostDominatorTree pdt = new PostDominatorTree(null,null);
        Graph tree = pdt.computePostDominanceTree(pdt.reverseGraph(cfg));

        //Print results for inspection (best visualised using GraphViz).
        System.out.println("ORIGINAL CFG: \n"+cfg
                +"\n\nPOST-DOMINATOR TREE:\n"+tree);
    }

    private Graph slicingChallengeCFG() {
        Graph g = new Graph();
        Node n1 = new Node("1:  Pass = 0");
        Node n2 = new Node("2:  Fail = 0");
        Node n3 = new Node("3:  Count = 0");
        Node n4 = new Node("4:  while (!eof()) {");
        Node n5 = new Node("5:  TotalMarks=0");
        Node n6 = new Node("6:  scanf(\\\"%d\\\",Marks)");
        Node n7 = new Node("7:  if (Marks >= 40)");
        Node n8 = new Node("8:  Pass = Pass + 1");
        Node n9 = new Node("9:  if (Marks < 40)");
        Node n10 = new Node("10: Fail = Fail + 1");
        Node n11 = new Node("11: Count = Count + 1");
        Node n12 = new Node("12: TotalMarks = TotalMarks+Marks");
        Node n14 = new Node("14: printf(\\\"Out of %d, %d passed and %d failed\\n\\\",Count,Pass,Fail)");
        Node n15 = new Node("15: average = TotalMarks/Count");
        Node n17 = new Node("17: printf(\\\"The average was %d\\n\\\",average)");
        Node n18 = new Node("18: PassRate = Pass/Count*100");
        Node n19 = new Node("19: printf(\\\"This is a pass rate of %d\\n\\\",PassRate)");

        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        g.addNode(n7);
        g.addNode(n8);
        g.addNode(n9);
        g.addNode(n10);
        g.addNode(n11);
        g.addNode(n12);
        g.addNode(n14);
        g.addNode(n15);
        g.addNode(n17);
        g.addNode(n18);
        g.addNode(n19);

        g.addEdge(n1,n2);
        g.addEdge(n2,n3);
        g.addEdge(n3,n4);
        g.addEdge(n4,n5);
        g.addEdge(n4,n14);
        g.addEdge(n5,n6);
        g.addEdge(n6,n7);
        g.addEdge(n7,n8);
        g.addEdge(n7,n9);
        g.addEdge(n8,n9);
        g.addEdge(n9,n10);
        g.addEdge(n10,n11);
        g.addEdge(n9,n11);
        g.addEdge(n11,n12);
        g.addEdge(n12,n4);
        g.addEdge(n14,n15);
        g.addEdge(n15,n17);
        g.addEdge(n17,n18);
        g.addEdge(n18,n19);

        return g;
    }


}