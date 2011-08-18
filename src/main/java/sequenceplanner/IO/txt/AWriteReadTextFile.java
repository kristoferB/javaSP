package sequenceplanner.IO.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Write to a text file, or read from a text file
 * @author patrik
 */
public abstract class AWriteReadTextFile {

    private String mReadFromFile = "";
    private String mWriteToFile = "";
    protected Set<String> mReadLineSet;

    public AWriteReadTextFile(final String iReadFromFile, final String iWriteToFile) {
        mReadFromFile = iReadFromFile;
        mWriteToFile = iWriteToFile;
    }

    public boolean readFromFile() {
        try {
            mReadLineSet = new HashSet<String>();
            BufferedReader bis = new BufferedReader(new FileReader(new File(mReadFromFile)));
            while (bis.ready()) {

                final String line = bis.readLine();
                if (line.length() > 1) {
                    mReadLineSet.add(line);
                }

            }
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean writeToFile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(mWriteToFile));


            whatToWriteToFile(out);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * iOut.write("Hello");
     * iOut.newLine();
     * @param iOut
     */
    abstract void whatToWriteToFile(BufferedWriter iOut);

    /**
     * Example<br/>
     * @param s PM
     * @param n 4
     * @return "PM  "
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    /**
     * Example<br/>
     * @param s PM
     * @param n 4
     * @return "  PM"
     */
    public static String padLeft(String s, int n) {
        return String.format("%1$#" + n + "s", s);
    }
}
