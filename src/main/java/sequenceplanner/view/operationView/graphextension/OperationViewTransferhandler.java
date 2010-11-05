/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.view.operationView.graphextension;

import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import sequenceplanner.model.data.LiasonData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.view.treeView.TransferableNode;

/**
 *
 * @author Erik
 */
public class OperationViewTransferhandler extends mxGraphTransferHandler {

   
   @Override
   public boolean canImport(JComponent comp, DataFlavor[] flavors) {

      for (int i = 0; i < flavors.length; i++) {
         if (flavors[i] == TransferableNode.resource) {
            return true;
         } else if (flavors[i] == TransferableNode.liason) {
            return true;
         }
      }
      return super.canImport(comp, flavors);
   }

   @Override
   public boolean importData(JComponent c, Transferable t) {

      try {
         if (c instanceof mxGraphComponent && !isLocalDrag()) {
            mxGraphComponent graphComponent = (mxGraphComponent) c;

            Cell cell = null;
            Object o = graphComponent.getCellAt(location.x, location.y);
            if (o != null && o instanceof Cell) {
               cell = (Cell)o;
            }


            if (graphComponent.isEnabled() && t.isDataFlavorSupported(TransferableNode.resource)) {
               ResourceData res = (ResourceData) t.getTransferData(TransferableNode.resource);

               if (cell != null && (cell.isSOP() || cell.isOperation() )) {
                  OperationData d = (OperationData) cell.getValue();
                  d.setRealizedBy(res.getId());
                  ((SPGraph)graphComponent.getGraph()).setPreferenceValue(cell, d);
               }

               return true;
            } else if (graphComponent.isEnabled() && t.isDataFlavorSupported(TransferableNode.liason)) {
               LiasonData li = (LiasonData) t.getTransferData(TransferableNode.liason);

               if (cell != null && (cell.isSOP() || cell.isOperation() ) ) {
                  OperationData d = (OperationData) cell.getValue();
                  d.setAccomplishes(li.getId());
                  ((SPGraph)graphComponent.getGraph()).setPreferenceValue(cell, d);
               }

               return true;
            }

         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return super.importData(c, t);
   }
}
