package sequenceplanner.algorithm;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Intention: Put all algorithms on separate threads!<br/>
 *
 * A class Alg that extends this abstract class needs to implement two classes; init and run.<br/>
 * init - is used to send all data to be used in algorithm.<br/>
 * run - the code for the algorithm.<br/>
 * Two events can be fired; NewMessage and Finished.<br/>
 * NewMessage - should be used to send feedback to e.g. GUI about current status.<br/>
 * Finished - should be used as last method call in run method. Data as result can be added in this event.<br/>
 * Methods in Alg should use the method getStatus to see if the class still has permission to run or if it has been stopped.<br/>
 *
 * A class B that has an algorithm object alg must implement the {@link IAlgorithmListener} interface in order to take actions on the events.<br/>
 * Class B can stop execution of the algorithm thread with the call alg.stop().<br/>
 *
 * Example:
 * class Alg extends AAlgorithm {
 * Integer i = "";
 * ...
 * init(List<Object> iList) {
 * i = (Integer) iList.get(0);
 * }
 * run() {
 * fireNewMessageEvent("Start...");
 * i = i*i;
 * getStatus("Running...");
 * i = i*i;
 * List returnList = new ArrayList();
 * returnList.add(i);
 * fireFinishedEvent(returnList);
 * }
 * }
 *
 * class B implements IAlgorithmListener {
 * ...
 * Alg alg = new Alg("Thread_Alg");
 * alg.addAlgorithmListener(this);
 * List intList = new ArrayList();
 * intList.add(new Integer(2));
 * alg.init(intList);
 * alg.start();
 * ...
 * algorithmHasFinished(List<Object> iList, IAlgorithm iFromAlgorithm) {
 * if (iFromAlgorithm instanceof Arg) {
 * System.out.println("Final value for integer: " + iList.get(0));
 * }
 * }
 * ...
 * newMessageFromAlgorithm(String iMessage, IAlgorithm iFromAlgorithm) {
 * System.out.println(iMessage);
 * }
 *
 * @author patrik
 */
public abstract class AAlgorithm implements IAlgorithm, Runnable {

    private Thread mWorkThread;
    private Set<IAlgorithmListener> mListeners;
    private boolean mGoOn = true;
    private long mRunMethodStarts = 0;

    @Override
    public abstract void init(List<Object> iList);

    @Override
    public abstract void run();

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
        mRunMethodStarts = Calendar.getInstance().getTimeInMillis();
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

    protected String getDurationForRunMethod() {
        final long timeDifference = Calendar.getInstance().getTimeInMillis() - mRunMethodStarts;
        return timeDifference / 1000 + "." + timeDifference % 1000 + " seconds";
    }
}
