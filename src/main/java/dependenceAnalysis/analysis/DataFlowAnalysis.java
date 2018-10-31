package dependenceAnalysis.analysis;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.Collection;

/**
 * Created by neilwalkinshaw on 21/10/2016.
 */
public class DataFlowAnalysis {

    /**
     * Return the collection of variables that are used by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> usedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);
        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getUses();
    }

    /**
     * Return the collection of variables that are defined by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> definedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);

        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getDefinitions();
    }

}
