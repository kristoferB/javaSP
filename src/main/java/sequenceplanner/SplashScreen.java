package sequenceplanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import sequenceplanner.utils.IconHandler;


public class SplashScreen extends JWindow {
    
	private static final long serialVersionUID = 575516828339282334L;
	
	//Define size of the splash screen.
	private Dimension size = new Dimension(350,250);
	
	public SplashScreen() {
		super();
		this.setAlwaysOnTop(true);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SplashScreen.this.dispose();		
			}
		});
		
	}
    
    public void showSplash(Rectangle bounds) {
        JPanel content = (JPanel)getContentPane();
        
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        setBackground(Color.LIGHT_GRAY);
        
        int x = bounds.x + (bounds.width-size.width)/2;
        int y = bounds.y + (bounds.height-size.height)/2;
        
        setBounds(x, y, size.width, size.height);
        
        content.setOpaque(false);
        content.setLayout(new BorderLayout());
        JLabel logo = new JLabel( IconHandler.getNewIcon("/sequenceplanner/resources/icons/SequencePlanner.png", false) );
        // &#11; = VT (Vertical Tab)
        JLabel label = new JLabel("<HTML>Created by <BR> Carl Thorstensson and Erik Ohlson <BR>in collaboration with Bengt Lennartson and Kristofer Bengtsson</HTML>");
        
        add(logo, BorderLayout.NORTH);
        add(label, BorderLayout.SOUTH);
        
        setVisible(true);
       
        try {
        	Thread.sleep(100000);
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        dispose();
    }
}