package sequenceplanner.IO.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * To parse data from excel file<br/>
 * The file has to be saved as an "Excel 97-2003 Workbook" aka .xls<br/>
 * No internal check that the file is .xls!<br/>
 * The methods are based on <b>jxl.jar</b><br/>
 * Two sheets can not have the same name!<br/>
 * @author patrik
 */
public class Excel {

    private String mFilePath = "";
    private Workbook mWorkbook = null;
    private Map<String,SheetTable> mSheetMap = null;

    public Excel() {
        this("");

    }

    /**
     * @param iFilePath Path to file
     */
    public Excel(String iFilePath) {
        mFilePath = iFilePath;
        mSheetMap = new HashMap<String,SheetTable>();
    }

    /**
     * Get all sheets in workbook
     * @return all sheets, name of sheet is key
     */
    public Map<String,SheetTable> getSheets() {
        return mSheetMap;
    }

    /**
     * Get a sheet in workbook based on the name of the sheet.
     * @param iSheetName name of sheet in workbook
     * @return sheet or null if no sheet with iSheetName
     */
    public SheetTable getSheet(String iSheetName) {
        if (!mSheetMap.containsKey(iSheetName)) {
            System.out.println("No sheet has name: " + iSheetName);
            return null;
        }
        return mSheetMap.get(iSheetName);
    }

    /**
     * Path to excel file.<br/>
     * The file has to be an "Excel 97-2003 Workbook" aka .xls<br/>
     * No internal check that the file is .xls!<br/>
     * @param iPath the path to file
     * @return true if set was ok else false
     */
    public boolean setFilePath(String iPath) {
        if (!iPath.equals("")) {
            return false;
        }
        mFilePath = iPath;
        return true;
    }

    /**
     * To parse from excel workbook to internal data type.<br/>
     * @return true if parse was ok else false
     */
    public boolean init() {
        if (!mFilePath.equals("")) {
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(new File(mFilePath));
                //What to do-----------------------------------------------------

                System.out.println("Start to parse excel file...");
                if (!openWorkbook(fs)) {
                    return false;
                }
                if (!parseSheets()) {
                    return false;
                }
                closeWorkbook();
                System.out.println("...finished to parse excel file!");
                //---------------------------------------------------------------
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fs.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean parseSheet(int iInt) {
        if (!(iInt >= 0 && iInt < mWorkbook.getNumberOfSheets())) {
            return false;
        }
        final Sheet s = mWorkbook.getSheet(iInt);
        final String sName = s.getName();

        //Create table to store data
        SheetTable st = new SheetTable(s.getRows(), s.getColumns());

        //Checks that sheet name not exists
        if (mSheetMap.put(sName, st) != null) {
            System.out.println("Sheet name has to be unique, " + s.getName() + " already added!");
            return false;
        }

        //Loop row and column and add to internal data structure
        for (int i = 0; i < s.getRows(); ++i) {
            for (int j = 0; j < s.getColumns(); ++j) {
                final String content = s.getCell(j, i).getContents();
                if(!st.setCellValue(i, j, content)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean parseSheets() {
        for (int i = 0; i < mWorkbook.getNumberOfSheets(); ++i) {
            if (!parseSheet(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean openWorkbook(InputStream iFileInputStream) {
        WorkbookSettings ws = null;
        try {
            ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            mWorkbook = Workbook.getWorkbook(iFileInputStream, ws);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (BiffException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void closeWorkbook() {
        mWorkbook.close();
    }

    @Override
    public String toString() {
        String returnString = "";
        for (String sheet : mSheetMap.keySet()) {
            returnString += sheet + "\n" + mSheetMap.get(sheet).toString();
        }
        return returnString;
    }
}

