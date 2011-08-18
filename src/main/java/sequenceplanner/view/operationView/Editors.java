package sequenceplanner.view.operationView;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sequenceplanner.utils.IconHandler;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.OperationData;

import sequenceplanner.view.operationView.graphextension.Cell;

/**
 * @deprecated 
 * @author Erik Ohlson
 */
public class Editors {
//
//   public static final ImageIcon resIcon = IconHandler.getNewIcon("/sequenceplanner/resources/icons/robot.png");
//   public static final ImageIcon productLiason = IconHandler.getNewIcon("/sequenceplanner/resources/icons/op.png");
//
//   static abstract class OperationEditor extends JPanel {
//
//      protected OperationView view;
//      protected String name;
//      protected Cell node;
//
//      public OperationEditor(OperationView view, String name) {
//         this.view = view;
//         this.name = name;
//      }
//
//      /**
//       * Sets value of labels
//       * @param node
//       */
//      public void setValue(Cell node) {
//         this.node = node;
//      }
//
//      public Cell getValue() {
//         return node;
//      }
//
//      public void clear() {
//         this.node = null;
//      }
//
//      @Override
//      public String getName() {
//         return this.name;
//      }
//
//      /**
//       * Saves a node with the values inserted into editor.
//       */
//      abstract protected void saveNode();
//   }
//
//   static class OperationConditionEditor extends OperationEditor {
//
//      private JTextField[] text = new JTextField[3];
//      private JTextField cost;
//      private JCheckBox[] check = new JCheckBox[2];
//      private JTextField description;
//      private JLabel realizedBy;
//      private JLabel accomplishes;
//
//      public OperationConditionEditor(OperationView view) {
//         super(view, "Conditions");
//         initializePanels();
//      }
//
//      protected void initializePanels() {
//         text[0] = new JTextField("Name");
//         text[1] = new JTextField("Start Condition");
//         text[2] = new JTextField("Stop Condition");
//         text[1].setEditable(false);
//         text[2].setEditable(false);
//         description = new JTextField();
//
//         ButtonGroup bg = new ButtonGroup() {
//
//            public void setSelected(ButtonModel m, boolean b) {
//
//               if (m != null && m == getSelection() && !b) {
//                  clearSelection();
//               } else {
//                  super.setSelected(m, b);
//               }
//            }
//         };
//         check[0] = new JCheckBox("preOperation");
//         check[1] = new JCheckBox("postOperation");
//         bg.add(check[0]);
//         bg.add(check[1]);
//
//
//         cost = new JTextField();
//         cost.setColumns(10);
//
//
//         realizedBy = new JLabel();
//         realizedBy.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
//         realizedBy.setIcon(resIcon);
//         realizedBy.setToolTipText("Click to empty");
//         realizedBy.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//               OperationData d = (OperationData) node.getValue();
//               d.setRealizedBy(0);
//               realizedBy.setText(" - ");
//            }
//         });
//
//
//         accomplishes = new JLabel();
//         accomplishes.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
//         accomplishes.setToolTipText("Click to empty");
//         accomplishes.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//               OperationData d = (OperationData) node.getValue();
//               d.setAccomplishes(0);
//               accomplishes.setText(" - ");
//            }
//         });
//
//         JLabel[] label = {new JLabel("Name:"), new JLabel("Precondition:"), new JLabel("Postcondition:"), new JLabel("Cost: "),
//            new JLabel("Realized by:"), new JLabel("Accomplishes:"), new JLabel("Description:")};
//
//
//
//         GroupLayout layout = new GroupLayout(this);
//         setLayout(layout);
//
//         layout.setHorizontalGroup(
//               layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[0]).addComponent(text[0], 0, 200, 200).addGroup(layout.createSequentialGroup().addComponent(check[0]).addGap(5).addComponent(check[1]).addGap(3))).addGap(5).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[1]).addComponent(text[1])).addGap(10).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[2]).addComponent(text[2]))).addGroup(layout.createSequentialGroup().addComponent(label[3]).addComponent(cost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(10).addComponent(label[4]).addComponent(realizedBy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(10).addComponent(label[5]).addComponent(accomplishes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))).addGroup(layout.createSequentialGroup().addComponent(label[6]).addComponent(description)));
//
//         layout.setVerticalGroup(
//               layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(label[0]).addComponent(label[1]).addComponent(label[2])).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(text[0]).addComponent(text[1]).addComponent(text[2])).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(check[0]).addComponent(check[1]).addComponent(label[3]).addComponent(cost).addGap(10).addComponent(label[4]).addComponent(realizedBy).addGap(10).addComponent(label[5]).addComponent(accomplishes).addGap(10)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label[6]).addComponent(description)));
//      }
//
//      @Override
//      public Cell getValue() {
//         return super.getValue();
//      }
//
//      @Override
//      public void clear() {
//         super.clear();
//
//         text[0].setText("");
//         text[1].setText("");
//         text[2].setText("");
//         cost.setText("");
//         realizedBy.setText("");
//         accomplishes.setText("");
//      }
//
//      @Override
//      public void setValue(Cell node) {
//         super.setValue(node);
//         OperationData d = (OperationData) node.getValue();
//         text[0].setText(d.getName());
//         text[1].setText(d.getPrecondition());
//         text[2].setText(d.getPostcondition());
//
//         check[0].setSelected(d.isPreoperation());
//         check[1].setSelected(d.isPostoperation());
//
//         description.setText(d.getDescription());
//
//         cost.setText(Double.toString(d.getCost()));
//
//
//         String[] s = view.getGraphModel().getNameCache().get(d.getRealizedBy());
//         if (s != null) {
//            realizedBy.setText(s[0] + (s[0].isEmpty() ? "" : ".") + s[1]);
//         } else {
//            realizedBy.setText(" - ");
//         }
//
//         s = view.getGraphModel().getNameCache().get(d.getAccomplishes());
//         if (s != null) {
//            accomplishes.setText(s[0] + (s[0].isEmpty() ? "" : ".") + s[1]);
//
//         } else {
//            accomplishes.setText(" - ");
//         }
//      }
//
//      //If the main model is updated, reset editor.
//      @Override
//      protected void saveNode() {
//         //TODO Should be a name check.
//         OperationData d = (OperationData) node.getValue();
//
//
//         String labelName = text[0].getText();
//
//         if (!labelName.equals(d.getName())) {
////            labelName = view.getCellName(labelName, node);
//         }
//         System.out.println("add cat");
//         d.setName(labelName);
//         d.setPreoperation(check[0].isSelected());
//         d.setPostoperation(check[1].isSelected());
//         d.setDescription(description.getText());
//
//         try {
//            d.setCost(Double.parseDouble(cost.getText()));
//         } catch (NumberFormatException e) {
//            System.out.println("Added a none double as cost.");
//         }
//         view.getGraph().setValue(node, d);
//
//      }
//   }
//
//   static class ResetEditor extends OperationEditor {
//
//      private JTextField[] text = new JTextField[3];
//      private JTextField cost;
//      private JCheckBox[] check = new JCheckBox[2];
//
//      public ResetEditor(OperationView view) {
//         super(view, "Conditions");
//         initializePanels();
//      }
//
//      private void initializePanels() {
//         text[0] = new JTextField("Name");
//         text[1] = new JTextField("Start Condition");
//         text[2] = new JTextField("Stop Condition");
//         text[1].setEditable(false);
//         text[2].setEditable(false);
//
//         check[0] = new JCheckBox("preOperation");
//         check[1] = new JCheckBox("postOperation");
//
//         cost = new JTextField();
//         cost.setColumns(10);
//
//
//
//         JLabel[] label = {new JLabel("Name:"), new JLabel("Precondition:"), new JLabel("Postcondition:"), new JLabel("Cost: "),
//            new JLabel("Realized by: "), new JLabel("Accomplishes: ")};
//
//
//
//         GroupLayout layout = new GroupLayout(this);
//         setLayout(layout);
//
//         layout.setHorizontalGroup(
//               layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[0]).addComponent(text[0], 0, 200, 200).addGroup(layout.createSequentialGroup().addComponent(check[0]).addGap(5).addComponent(check[1]).addGap(3))).addGap(5).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[1]).addComponent(text[1]).addGroup(layout.createSequentialGroup().addComponent(label[3]).addComponent(cost).addGap(10))).addGap(5).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label[2]).addComponent(text[2])));
//
//         layout.setVerticalGroup(
//               layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(label[0]).addComponent(label[1]).addComponent(label[2])).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(text[0]).addComponent(text[1]).addComponent(text[2])).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(check[0]).addComponent(check[1]).addComponent(label[3]).addComponent(cost)));
//      }
//
//      @Override
//      public Cell getValue() {
//         return super.getValue();
//      }
//
//      @Override
//      public void setValue(Cell node) {
//         super.setValue(node);
//         OperationData d = (OperationData) node.getValue();
//
//         text[0].setText(d.getName());
//         text[1].setText(d.getPrecondition());
//         text[2].setText(d.getPostcondition());
//
//         check[0].setSelected(d.isPreoperation());
//         check[1].setSelected(d.isPostoperation());
//
//         cost.setText(Double.toString(d.getCost()));
//
//
//      }
//
//      //If the main model is updated, reset editor.
//      @Override
//      protected void saveNode() {
//         //TODO Should be a name check.
//         OperationData d = (OperationData) node.getValue();
//
//
//         String labelName = text[0].getText();
//
//         if (!labelName.equals(d.getName())) {
//            labelName = view.getCellName(labelName, node);
//         }
//
//         d.setName(labelName);
//         d.setPreoperation(check[0].isSelected());
//         d.setPostoperation(check[1].isSelected());
//
//
//         try {
//            double t = Double.parseDouble(cost.getText());
//            d.setCost(t);
//         } catch (Exception e) {
//            cost.setText("ERROR - has to be and double");
//         }
//
//
//
//
//         view.getGraph().setValue(node, d);
//
//      }
//   }
//
//   static class SequenceConditionEditor extends OperationEditor {
//
//      JLabel totalExpression;
//      JList and;
//      JList or;
//      JList resources;
//      JButton addAnd;
//      JButton addOr;
//      JTextField variableValue;
//      JPanel choose;
//      JComboBox variableState;
//      JTextField addOperation;
//      JComboBox operationState;
//      JTextField addResource;
//      JComboBox resourceBooking;
//      int selectedOrId = -1;
//      private boolean preCondition;
//
//      public SequenceConditionEditor(OperationView view, boolean preCondition, String name) {
//         super(view, name);
//         this.preCondition = preCondition;
//
//         initializePanels();
//         initializeListeners();
//      }
//
//      protected void initializePanels() {
//
//         totalExpression = new JLabel();
//         totalExpression.setVisible(true);
//
//         and = new JList();
//         or = new JList();
//
//         resources = new JList(new Object[]{"Hej", "ha"});
//         addAnd = new JButton("Add and");
//
//         addOperation = new JTextField();
//         addOr = new JButton("add");
//         addOr.setMargin(new Insets(0, 0, 0, 0));
//
//
//         variableValue = new JTextField();
//         choose = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//
//
//         Object[] opStates = new Object[]{Model.getOperationEnding(0), Model.getOperationEnding(1), Model.getOperationEnding(2)};
//         operationState = new JComboBox(opStates);
//         operationState.setSelectedIndex(2);
//
//         Object[] variableStates = new Object[]{Model.getVariabelCheck(0),
//                                                Model.getVariabelCheck(1),
//                                                Model.getVariabelCheck(2),
//                                                Model.getVariabelCheck(3),
//                                                Model.getVariabelCheck(4)};
//         variableState = new JComboBox(variableStates);
//         variableState.setSelectedIndex(0);
//
//
//         addOperation.setEditable(false);
//         addResource = new JTextField();
//
//         Object[] resStates = new Object[]{Model.getResourceEnding(0), Model.getResourceEnding(1)};
//         resourceBooking = new JComboBox(resStates);
//         resourceBooking.setSelectedIndex(1);
//
//         GroupLayout layout = new GroupLayout(this);
//         layout.setAutoCreateContainerGaps(true);
//         layout.setAutoCreateGaps(true);
//         this.setLayout(layout);
//
//         layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(totalExpression).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(and, 100, 100, 100).addComponent(addAnd)).addGap(5).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(or).addGroup(layout.createSequentialGroup().addComponent(addOr).addComponent(addOperation).addComponent(choose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(resources).addGroup(layout.createSequentialGroup().addComponent(addResource).addComponent(resourceBooking, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))))));
//
//         layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(totalExpression)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(and).addComponent(or).addComponent(resources)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(addAnd).addComponent(addOr).addComponent(addOperation).addComponent(choose).addComponent(addResource).addComponent(resourceBooking))));
//      }
//
//      protected void initializeListeners() {
//         and.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//         KeyListener del = new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//               if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//                  JList list = (JList) e.getSource();
//                  int i = list.getSelectedIndex();
//                  if (i >= 0) {
//
//                     ((DefaultListModel) list.getModel()).remove(i);
//                  }
//               }
//            }
//         };
//
//
//         and.addListSelectionListener(new ListSelectionListener() {
//
//            public void valueChanged(ListSelectionEvent e) {
//               if (getAnd().getSelectedIndex() >= 0) {
//                  Object o = getAnd().getModel().getElementAt(getAnd().getSelectedIndex());
//
//                  if (o instanceof AndNode) {
//                     AndNode aNode = (AndNode) o;
//
//                     if (getOr().getModel() != aNode.model) {
//                        getOr().setModel(aNode.model);
//                     }
//
//                     addOperation.setEditable(true);
//                  }
//               } else {
//                  addOperation.setEditable(false);
//               }
//            }
//         });
//
//
//
//
//         addAnd.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//               DefaultListModel model = new DefaultListModel();
//               ((DefaultListModel) getAnd().getModel()).addElement(new AndNode(model, ""));
//               update();
//            }
//         });
//
//         or.addKeyListener(del);
//         or.addListSelectionListener(new ListSelectionListener() {
//
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//               if (getOr().getSelectedIndex() >= 0) {
//                  Object o = getOr().getModel().getElementAt(getOr().getSelectedIndex());
//
//                  if (o instanceof OrNode) {
//                     OrNode aNode = (OrNode) o;
//                     addOperation.setText(Integer.toString(aNode.model.id));
//
//                  }
//               } else {
//                  addOperation.setText("");
//               }
//            }
//         });
//
////         addOperation.addKeyListener(new KeyAdapter() {
////
////            @Override
////            public void keyReleased(KeyEvent e) {
////               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
////                  String text = addOperation.getText();
////                  int id = -1;
////                  try {
////                     id = Integer.parseInt(text);
////
////
////                     String[] s = view.getGraphModel().getNameCache().get(id);
//
////
////                     if (s != null && getOr().getModel() instanceof DefaultListModel) {
////                        int state = operationState.getSelectedIndex();
////
////                        OrNode n = new OrNode(new SeqCond(id, state), s[0] + "." + s[1]);
////
////                        boolean present = false;
////                        for (int i = 0; i < getOr().getModel().getSize(); i++) {
////
////                           if (n.equals(getOr().getModel().getElementAt(i) ) ) {
////                              present = true;
////                              break;
////                           }
////
////                        }
////
////                        if (!present) {
////                           ((DefaultListModel) (getOr().getModel())).addElement(n);
////                           update();
////                        } else {
////                           addOperation.setText("Already present");
////                        }
////
////                     } else {
////                        System.out.println("S: " + s + " Model: " + getOr().getModel().getClass().getName());
////                        addOperation.setText("Error");
////                     }
////
////
////                  } catch (NumberFormatException nrE) {
////                     System.out.println("Is not a parsable int");
////                  }
////               }
////
////            }
////         });
//
//         addOperation.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                  String text = addOperation.getText();
//                  int id = -1;
//                  try {
//                     id = Integer.parseInt(text);
//                     String[] s = view.getGraphModel().getNameCache().get(id);
//
//                     TreeNode node = view.getModel().getResource(id);
//
//                     if (node != null && s != null) { //This a resource (hopefully a variable
//                        choose.removeAll();
//                        choose.add(variableState);
//                        variableValue.setColumns(2);
//                        choose.add(variableValue);
//
//
//                        addOperation.setText(".." + s[1]);
//                        selectedOrId = id;
//                        addOperation.setEditable(false);
//                        choose.getParent().validate();
//                     } else if (s != null) {  //This should be an operation
//                        choose.removeAll();
//                        choose.add(operationState);
//
//                        addOperation.setText(".." + s[1]);
//                        selectedOrId = id;
//                        addOperation.setEditable(false);
//                        choose.getParent().validate();
//                     }
//
//
//
//                  } catch (NumberFormatException nrE) {
//                     System.out.println("Is not a parsable int");
//                  }
//               }
//            }
//         });
//
//         addOperation.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//               if (!addOperation.isEditable() && !and.isSelectionEmpty()) {
//                  selectedOrId = -1;
//                  addOperation.setEditable(true);
//                  choose.removeAll();
//                  addOperation.setText("");
//               }
//            }
//         });
//
//         addOr.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//               if (choose.getComponentCount() > 0 && selectedOrId > 0) {
//                  String[] s = view.getGraphModel().getNameCache().get(selectedOrId);
//
//                  try {
//                     if (s != null && getOr().getModel() instanceof DefaultListModel) {
//
//                        OrNode n = null;
//
//                        if (choose.getComponentCount() > 1) { //variable
//                           int state = variableState.getSelectedIndex();
//                           int value = Integer.parseInt(variableValue.getText());
//                           n = new OrNode(new SeqCond(selectedOrId, state, value), s[0] + "." + s[1]);
//
//                        } else { //Operation
//                           int state = operationState.getSelectedIndex();
//                           n = new OrNode(new SeqCond(selectedOrId, state), s[0] + "." + s[1]);
//
//                        }
//                        boolean present = false;
//                        for (int i = 0; i < getOr().getModel().getSize(); i++) {
//
//                           if (n.equals(getOr().getModel().getElementAt(i))) {
//                              present = true;
//                              break;
//                           }
//                        }
//
//                        if (!present) {
//                           ((DefaultListModel) (getOr().getModel())).addElement(n);
//                           update();
//                        } else {
//                           addOperation.setText("Already present");
//                        }
//
//
//                     } else {
//                        System.out.println("S: " + s + " Model: " + getOr().getModel().getClass().getName());
//                        addOperation.setText("Error");
//                     }
//                  } catch (NumberFormatException ev) {
//                     variableValue.setText("E");
//                  }
//               }
//            }
//         });
//
//
//
//
//         addResource.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                  String text = addResource.getText();
//                  int id = -1;
//                  try {
//                     id = Integer.parseInt(text);
//
//
//                     String[] s = view.getGraphModel().getNameCache().get(id);
//
//                     if (s != null && resources.getModel() instanceof DefaultListModel && view.getModel().isResourcePresent(id)) {
//                        int state = resourceBooking.getSelectedIndex();
//                        ResNode n = new ResNode(new Integer[]{id, state}, s[0] + "." + s[1]);
//
//                        boolean present = false;
//
//                        for (int i = 0; i < resources.getModel().getSize(); i++) {
//                           if (n.equals(resources.getModel().getElementAt(i))) {
//                              present = true;
//                           }
//
//                        }
//                        if (!present) {
//                           ((DefaultListModel) (resources.getModel())).addElement(n);
//                           update();
//                        } else {
//                           addResource.setText("Already present");
//                        }
//
//                     } else {
//                        addResource.setText("Error");
//                     }
//
//
//                  } catch (NumberFormatException nrE) {
//                     System.out.println("Is not a parsable int");
//                  }
//               }
//
//            }
//         });
//
//      }
//
//      public JList getAnd() {
//         return and;
//      }
//
//      public JList getOr() {
//         return or;
//      }
//
//      public JList getResources() {
//         return resources;
//      }
//
//      @Override
//      public Cell getValue() {
//         return super.getValue();
//      }
//
//      @Override
//      public void setValue(Cell node) {
//         super.setValue(node);
//         OperationData d = (OperationData) node.getValue();
//
//         DefaultListModel main = new DefaultListModel();
//
//         LinkedList<LinkedList<SeqCond>> sc = preCondition ? d.getSequenceCondition() : d.getPSequenceCondition();
//         LinkedList<Integer[]> resource = preCondition ? d.getResourceBooking() : d.getPResourceBooking();
//
//         for (LinkedList<SeqCond> linkedList : sc) {
//            main.addElement(convertOr(linkedList));
//         }
//
//         if (main.isEmpty()) {
//            DefaultListModel model = new DefaultListModel();
//            main.addElement(new AndNode(model, ""));
//         }
//         getAnd().setModel(main);
//
//
//         convertResource(resource);
//
//         update();
//
//      }
//
//      private void update() {
//
//         DefaultListModel model = (DefaultListModel) getAnd().getModel();
//
//         String label = "";
//
//         for (int i = 0; i < model.size(); i++) {
//            AndNode node = (AndNode) model.elementAt(i);
//            updateAnd(node);
//            label = label.isEmpty() ? "" : label + " " + Constants.AND;
//            label += " ( " + node.representation + " ) ";
//            System.out.println(label);
//            model.set(i, node);
//
//         }
//
//         model = (DefaultListModel) resources.getModel();
//         for (int i = 0; i < model.size(); i++) {
//            ResNode node = (ResNode) model.elementAt(i);
//
//            label = label.isEmpty() ? "" : label + " " + Constants.AND + " ";
//            label += node.representation;
//
//            model.set(i, node);
//
//         }
//
//         totalExpression.setText(label);
//
//         view.getPane().resetToPreferredSizes();
//      }
//
//      private void updateAnd(AndNode node) {
//         DefaultListModel model = node.model;
//
//         String rep = "";
//
//         for (int i = 0; i < model.size(); i++) {
//            OrNode orNode = (OrNode) model.elementAt(i);
//            updateOr(orNode);
//            rep = rep.isEmpty() ? rep : rep + " " + Constants.OR + " ";
//            rep += orNode.representation;
//         }
//         node.representation = rep;
//
//      }
//
//      private void updateOr(OrNode node) {
//         String[] t = view.getGraphModel().getNameCache().get(node.model.id);
//
//         if (t != null && node.model.isOperationCheck()) {
//            node.representation = t[0] + "." + t[1] + Model.getOperationEnding(node.model.state);
//         } else if (t != null && node.model.isVariableCheck()) {
//            node.representation = t[0] + "." + t[1] + " " + Model.getVariabelCheck(node.model.state) + " " + Integer.toString(node.model.value);
//         }
//      }
//
//      private AndNode convertOr(LinkedList<SeqCond> or) {
//
//         DefaultListModel model = new DefaultListModel();
//
//         String rep = "";
//
//         for (SeqCond seqCond : or) {
//            String[] t = view.getGraphModel().getNameCache().get(seqCond.id);
//            OrNode n = new OrNode((SeqCond) seqCond.clone(), t[0] + "." + t[1]);
//            model.addElement(n);
//
//            rep = rep.isEmpty() ? rep : rep + " " + Constants.OR + " ";
//            rep += n.representation;
//         }
//
//         AndNode node = new AndNode(model, rep);
//
//
//
//         return node;
//      }
//
//      private void convertResource(LinkedList<Integer[]> res) {
//
//         DefaultListModel model = new DefaultListModel();
//
//         for (Integer[] info : res) {
//            String[] t = view.getGraphModel().getNameCache().get(info[0]);
//            ResNode n = new ResNode(new Integer[]{info[0], info[1]}, t[0] + "." +
//                  t[1] + Model.getResourceEnding(info[1]));
//            model.addElement(n);
//         }
//
//         resources.setModel(model);
//      }
//
//      @Override
//      public void clear() {
//
//         if (and.getModel() instanceof DefaultListModel) {
//            ((DefaultListModel) and.getModel()).removeAllElements();
//         }
//
//         if (or.getModel() instanceof DefaultListModel) {
//            ((DefaultListModel) or.getModel()).removeAllElements();
//         }
//
//         if (resources.getModel() instanceof DefaultListModel) {
//            ((DefaultListModel) resources.getModel()).removeAllElements();
//         }
//
//         addOperation.setText("");
//         addResource.setText("");
//
//      }
//
//      //If the main model is updated, reset editor.
//      @Override
//      protected void saveNode() {
//         //TODO Should be a name check.
//         OperationData d = (OperationData) node.getValue();
//
//
//         if (preCondition) {
//            d.setSequenceCondition(restoreAnd((DefaultListModel) getAnd().getModel()));
//            d.setResourceBooking(restoreResource((DefaultListModel) resources.getModel()));
//
//         } else {
//            d.setPSequenceCondition(restoreAnd((DefaultListModel) getAnd().getModel()));
//            d.setPResourceBooking(restoreResource((DefaultListModel) resources.getModel()));
//         }
//      }
//
//      protected LinkedList<Integer[]> restoreResource(DefaultListModel model) {
//         LinkedList<Integer[]> list = new LinkedList<Integer[]>();
//
//         for (int i = 0; i < model.size(); i++) {
//            ResNode resNode = (ResNode) model.elementAt(i);
//            list.add(resNode.model);
//         }
//         return list;
//      }
//
//      protected LinkedList<LinkedList<SeqCond>> restoreAnd(DefaultListModel model) {
//         LinkedList<LinkedList<SeqCond>> list = new LinkedList<LinkedList<SeqCond>>();
//
//         for (int i = 0; i < model.size(); i++) {
//            AndNode andNode = (AndNode) model.elementAt(i);
//            list.add(restoreOr(andNode.model));
//         }
//
//         return list;
//      }
//
//      protected LinkedList<SeqCond> restoreOr(DefaultListModel model) {
//         LinkedList<SeqCond> list = new LinkedList<SeqCond>();
//
//         for (int i = 0; i < model.size(); i++) {
//            OrNode resNode = (OrNode) model.elementAt(i);
//            list.add((SeqCond) resNode.model.clone());
//         }
//
//         return list;
//      }
//
//      protected class OrNode {
//
//         public SeqCond model;
//         public String representation;
//
//         public OrNode(SeqCond model, String repr) {
//            this.model = model;
//            this.representation = repr;
//         }
//
//         @Override
//         public boolean equals(Object obj) {
//            if (obj == null) {
//               return false;
//            }
//
//            if (getClass() != obj.getClass()) {
//               return false;
//            }
//
//            final OrNode other = (OrNode) obj;
//            System.out.println(other + " _ " + toString());
//
//            if (!model.equals(other.model)) {
//               return false;
//            }
//
//            return true;
//         }
//
//         @Override
//         public String toString() {
//            return representation;
//         }
//      }
//
//      protected class ResNode {
//
//         public Integer[] model;
//         public String representation;
//
//         public ResNode(Integer[] model, String repr) {
//            this.model = model;
//            this.representation = repr;
//         }
//
//         @Override
//         public boolean equals(Object obj) {
//            if (obj == null) {
//               return false;
//            }
//            if (getClass() != obj.getClass()) {
//               return false;
//            }
//            final ResNode other = (ResNode) obj;
//
//            if (model[0] != other.model[0] || model[1] != other.model[1]) {
//               return false;
//            }
//            return true;
//         }
//
//         @Override
//         public int hashCode() {
//            int hash = 5;
//            hash = 41 * hash + (this.model != null ? this.model.hashCode() : 0);
//            return hash;
//         }
//
//         @Override
//         public String toString() {
//            return representation;
//         }
//      }
//
//      protected class AndNode {
//
//         public DefaultListModel model;
//         public String representation;
//
//         public AndNode(DefaultListModel model, String repr) {
//            this.model = model;
//            this.representation = repr;
//         }
//
//         @Override
//         public String toString() {
//            if (representation == "") {
//               return "Empty";
//            } else {
//               return "Children " + model.getSize();
//            }
//         }
//      }
//   }
//
//   static class Invariant extends OperationEditor {
//
//      public Invariant(OperationView view, String name) {
//         super(view, name);
//      }
//
//      @Override
//      protected void saveNode() {
//         throw new UnsupportedOperationException("Not supported yet.");
//      }
//   }
//
//   static class ActionEditor extends OperationEditor {
//
//      JLabel[] labels = {new JLabel("Actions:"), new JLabel("Add")};
//      JList actions;
//      JTextField addAction;
//      JTextField actionValue;
//      JComboBox actionSetOptions;
//      JButton add;
//      int selectedOrId = -1;
//
//      public ActionEditor(OperationView view, String name) {
//         super(view, name);
//
//         addAction = new JTextField();
//         actionValue = new JTextField();
//
//         Object[] resStates = new Object[]{Model.getActionSetType(OperationData.ACTION_ADD),
//            Model.getActionSetType(OperationData.ACTION_DEC),
//            Model.getActionSetType(OperationData.ACTION_EQ)};
//         actionSetOptions = new JComboBox(resStates);
//         actionSetOptions.setSelectedIndex(1);
//
//         actions = new JList(new Object[]{"Hej"});
//         add = new JButton("Add");
//
//         initPanel();
//         initalizeListeners();
//      }
//
//      protected void initPanel() {
//         GroupLayout l = new GroupLayout(this);
//         l.setAutoCreateContainerGaps(true);
//         l.setAutoCreateGaps(true);
//         this.setLayout(l);
//
//         l.setHorizontalGroup(l.createSequentialGroup().addGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(labels[0]).addComponent(add)).addGap(5).addGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(actions).addGroup(l.createSequentialGroup().addComponent(addAction, 200, 300, 9999).addComponent(actionSetOptions, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(actionValue, 40, 60, 80))).addGap(0, 99999, 99999));
//
//
//         l.setVerticalGroup(l.createSequentialGroup().addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labels[0]).addComponent(actions)).addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(add).addComponent(addAction).addComponent(actionSetOptions).addComponent(actionValue)));
//      }
//
//      @Override
//      public void setValue(Cell node) {
//         super.setValue(node);
//         OperationData d = (OperationData) node.getValue();
//
//         actions.setModel(convertActions(d.getActions()));
//
//         update();
//      }
//
//      private DefaultListModel convertActions(LinkedList<Action> act) {
//
//         DefaultListModel model = new DefaultListModel();
//
//         for (Action info : act) {
//            ActNode n = new ActNode(info, "");
//            updateDescription(n);
//            model.addElement(n);
//         }
//
//         return model;
//      }
//
//      @Override
//      protected void saveNode() {
//         OperationData d = (OperationData) node.getValue();
//
//         d.setActions(restoreAction());
//
//      }
//
//      protected LinkedList<Action> restoreAction() {
//         LinkedList<Action> result = new LinkedList<Action>();
//
//         ListModel m = actions.getModel();
//
//         for (int i = 0; i < m.getSize(); i++) {
//            ActNode a = (ActNode) m.getElementAt(i);
//
//            result.add(a.model);
//         }
//
//         return result;
//
//      }
//
//      @Override
//      public void clear() {
//
//         if (actions.getModel() instanceof DefaultListModel) {
//            ((DefaultListModel) actions.getModel()).removeAllElements();
//         }
//         addAction.setText("");
//         actionValue.setText("");
//
//
//
//      }
//
//      private void initalizeListeners() {
//         addAction.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//               if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                  String text = addAction.getText();
//
//
//                  try {
//                     int id = Integer.parseInt(text);
//
//
//                     String[] s = view.getGraphModel().getNameCache().get(id);
//
//                     if (s != null && actions.getModel() instanceof DefaultListModel && view.getModel().isResourcePresent(id)) {
//                        addAction.setText((s[0].isEmpty() ? "" : s[0] + ".") + s[1]);
//                        selectedOrId = id;
//                     } else {
//                        addAction.setText("Error");
//                     }
//
//
//                  } catch (NumberFormatException nrE) {
//                     addAction.setText("Only inputtype is int");
//                  }
//               }
//
//            }
//         });
//
//         addAction.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//               if (selectedOrId >= 0) {
//                  addAction.setText(Integer.toString(selectedOrId));
//               }
//               selectedOrId = -1;
//            }
//         });
//
//         add.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//               try {
//
//                  if (selectedOrId >= 0) {
//
//                     String[] s = view.getGraphModel().getNameCache().get(selectedOrId);
//
//
//                     if (s != null && actions.getModel() instanceof DefaultListModel) {
//                        ActNode n = null;
//
//                        int state = actionSetOptions.getSelectedIndex();
//                        int value = Integer.parseInt(actionValue.getText());
//                        n = new ActNode(new Action(selectedOrId, state, value), "");
//                        updateDescription(n);
//
//                        boolean present = false;
//                        for (int i = 0; i < actions.getModel().getSize(); i++) {
//
//                           if (n.equals(actions.getModel().getElementAt(i))) {
//                              present = true;
//                              break;
//                           }
//                        }
//
//                        if (!present) {
//                           ((DefaultListModel) (actions.getModel())).addElement(n);
//                           update();
//                        }
//                     }
//                  } else {
//
//                     addAction.setText("Error");
//                  }
//               } catch (NumberFormatException ev) {
//                  actionValue.setText("E");
//               }
//            }
//         });
//
//         actions.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//               if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//                  JList list = (JList) e.getSource();
//                  int i = list.getSelectedIndex();
//                  if (i >= 0) {
//
//                     ((DefaultListModel) list.getModel()).remove(i);
//                  }
//               }
//            }
//         });
//
//      }
//
//      protected void update() {
//         view.getPane().resetToPreferredSizes();
//      }
//
//      protected void updateDescription(ActNode a) {
//         String result = "";
//
//         String[] t = view.getGraphModel().getNameCache().get(a.model.id);
//
//         if (t != null) {
//            result = (t[0].isEmpty() ? "" : t[0] + ".") + t[1] + Model.getActionSetType(a.model.state) +
//                  Integer.toString(a.model.value);
//         }
//
//         a.representation = result;
//      }
//
//      protected class ActNode {
//
//         public Action model;
//         public String representation;
//
//         public ActNode(Action model, String repr) {
//            this.model = model;
//            this.representation = repr;
//         }
//
//         @Override
//         public boolean equals(Object obj) {
//            return model.equals(obj);
//         }
//
//         @Override
//         public String toString() {
//            return representation;
//         }
//      }
//   }
}
