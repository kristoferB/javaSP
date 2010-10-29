package sequenceplanner.view;


import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import sequenceplanner.SPContainer;
import sequenceplanner.SequencePlanner;
import sequenceplanner.model.Model;

/**
 *
 * @author Erik
 */
public abstract class AbstractView extends JPanel {

   protected final Model model;
   protected final SPContainer container;


   static Logger logger = Logger.getLogger(AbstractView.class);



   public AbstractView(SPContainer spc, String name ) {
      this.model = spc.getModel();
      this.container = spc;
      setName(name);
   }

   public Model getModel() {
      return model;
   }

   abstract public boolean closeView();

   public SPContainer getSPContainer() {
      return container;
   }


   public Action createAction(String name,
			ActionListener usedAction, String icon) {
         return createAction(name, usedAction, icon, this);
   }

   public JComponent getOutline() {
      return null;
   }

	// TODO: only scale icons when it is really needed
	public Action createAction(String name,
			final ActionListener usedAction, String icon, final Object source) {

		ImageIcon larIcon = null;
		ImageIcon smaIcon = null;
		if (icon != null && !icon.equals("")) {
			
         try {
            larIcon = new ImageIcon(SequencePlanner.class.getResource(icon) );
         } catch(Exception e) {
            logger.error("Could not load icon: " + icon);
         }

			Image img = larIcon.getImage();
			smaIcon =
				new ImageIcon(img.getScaledInstance(16, -1, Image.SCALE_SMOOTH) );
		}

		AbstractAction newAction = new AbstractAction(name) {

			public void actionPerformed(ActionEvent e) {
				
				ActionEvent ret = new ActionEvent(source, e.getID(), e.getActionCommand() );
				usedAction.actionPerformed(ret);
			}
		};

	  newAction.putValue(Action.SMALL_ICON, smaIcon);
//	  newAction.putValue(Action.LARGE_ICON_KEY, larIcon);

		return newAction;
	}
}
