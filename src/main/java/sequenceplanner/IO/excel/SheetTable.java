package sequenceplanner.IO.excel;

/**
 * To store data from excel parse.<br/>
 * For example using {@link Excel}
 * @author patrik
 */
public class SheetTable {

    private int mNbrOfRows = 0;
    private int mNbrOfColumns = 0;
    private String[][] mCell;

    public SheetTable(int iNbrOfRows, int iNbrOfCols) {
        mNbrOfRows = iNbrOfRows;
        mNbrOfColumns = iNbrOfCols;
        mCell = new String[iNbrOfRows][iNbrOfCols];
    }

    public int getNbrOfRows() {
        return mNbrOfRows;
    }

    public int getNbrOfColumns() {
        return mNbrOfColumns;
    }

    public int getNbrOfRowsInCol(int iCol) {
        int row;
        for (row = mNbrOfRows - 1; row > 0; --row) {
            if (!getCellValue(row, iCol).equals("")) {
                return row + 1;
            }
        }
        return 0;
    }

    public String getCellValue(int iRow, int iCol) {
        return mCell[iRow][iCol].toLowerCase().replaceAll(" ", "");
    }

    /**
     * Get a sub table
     * @param iLowRow row with lowest index
     * @param iHighRow row with highest index
     * @param iLeftCol col with lowest index
     * @param iRightCol col with highest index
     * @return the created sub {@link SheetTable} or null if parameters are outside boundary of this {@link SheetTable}
     */
    public SheetTable getSubSheetTable(int iLowRow, int iHighRow, int iLeftCol, int iRightCol) {
        //init check
        if (iLowRow < 0 || iLowRow > iHighRow || iHighRow > mNbrOfRows - 1 || iLeftCol < 0 || iLeftCol > iRightCol || iRightCol > mNbrOfColumns - 1) {
            return null;
        }

        //Create new sheet table
        SheetTable st = new SheetTable(iHighRow - iLowRow + 1, iRightCol - iLeftCol + 1);

        //Copy values
        int returnRowIndex = 0;
        for (int i = iLowRow; i <= iHighRow; ++i) {
            int returnColIndex = 0;
            for (int j = iLeftCol; j <= iRightCol; ++j) {
                st.setCellValue(returnRowIndex, returnColIndex, getCellValue(i,j));
                ++returnColIndex;
            }
            ++returnRowIndex;
        }
        return st;
    }

    /**
     * Overwrites old value.
     * @param iRow
     * @param iCol
     * @param iValue
     * @return ture if set was ok else false
     */
    public boolean setCellValue(int iRow, int iCol, String iValue) {
        if (iRow < 0 || iRow > mNbrOfRows - 1 || iCol < 0 || iCol > mNbrOfColumns - 1) {
            return false;
        }
        mCell[iRow][iCol] = iValue;
        return true;
    }

    @Override
    public String toString() {
        String returnString = "";
        for (int i = 0; i < mNbrOfRows; ++i) {
            returnString += i + ":[";
            for (int j = 0; j < mNbrOfColumns; ++j) {
                if (j != 0) {
                    returnString += ",";
                }
                returnString += getCellValue(i, j);
            }
            returnString += "]\n";
        }
        return returnString;
    }
}
