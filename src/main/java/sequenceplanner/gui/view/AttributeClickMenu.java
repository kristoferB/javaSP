/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.gui.view;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Peter
 */
public class AttributeClickMenu extends JPopupMenu {

    private Object node;
    private ActionListener menuListener;
    private boolean enabled;

    public AttributeClickMenu(Object n, ActionListener l, boolean enabled) {
        node = n;
        menuListener = l;
        this.enabled = enabled;
    }

    /**
     * Shows a menu for editing or deleting conditions in attributepanel
     *
     * @param e
     */
    public void showAttributePanelMenu(MouseEvent e) {
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), e.getComponent());

        JMenuItem edit = new JMenuItem("Edit") {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                AttributeClickMenu.this.repaint();
            }
        };
        edit.setActionCommand("EDIT_VALUE");
        edit.addActionListener(menuListener);
        

        JMenuItem delete = new JMenuItem("Delete") {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                AttributeClickMenu.this.repaint();
            }
        };
        delete.setActionCommand("DELETE_VALUE");
        delete.addActionListener(menuListener);
        if(enabled == true){
            edit.setForeground(Color.BLACK);
            delete.setForeground(Color.BLACK);
        }else{
            edit.setForeground(Color.GRAY);
            delete.setForeground(Color.GRAY);
            edit.setEnabled(false);
            delete.setEnabled(false);
        }
        add(edit);
        add(delete);
        show(e.getComponent(), p.x, p.y);
    }



    @Override
    public void show(Component invoker, int x, int y) {

        super.show(invoker, x, y);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int[] polX = {0, 0, 14};
        int[] polY = {0, 14, 0};
        g.setColor(Color.BLACK);
        g.fillPolygon(polX, polY, 3);
    }
}

