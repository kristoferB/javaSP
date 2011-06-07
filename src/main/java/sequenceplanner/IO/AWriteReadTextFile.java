package sequenceplanner.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Write to a text file, or read from a text file
 * @author patrik
 */
public abstract class AWriteReadTextFile {

    private String mReadFromFile = "";
    private String mWriteToFile = "";

    public AWriteReadTextFile(final String iReadFromFile, final String iWriteToFile) {
        mReadFromFile = iReadFromFile;
        mWriteToFile = iWriteToFile;
    }

    public boolean readFromFile() {
        try {
            BufferedReader bis = new BufferedReader(new FileReader(new File(mReadFromFile)));
            while (bis.ready()) {

                whatToDoWithLine(bis.readLine());

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

    abstract void whatToDoWithLine(final String iLine);

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