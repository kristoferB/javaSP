package sequenceplanner.view.operationView.graphextension;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.Hashtable;
import java.util.List;

import sequenceplanner.model.data.OperationData;
import sequenceplanner.view.operationView.Constants;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

/**
 * Class for drawing stuf in OperationViews.
 * @author Erik
 */
public class custom2DCanvas extends mxInteractiveCanvas {

        private Color operationColor = Constants.DEFAULT_OPERATION_COLOR;
	// TODO Show number of operations in collapsed group cell.

	// Fonts used to draw the cell
	public static transient Font labelFont = new Font("Helvetica", Font.BOLD,
			13);
	public static transient Font syncFont = new Font("Helvetica", Font.PLAIN,
			10);
	// Shows boxes around text to find missalignments
	private boolean test = false;
	protected Stroke prePostOperation = new BasicStroke((float) (2 * scale),
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {
					3.0f, 3.0f }, 0.0f);
	private static Hashtable<TextAttribute, Object> label = new Hashtable<TextAttribute, Object>();

	static {


	}

	public custom2DCanvas() {
            setLabel();
	}

	public custom2DCanvas(Graphics2D g) {
                setLabel();
		this.g = g;
	}
        
        /**
         * Private mathod for setting the static variable label.
         */
        private void setLabel(){
                label.put(TextAttribute.FAMILY, "Helvetica");
		label.put(TextAttribute.SIZE, 10);
		label.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		label.put(TextAttribute.FOREGROUND, Color.BLACK);
            
        }

	@Override
	public Object drawEdge(List pts, Hashtable style) {
		return super.drawEdge(pts, style);
	}

	public void drawVertex(mxCellState state) {

		int x = (int) state.getX() + translate.x;
		int y = (int) state.getY() + translate.y;
		int w = (int) state.getWidth();
		int h = (int) state.getHeight();

		SPGraph graph = (SPGraph) state.getView().getGraph();

		if (g != null && g.hitClip(x, y, w, h)) {

			// Saves the stroke
			Stroke stroke = g.getStroke();

			Cell c = (Cell) state.getCell();

			if (c.isSOP()) {

				if (((mxCell) state.getCell()).isCollapsed()) {
					drawOperation(x, y, w, h, state);

				} else {
					drawGroup(x, y, w, h, state);
				}

			} else if (c.isOperation()) {

				drawOperation(x, y, w, h, state);
			} else if (c.isParallel()) {
				Stroke s = new BasicStroke((int) (3 * scale));

				boolean top = graph.hasGroupCellConnectingEdges(true, c);
				boolean bottom = graph.hasGroupCellConnectingEdges(false, c);

				Color bg = Color.BLACK;

				if (!top && !bottom) {
					bg = new Color(0, 0, 0, 50);
					top = bottom = true;
				}

				drawParallel(x, y, w, h, s, top, bottom, bg);

			} else if (c.isAlternative()) {

				Stroke s = new BasicStroke((int) (2 * scale));
				mxCell cell = (mxCell) state.getCell();

				double topLeft = 99999, botLeft = 99999;
				double topRight = 0, botRight = 0;

				// Fast fix function to determine how long the alternative lines
				// should be.
				if (!cell.isCollapsed()) {
					for (int i = 0; i < cell.getEdgeCount(); i++) {
						mxICell edge = cell.getEdgeAt(i);
						mxCellState edgeState = state.getView().getState(edge);
						if (edge.getParent() == cell) {
							try {
								if (edge.getTerminal(true) == cell) {
									double x1 = edgeState.getAbsolutePoint(0)
											.getX();
									topLeft = topLeft > x1 ? x1 : topLeft;
									topRight = topRight < x1 ? x1 : topRight;
								} else {
									double x1 = edgeState
											.getAbsolutePoint(
													edgeState
															.getAbsolutePointCount() - 1)
											.getX();
									botLeft = botLeft > x1 ? x1 : botLeft;
									botRight = botRight < x1 ? x1 : botRight;
								}
							} catch (Exception e) {
								System.out
										.println("Error in calculation of alternative top-bottom");
							}
						}
					}
				}

				if (topRight > 0 || botRight > 0) {
					drawAlternative(x, y, w, h, (int) topLeft, (int) topRight,
							(int) botLeft, (int) botRight, s);
				} else {
					drawAlternative(x, y, w, h, x, x + w, x, x + w, s);
				}
			} else if (c.isArbitrary()) {

				Stroke s = new BasicStroke((float) (3 * scale),
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f,
						new float[] { 10.0f, 6.0f }, 0.0f);

				drawParallel(x, y, w, h, s, true, true, Color.BLACK);
			}

			// Restores the stroke
			g.setStroke(stroke);
		}
	}

	private void drawOperation(int x, int y, int w, int h, mxCellState state) {

		OperationData value = (OperationData) ((mxCell) state.getCell())
				.getValue();
		SPGraph graph = ((SPGraph) state.getView().getGraph());

		// To compensate for the shadow
		w -= 3;
		h -= 3;

		// Shadow
                g.setColor(Color.gray);
		g.fillRoundRect(x + 3, y + 3, w, h, 5, 5);

		g.setColor(operationColor);
		g.fillRoundRect(x, y, w, h, 5, 5);

		Stroke old = g.getStroke();
		g.setStroke(value.isPreoperation() || value.isPostoperation() ? prePostOperation
				: old);

		g.setColor(Color.black);
		g.drawRoundRect(x, y, w, h, 5, 5);
		g.setStroke(old);

		g.setColor(Color.BLACK);

		Insets inset = SPGraph.opInset;

		Font big = new Font(labelFont.getFamily(), labelFont.getStyle(),
				(int) Math.round(labelFont.getSize2D() * scale));
		Font small = new Font(syncFont.getFamily(), syncFont.getStyle(),
				(int) Math.round(syncFont.getSize2D() * scale));

		int start = (int) (inset.top * scale);

		g.setFont(small);

		label.put(TextAttribute.SIZE,
				(int) Math.round(syncFont.getSize2D() * scale));

		String v = value.getPrecondition();
		if (v.length() > SPGraph.cutOff) {
			v = v.substring(0, SPGraph.cutOff) + "...";
		}

		// THIS IS A REAL SLOW SOLUTION, REMOVE IS EXPERIENCE PERFORMANCE
		// PROBLEMS
		AttributedString attText = null;
		if (!v.isEmpty()) {
			attText = new AttributedString(v, label);
		}

		for (int i = 0; i < v.length(); i++) {
			if (Character.toString(v.charAt(i)).equals(Constants.AND)
					|| Character.toString(v.charAt(i)).equals(Constants.OR)) {
				if (v.charAt(i - 1) == ' ' && v.charAt(i + 1) == ' ') {
					attText.addAttribute(TextAttribute.FOREGROUND, Color.BLUE,
							i, i + 1);
					attText.addAttribute(TextAttribute.SIZE, 11, i, i + 1);
				}
			}
		}

		Rectangle2D bounds = g.getFontMetrics().getStringBounds(v, g)
				.getBounds();

		if (graph.OPTION_SHOW_STARTCONDITION && !v.isEmpty()) {
			g.setColor(Color.BLACK);

			if (test) {
				g.drawRect((int) (x + (w - bounds.getWidth()) / 2), y + start,
						(int) bounds.getWidth(), (int) bounds.getHeight());
			}

			start += g.getFontMetrics().getMaxAscent();
			g.setColor(Color.black);
			g.drawString(attText.getIterator(),
					(float) (x + (w - bounds.getWidth()) / 2), (float) (y
							+ start - 2));
			start += 1 * scale;
			g.setColor(Color.GRAY);

			g.drawLine(x + inset.left, (y + start), x + w - inset.right,
					(y + start));
			start += 4 * scale;
		}

		{
			g.setColor(Color.BLACK);
			g.setFont(big);
			v = value.getName();
			if (v.length() > SPGraph.cutOff) {
				v = v.substring(0, SPGraph.cutOff) + "...";
			}

			bounds = (g.getFontMetrics().getStringBounds(v, g));

			if (test) {
				g.drawRect((int) (x + (w - bounds.getWidth()) / 2), y + start,
						(int) bounds.getWidth(), (int) bounds.getHeight());
			}

			start += g.getFontMetrics().getMaxAscent();
			g.drawString(v, (int) (x + (w - bounds.getWidth()) / 2),
					(y + start));
			g.setFont(small);
			v = value.getPostcondition();
			if (v.length() > SPGraph.cutOff) {
				v = v.substring(0, SPGraph.cutOff) + "...";
			}

			bounds = g.getFontMetrics().getStringBounds(v, g).getBounds();
		}

		if (graph.OPTION_SHOW_STOPCONDITION && !v.isEmpty()) {

			attText = new AttributedString(v, label);

			for (int i = 0; i < v.length(); i++) {
				if (Character.toString(v.charAt(i)).equals(Constants.AND)
						|| Character.toString(v.charAt(i))
								.equals(Constants.OR)) {
					if (v.charAt(i - 1) == ' ' && v.charAt(i + 1) == ' ') {
						attText.addAttribute(TextAttribute.FOREGROUND,
								Color.BLUE, i, i + 1);
						attText.addAttribute(TextAttribute.SIZE, 11, i, i + 1);
					}
				}
			}

			g.setColor(Color.GRAY);
			start += 4 * scale;
			g.drawLine(x + inset.left, (y + start), x + w - inset.right,
					(y + start));
			start += 1 * scale;

			if (test) {
				g.drawRect((int) (x + (w - bounds.getWidth()) / 2), y + start,
						(int) bounds.getWidth(), (int) bounds.getHeight());
			}

			start += g.getFontMetrics().getMaxAscent();
			g.setColor(Color.blue);
			g.drawString(attText.getIterator(),
					(float) (x + (w - bounds.getWidth()) / 2),
					(float) (y + start));
		}
	}

	private void drawAlternative(int x, int y, int w, int h, int topLeft,
			int topRight, int botLeft, int botRight, Stroke s) {
		g.setColor(Color.black);
		Stroke old = g.getStroke();
		g.setStroke(s);

		g.drawLine((int) (scale * topLeft), y, (int) (scale * topRight), y);
		g.drawLine((int) (scale * botLeft), y + h, (int) (scale * botRight), y
				+ h);

		g.setStroke(old);
	}

	private void drawParallel(int x, int y, int w, int h, Stroke s,
			boolean top, boolean bottom, Color c) {

		Color oldColor = g.getColor();
		g.setColor(c);

		Stroke old = g.getStroke();
		g.setStroke(s);

		if (top) {
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y + (int) (5 * scale), x + w, y + (int) (5 * scale));
		}

		if (bottom) {
			g.drawLine(x, y + h - (int) (7 * scale), x + w, y + h
					- (int) (7 * scale));
			g.drawLine(x, y + h - (int) (2 * scale), x + w, y + h
					- (int) (2 * scale));
		}

		g.setStroke(old);
		g.setColor(oldColor);
	}

	private void drawGroup(int x, int y, int w, int h, mxCellState state) {
		g.setStroke(new BasicStroke(0));

		Color c = mxUtils.getColor(state.getStyle(),
				mxConstants.STYLE_FILLCOLOR);
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));

		OperationData data = (OperationData) ((mxCell) state.getCell())
				.getValue();

		g.fillRoundRect(x, y, w, h, 20, 20);

		g.setColor(Color.BLACK);
		Font big = new Font(labelFont.getFamily(), labelFont.getStyle(),
				(int) Math.round(labelFont.getSize2D() * scale));

		g.setFont(big);
		String v = data.getName();
		Rectangle2D r = g.getFontMetrics().getStringBounds(v, g);

		g.drawString(v, (int) (x + w - 10 - r.getWidth()),
				(int) (y + r.getHeight()));

		Stroke old = g.getStroke();
		g.setStroke(data.isPreoperation() || data.isPostoperation() ? prePostOperation
				: new BasicStroke((float) 0.5));
		g.drawRoundRect(x, y, w, h, 20, 20);
		g.setStroke(old);
	}

	@Override
	public boolean hitSwimlaneContent(mxGraphComponent graphComponent,
			mxCellState swimlane, int x, int y) {
		return super.hitSwimlaneContent(graphComponent, swimlane, x, y);
	}
}
