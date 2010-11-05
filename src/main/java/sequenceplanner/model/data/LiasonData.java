package sequenceplanner.model.data;


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
