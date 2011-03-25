package sequenceplanner.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *Singelton filefilter for the SequencePlanner project.
 * Implemented according to Bill Pugh solution
 *
 * @author QW4z1
 */
public class SPFileFilter {

    private SPFileFilter() {
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SPFileFilterHolder {

        private static FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".sopx") || f.isDirectory();
            }

            public String getDescription() {
                return "Sequence Planner Project File";
            }
        };
    }

    public static FileFilter getInstance() {
        return SPFileFilterHolder.filter;

    }
}