package sequenceplanner.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sequenceplanner.model.data.Data;

/**
 *
 * @author Erik Ohlson
 */
public class TreeNodeTest  {

    public TreeNodeTest() {
    }


    
    @Test
    public void dummy() {
    	
    }
    
    /**
     * Test of equals method, of class TreeNode.
     */
    public void equalsCheck() {
        System.out.println("\nTesting TreeNode.equals with simple objects");

        //Create two simple objects for testing
        TreeNode testOne = new TreeNode(new Data("Test", 1) );
        TreeNode testTwo = new TreeNode(new Data("Test", 1) );

        //Test that a simple object equals itself.
        assertTrue(testOne.equals(testOne));
        assertTrue(testTwo.equals(testTwo));

        // Create to objects simple that should be the same               
        assertTrue(testOne.equals(testTwo));
        assertTrue(testTwo.equals(testOne));

        //-------------------------------------------------------

        System.out.println("Testing TreeNode.equals with complex objects");
        
        //Complex node 1
        TreeNode child1One = new TreeNode(new Data("Child1", 2));
        TreeNode child2One = new TreeNode(new Data("Child2", 3));
        TreeNode child3One = new TreeNode(new Data("Child3", 4));
        testOne.insert(child1One);
        testOne.insert(child2One);
        testOne.insert(child3One);

        TreeNode child12One = new TreeNode(new Data("Child2Child1", 5) );
        child2One.insert(child12One);

        //Complex node 2
        TreeNode child1Two = new TreeNode(new Data("Child1", 2));
        TreeNode child2Two = new TreeNode(new Data("Child2", 3));
        TreeNode child3Two = new TreeNode(new Data("Child3", 4));
        testTwo.insert(child1Two);
        testTwo.insert(child2Two);
        testTwo.insert(child3Two);

        TreeNode child12Two = new TreeNode(new Data("Child2Child1", 5) );
        child2Two.insert(child12Two);

        //Test that a complex object equals itself.
        assertTrue(testOne.equals(testOne));
        assertTrue(testTwo.equals(testTwo));

        // Create to complex simple that should be the same
        assertTrue(testOne.equals(testTwo));
        assertTrue(testTwo.equals(testOne));


        // --------------------------------------
        System.out.println("Testing TreeNode.equals (not Equal) with complex objects");

        child2One.getNodeData().setId(10);
        
        // Check that to not equal objects return false
        assertFalse(testOne.equals(testTwo));
        assertFalse(testTwo.equals(testOne));

        // Test that it return false with different parents
        child2One.getNodeData().setId(1);
        child12One.remove(child12One);


        // Check that to not equal objects return false
        assertFalse(testOne.equals(testTwo));
        assertFalse(testTwo.equals(testOne));


    }

}