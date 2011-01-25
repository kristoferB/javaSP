/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Evelina
 */
public class GlobPropV extends JPanel{
    
    private GlobPropM model;
    private JTree tree;

    private JTextField propertyInput = new JTextField(20);
    private JTextField valueInput = new JTextField(20);
    private JButton    addProperty = new JButton("Add");

    GlobPropV(GlobPropM m){
        model = m;
/* test input
        setLayout(new FlowLayout());
        add(new JLabel("Property"));
        add(propertyInput);
        add(new JLabel("Value"));
        add(valueInput);
        add(addProperty);
 *
 */
        tree = new JTree(m.getGlobalProperties());
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        add(tree);
    }

    public void createNodes(DefaultMutableTreeNode node){

    }

    public String getPropertyInput() {
        return propertyInput.getText();
    }

    public String getValueInput() {
        return valueInput.getText();
    }

    void addAddPropertyListener(ActionListener add) {
        addProperty.addActionListener(add);
    }

    void addEditorTreeModelListener(TreeModelListener t){
        tree.getModel().addTreeModelListener(t);
    }

}
