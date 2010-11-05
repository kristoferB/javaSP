package sequenceplanner.model.data;

import sequenceplanner.model.data.Data;

/**
 *
 * @author Erik Ohlson
 */
public class ResourceData extends Data {

   private String desc = "";


   public ResourceData(String name, int type, int id) {
      super(name, type, id);
    }

   public ResourceData(String name, int id) {
      this(name, Data.RESOURCE, id);
      setDescription("");
   }

   public String getDescription() {
      return desc;
   }

   public void setDescription(String desc) {
      this.desc = desc;
   }
}
