package sequenceplanner.multiproduct.RAS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import sequenceplanner.IO.EFA.IVariable;
import sequenceplanner.IO.EFA.ModuleBase;
import sequenceplanner.IO.EFA.SingleFlowerSingleTransitionModule;
import sequenceplanner.IO.EFA.Transition;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.algorithm.IAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;

/**
 * Each product type is preprocessed separate.<br/>
 * Then all product types are synthesized togheter.<br/>
 * DOES NOT HANDLE JOIN PRODUCT TYPES (do sync instead of synthesis?).<br/>
 * @author patrik
 */
public class PreProcessingProductTypes extends AAlgorithm implements IAlgorithmListener {

    private Set<Operation> mOperationSet = null;
    private Set<Variable> mVariableSet = null;
    private Set<String> mProductTypeSet = null;
    private ModuleBase mFinalModuleBase = null;
    private String mFilePath = null;
    private String mComment = null;
    private int mFinishedPreprocesses = 0;

    public PreProcessingProductTypes(String iThreadName) {
        super(iThreadName);
    }

    @Override
    public void init(List<Object> iList) {
        mOperationSet = (Set<Operation>) iList.get(0);
        mVariableSet = (Set<Variable>) iList.get(1);
        mProductTypeSet = (Set<String>) iList.get(2);
        mFilePath = (String) iList.get(3);
        mComment = (String) iList.get(4);

        mFinalModuleBase = new ModuleBase();
    }

    @Override
    public void run() {
        CreateTransitionsAndVariables ctav;

        for (final String productType : mProductTypeSet) {
            if (!getStatus("Start preprocessing of product type: " + productType + " ...")) {
                return;
            }

            ctav = new CreateTransitionsAndVariables("CTAV_Thread_" + productType);
            ctav.addAlgorithmListener(this);
            final List<Object> initList = new ArrayList<Object>();
            initList.add(mOperationSet);
            initList.add(mVariableSet);
            initList.add(productType);
            ctav.init(initList);
            ctav.start();
        }
    }

    private synchronized void addToModuleBase(final ModuleBase iMBProduct, final ModuleBase iMBResourcesProduct, final String iProductType) {
        final ModuleBase moduleBaseReturnedFromMonolithicSynthesis = new ModuleBase();
        final SingleFlowerSingleTransitionModule module = new SingleFlowerSingleTransitionModule("TestRASPreprecessing_productType_"+iProductType, mComment, iMBProduct);
        module.saveToWMODFile(mFilePath);
        //Monolithic synthesis on system. Only one product at a time, all other transitions have gurad "and 0".
        module.translateAutomatonToModuleBase(moduleBaseReturnedFromMonolithicSynthesis, Operation.PRODUCT_TYPE + iProductType);

        //Add product variable to module base, (just one variable)
        for (final IVariable var : moduleBaseReturnedFromMonolithicSynthesis.getVariableSet()) {
            mFinalModuleBase.storeVariable(var);
        }
        //Add resource variables to module base
        for (final IVariable var : iMBResourcesProduct.getVariableSet()) {
            mFinalModuleBase.storeVariable(var);
        }
        //Add resource guard and action to module base
        for (final Transition trans : moduleBaseReturnedFromMonolithicSynthesis.getTransitionSet()) {
            final Transition transCopy = getTransitionCopy(trans.getLabel(), iMBResourcesProduct);
            transCopy.setStartLocation(trans.getStartLocation());
            transCopy.setFinishLocation(trans.getFinishLocation());
            mFinalModuleBase.getTransitionSet().add(transCopy);
        }

        //Keep track of how many product types that has been preprocessed.
        ++mFinishedPreprocesses;

        if (!getStatus("Product type: " + iProductType + ", finished preprocessing. " + "(" + mFinishedPreprocesses + "/" + mProductTypeSet.size() + ")")) {
            return;
        }

        //Go on with synthesis when all product types have been preprocessed.
        if (mFinishedPreprocesses == mProductTypeSet.size()) {

            fireNewMessageEvent("Preprocessing took " + getDurationForRunMethod());
            if(!getStatus("Start processing of supervisor...")) {
                return;
            }

            final SingleFlowerSingleTransitionModule sfstm = new SingleFlowerSingleTransitionModule("TestPreProcessedRASmodel", mComment, mFinalModuleBase);
            sfstm.saveToWMODFile(mFilePath);
            sfstm.getExtractedGuards(2);
            
            fireFinishedEvent(null);
            return;
        }
    }

    @Override
    public synchronized void algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
        if (iFromAlgorithm instanceof CreateTransitionsAndVariables) {
            final ModuleBase moduleBase = (ModuleBase) iList.get(0);
            final ModuleBase moduleBaseResources = (ModuleBase) iList.get(1);
            final String productType = (String) iList.get(2);
//            System.out.println(moduleBase.toString());

            addToModuleBase(moduleBase, moduleBaseResources, productType);

        }
    }

    @Override
    public void newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
//        System.out.println(iMessage);
    }

    private static Transition getTransitionCopy(final String iTransLabel, final ModuleBase iModuleBase) {
        for (final Transition trans : iModuleBase.getTransitionSet()) {
            if (trans.getLabel().equals(iTransLabel)) {
                return trans.copy();
            }
        }
        return null;
    }
}
