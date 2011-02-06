/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

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

 //       model.addEditorTreeModelListener(new EditorTreeModelListener());
        view.addMouseListener(new EditorMouseAdapter(view.getTree(), model.getGlobalProperties()));
              
   }

public static void main(String[] args){
    
    GlobPropM t_model = new GlobPropM();
    GlobPropV t_view = new GlobPropV(t_model);
    GlobPropC t_contr = new GlobPropC(t_model, t_view);


    JFrame frame = new JFrame();
    frame.setContentPane(t_view);
    frame.setSize(200, 200);

    frame.setTitle("Global Properties - test");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    
}

}


