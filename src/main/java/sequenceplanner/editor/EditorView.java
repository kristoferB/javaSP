package sequenceplanner.editor;

import java.awt.event.MouseAdapter;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

/**
 * Editor view which shows the global properties
 * 
 * @author Evelina
 */
public class EditorView extends JScrollPane{
    
    private JTree tree;

    EditorView(EditorTreeModel m){

        tree = new JTree(m);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        setViewportView(tree);
    }

    public JTree getTree(){
        return tree;
    }

    void addMouseListener(MouseAdapter m){
        tree.addMouseListener(m);
    }

}
