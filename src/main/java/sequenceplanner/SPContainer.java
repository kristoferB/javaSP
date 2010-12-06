package sequenceplanner;

import java.awt.Component;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;



import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.ViewData;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.resourceView.ResourceView;
import sequenceplanner.view.treeView.TreeView;

/* TODO
 *  - Placement when inserting into groupCell
 *  - Save alternative in a correct way
 *  - Make correct edges when inserting cell in cell.
 *  - Drag'n'Drop
 *
 *
 * /

/**
 *
 * @author Erik Ohlson
 */
public class SPContainer {
    //Variable for holding all views

    private View[] operationViews = new View[50];
    private View[] resourceViews = new View[50];
    private View[] nonOperationViews = new View[15];
    private RootWindow rootWindow;
    private TabWindow tabWindow;
    //Map of all the views
    private ViewMap viewMap = new ViewMap();
    protected static int viewCounter = 0;
    private int opViewCounter = 0;
    private int reViewCounter = 0;
    //Not used
        /*
    // Container for most of the views
    protected JTabbedPane viewPane;

    // Container for project / Library views
    protected JSplitPane projectPane;
     */
    // Referense to the main data model
    private Model model;

    public RootWindow getRootWindow() {
        return this.rootWindow;
    }

    public void setRoot(RootWindow r) {
        this.rootWindow = r;
    }

    public ViewMap getViewMap() {
        return viewMap;
    }

    public View getOpView(int i) {
        return operationViews[i];
    }

    public View getNonOpView(int i) {
        return nonOperationViews[i];
    }

    public TabWindow getTabWindow() {
        return tabWindow;
    }

    public void setTabWindow(TabWindow tw) {
        tabWindow.addTab(tw);
    }

    public SPContainer() {
        this.model = new Model();
        initializePanes();

        //createOperationView("Free view " + Integer.toString(1));

    }

    private void initializePanes() {

        nonOperationViews[0] = new View("TreeView ", null, new TreeView(this));
        viewMap.addView(0, nonOperationViews[0]);

        operationViews[0] = new View("Free View " + 1, null, new OperationView(this, "OperationView"));
        viewMap.addView(1, operationViews[0]);

        tabWindow = new TabWindow();



        /*

        //viewPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {


        @Override
        public void add(Component component, Object constraints) {
        super.add(component, new CustomTabComponent(SPContainer.this,
        component));
        setTabComponentAt(indexOfComponent(component),
        new CustomTabComponent(SPContainer.this, component));
        setSelectedComponent(component);
        }
        //};

        viewPane.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
        Component c = viewPane.getSelectedComponent();

        Component prev = projectPane.getBottomComponent();
        if (prev != null) {
        prev.setPreferredSize(new Dimension(prev.getWidth(), prev
        .getHeight()));
        }

        if (c != null) {
        JComponent input = ((AbstractView) c).getOutline();
        projectPane.setBottomComponent(input);
        } else {
        projectPane.setBottomComponent(null);
        }
        projectPane.resetToPreferredSizes();

        }
        });

        projectPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
        new TreeView(this), new JPanel());
        projectPane.setDividerSize(3);
        projectPane.setResizeWeight(1.0);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
        projectPane, viewPane);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

         */
    }
    //End of InitializePanes

    /**
     * Updates all of the current views.
     */
    public void updateTabWindow() {

        tabWindow.addTab(operationViews[opViewCounter]);
        if (reViewCounter > 0) {
            tabWindow.addTab(resourceViews[reViewCounter]);
        }

        rootWindow.setWindow(new SplitWindow(true, 0.15f, nonOperationViews[0], tabWindow));
    }

    /**
     * Create a resource view and adds it to the tabWindow
     * @param root TreeNode object containing all of the current resources.
     */
    public void createResourceView(TreeNode root) {
        //       if (reViewCounter == 0) {
        viewCounter++;
        resourceViews[++reViewCounter] = new View("Resources", null, new ResourceView(this, root, "Name"));
        System.out.print(reViewCounter);
        viewMap.addView(viewCounter, resourceViews[reViewCounter]);

        updateTabWindow();
        //     }
    }

    /**
     * Creates new OperationView and adds it to the tabbed window
     * @param name
     */
    public void createOperationView(String name) {

        viewCounter++;

        operationViews[++opViewCounter] = new View("Free View " + (opViewCounter + 1), null, new OperationView(this, "Name"));

        viewMap.addView(viewCounter, operationViews[opViewCounter]);

        if (opViewCounter == 1) {
            tabWindow.addTab(operationViews[0]);
        }

        updateTabWindow();

    }

    public boolean createOperationView(ViewData d) {
        OperationView ov = new OperationView(this, d);
//        for (int i = 0; i < opViewCounter; i++) {
//            Component c = tabWindow.getChildWindow(i).getComponent(i);
//
//            if (c instanceof OperationView
//                    && ((OperationView) c).getName().equals(d.getName())) {
//
//
//                //viewPane.setSelectedIndex(i);
//                tabWindow.setSelectedTab(i);
//                return true;
//            }
//        }
        viewCounter++;
        operationViews[++opViewCounter] = new View("Free View " + (opViewCounter + 1), null, ov);
        //viewPane.add(ov, d.getName());
        return true;
    }

    public Model getModel() {
        return model;
    }
}
