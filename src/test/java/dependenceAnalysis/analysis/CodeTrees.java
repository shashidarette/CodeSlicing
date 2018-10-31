package dependenceAnalysis.analysis;

import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;

/**
 * This class is used as the base for the tests for code trees generated based on various sources.
 * To keep the test simplified, the controlFlowGraph had to be made public and non-final. 
 * But can use other mock test frameworks to avoid it.
 * 
 * @author Shashidar Ette : se146
 *
 */
public class CodeTrees {
	/*
	 *  Ref: Dominance and Slicing pdf - Software System Reengineering (CO7206 / CO7506)
	 *  Neil Walkinshaw 
	 *  The University of Leicester
		1 PRINT TAB(32);"3D PLOT"
		2 PRINT TAB(15);"CREATIVE COMPUTING"
		3 PRINT:PRINT:PRINT
		5 DEF FNA(Z)=30*EXP(-Z*Z/100)
		100 PRINT
		110 FOR X=-30 TO 30 STEP 1.5
		120 L=0
		130 Y1=5*INT(SQR(900-X*X)/5)
		140 FOR Y=Y1 TO -Y1 STEP -5
		150
		Z=INT(25+FNA(SQR(X*X+Y*Y))-.7*Y)
		160 IF Z<=L THEN 190
		170 L=Z
		180 PRINT TAB(Z);"*";
		190 NEXT Y
		200 PRINT
		210 NEXT X
		300 END
	 * 
	 */
	public static Graph getLecturePptTree() {
		Graph g = new Graph();
		Node entry = new Node("Entry");
        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        Node n5 = new Node("5");
        Node n100 = new Node("100");
        Node n110 = new Node("110");
        Node n120 = new Node("120");
        Node n130 = new Node("130");
        Node n140 = new Node("140");
        Node n150 = new Node("150");
        Node n160 = new Node("160");
        Node n170 = new Node("170");
        Node n180 = new Node("180");
        Node n190 = new Node("190");
        Node n200 = new Node("200");
        Node n210 = new Node("210");
        Node n300 = new Node("300");
        Node exit = new Node("Exit");
        
        g.addNode(entry); g.addNode(exit);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n5);
        g.addNode(n100);
        g.addNode(n110);
        g.addNode(n120);
        g.addNode(n130);
        g.addNode(n140);
        g.addNode(n150);
        g.addNode(n160);
        g.addNode(n170);
        g.addNode(n180);
        g.addNode(n190);
        g.addNode(n200);
        g.addNode(n210);
        g.addNode(n300);
        
        g.addEdge(entry, n1);
        g.addEdge(n1, n2); g.addEdge(n2, n3);g.addEdge(n3, n5);g.addEdge(n5, n100);g.addEdge(n100, n110);
        
        g.addEdge(n110, n120); g.addEdge(n110, n300);
        
        g.addEdge(n120, n130); g.addEdge(n130, n140);  g.addEdge(n140, n150);
        g.addEdge(n150, n160); g.addEdge(n160, n170); g.addEdge(n170, n180); g.addEdge(n180, n190);
        
        g.addEdge(n160, n190); g.addEdge(n190, n140);
        
        g.addEdge(n140, n200); g.addEdge(n200, n210); g.addEdge(n210, n110);        
        g.addEdge(n300, exit);
        
        return g;
	}
	
	/**
	 * Graph for Bounce.BAS code 
	 * Ref: Walkinshaw, N. - Reverse-Engineering Software Behaviour, Advances inComputers - volume 91. 
	 */
	public static Graph getBounceBASTree() {
		Graph g = new Graph();
		
		Node[] n = new Node[37];
		for (int index = 0; index < 37; index++) {
			n[index] = new Node("node" + (index +1));
			g.addNode(n[index]);
		}
		
		Node entry = new Node("Entry");
		g.addNode(entry);
		
		Node exit = new Node("Exit");
		g.addNode(exit);
		
		g.addEdge(entry, n[0]);
		// 1-7
		g.addEdge(n[0], n[1]);
		g.addEdge(n[1], n[2]);
		g.addEdge(n[2], n[3]);
		g.addEdge(n[3], n[4]);
		g.addEdge(n[4], n[5]);
		g.addEdge(n[5], n[6]);
		
		// 7-8-9 branch
		g.addEdge(n[6], n[7]); g.addEdge(n[7], n[8]); g.addEdge(n[8], n[6]);
		
		// 7-10 branch
		g.addEdge(n[6], n[9]);
		
		// 10-T branch
		g.addEdge(n[9], n[10]); g.addEdge(n[10], n[11]); g.addEdge(n[11], n[12]); g.addEdge(n[12], n[13]);
		// 11-T
		g.addEdge(n[10], n[12]);
		
		// 14-T Branch
		g.addEdge(n[13], n[14]);
		
		// 15-T Branch
		g.addEdge(n[14], n[15]);
		g.addEdge(n[15], n[16]);
		g.addEdge(n[16], n[17]);
		g.addEdge(n[17], n[18]);
		g.addEdge(n[18], n[14]);
		g.addEdge(n[16], n[18]);
		
		// 15-F Branch
		g.addEdge(n[14], n[19]);
		g.addEdge(n[19], n[20]);
		g.addEdge(n[20], n[21]);
		g.addEdge(n[21], n[13]);
		
		// 21-T Branch
		g.addEdge(n[20], n[22]);
		
		// 14-F Branch
		g.addEdge(n[13], n[22]);
		g.addEdge(n[22], n[23]);
		g.addEdge(n[23], n[9]);
		
		// 10-F branch
		g.addEdge(n[9], n[24]); g.addEdge(n[24], n[25]); 
		
		// 26 - T Branch
		g.addEdge(n[25], n[26]);  g.addEdge(n[26], n[27]); g.addEdge(n[27], n[25]);
 
		// 26 - F Branch
		g.addEdge(n[25], n[28]); g.addEdge(n[28], n[29]); g.addEdge(n[29], n[30]);
		
		// 31 - T branch
		g.addEdge(n[30], n[31]); g.addEdge(n[31], n[32]); g.addEdge(n[32], n[30]);
		// 31 - F branch
		g.addEdge(n[30], n[33]); g.addEdge(n[33], n[34]); g.addEdge(n[34], n[35]); g.addEdge(n[35], n[36]); g.addEdge(n[36], exit);
		
		return g;
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
	public static Graph getSliceChallengeTree() {
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
		
	public static Graph getCodeTree4() {
		Graph g = new Graph();
		Node entry = new Node("Entry");
        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        Node n4 = new Node("4");
        Node n5 = new Node("5");
        Node n6 = new Node("6");
        Node exit = new Node("Exit");
        
        g.addNode(entry); g.addNode(exit);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        
        g.addEdge(entry, n1);
        g.addEdge(n1, n2); g.addEdge(n1, n4);g.addEdge(n2, n3);g.addEdge(n2, n5);g.addEdge(n4, n5);
        
        g.addEdge(n5, n6);       
        g.addEdge(n3, exit);
        g.addEdge(n6, exit);
        return g;
	}
}
