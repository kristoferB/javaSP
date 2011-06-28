package sequenceplanner.IO;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sequenceplanner.efaconverter.SEGA;
import sequenceplanner.multiProduct.OperationResourceDataStructure;

/**
 *
 * @author patrik
 */
public class WriteToPSOP extends AWriteReadTextFile {

    List<String> mSequence = new ArrayList<String>();

    public WriteToPSOP(String iReadFromFile, String iWriteToFile) {
        super(iReadFromFile, iWriteToFile);
    }

    @Override
    void whatToDoWithLine(String iLine) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void whatToWriteToFile(BufferedWriter iOut) {
        try {

            for (final String s : mSequence) {
                iOut.write(s);
                iOut.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToSequence(final Map<OperationResourceDataStructure.Operation, List<SEGA>> iMap, final String iTitle,
            final int iPadForIF, final int iPadForPREACTION, final int iPadForPOSTACTION) {

        mSequence.add("SEQUENCE(" + iTitle + ")");
        mSequence.add("{");

        for (final OperationResourceDataStructure.Operation op : iMap.keySet()) {
            String s = "";
            s += padLeft("1:", 5);

            //Carrier
            String sCarrier = "";
            for (final OperationResourceDataStructure.Resource r : op.mViaResourceMap.keySet()) {
                sCarrier += " " + r.mName;
            }
            s += padRight(sCarrier, 6);

            //Source
            String sSource = "";
            for (final OperationResourceDataStructure.Resource r : op.mSourceResourceMap.keySet()) {
                sSource += " " + r.mName;
            }
            s += sSource;

            s += " ->";

            //IF
            String sIF = "";
            sIF += " IF{";
            sIF += iMap.get(op).get(0).getGuard();
            sIF += "}";
            s += padRight(sIF, iPadForIF);

            //PREACTION
            String sPREACTION = "";
            sPREACTION += " PREACTIONS{";
            sPREACTION += iMap.get(op).get(0).getAction();
            sPREACTION += "}";
            s += padRight(sPREACTION, iPadForPREACTION);

            //POSTACTION
            String sPOSTACTION = "";
            sPOSTACTION += " POSTACTIONS{";
            sPOSTACTION += iMap.get(op).get(1).getAction();
            sPOSTACTION += "}";
            s += padRight(sPOSTACTION, iPadForPOSTACTION);

            //Sink
            for (final OperationResourceDataStructure.Resource r : op.mDestResourceMap.keySet()) {
                s += " " + r.mName;
            }

            //;
            s += ";";

            mSequence.add(s);
        }
        mSequence.add("};");
    }
}
