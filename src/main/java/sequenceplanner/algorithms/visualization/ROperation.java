package sequenceplanner.algorithms.visualization;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author patrik
 */
public class ROperation implements IROperation {

        /**
         * Relations to other operations (their locations).<br/>
         * Outside keyset = {"up" (init->exec), "down" (exec->finish)}.<br/>
         * Inside keyset = {{@link RVNode}} for all operations in project.<br/>
         * Inside valueset = {0,1,2,01,02,12,012}
         */
        private Map<String, Map<IROperation, Set<String>>> mEventOperationLocationSetMap = null;
        /**
         * Key: other {@link RVNode} operation, Value: Relation between this and key.
         */
        private Map<IROperation, Integer> mOperationRelationMap = null;
        private int mId = -1;
        private boolean mHasToFinish = false;

        public ROperation(final int iId, final boolean iHasToFinish) {
            this.mId = iId;
            this.mHasToFinish = iHasToFinish;
        }

        @Override
        public int getId() {
            return mId;
        }

        @Override
        public String getStringId() {
            return Integer.toString(mId);
        }

        @Override
        public Integer getRelationToIOperation(IROperation iOperation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasToFinish() {
            return mHasToFinish;
        }

        @Override
        public boolean subsetContainsRelationValue(Set<IROperation> iSet, Integer iRelation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean equals(int iId) {
            final String thisIdString = Integer.toString(mId);
            final String iIdString = Integer.toString(iId);
            return thisIdString.equals(iIdString);
        }

        public Map<String, Map<IROperation, Set<String>>> getmEventOperationLocationSetMap() {
            return mEventOperationLocationSetMap;
        }

        public void setmEventOperationLocationSetMap(Map<String, Map<IROperation, Set<String>>> mEventOperationLocationSetMap) {
            this.mEventOperationLocationSetMap = mEventOperationLocationSetMap;
        }

        public Map<IROperation, Integer> getmOperationRelationMap() {
            return mOperationRelationMap;
        }

        public void setmOperationRelationMap(Map<IROperation, Integer> mOperationRelationMap) {
            this.mOperationRelationMap = mOperationRelationMap;
        }
    }
