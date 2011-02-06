/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 *
 * @author Evelina
 */
public class EditorTreeModelListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            System.out.println("TreeModelEvent is generated");
            if(e.getTreePath().getLastPathComponent() instanceof IGlobalProperty){
                IGlobalProperty gp = (GlobalProperty) e.getTreePath().getLastPathComponent();
                System.out.println("Node changed");
            }

        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println("TreeModelEvent is generated");
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println("TreeModelEvent is generated");
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println("TreeModelEvent is generated");
            throw new UnsupportedOperationException("Not supported yet.");
        }



    }
