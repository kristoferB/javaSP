package sequenceplanner.restart;

/**
 *
 * @author patrik
 */
public class RestartModelRestartInAlternative {

    private RestartModelStructure rms = new RestartModelStructure();

    public RestartModelRestartInAlternative() {

        //Build model------------------------------------------------------------
        rms.addOperation(new RestartOperation("A", false));
        rms.addOperation(new RestartOperation("B", false, "", "", "", "C=2"));
        rms.addOperation(new RestartOperation("C", false, "", "", "", "A=2;B=2"));
        rms.addOperation(new RestartOperation("D", true));

        rms.addResource(new RestartResource("M1"));

        if (!rms.addBranch("M1", "A,B,C,D")) {
            System.err.println("Problems when connecting resources and operations!");
        }

        if (!rms.addExcludeSet("A", IRestartOperation.Property.CONTAINS, "B|C|D") ||
                !rms.addExcludeSet("B", IRestartOperation.Property.CONTAINS, "C|D") ||
                !rms.addExcludeSet("C", IRestartOperation.Property.CONTAINS, "A|B|D") ||
                !rms.addExcludeSet("D", IRestartOperation.Property.NOT_ONLY_CONTAINS, "A,C|B,C|C,A,B")) {
            System.err.println("Problems when adding exclusion sets to operations!");
        }

        if (!rms.addForbiddenStatesCombination("A!=2&B!=0") ||
                !rms.addForbiddenStatesCombination("A!=0&B!=2&C!=0") ||
                !rms.addForbiddenStatesCombination("B==2&C==1") ||
                !rms.addForbiddenStatesCombination("B!=2&C!=2&D!=0")) {
            System.err.println("Problems when adding forbidden states combinations!");
        }

        //Set sifting------------------------------------------------------------
//        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.TO_ONLY_AFFECT_RESOURCES_IN_ERROR_OPERATION, true);
//        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.TO_ONLY_ENABLE_PLACEMENT_IN_HOME_STATES_FOR_ALL_RESOURCES, true);
        rms.getmSiftMap().put(RestartModelStructure.PlacementSifting.EXCLUDE_TRANSITION_IF_POWERSETELEMENT_CONTAINS_UNWANTED_OPERATIONS, true);

        //Generate wmod file for model-------------------------------------------
        rms.generateWmodFile("placementTransTest", "C:\\Users\\patrik\\Desktop\\");
    }
}