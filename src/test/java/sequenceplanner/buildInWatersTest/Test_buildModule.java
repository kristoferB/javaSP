package sequenceplanner.buildInWatersTest;

import java.io.File;
import java.util.Collections;
import java.util.List;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.expr.BinaryOperator;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.model.module.EventDeclProxy;
import net.sourceforge.waters.subject.module.BinaryExpressionSubject;
import net.sourceforge.waters.subject.module.GraphSubject;
import net.sourceforge.waters.subject.module.IdentifierSubject;
import net.sourceforge.waters.subject.module.IntConstantSubject;
import net.sourceforge.waters.subject.module.LabelBlockSubject;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import net.sourceforge.waters.subject.module.NodeSubject;
import net.sourceforge.waters.subject.module.SimpleComponentSubject;
import net.sourceforge.waters.subject.module.SimpleIdentifierSubject;
import net.sourceforge.waters.subject.module.VariableComponentSubject;
import net.sourceforge.waters.subject.module.VariableMarkingSubject;
import net.sourceforge.waters.xsd.base.ComponentKind;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author patrik
 */
public class Test_buildModule {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void Test1() {
        ModuleSubjectFactory factory = ModuleSubjectFactory.getInstance();
        ModuleSubject ms = factory.createModuleProxy("Module1", null);

        //To create an automaton:
        SimpleIdentifierSubject sis = new SimpleIdentifierSubject("auto2");
        SimpleComponentSubject automaton = new SimpleComponentSubject(sis, ComponentKind.PLANT, new GraphSubject());
        ms.getComponentListModifiable().add(automaton);

        //create a location
        NodeSubject ns = factory.createSimpleNodeProxy("S1");
        automaton.getGraph().getNodesModifiable().add(ns);

        //create a self loop
        LabelBlockSubject lbs = factory.createLabelBlockProxy();
        lbs.getEventListModifiable();
        

        //To create a variable:
        IdentifierSubject var = new SimpleIdentifierSubject("Var1");

        IntConstantSubject lowerBound = factory.createIntConstantProxy(0);
        IntConstantSubject upperBound = factory.createIntConstantProxy(5);
        BinaryOperator bo = CompilerOperatorTable.getInstance().getRangeOperator();
        BinaryExpressionSubject bes = new BinaryExpressionSubject(bo, lowerBound, upperBound);

        IntConstantSubject startValue = factory.createIntConstantProxy(3);
        BinaryOperator boStart = CompilerOperatorTable.getInstance().getEqualsOperator();
        BinaryExpressionSubject besStart = new BinaryExpressionSubject(boStart, var.clone(), startValue);

        final List<VariableMarkingSubject> markings;
        final IdentifierSubject accepting =
                factory.createSimpleIdentifierProxy(EventDeclProxy.DEFAULT_MARKING_NAME);
        final BinaryOperator or = CompilerOperatorTable.getInstance().getOrOperator();
        final BinaryOperator boM = CompilerOperatorTable.getInstance().getEqualsOperator();
        final IntConstantSubject markedval =
                factory.createIntConstantProxy(4);
        final BinaryExpressionSubject left1 =
                factory.createBinaryExpressionProxy(boM, var.clone(), markedval);
        final IntConstantSubject markedval2 =
                factory.createIntConstantProxy(3);
        final BinaryExpressionSubject left2 =
                factory.createBinaryExpressionProxy(boM, var.clone(), markedval2);
        final BinaryExpressionSubject left =
                factory.createBinaryExpressionProxy(or, left1, left2);
        final IntConstantSubject markedval1 =
                factory.createIntConstantProxy(5);
        final BinaryExpressionSubject right =
                factory.createBinaryExpressionProxy(boM, var.clone(), markedval1);
        final BinaryExpressionSubject expr =
                factory.createBinaryExpressionProxy(or, left, right);
        final VariableMarkingSubject marking =
                factory.createVariableMarkingProxy(accepting, expr);
        markings = Collections.singletonList(marking);

        VariableComponentSubject variable = new VariableComponentSubject(var, bes, true, besStart, markings);
        ms.getComponentListModifiable().add(variable);


        assertTrue(saveToWMODFile("C:\\Users\\patrik\\Desktop\\buildWaterModule.wmod", ms));

    }

    public static boolean saveToWMODFile(final String iFilePath, final ModuleSubject iModuleSubject) {
        try {
            final File file = new File(iFilePath);
//            iModuleSubject.setName(file.getName().replaceAll(".wmod", ""));
            final ModuleSubjectFactory factory = ModuleSubjectFactory.getInstance();
            // Save module to file
            JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(factory, CompilerOperatorTable.getInstance());
            marshaller.marshal(iModuleSubject, file);

            return true;
        } catch (Exception t) {
            t.printStackTrace();
        }
        return false;
    }
}
