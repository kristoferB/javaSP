package sequenceplanner.SPIcon;

import java.awt.Image;
import javax.swing.ImageIcon;
import sequenceplanner.SequencePlanner;

/**
 *Class for handling icons
 * @author qw4z1
 */
public class IconHandler {

    public IconHandler(){

    }

      public static ImageIcon getNewIcon(String dir) {
      return getNewIcon(dir, true);
   }

   public static ImageIcon getNewIcon(String dir, boolean resize) {
      if (dir != null && !dir.equals("")) {
         try {
            ImageIcon ico = new ImageIcon(SequencePlanner.class.getResource(dir));
            if (resize) {
               return new ImageIcon(ico.getImage().getScaledInstance(16, -1, Image.SCALE_SMOOTH));
            } else {
               return ico;
            }
         } catch (Exception e) {
            System.out.print("getNewIcon: Inputted icon path is not present");
         }

      } else {
         System.out.print("getNewIcon called with an empty string");
      }

      return null;
   }
}
