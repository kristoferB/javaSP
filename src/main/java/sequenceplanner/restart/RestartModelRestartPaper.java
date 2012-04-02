    package sequenceplanner.restart;

/**
 *
 * @author patrik
 */
public class RestartModelRestartPaper implements Runnable {

    private RestartModelStructure rms = new RestartModelStructure();

    public RestartModelRestartPaper() {
    }

    @Override
    public void run() {
        //Build model------------------------------------------------------------
        rms.addOperation(new RestartOperation("A", true));
        rms.addOperation(new RestartOperation("B", true));
        rms.addOperation(new RestartOperation("C", false));
        rms.addOperation(new RestartOperation("D", true));
        rms.addOperation(new RestartOperation("E", true));
        rms.addOperation(new RestartOperation("F", true));
        rms.addOperation(new RestartOperation("G", false));

        rms.addResource(new RestartResource("M1"));
        rms.addResource(new RestartResource("M2"));
        rms.addResource(new RestartResource("M3"));

        if (!rms.addBranch("M1", "A,B") ||
                !rms.addBranch("M1", "C") ||
                !rms.addBranch("M1", "E") ||
                !rms.addBranch("M2", "D,E,F") ||
                !rms.addBranch("M3", "A") ||
                !rms.addBranch("M3", "G")) {
            System.err.println("Problems when connecting resources and operations!");
        }

        if (!rms.addExcludeSet("A", IRestartOperation.Property.CONTAINS, "B|C|D|E|F|G") ||
                !rms.addExcludeSet("B", IRestartOperation.Property.CONTAINS, "C|F|G") ||
                !rms.addExcludeSet("D", IRestartOperation.Property.CONTAINS, "E|F") ||
                !rms.addExcludeSet("E", IRestartOperation.Property.CONTAINS, "F")) {
            System.err.println("Problems when adding exclusion sets to operations!");
        }

        if (!rms.addForbiddenStatesCombination("A!=2&B!=0") ||
                !rms.addForbiddenStatesCombination("A!=2&D!=0") ||
                !rms.addForbiddenStatesCombination("B!=2&C!=0") ||
                !rms.addForbiddenStatesCombination("B!=2&G!=0") ||
                !rms.addForbiddenStatesCombination("C!=0&G!=0") ||
                !rms.addForbiddenStatesCombination("D!=2&E!=0") ||
                !rms.addForbiddenStatesCombination("B!=2&F!=0") ||
                !rms.addForbiddenStatesCombination("E!=2&F!=0")) {
            System.err.println("Problems when adding forbidden states combinations!");
        }

        //Set sifting------------------------------------------------------------
        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.TO_ONLY_AFFECT_RESOURCES_IN_ERROR_OPERATION, true);
//        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.TO_ONLY_ENABLE_PLACEMENT_IN_HOME_STATES_FOR_ALL_RESOURCES, true);
        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.EXCLUDE_TRANSITION_IF_POWERSETELEMENT_CONTAINS_UNWANTED_OPERATIONS, true);

        //Generate wmod file for model-------------------------------------------
        rms.generateWmodFile("placementTrans", "C:\\Users\\patrik\\Desktop\\");
    }
}
