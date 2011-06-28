package sequenceplanner.view.treeView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.LinkedList;

import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.LiasonData;
import sequenceplanner.model.data.OperationData;
import sequenceplanner.model.data.ResourceData;
import sequenceplanner.view.operationView.graphextension.Cell;
import sequenceplanner.view.operationView.graphextension.SPGraph;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxRectangle;
import sequenceplanner.view.operationView.Constants;

/**
 *
 * @author Erik Ohlson
 */
public class TransferableNode implements Transferable {

   public static final DataFlavor operation = new DataFlavor(OperationData.class, "OperationSOP");
   public static final DataFlavor resource = new DataFlavor(ResourceData.class, "Resource");
   public static final DataFlavor liason = new DataFlavor(LiasonData.class, "Liason");
   LinkedList<DataFlavor> supported;
   TreeNode node = null;

   public TransferableNode(TreeNode node) {
      this.node = node;
      supported = new LinkedList<DataFlavor>();

      if (Model.isOperation(node.getNodeData())) {
         supported.add(operation);
         supported.add(mxGraphTransferable.dataFlavor);
      } else if (Model.isResource(node.getNodeData())) {
         supported.add(resource);
      } else if (Model.isLiason(node)) {
         supported.add(liason);
      }
   // TODO Maby could add support for dropping resources or liasons
   }

   @Override
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (!isDataFlavorSupported(flavor)) {
         throw new UnsupportedFlavorException(flavor);
      }

      if (flavor.equals(operation)) {
         return node.getNodeData();
      } else if (flavor.equals(mxGraphTransferable.dataFlavor)) {

         Cell cell = new Cell(node.getNodeData());
         cell.setCollapsed(true);
         cell.setVertex(true);
         cell.setStyle("perimeter=custom.operationPerimeter;fillColor=#FFFF00");
         cell.setConnectable(false);
         mxGeometry geo = new mxGeometry();


         if (node.getChildCount() > 0) {
            cell.setType(Constants.SOP);
         } else {
            cell.setType(Constants.OP);
         }

         mxRectangle rect = SPGraph.getSizeForOperation(cell);
         geo.setHeight(rect.getHeight());
         geo.setWidth(rect.getWidth());
         cell.setGeometry(geo);
         return new mxGraphTransferable(new Object[]{cell}, rect);
      } else if (flavor.equals(resource) || flavor.equals(liason)) {
         return node.getNodeData();
      }

      return false;
   }

   @Override
   public DataFlavor[] getTransferDataFlavors() {
      return supported.toArray(new DataFlavor[0]);
   }

   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      for (DataFlavor dataFlavor : supported) {
         if (flavor.equals(dataFlavor)) {
            return true;
         }
      }
      return false;
   }
}
