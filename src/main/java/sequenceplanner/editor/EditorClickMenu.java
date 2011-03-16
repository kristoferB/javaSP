package sequenceplanner.editor;

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
 * Popupmenu for editing global properties
 *
 * @author Evelina
 */
public class EditorClickMenu extends JPopupMenu {

    private Object node;
    private ActionListener menuListener;

    public EditorClickMenu(Object n, ActionListener l) {
        node = n;
        menuListener = l;
    }

    /**
     * Shows a menu for editing the root of the tree
     *
     * @param e the MouseEvent
     */
    public void showRootMenu(MouseEvent e){
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), e.getComponent());

        JMenuItem insert = new JMenuItem("Insert property") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        insert.setActionCommand("INSERT_PROPERTY");
        insert.addActionListener(menuListener);

        add(insert);
        show(e.getComponent(), p.x, p.y);

    }

    /**
     * Shows a menu for editing a global property
     *
     * @param e the MouseEvent
     */
    public void showPropertyMenu(MouseEvent e){
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), e.getComponent());

        JMenuItem insert = new JMenuItem("Insert value") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        insert.setActionCommand("INSERT_VALUE");
        insert.addActionListener(menuListener);

        JMenuItem remove = new JMenuItem("Remove") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        remove.setActionCommand("REMOVE_PROPERTY");
        remove.addActionListener(menuListener);

        JMenuItem rename = new JMenuItem("Rename") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        rename.setActionCommand("RENAME_PROPERTY");
        rename.addActionListener(menuListener);

        add(insert);
        add(remove);
        add(rename);
        show(e.getComponent(), p.x, p.y);
    }

    /**
     * Shows a menu for editing a value of a global property
     *
     * @param e
     */
    public void showValueMenu(MouseEvent e){
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), e.getComponent());

        JMenuItem remove = new JMenuItem("Remove") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        remove.setActionCommand("REMOVE_VALUE");
        remove.addActionListener(menuListener);

        JMenuItem rename = new JMenuItem("Rename") {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                EditorClickMenu.this.repaint();
            }
        };
        rename.setActionCommand("RENAME_VALUE");
        rename.addActionListener(menuListener);

        add(remove);
        add(rename);
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
