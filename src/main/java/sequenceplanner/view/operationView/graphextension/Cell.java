package sequenceplanner.view.operationView.graphextension;

import sequenceplanner.model.data.Data;
import sequenceplanner.model.data.OperationData;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import sequenceplanner.view.operationView.Constants;



public class Cell extends mxCell {
   // To know if cell is moved or copied.
   

   private int type = -1;


	public Cell() {
		super();
	}
	
	
	public Cell(Object value)	{
		this(value, null, null);
	}
	
	public Cell(Object value, mxGeometry geometry, String style)	{
		setValue(value);
		setGeometry(geometry);
		setStyle(style);
	}

   public void setType(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public boolean isOperation() {
      return type == Constants.OP;
   }

   public boolean isSOP() {
      return this.type == Constants.SOP;
   }

   public boolean isParallel() {
      return this.type == Constants.PARALLEL;
   }

   public boolean isAlternative() {
      return this.type == Constants.ALTERNATIVE;
   }
   
   public boolean isArbitrary() {
      return type == Constants.ARBITRARY;
   }

   public boolean isGroup() {
      return isAlternative() || isArbitrary() || isParallel();
   }

   public int getUniqueId() {
      if (getValue() instanceof Data) {
         return ((Data)getValue()).getId();
      }

      return -1;
   }

/*   @Override
   public boolean equals(Object arg0){

       if (this.getUniqueId() != -1 && arg0 instanceof Cell)
           return this.getUniqueId() == ((Cell) arg0).getUniqueId();

       return this == arg0;
   }

    @Override
    public int hashCode() {
        return this.getUniqueId();
    }
*/

	@Override
	public Object clone()
	{
		Cell clone = new Cell(cloneValue(), null, getStyle());
		clone.setCollapsed(isCollapsed());
		clone.setConnectable(isConnectable());
		clone.setEdge(isEdge());
		clone.setVertex(isVertex());
		clone.setVisible(isVisible());
		//Unsaved
		clone.setId(null);

		mxGeometry geometry = getGeometry();
        clone.setType(type);

		if (geometry != null)
		{
			clone.setGeometry((mxGeometry) geometry.clone());
		}
        

		return clone;
	}
	
	@Override
	protected Object cloneValue() {
		Object value = getValue();

		if (value instanceof OperationData) {
         return ((OperationData)value).clone();
		}


		
		return super.cloneValue();
	}
	
	@Override
	public String toString() {
        if (this.isOperation() && value != null){
            if (value instanceof OperationData){
                OperationData d = (OperationData) value;
                return "Operation: " + d.getName() + ", " + getUniqueId();
            }
            return "Operation: " + getUniqueId();
        }
            

        if (this.isSOP()){
            if (value instanceof OperationData){
                OperationData d = (OperationData) value;
                return "SOP: " + d.getName();
            } else return "SOP " + getUniqueId();

        }

        if (this.isAlternative())
            return "ALTERNATIVE: "  + getUniqueId();

        if (this.isParallel())
            return "PARALLEL: "  + getUniqueId();

        if (this.isArbitrary())
            return "ARBITRARY: "  + getUniqueId();
        
        return getValue().toString();
	}
	

}
