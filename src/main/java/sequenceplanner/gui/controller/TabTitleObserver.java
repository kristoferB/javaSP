package sequenceplanner.gui.controller;

import java.util.Observable;
import java.util.Observer;
import net.infonode.docking.View;
import sequenceplanner.model.data.Data;

/**
 * Observer for the title in sop views and attribute views.<br/>
 * Update method changes title according to notifyObservers call in <code>setName</code> in {@link Data}
 * @author patrik
 */
public class TabTitleObserver implements Observer {

    private View mInfoNodeView;

    public TabTitleObserver(final View iInfoNodeView) {
        this.mInfoNodeView = iInfoNodeView;
    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof String && mInfoNodeView != null) {
            final String newName = (String) arg;
            if (newName != null) {
                mInfoNodeView.getViewProperties().setTitle(newName);
            }
        }
//        System.out.println("o: " + o);
//        System.out.println("arg: " + arg);
    }
}
