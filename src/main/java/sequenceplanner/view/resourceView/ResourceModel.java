package sequenceplanner.view.resourceView;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.view.AbstractSyncModel;

/**
 *
 * @author Erik Ohlson
 */
public class ResourceModel extends AbstractSyncModel {

   public ResourceModel( Model model ) {
      this(model, model.getResourceRoot());
   }

   public ResourceModel(Model model, TreeNode root) {
      super(model, root);
   }


}
