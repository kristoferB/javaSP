package sequenceplanner.utils;

import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Erik Ohlson
 */
public class SPToolBar extends JToolBar {

   public SPToolBar(String name) {
      super(name);
   }

   public SPToolBar() {
   }

   
   public void add(final JPopupMenu popup) {
      

      final JToggleButton tb = new JToggleButton(popup.getLabel()) {

         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // TODO insert an icon that shows that this is an dropdown menu

//            if (getHeight() > 15 && getWidth() > 15) {
//               int xins = getInsets().left;
//               int yins = getInsets().bottom;
//
//               int[] polyX = { xins, xins+4, xins+2 };
//               int[] polyY = { getHeight()-yins-4, getHeight()-yins-4, getHeight()-yins };
//
//               g.fillPolygon(polyX, polyY, 3);
//            }
         }
      };
      tb.setFocusable(false);

      tb.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
               popup.show(tb, 0, tb.getHeight());
            } else if (popup.isVisible()) {
               popup.setVisible(false);
            }
         }
      });

      popup.addPopupMenuListener(new PopupMenuListener() {

         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            tb.setSelected(true);
         }

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            tb.setSelected(false);
         }

         @Override
         public void popupMenuCanceled(PopupMenuEvent e) {
            tb.setSelected(false);
         }
      });

      this.add(tb);

   }
}
