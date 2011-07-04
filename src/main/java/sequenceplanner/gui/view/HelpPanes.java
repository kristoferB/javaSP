/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.gui.view;

import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 *
 * @author Peter
 */
public class HelpPanes extends JFrame{
    //JEditorPane ep = new JEditorPane();
    //private String sc = "\\..\\..\\..\\..\\..\\main\\resources\\sequenceplanner\\resources";"resources/ShortCommands.html"
    //private URL url = HelpPanes.class.getResource("ShortCommands.html");
    public HelpPanes(String window){
        try{
        if(window.equals("Short Commands")){
            String url = "C:\\Users\\Peter\\Sequence-Planner\\src\\main\\resources\\sequenceplanner\\resources";
            JEditorPane ep = new JEditorPane();
            ep.setContentType("text/html");
            ep.setPage(url);
            ep.setEditable(false);
            this.setVisible(true);
            //ep.setPage("/ShortCommands.html");
        }
        else if(window.equals("About")){
            //ep.setPage("\\About.xhtml");
        }
        }catch(IOException e){System.out.println("IOException: Wrong folder adress" + e);}
    }
}
