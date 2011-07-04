package sequenceplanner.multiProduct.summer2011;

import java.util.HashSet;
import java.util.Set;
import sequenceplanner.condition.ConditionExpression;
import sequenceplanner.condition.ConditionOperator.Type;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ASupervisorFromOperationsBasedOnSingleTransition {

    private Model mModel;
    private LocalModel mLocalModel;

    public ASupervisorFromOperationsBasedOnSingleTransition(final Model iModel) {
        this.mModel = iModel;

        init();
        run();
    }

    private void init() {
        mLocalModel = new LocalModel();
    }

    private void run() {
        collectData();
        print();
//        workWithData();
    }

    private void collectData() {
        for (final TreeNode tn : mModel.getAllOperations()) {
            final Data data = tn.getNodeData();
            if (Model.isOperation(data)) {
                //Init-----------------------------------------------------------
                final OperationData opData = (OperationData) data;                

                //Collect data---------------------------------------------------
                final String productType = getProductType(opData);
                final Set<String> resources = getResources(opData);

                //Store data-----------------------------------------------------
                final AOperation op = new Operation(opData);
                mLocalModel.addOperationToProductType(op, productType);
                mLocalModel.addResourcesToOperation(resources, op);
                //---------------------------------------------------------------
            }
        }
    }

    private void workWithData() {
        for (final ProductType pt : mLocalModel.mProductTypeSet) {
            for (final AOperation op : pt.mOperationSet) {
                //Init-----------------------------------------------------------
                final Set<AOperation> predecessorOperationSet = op.getPredecessors();
                final Set<Resource> predecessorResourceSet = new HashSet<Resource>();
                for(final AOperation pOp : predecessorOperationSet) {
                    predecessorResourceSet.addAll(pOp.mResourceSet);
                }

                //Get resources that
                final Set<Resource> preRemainingResourceSet = Resource.cloneSet(predecessorResourceSet);
                preRemainingResourceSet.removeAll(op.mResourceSet);

                //Guard----------------------------------------------------------
                //---------------------------------------------------------------

                //Check that in predecessor
                final ConditionExpression guard = new ConditionExpression();
                for(final AOperation pOp : predecessorOperationSet) {
                    guard.appendElement(Type.AND, guard);
                }
            }
        }
    }

    public void print() {
        System.out.println("------------");
        mLocalModel.printProductTypesAndOperations();
        System.out.println(mLocalModel.mResourceSet.toString());
        System.out.println("------------");
    }

    abstract String getProductType(final OperationData opData);

    abstract Set<String> getResources(final OperationData opData);
}
