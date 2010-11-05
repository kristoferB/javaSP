package sequenceplanner.model;

/**
 *
 * @author Erik Ohlson
 */
public interface IModel {

   /**
    *  Add a listener for this model. Work pretty much like an observer
    *
    * @param l ModelListener to register
    */
   public void addAsyncModelListener( AsyncModelListener l);

   /**
    *  Remove a listener for this model.
    *
    * @param l ModelListener to register
    */
   public void removeAsyncModelListener( AsyncModelListener l );

   public void addSyncModelListener( SyncModelListener l);

   public void removeSyncModelListener( SyncModelListener l );

   public interface AsyncModelListener {

      /**
       *
       * @param Called when a Node/Nodes has been updated
       */
      public void change( Integer[] changedNodes );
   }

   public interface SyncModelListener {

      /**
       *
       * @param Called when a Node/Nodes has been updated
       */
      public void bigChange( TreeNode[] path );

      public void remove( TreeNode removedNode, TreeNode[] previusPath, int place );

      public void insert( TreeNode insertedNode, TreeNode[] Path, int place );

      public void dataChange( TreeNode changedNode, TreeNode[] Path, int place );
   }
}