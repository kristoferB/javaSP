package sequenceplanner.gui.controller;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.gui.view.GUIView;

/**
 *Listener class for controlling DockingWindowEvents. 
 * @author Qw4z1
 */
class OperationWindowListener implements DockingWindowListener{
    //Private instance of the GUIView object.
    private GUIView view;

    public OperationWindowListener(GUIView view) {
        this.view = view;
    }

    @Override
    public void windowAdded(DockingWindow dw, DockingWindow dw1) {
        System.out.println("Not supported yet.1");
    }

    @Override
    public void windowRemoved(DockingWindow dw, DockingWindow dw1) {
        System.out.println("Not supported yet.2");
    }

    @Override
    public void windowShown(DockingWindow dw) {

        System.out.println("Window shown");
        if(dw.getComponent(0).getComponentAt(10, 10) instanceof OperationView){
            OperationView op = (OperationView) dw.getComponent(0).getComponentAt(10, 10);

                    op.setHidden(false);
        }
    }

    @Override
    public void windowHidden(DockingWindow dw) {
        System.out.println("Window hidden");
        if(dw.getComponent(0).getComponentAt(10, 10) instanceof OperationView){
            OperationView op = (OperationView) dw.getComponent(0).getComponentAt(10, 10);

                    op.setHidden(true);
                    System.out.println(op.getName() + " is hidden? " +op.isHidden());
        }
    }

    @Override
    public void viewFocusChanged(View view, View view1) {
System.out.println("Not supported yet.3");
    }

    @Override
    public void windowClosing(DockingWindow dw) throws OperationAbortedException {
System.out.println("Not supported yet.4");
    }

    @Override
    public void windowClosed(DockingWindow dw) {
        System.out.println("Window closed");
        if(dw.getComponent(0).getComponentAt(10, 10) instanceof OperationView){
            OperationView op = (OperationView) dw.getComponent(0).getComponentAt(10, 10);
                    op.setClosed(true);
                    System.out.println(op.getName() + " is closed? " +op.isClosed());
        }
    }

    @Override
    public void windowUndocking(DockingWindow dw) throws OperationAbortedException {
        System.out.println("Not supported yet.5");
    }

    @Override
    public void windowUndocked(DockingWindow dw) {
        System.out.println("Not supported yet.6");
    }

    @Override
    public void windowDocking(DockingWindow dw) throws OperationAbortedException {
        System.out.println("Not supported yet.7");
    }

    @Override
    public void windowDocked(DockingWindow dw) {
        System.out.println("Not supported yet.8");
    }

    @Override
    public void windowMinimizing(DockingWindow dw) throws OperationAbortedException {
        System.out.println("Not supported yet.9");
    }

    @Override
    public void windowMinimized(DockingWindow dw) {
        System.out.println("Not supported yet.10");
    }

    @Override
    public void windowMaximizing(DockingWindow dw) throws OperationAbortedException {
        System.out.println("Not supported yet.11");
   }

    @Override
    public void windowMaximized(DockingWindow dw) {
        System.out.println("Not supported yet.12");
    }

    @Override
    public void windowRestoring(DockingWindow dw) throws OperationAbortedException {
        System.out.println("Not supported yet.13");
    }

    @Override
    public void windowRestored(DockingWindow dw) {
       System.out.println("Not supported yet.14");
    }

}
