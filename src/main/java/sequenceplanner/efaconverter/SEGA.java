package sequenceplanner.efaconverter;

import org.supremica.external.avocades.common.EGA;

/**
 * Help class for creation of transitions with event, guards and actions
 * @author patrik
 */
public class SEGA extends EGA {

    public SEGA() {
        super();
    }

    public SEGA(String event) {
        super(event);
    }

    /**
     * To book <i>to</i> and unbook <i>from</i> if object is in <i>from</i><br/>
     * @param from object is here
     * @param to object should go here
     */
    public void addBasicPositionBookAndUnbook(String from, String to) {
        if (from.length() > "".length()) {
            andGuard(from + ">0");
            addAction(from + "-1");
        }
        if (to.length() > "".length()) {
            addAction(to + "+1");
        }
    }

    public String guardFromSPtoEFASyntaxTranslation(String ioGuard) {
        //Change all _i to ==0
        ioGuard = ioGuard.replaceAll("_i", "==0");
        //Change all _e to ==1
        ioGuard = ioGuard.replaceAll("_e", "==1");
        //Change all _f to ==2
        ioGuard = ioGuard.replaceAll("_f", "==2");
        //Change all A to &
        ioGuard = ioGuard.replaceAll("A", "&");
        //Change all V to |
        ioGuard = ioGuard.replaceAll("V", "|");

        return ioGuard;
    }

    public void addGuardBasedOnSPCondition(String iCondition, String iOpVariablePrefix, ModelParser iModelParser) {
        //Example of raw precondition 18_f A (143_iV19_f)

        //add precondition to guard
        if (!iCondition.equals("")) {

            //Change all ID to ProductType_ID
            for (OpNode opNode : iModelParser.getOperations()) {
                iCondition = iCondition.replaceAll(opNode.getStringId(), iOpVariablePrefix + opNode.getStringId());
            }

            iCondition = guardFromSPtoEFASyntaxTranslation(iCondition);

            andGuard(iCondition);
        }
    }


}

