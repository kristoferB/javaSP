package sequenceplanner.weightNonBlocking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.gui.view.GUIView;
import sequenceplanner.model.SOP.SopNode;
import sequenceplanner.view.operationView.OperationView;
import sequenceplanner.view.operationView.OperationViewController;

/**
 *
 * @author patrik
 */
public class WeightNonBlocking implements IAlgorithmListener {

    private final File mFileXML;
    private final OperationViewController mOpViewController;
    private XMLInteraction mXMLInteraction;
    private Algorithm mAlgorithm;

    public WeightNonBlocking(File iFile, OperationViewController iOpViewController) {
        this.mFileXML = iFile;
        this.mOpViewController = iOpViewController;
    }

    public void run() {

        //Read indata
        readFromFile();
    }

    private void readFromFile() {
        mXMLInteraction = new XMLInteraction(this);
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(mFileXML);
        mXMLInteraction.init(list1);
        mXMLInteraction.start();
    }

    private void runAlgorithm(final Set<Seam> seamSet, final Resource resource) {
        mAlgorithm = new Algorithm(this);
        final List<Object> list1 = new ArrayList<Object>();
        list1.add(seamSet);
        list1.add(resource);
        mAlgorithm.init(list1);
        mAlgorithm.start();
    }

    @Override
    public void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        if (iFromAlgorithm == mXMLInteraction) {
            final Set<Seam> seamSet = (Set<Seam>) iList.get(0);
            final Resource resource = (Resource) iList.get(1);
            runAlgorithm(seamSet, resource);
            return;
        }
        if (iFromAlgorithm == mAlgorithm) {
            final SopNode rootNode = (SopNode) iList.get(0);
            final OperationView ow = mOpViewController.createOperationView();
            ow.drawGraph(rootNode);
            return;
        }
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
        GUIView.printToConsole(iMessage);
    }
}
