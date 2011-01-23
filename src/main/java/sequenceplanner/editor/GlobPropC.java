/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.JFrame;

/**
 *
 * @author Evelina
 */
public class GlobPropC{

    private GlobPropV view;
    private GlobPropM model;

    GlobPropC(GlobPropM m, GlobPropV v){
        model = m;
        view = v;

        view.addAddPropertyListener(new addPropertyListener());
    }

    //Koppla skapande av property (Editor) i view till model
    //Koppla s�ttande av property f�r en operation (Attribute) i view till dummy operation, ev skriv test case

    class addPropertyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String prop = "";
            String[] values = new String[1];

            prop = view.getPropertyInput();
            values[0] = view.getValueInput();

            model.addProperty(prop, values);

//Testing if property and value is saved in model...
            LinkedList<GlobalProperty> gp = model.getGlobalProperties();
            for(int i = 0; i < gp.size(); i++){
                System.out.println(gp.get(i).getName());
                String[] val = gp.get(i).getValues();
                for(int j = 0; j < val.length; j++){
                    System.out.println(val[j]);
                }
            }
        }
    }

public static void main(String[] args){
    
    GlobPropM t_model = new GlobPropM();
    GlobPropV t_view = new GlobPropV(t_model);
    GlobPropC t_contr = new GlobPropC(t_model, t_view);

    JFrame frame = new JFrame();
    frame.setContentPane(t_view);
    frame.pack();

    frame.setTitle("Global Properties - test");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    
}

}


