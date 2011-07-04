package sequenceplanner.model.data;

import java.util.HashMap;
import java.util.Map;
import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class SopNodeData extends Data{

    public Map<ViewData,ISopNode> mViewDataSopNodeMap = new HashMap<ViewData, ISopNode>();

    public SopNodeData(String name, int type, int id) {
        super(name, type, id);
    }

    public SopNodeData(String name, int id) {
        super(name, id);
    }

}
