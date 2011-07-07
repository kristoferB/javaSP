package sequenceplanner.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.graphextension.Cell;

/**
 *
 * @author Erik Ohlson
 */
public class Actions {

    protected static AbstractView getAbstractView(ActionEvent e) {
        return (AbstractView) e.getSource();
    }

    /**
     *
     */
    public static class InsertVariable implements ActionListener {

        private int child = -1;
        private TreeNode parent = null;

        public InsertVariable(TreeNode parent, int type) {
            this.parent = parent;
            this.child = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Model m = getAbstractView(e).getModel();

            m.insertChild(parent, m.getChild(child));
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }
    }

    public static class InsertOperation implements ActionListener {

        private int child = -1;
        private TreeNode parent = null;

        public InsertOperation(TreeNode parent, int type) {
            this.parent = parent;
            this.child = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Model m = getAbstractView(e).getModel();
            int id = Model.newId();
            OperationData opData = new OperationData("OP " + id, id);
            TreeNode[] toAdd = new TreeNode[1];
            toAdd[0] = new TreeNode(opData);
            m.saveOperationData(toAdd);
        }
    }
    public static class DeleteOperation implements ActionListener {
        private TreeNode toRemove = null;
        public DeleteOperation(TreeNode toRemove){
            System.out.println("Deleeeeete");
            this.toRemove = toRemove;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            Model m = getAbstractView(e).getModel();
            //Delete from Tree
            m.removeChild(toRemove.getParent(), toRemove);
            //Delete from view
            //Does not work yet, the removeCells method doesn't seem to work
            toRemove.getNodeData();
            
            LinkedList<OperationView> list = m.getGUIModel().getOperationViews();

            Cell cell = list.getFirst().nodeToCell(toRemove);
            Cell [] cells = {cell};
            list.getFirst().getGraph().removeCells(cells);
        }
    }
    /**
     *
     */
    public static class RemoveNode implements ActionListener {

        private TreeNode toRemove = null;

        public RemoveNode(TreeNode toRemove) {
            this.toRemove = toRemove;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Model m = getAbstractView(e).getModel();
            m.removeChild(toRemove.getParent(), toRemove);
        }
    }

    /**
     *
     */
    public static class OpenResourceView implements ActionListener {

        private TreeNode root = null;

        public OpenResourceView(TreeNode root) {
            this.root = root;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // getAbstractView(e).getSPContainer().createResourceView(root);
        }
    }

    //TODO : get this working again!
    public static class OpenOperationsRealizedBy implements ActionListener {

        TreeNode node;

        public OpenOperationsRealizedBy(TreeNode node) {
            this.node = node;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /* OperationView v = getAbstractView(e).getSPContainer().createOperationView(node.getNodeData().getName());
            Model model = getAbstractView(e).getModel();
            v.open(model.getOperationRealizedBy(node.getId()));*/
        }
    }

    public static class ExitApplication implements ActionListener {

        public ExitApplication() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
