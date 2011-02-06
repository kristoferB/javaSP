/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.awt.event.MouseAdapter;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Evelina
 */
public class GlobPropV extends JPanel{
    
    private GlobPropM model;
    private JTree tree;

    GlobPropV(GlobPropM m){
        model = m;

        tree = new JTree(m.getGlobalProperties());
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        add(tree);
    }

    //Methods for notifying listeners of change..

    public JTree getTree(){
        return tree;
    }

    void addMouseListener(MouseAdapter m){
        tree.addMouseListener(m);
    }

}
