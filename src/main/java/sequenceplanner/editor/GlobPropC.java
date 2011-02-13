package sequenceplanner.editor;

import javax.swing.JFrame;

/**
 *
 * @author Evelina
 */
public class GlobPropC{

    private EditorView view;
    private GlobPropM model;

    GlobPropC(GlobPropM m, EditorView v){
        model = m;
        view = v;

        view.addMouseListener(new EditorMouseAdapter(view.getTree(), model.getGlobalProperties()));
              
   }

public static void main(String[] args){
    
    GlobPropM t_model = new GlobPropM();
    EditorView t_view = new EditorView(t_model.getGlobalProperties());
    GlobPropC t_contr = new GlobPropC(t_model, t_view);


    JFrame frame = new JFrame();
    frame.setContentPane(t_view);
    frame.setSize(200, 200);

    frame.setTitle("Global Properties - test");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    
}

}


