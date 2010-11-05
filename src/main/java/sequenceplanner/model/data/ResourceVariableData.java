/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.model.data;

/**
 * Describe a variable hold by a resource.
 *
 * @author Erik Ohlson
 */
public class ResourceVariableData extends Data {

   // Type definition
   public static final int INTEGER = 0;
   public static final int BINARY = 1;

   //Name of this varible
   private String name;

   //Type of variable
   private int type;

   //Default inital value of the variable is 0;
   private int initialValue = 0;
   private int[] constraint = new int[2];

   public ResourceVariableData(String name, int type, int id) {
      super(name, type, id);
   }

   public ResourceVariableData(String name, int id) {
      super(name, Data.RESOURCE_VARIABLE, id);
      setData(0, 0, 1, 0);
   }

   public void setData(int type, int min, int max, int initial) {
      setType(type);
      setMin(min);
      setMax(max);
      setInitialValue(initial);
   }


   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getInitialValue() {
      return initialValue;
   }

   public void setInitialValue(int initialValue) {
      this.initialValue = initialValue;
   }

   public int getMin() {
      return constraint[0];
   }

   public void setMin(int min) {
      constraint[0] = min;
   }

   public int getMax() {
      return constraint[1];
   }

   public void setMax(int max) {
      constraint[1] = max;
   }
}
