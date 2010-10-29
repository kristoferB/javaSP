package sequenceplanner.model.data;

import sequenceplanner.model.Model;
import sequenceplanner.model.data.Data;

/**
 *
 * @author Erik Ohlson
 */
public class LiasonData extends Data {

   public LiasonData( String name, int type, int id ) {
      super( name, type, id );
   }

   public LiasonData( String name, int id ) {
      this( name, Data.LIASON, id );
   }
   
}
