package sequenceplanner.view.operationView.graphextension;

import com.mxgraph.swing.handler.mxGraphTransferHandler;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellMarker;
import com.mxgraph.view.mxCellState;
import javax.swing.TransferHandler;

public class IntoCellMarker extends mxCellMarker {

    private final static int TOP = 0;
    private final static int BOTTOM = 1;
    private final static int GRP_TOP = 3;
    private final static int GRP_BOT = 4;
    private final static int GRP_INSIDE = 5;
    private final static int GRP_MARK = 10;
    private final static int MARK_OFFSET = 10;
    protected final Stroke STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_MITER, 10.0f, new float[]{3.0f}, 0.0f);
    protected Object[] cs = null;
    protected boolean validConnect = false;
    protected int typeMarkedState = -1;
    SPGraph graph;

    public IntoCellMarker(mxGraphComponent graphComponent, Color validColor) {
        super(graphComponent, validColor);

        graph = (SPGraph) (graphComponent.getGraph());
    }

    public boolean isEnabled(MouseEvent e) {
        if (graphComponent instanceof SPGraphComponent) {
            boolean en = (((SPGraphComponent) graphComponent).getMoveInto() || e.isControlDown()) && enabled;
            TransferHandler th = graphComponent.getTransferHandler();

            boolean isLocal = th instanceof mxGraphTransferHandler && ((mxGraphTransferHandler) th).isLocalDrag();

            return en || !isLocal;
        }

        return enabled;
    }

    @Override
    public mxCellState process(MouseEvent e) {
        mxCellState state = null;
        int type = -1;

        
        if (isEnabled(e)) {
            state = getState(e);

            
            boolean isValid = (state != null) ? isValidState(state) : false;
            Color color = getMarkerColor(e, state, isValid);

            if (isValid) {
                validState = state;
            } else {
                validState = null;
            }

            if (state != null) {

                type = graph.getClickArea((Cell) state.getCell(), e.getPoint(), state);
            } else {
                type = -1;
            }

            boolean validMark = state != markedState || color != currentColor || typeMarkedState != type;


            if (validMark) {

                currentColor = color;

                if (state != null && currentColor != null) {
                    markedState = state;
                    typeMarkedState = type;

                    if (cs != null) {
                        Object o = graph.getSortedSequence(cs);
                        validConnect = (o != null && typeMarkedState <= 4 && typeMarkedState >= 0) || typeMarkedState == GRP_INSIDE;
                    }

                    if (validConnect) {
                        mark();
                    }

                } else if (markedState != null) {
                    typeMarkedState = -1;
                    markedState = null;
                    validConnect = false;
                    unmark();
                }
            }
        }

        // Fix to validate marking on connect
        cs = null;
        return state;
    }

    @Override
    protected void mark() {
        if (markedState != null) {
            Rectangle bounds = markedState.getRectangle();

            if (typeMarkedState == TOP || typeMarkedState == GRP_TOP) {
                bounds.y -= MARK_OFFSET;
                bounds.height += MARK_OFFSET;
                bounds.x -= 5;
                bounds.width += 10;
            } else if (typeMarkedState == BOTTOM) {
                bounds.height += MARK_OFFSET - 3;
                bounds.x -= 5;
                bounds.width += 10;
            } else if (typeMarkedState == GRP_BOT) {
                bounds.height += MARK_OFFSET;
                bounds.x -= 5;
                bounds.width += 10;
            } else if (typeMarkedState == GRP_INSIDE) {
                bounds.grow(3, 3);
                bounds.width += 1;
                bounds.height += 1;
            }

            setBounds(bounds);

            if (this.getParent() == null) {
                setVisible(true);
                graphComponent.getGraphControl().add(this);
            }


        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (markedState != null && currentColor != null) {

            g.setColor(currentColor);

            if (markedState.getAbsolutePointCount() > 0) {

                ((Graphics2D) g).setStroke(new BasicStroke((float) 3.0));
                Point last = markedState.getAbsolutePoint(0).getPoint();

                for (int i = 1; i < markedState.getAbsolutePointCount(); i++) {
                    Point current = markedState.getAbsolutePoint(i).getPoint();
                    g.drawLine(last.x - getX(), last.y - getY(), current.x - getX(), current.y - getY());
                    last = current;
                }
            } else {
                ((Graphics2D) g).setStroke(STROKE);

                if (typeMarkedState == TOP || typeMarkedState == GRP_TOP) {
                    int[] xPoints = {getWidth() / 2, getWidth() / 2 - MARK_OFFSET, getWidth() / 2 + MARK_OFFSET};
                    int[] yPoints = {0, MARK_OFFSET, MARK_OFFSET};
                    g.fillPolygon(xPoints, yPoints, 3);

                    int y = 0;

                    if (typeMarkedState == TOP) {
                        y = (getHeight() - MARK_OFFSET) / 2 + MARK_OFFSET;
                    } else {
                        y = MARK_OFFSET + GRP_MARK;
                    }

                    g.drawLine(0, y, getWidth(), y);

                } else if (typeMarkedState == BOTTOM || typeMarkedState == GRP_BOT) {
                    int[] xPoints = {getWidth() / 2, getWidth() / 2 - MARK_OFFSET, getWidth() / 2 + MARK_OFFSET};
                    int[] yPoints = {getHeight(), getHeight() - MARK_OFFSET, getHeight() - MARK_OFFSET};

                    g.fillPolygon(xPoints, yPoints, 3);

                    int y = 0;

                    if (typeMarkedState == BOTTOM) {
                        y = (getHeight() - MARK_OFFSET + 3) / 2;
                    } else {
                        y = getHeight() - (MARK_OFFSET + GRP_MARK);
                    }
                    g.drawLine(0, y, getWidth(), y);

                } else {
                    ((Graphics2D) g).setStroke(DEFAULT_STROKE);
                    g.drawRect(1, GRP_MARK, getWidth() - 3, getHeight() - 3 - 2 * GRP_MARK);
                }
            }

        }
    }
}