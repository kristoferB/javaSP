
package sequenceplanner.efaconverter2.SpEFA;

import sequenceplanner.model.TreeNode;

/**
 *
 * @author Mohammad Reza Shoaei
 * @version 21062011
 */

public interface IModelParser {
    
    public SpEFAutomata getSpEFAutomata();
    
    public SpEFA getSpEFA(TreeNode iOperation);
    
    public SpVariable getSpVariable(TreeNode iVariable);
    
}
