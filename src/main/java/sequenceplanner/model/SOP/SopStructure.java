package sequenceplanner.model.SOP;

/**
 *
 * @author Qw4z1
 * *Till viktor*
 * Vi måste ha en Linked List för varje sekvens där ny root "Before ->Operation"
 * läggs till som ny "addFirst". Läggs en ny operation till "after" så läggs den
 * i sist i listan. Läggs en parallell eller alternativ till så måste de länkas
 * ihop i en annan lista via listan.
 *
 */
public class SopStructure implements ISopStructure{
    private ISopNode node;

    @Override
    public void addNode(ISopNode node) {
        
    }

    @Override
    public void addNodeToRoot(ISopNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addNodeToSequence(ISopNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
