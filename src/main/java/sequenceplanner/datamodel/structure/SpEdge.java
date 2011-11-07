package sequenceplanner.datamodel.structure;

import org.jgrapht.graph.DefaultEdge;

/**
 *
 * @param <A> 
 * @author kbe
 */
public class SpEdge<A>  extends DefaultEdge {

    A attribute;
        
    public A getAttribute(){
        return this.attribute;
    }
    
    public void setAttribute(A attribute){
        this.attribute = attribute;
    }
    
    
}
