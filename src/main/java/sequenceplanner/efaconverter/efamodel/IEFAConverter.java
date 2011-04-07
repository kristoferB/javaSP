/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efaconverter.efamodel;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author shoaei
 */
public interface IEFAConverter {

    public IEFAutomata getEFAutomata(Model iModel);
    public IEFAutomata getEFAutomata(TreeNode iOperation);
    public IEFAutomaton getProjectEFAutomaton(Model model);

}
