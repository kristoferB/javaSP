/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.algorithms.visualization;

import sequenceplanner.model.SOP.ISopNode;

/**
 *
 * @author patrik
 */
public class RelationsForOperationSet implements IRelationsForOperationSet{

    private ISopNode mSopNodeOset = null;
    private ISopNode mSopNodeOsubset = null;
    private ISopNode mSopNodeOfinish = null;

    public RelationsForOperationSet() {
    }

    public ISopNode getmSopNodeOfinish() {
        return mSopNodeOfinish;
    }

    public void setmSopNodeOfinish(ISopNode mSopNodeOfinish) {
        this.mSopNodeOfinish = mSopNodeOfinish;
    }

    public ISopNode getmSopNodeOset() {
        return mSopNodeOset;
    }

    public void setmSopNodeOset(ISopNode mSopNodeOset) {
        this.mSopNodeOset = mSopNodeOset;
    }

    public ISopNode getmSopNodeOsubset() {
        return mSopNodeOsubset;
    }

    public void setmSopNodeOsubset(ISopNode mSopNodeOsubset) {
        this.mSopNodeOsubset = mSopNodeOsubset;
    }




}
