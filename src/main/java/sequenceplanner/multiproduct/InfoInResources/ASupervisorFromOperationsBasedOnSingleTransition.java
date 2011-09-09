package sequenceplanner.multiproduct.InfoInResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import sequenceplanner.model.Model;
import sequenceplanner.model.TreeNode;
import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;

/**
 *
 * @author patrik
 */
public abstract class ASupervisorFromOperationsBasedOnSingleTransition implements Runnable {

    final Model mModel;
    LocalModel mLocalModel;
    private Thread mThread;
    private boolean mContinue = true;

    public ASupervisorFromOperationsBasedOnSingleTransition(final Model iModel) {
        this.mModel = iModel;

        init();
    }

    protected void init() {
        mLocalModel = new LocalModel();
    }

    public void start() {
        mThread = new Thread(this, "work thread");
        mThread.start();
        monitor();
    }

    @Override
    public void run() {
        collectData();
//        print();
        workWithData();

        mContinue = false;
    }

    public void monitor() {
        while (mContinue) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                System.out.println("ASupervisorFromOperationsBasedOnSingleTransition: " + ie);
            }
        }
    }

    private void collectData() {
        for (final TreeNode tn : mModel.getAllOperations()) {
            final Data data = tn.getNodeData();
            if (Model.isOperation(data)) {
                addData(data);
            }
        }

        //Add virtual operation for last transition.
        for (final ProductType pt : mLocalModel.mProductTypeSet) {
            for (final AOperation lastOp : pt.getLastOperations()) {
                final OperationData opData = createLastData(lastOp);
                addData(opData, pt.mName, new HashSet<String>());
            }
        }
    }

    private void addData(final OperationData iOpData, final String iProductType, final Set<String> iResources) {
        final AOperation op = new Operation(iOpData);
        mLocalModel.addOperationToProductType(op, iProductType);
        mLocalModel.addResourcesToOperation(iResources, op);
    }

    private void addData(final Data iData) {
        final OperationData opData = (OperationData) iData;
        addData(opData, getProductType(opData), getResources(opData));
    }

    abstract OperationData createLastData(final AOperation iOperation);

    private void workWithData() {
        for (final ProductType pt : mLocalModel.mProductTypeSet) {
            for (final AOperation op : pt.mOperationSet) {
                //Init-----------------------------------------------------------
                final Init init = new Init(op);
//Flytta denna till collect data, när operation skapas.
                //Guards---------------------------------------------------------
                final Guards guards = new Guards(op, init.toBookResourceSet);

                //Actions--------------------------------------------------------
                final Actions actions = new Actions(pt, op, init.toBookResourceSet, init.toUnbookResourceOperationMap);

                //---------------------------------------------------------------
                System.out.println(op.toString());
                System.out.println("guard " + guards.guardSet);
                System.out.println("action " + actions.actionSet);

            }
        }
    }

    class Actions {

        final Set<String> actionSet;

        public Actions(final ProductType iPT, final AOperation iOp, final Set<Resource> iToBookResourceSet,
                final Map<Resource, Set<AOperation>> iToUnbookResourceOperationMap) {
            final ProductType pt = iPT;
            final AOperation op = iOp;
            final Set<Resource> toBookResourceSet = iToBookResourceSet;
            final Map<Resource, Set<AOperation>> toUnbookResourceOperationMap = iToUnbookResourceOperationMap;
            actionSet = new HashSet<String>();

            //First operation?
            if (pt.getFirstOperations().contains(op)) {
                actionSet.add(LocalModel.counterName(iPT) + "+=" + "1");
            }

            //Last operation?

            //Book resources
            for (final Resource r : toBookResourceSet) {
                actionSet.add(LocalModel.variableName(r, null) + "+=" + "1");
            }

            //Set operation finished
            final Map<String, String> map = op.statusOfResourcesAtFinish();
            for (final String key : map.keySet()) {
                actionSet.add(key + "=" + map.get(key));
            }

            //Unbook resources not used anymore
            for (final Resource r : toUnbookResourceOperationMap.keySet()) {
                actionSet.add(LocalModel.variableName(r, null) + "-=" + "1");

                for (final AOperation pOp : toUnbookResourceOperationMap.get(r)) {
                    actionSet.add(LocalModel.variableName(r, pOp) + "=" + "0");
                }
            }
        }
    }

    class Guards {

        final Set<String> guardSet;

        public Guards(final AOperation iOp, final Set<Resource> iToBookResourceSet) {
            final AOperation op = iOp;
            final Set<Resource> toBookResourceSet = iToBookResourceSet;
            guardSet = new HashSet<String>(); //Conjuction of elements

            //Is previous operation finished?
            for (final AOperation pOp : op.mPredecessorSet) {
                final Map<String, String> map = pOp.statusOfResourcesAtFinish();
                for (final String key : map.keySet()) {
                    guardSet.add(key + "==" + map.get(key));
                }
            }

            //Are new resources to use free? NOT NEEDED IF BINARY RESOURCES
            for (final Resource r : toBookResourceSet) {
                guardSet.add(LocalModel.variableName(r, null) + "==" + "0");
            }
        }
    }

    class Init {

        final Set<Resource> toBookResourceSet;
        final Map<Resource, Set<AOperation>> toUnbookResourceOperationMap;

        public Init(final AOperation iOp) {
            final AOperation op = iOp;

            toUnbookResourceOperationMap = new HashMap<Resource, Set<AOperation>>();

            //Get predecessor operations...
            for (final String id : op.getPredecessors()) {
                final AOperation operationToAdd = mLocalModel.getOperationBasedOnId(id);
                if (operationToAdd != null) {
                    op.mPredecessorSet.add(operationToAdd);

                    //... and their resources
                    for (final Resource r : operationToAdd.mResourceSet) {
                        if (!toUnbookResourceOperationMap.containsKey(r)) {
                            toUnbookResourceOperationMap.put(r, new HashSet<AOperation>());
                        }
                        toUnbookResourceOperationMap.get(r).add(operationToAdd);
                    }
                }
            }

            //Resources to book
            toBookResourceSet = Resource.cloneSet(op.mResourceSet);
            toBookResourceSet.removeAll(toUnbookResourceOperationMap.keySet());

//            System.out.println("to book: " + toBookResourceSet);

            //Resources to unbook
            toUnbookResourceOperationMap.keySet().removeAll(op.mResourceSet);

//            System.out.println("to unbook: " + toUnbookResourceOperationMap.keySet());
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
