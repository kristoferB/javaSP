package sequenceplanner.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author patrik
 */
public abstract class AAlgorithm implements IAlgorithm, Runnable {

    private Thread mWorkThread;
    private Set<IAlgorithmListener> mListeners;
    private boolean mGoOn = true;

    public AAlgorithm(String iThreadName) {
        this.mListeners = new HashSet<IAlgorithmListener>();
        this.mWorkThread = new Thread(this, iThreadName);
    }

    public synchronized void addAlgorithmListener(IAlgorithmListener iListener) {
        if (iListener != null) {
            mListeners.add(iListener);
        }
    }

    public synchronized void removeAlgorithmListener(IAlgorithmListener iListner) {
        if (iListner != null) {
            mListeners.remove(iListner);
        }
    }

    protected synchronized void fireNewMessageEvent(String iMessage) {
        for (final IAlgorithmListener al : mListeners) {
            al.newMessageFromAlgorithm(iMessage, this);
        }
    }

    protected synchronized void fireFinishedEvent(List<Object> iObjectList) {
        for (final IAlgorithmListener al : mListeners) {
            al.algorithmHasFinished(iObjectList, this);
        }
    }

    @Override
    public void start() {
        mWorkThread.start();
    }

    @Override
    public void stop() {
        mGoOn = false;
        mWorkThread.interrupt();
    }

    protected boolean getStatus(final String iText) {
        if (!mGoOn) {
            return false;
        }
        if (!iText.isEmpty()) {
            fireNewMessageEvent(iText);
        }

        return true;
    }
}
