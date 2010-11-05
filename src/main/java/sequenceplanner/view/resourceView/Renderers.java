package sequenceplanner.view.resourceView;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.model.data.ResourceVariableData;

/**
 *
 * @author projekt2
 */
public class Renderers {

    static abstract class TreeRenderer extends JPanel {

        protected ResourceView view;
        protected TreeNode node;

        public TreeRenderer(ResourceView view) {
            this.view = view;
        }

        public void setValue(TreeNode node) {
            this.node = node;
        }

        public TreeNode getValue() {
            return node;
        }
    }

    static class ResourcePanel extends TreeRenderer {

        private JLabel description;
        private JLabel name;
        private ResourceData value;

        public ResourcePanel(ResourceView view) {
            super(view);
            name = new JLabel();
            description = new JLabel();

            GroupLayout l = new GroupLayout(this);
            this.setLayout(l);

            l.setHorizontalGroup(l.createSequentialGroup().addComponent(name).addGap(5).addComponent(description).addGap(5));

            l.setVerticalGroup(l.createParallelGroup().addComponent(name).addComponent(description));
        }

        public void setValue(TreeNode input) {
            super.setValue(input);
            this.value = (ResourceData) input.getNodeData();

            name.setText(value.getName());
            description.setText(value.getDescription());
        }
    }

    static class VariablePanel extends TreeRenderer {

        private JLabel name;
        private JLabel type;
        private JLabel constraint;
        private ResourceVariableData value;
        private TitledBorder b;

        public VariablePanel(ResourceView view) {
            super(view);
            name = new JLabel();
            type = new JLabel();
            constraint = new JLabel();



            GroupLayout l = new GroupLayout(this);
            this.setLayout(l);
            l.setHorizontalGroup(l.createSequentialGroup().addGap(3)
                    .addComponent(name).addGap(5).addComponent(type).addGap(10).addComponent(constraint).addGap(5));
            l.setVerticalGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(name).addComponent(type).addComponent(constraint));
        }

        public void setValue(TreeNode input) {
            super.setValue(input);
            this.value = (ResourceVariableData) input.getNodeData();

            String ty;
            String con;
            if (value.getType() == ResourceVariableData.BINARY) {
                ty = "Binary";
                con = "";
            } else {
                ty = "Integer";
                con = value.getMin() + ".." + value.getMax();
            }
            name.setText(value.getName());
            type.setText(ty);
            constraint.setText(con);
        }
    }
}
