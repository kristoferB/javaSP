/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

import sequenceplanner.efaconverter2.SpEFA.SpEFAutomata;
import sequenceplanner.efaconverter2.SpEFA.SpVariable;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author Mohammad Reza
 */
public interface IModelParser {
    
    public SpEFAutomata getSpEFAutomata();
    
    public SpEFAutomata getSpEFA(TreeNode iOperation);
    
    public SpVariable getSpVariable(TreeNode iVariable);
    
}
