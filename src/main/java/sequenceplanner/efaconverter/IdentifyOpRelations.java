
package sequenceplanner.efaconverter;


import java.util.Set;

import sequenceplanner.efaconverter.efamodel.SpEFAutomata;
import sequenceplanner.model.Model;

/**
 *
 * This class identifies the relations among a set of operations
 *
 * Comment: In Sp2 we need to have a better separation between
 * the model and other parts of the code. If the model are changed
 * now everything will change.
 * We should use an interface where info are retrived independent of the
 * structure of model.
 *
 *
 * @author kbe
 */
public class IdentifyOpRelations {

    private ModelParser modelparser;
    private OperationSequencer operationSequencer;

    /*
     * The constructor.
     */
    public IdentifyOpRelations() {

    }

    /**
     * This method is first creating a set of sequences based on the
     * relations between the operation locations (variables are not
     * considered). After that these sequencese are translated to EFA.
     * @param spModel
     */
    public void identifyRelations(Model spModel){
        this.modelparser = new ModelParser(spModel);

        //The modelParser should be aware of the relations to it's children.
        //AND WHAT RELATION THAT IS NEEDED TO FINISH THE OPERATION.
        //What to do if the last to children are in an alternative?
        //How to get correct disjunction relation in parent.

        operationSequencer = new OperationSequencer(modelparser);
        Set<OpNode> tops = operationSequencer.sequenceOperations();

        convertSeqToEFA seqToEFA = new convertSeqToEFA(tops, modelparser);

        //Look for a good and general interface to Supremica
        SpEFAutomata automata = seqToEFA.createSpEFA();
        seqToEFA.createWmodFile(automata);

        //SpEFASync sync = new SpEFASync(automata);
        //SpEFA efa = sync.syncAutomata();

    }

}