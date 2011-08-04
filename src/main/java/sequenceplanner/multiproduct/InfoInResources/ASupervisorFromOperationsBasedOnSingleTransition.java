package sequenceplanner.multiproduct.InfoInResources;

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
                //Init-----------------------------------------------------------
                final OperationData opData = (OperationData) data;

//                System.out.println("cd: " + opData.getName());

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
                final Init init = new Init(op);

                //Guards---------------------------------------------------------
                final Guards guards = new Guards(op, init.toBookResourceSet);

                //Actions--------------------------------------------------------
                final Actions actions = new Actions(pt, op, init.toBookResourceSet, init.toUnbookResourceSet);



                //---------------------------------------------------------------
            }
        }
    }

    class Actions {

        final Set<String> actionSet;

        public Actions(final ProductType iPT, final AOperation iOp, final Set<Resource> iToBookResourceSet,
                final Set<Resource> iToUnBookResourceSet) {
            final ProductType pt = iPT;
            final AOperation op = iOp;
            final Set<Resource> toBookResourceSet = iToBookResourceSet;
            final Set<Resource> toUnBookResourceSet = iToUnBookResourceSet;
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
            for (final Resource r : toUnBookResourceSet) {
                actionSet.add(LocalModel.variableName(r, null) + "-=" + "1");
            }
            //ALSO THE LOCAL VARIABELES SET TO ZERO...
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
        final Set<Resource> toUnbookResourceSet;

        public Init(final AOperation iOp) {
            final AOperation op = iOp;

            //Get predecessor operations...
            for (final String id : op.getPredecessors()) {
                final AOperation operationToAdd = mLocalModel.getOperationBasedOnId(id);
                if (operationToAdd != null) {
                    op.mPredecessorSet.add(operationToAdd);
                }
            }
            //... and there resources
            final Set<Resource> predecessorResourceSet = new HashSet<Resource>();
            for (final AOperation pOp : op.mPredecessorSet) {
                predecessorResourceSet.addAll(pOp.mResourceSet);
            }

            //Resources to book
            toBookResourceSet = Resource.cloneSet(op.mResourceSet);
            toBookResourceSet.removeAll(predecessorResourceSet);

            System.out.println("to book: " + toBookResourceSet);

            //Resources to unbook
            toUnbookResourceSet = Resource.cloneSet(predecessorResourceSet);
            toUnbookResourceSet.removeAll(op.mResourceSet);

            System.out.println("to unbook: " + toUnbookResourceSet);
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
