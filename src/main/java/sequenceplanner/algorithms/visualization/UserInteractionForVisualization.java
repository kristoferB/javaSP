package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.DrawSopNode;
import sequenceplanner.view.operationView.OperationView;

/**
 * To manage the user interaction for {@link Visualization}.<br/>
 * @author patrik
 */
public class UserInteractionForVisualization {

    private OperationView mOpView = null;

    public UserInteractionForVisualization(OperationView mOpView) {
        this.mOpView = mOpView;
        new DrawSopNode(mOpView.getGraph());
    }
    
    public void method() {
        
    }

}
