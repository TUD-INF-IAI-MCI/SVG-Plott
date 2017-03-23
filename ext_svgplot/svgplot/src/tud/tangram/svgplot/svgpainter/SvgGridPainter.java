package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.legend.LegendTextItem;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.GridStyle;
import tud.tangram.svgplot.styles.TicStyle;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * Paints a grid and axes to the SVG Document. Leaves the legend untouched.
 */
public class SvgGridPainter extends SvgPainter {

	private static final String CLASS = "class";

	private static final String AXES_ID = "axes";

	private static final String X_AXIS_ARROW_ID = "x-axis-arrow";
	private static final String X_AXIS_BIGGER = "x-axis-bigger";
	private static final String X_AXIS_SMALLER_ID = "x-axis-smaller";
	private static final String X_AXIS_ID = "x-axis";
	private static final String X_TICS_ID = "x-tics";

	private static final String Y_AXIS_ARROW_ID = "y-axis-arrow";
	private static final String Y_AXIS_BIGGER = "y-axis-bigger";
	private static final String Y_AXIS_SMALLER_ID = "y-axis-smaller";
	private static final String Y_AXIS_ID = "y-axis";
	private static final String Y_TICS_ID = "y-tics";

	private Range xRange;
	private Range yRange;
	private CoordinateSystem cs;
	private AxisStyle xAxisStyle;
	private AxisStyle yAxisStyle;
	private GridStyle gridStyle;

	/**
	 * Input all needed parameters. The {@link AxisStyleSettings} and
	 * {@link GridStyle} params may be <code>null</code> leading to display
	 * everything.
	 * 
	 * @param cs
	 * @param xRange
	 * @param yRange
	 * @param xAxisStyle
	 * @param yAxisStyle
	 * @param gridStyle
	 *            may be <code>null</code>
	 */
	public SvgGridPainter(CoordinateSystem cs, AxisStyle xAxisStyle, AxisStyle yAxisStyle,
			GridStyle gridStyle) {
		super();
		this.cs = cs;
		this.xRange = cs.xAxis.range;
		this.yRange = cs.yAxis.range;
		this.gridStyle = gridStyle != null ? gridStyle : new GridStyle();
		this.yAxisStyle = yAxisStyle;
		this.xAxisStyle = xAxisStyle;
	}

	@Override
	protected String getPainterName() {
		return "Grid Painter";
	}

	/**
	 * Paint the grid and the axes to the doc.
	 */
	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		createGrid(doc, viewbox);
		createAxes(doc, viewbox);
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions.append("#grid { stroke: #777777; }").append(System.lineSeparator());
		defaultOptions.append("#axes, #reference-lines, .box { stroke: #111111; fill: transparent; }")
				.append(System.lineSeparator());
		deviceCss.put(OutputDevice.Default, defaultOptions.toString());

		StringBuilder screenHighContrastOptions = new StringBuilder();
		screenHighContrastOptions.append("#axes, #reference-lines, .box { stroke: white; }")
				.append(System.lineSeparator());
		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastOptions.toString());

		return deviceCss;
	}

	/**
	 * Adds the grid overlays to the specified list.
	 * 
	 * @param overlays
	 *            list where the overlays shall be added to
	 */
	public void addOverlaysToList(OverlayList overlays) {
		overlays.add(new Overlay(0, 0));
		for(double x : new double[]{xRange.getFrom(), xRange.getTo()}) {
			for(double y : new double[]{yRange.getFrom(), yRange.getTo()})
				overlays.add(new Overlay(x, y));
		}
		for (double tic : cs.xAxis.ticLines()) {
			overlays.add(new Overlay(tic, 0));
			overlays.add(new Overlay(tic, yRange.getFrom()));
			overlays.add(new Overlay(tic, yRange.getTo()));
		}
		for (double tic : cs.yAxis.ticLines()) {
			overlays.add(new Overlay(0, tic));
			overlays.add(new Overlay(xRange.getFrom(), tic));
			overlays.add(new Overlay(xRange.getTo(), tic));
		}
	}

	/**
	 * Paint the grid to the svg file.
	 */
	private Node createGrid(SvgDocument doc, Element viewbox) {
		double dotDistance;
		int factor;

		Node grid = viewbox.appendChild(doc.createGroup("grid"));

		// Skip the zero grid line in x direction if there is an axis line
		boolean skipZeroX = xAxisStyle == AxisStyle.EDGE_MIDDLE || xAxisStyle == AxisStyle.BOX_MIDDLE
				|| xAxisStyle == AxisStyle.GRAPH;
		
		if (gridStyle.showX) {
			Element xGrid = (Element) grid.appendChild(doc.createGroup("x-grid"));
			dotDistance = cs.convertYDistance(cs.yAxis.gridInterval);
			factor = (int) (dotDistance / 2.3);
			dotDistance = (dotDistance - factor * Constants.STROKE_WIDTH) / factor;
			xGrid.setAttribute("stroke-dasharray", Constants.STROKE_WIDTH + ", " + SvgTools.format2svg(dotDistance));
			for (double pos : cs.xAxis.gridLines()) {
				if (skipZeroX && Math.abs(pos) < Constants.EPSILON)
					continue;
				Point from = cs.convert(pos, cs.yAxis.range.getTo(), 0, -Constants.STROKE_WIDTH / 2);
				Point to = cs.convert(pos, cs.yAxis.range.getFrom(), 0, Constants.STROKE_WIDTH / 2);
				xGrid.appendChild(doc.createLine(from, to));
			}
		}

		// Skip the zero grid line in y direction if there is an axis line
		boolean skipZeroY = yAxisStyle == AxisStyle.EDGE_MIDDLE || yAxisStyle == AxisStyle.BOX_MIDDLE
				|| yAxisStyle == AxisStyle.GRAPH;
		
		if (gridStyle.showY) {
			Element yGrid = (Element) grid.appendChild(doc.createGroup("y-grid"));
			dotDistance = cs.convertXDistance(cs.xAxis.gridInterval);
			factor = (int) (dotDistance / 2.3);
			dotDistance = (dotDistance - factor * Constants.STROKE_WIDTH) / factor;
			yGrid.setAttribute("stroke-dasharray", Constants.STROKE_WIDTH + ", " + SvgTools.format2svg(dotDistance));
			for (double pos : cs.yAxis.gridLines()) {
				if (skipZeroY && Math.abs(pos) < Constants.EPSILON)
					continue;
				Point from = cs.convert(cs.xAxis.range.getFrom(), pos, -Constants.STROKE_WIDTH / 2, 0);
				Point to = cs.convert(cs.xAxis.range.getTo(), pos, Constants.STROKE_WIDTH / 2, 0);
				yGrid.appendChild(doc.createLine(from, to));
			}
		}

		return grid;
	}

	/**
	 * Paint the axes to the svg file.
	 */
	private Node createAxes(SvgDocument doc, Element viewbox) {
		Element axes = doc.getOrCreateChildGroupById(viewbox, AXES_ID);

		createXAxis(doc, axes);
		createYAxis(doc, axes);

		// Duplicate axes and append them as spacers
		Element axesClone = (Element) axes.cloneNode(true);
		axesClone.setAttribute("id", "axes_spacer");
		axesClone.setAttribute(CLASS, axesClone.hasAttribute(CLASS)
				? axesClone.getAttribute(CLASS) + " " + Constants.SPACER_CSS_CLASS : Constants.SPACER_CSS_CLASS);

		axes.insertBefore(axesClone, axes.getFirstChild());

		return axes;
	}

	private void createXAxis(SvgDocument doc, Element axes) {
		Point from = cs.convert(xRange.getFrom(), 0, -15, 0);
		Point to = cs.convert(xRange.getTo(), 0, 10, 0);

		Point fromSmaller = cs.convert(xRange.getFrom(), yRange.getFrom(), -15, 0);
		Point toSmaller = cs.convert(xRange.getTo(), yRange.getFrom(), 10, 0);

		Point fromBigger = cs.convert(xRange.getFrom(), yRange.getTo(), -15, 0);
		Point toBigger = cs.convert(xRange.getTo(), yRange.getTo(), 10, 0);

		// Create the x axis line
		if (xAxisStyle == AxisStyle.BOX_MIDDLE || xAxisStyle == AxisStyle.EDGE_MIDDLE || xAxisStyle == AxisStyle.GRAPH)
			createAxisLine(doc, axes, X_AXIS_ID, from, to);

		if (xAxisStyle != AxisStyle.GRAPH)
			createAxisLine(doc, axes, X_AXIS_SMALLER_ID, fromSmaller, toSmaller);

		if (xAxisStyle == AxisStyle.BOX || xAxisStyle == AxisStyle.BOX_MIDDLE)
			createAxisLine(doc, axes, X_AXIS_BIGGER, fromBigger, toBigger);

		// Create the x arrow
		if (xAxisStyle == AxisStyle.GRAPH) {
			Element xAxisArrow = doc.createElement("polyline", X_AXIS_ARROW_ID);
			axes.appendChild(xAxisArrow);

			Point arrowPos = new Point(to);

			arrowPos.translate(0, -3);
			String points = arrowPos.toString();
			arrowPos.translate(5.2, 3);
			points += " " + arrowPos;
			arrowPos.translate(-5.2, 3);
			points += " " + arrowPos;

			xAxisArrow.setAttribute("points", points);
			xAxisArrow.appendChild(doc.createTitle(SvgTools.translate("xaxis")));
		}

		// Create the x label according to the axis style
		if (xAxisStyle == AxisStyle.GRAPH) {
			Point pos2 = cs.convert(xRange.getTo(), 0, 10, 16);
			doc.appendChild(doc.createLabel("x", pos2, "x_label", "label"));
		}

		// Create the x tics according to the axis style
		boolean skipZero = yAxisStyle == AxisStyle.EDGE_MIDDLE || yAxisStyle == AxisStyle.BOX_MIDDLE
				|| yAxisStyle == AxisStyle.GRAPH;
		if (xAxisStyle == AxisStyle.GRAPH)
			createXTics(doc, axes, TicStyle.BOTH_SIDES, X_TICS_ID, 0, skipZero);
		else
			createXTics(doc, axes, TicStyle.SMALLER_SIDE, X_TICS_ID, yRange.getFrom(), skipZero);

		if (xAxisStyle == AxisStyle.BOX || xAxisStyle == AxisStyle.BOX_MIDDLE)
			createXTics(doc, axes, TicStyle.BIGGER_SIDE, X_TICS_ID, yRange.getTo(), skipZero);
	}

	private void createYAxis(SvgDocument doc, Element axes) {
		Point from = cs.convert(0, yRange.getFrom(), 0, 15);
		Point to = cs.convert(0, yRange.getTo(), 0, -10);

		Point fromSmaller = cs.convert(xRange.getFrom(), yRange.getFrom(), 0, 15);
		Point toSmaller = cs.convert(xRange.getFrom(), yRange.getTo(), 0, -10);

		Point fromBigger = cs.convert(xRange.getTo(), yRange.getFrom(), 0, 15);
		Point toBigger = cs.convert(xRange.getTo(), yRange.getTo(), 0, -10);

		// Create the y axis line
		if (yAxisStyle == AxisStyle.BOX_MIDDLE || yAxisStyle == AxisStyle.EDGE_MIDDLE || yAxisStyle == AxisStyle.GRAPH)
			createAxisLine(doc, axes, Y_AXIS_ID, from, to);

		if (yAxisStyle != AxisStyle.GRAPH)
			createAxisLine(doc, axes, Y_AXIS_SMALLER_ID, fromSmaller, toSmaller);

		if (yAxisStyle == AxisStyle.BOX || yAxisStyle == AxisStyle.BOX_MIDDLE)
			createAxisLine(doc, axes, Y_AXIS_BIGGER, fromBigger, toBigger);

		// Create the y arrow
		if (yAxisStyle == AxisStyle.GRAPH) {
			Element yAxisArrow = doc.createElement("polyline", Y_AXIS_ARROW_ID);
			axes.appendChild(yAxisArrow);

			Point arrowPos = new Point(to);

			arrowPos.translate(-3, 0);
			String points = arrowPos.toString();
			arrowPos.translate(3, -5.2);
			points += " " + arrowPos;
			arrowPos.translate(3, 5.2);
			points += " " + arrowPos;

			yAxisArrow.setAttribute("points", points);
			yAxisArrow.appendChild(doc.createTitle(SvgTools.translate("yaxis")));
		}

		// Create the y label according to the axis style
		if (yAxisStyle == AxisStyle.GRAPH) {
			Point pos3 = cs.convert(0, yRange.getTo(), -12, -10);
			doc.appendChild(doc.createLabel("y", pos3, "y_label", "label"));
		}

		// Create the y tics according to the axis style
		boolean skipZero = xAxisStyle == AxisStyle.EDGE_MIDDLE || xAxisStyle == AxisStyle.BOX_MIDDLE
				|| xAxisStyle == AxisStyle.GRAPH;
		if (yAxisStyle == AxisStyle.GRAPH)
			createYTics(doc, axes, TicStyle.BOTH_SIDES, Y_TICS_ID, 0, skipZero);
		else
			createYTics(doc, axes, TicStyle.SMALLER_SIDE, Y_TICS_ID, xRange.getFrom(), skipZero);

		if (yAxisStyle == AxisStyle.BOX || yAxisStyle == AxisStyle.BOX_MIDDLE)
			createYTics(doc, axes, TicStyle.BIGGER_SIDE, Y_TICS_ID, xRange.getTo(), skipZero);
	}

	@Override
	public void prepareLegendRenderer(LegendRenderer renderer, OutputDevice device, int priority) {
		super.prepareLegendRenderer(renderer, device, priority);

		renderer.add(new LegendTextItem(priority,
				SvgTools.translate("legend.xrange", SvgTools.formatX(cs, cs.xAxis.range.getFrom()),
						SvgTools.formatX(cs, cs.xAxis.range.getTo()), SvgTools.formatName(cs.xAxis.range.getName())),
				SvgTools.translate("legend.xtic", SvgTools.formatX(cs, cs.xAxis.ticInterval))));

		renderer.add(new LegendTextItem(priority,
				SvgTools.translate("legend.yrange", SvgTools.formatY(cs, cs.yAxis.range.getFrom()),
						SvgTools.formatY(cs, cs.yAxis.range.getTo()), SvgTools.formatName(cs.yAxis.range.getName())),
				SvgTools.translate("legend.ytic", SvgTools.formatY(cs, cs.yAxis.ticInterval))));
	}

	/**
	 * Create tics for an x axis at the specified y position. The tics are
	 * rendered according to {@code ticStyle}.
	 * 
	 * @param doc
	 *            the SVG document
	 * @param parent
	 *            the parent element of the tic group
	 * @param ticStyle
	 *            the style of the tics
	 * @param id
	 *            the id of the tic group
	 * @param yPos
	 *            the y position where the axis shall lie
	 * @param skipZero
	 *            if true, zero tics are not drawn (used when there is an axis
	 *            in zero position)
	 */
	private void createXTics(SvgDocument doc, Element parent, TicStyle ticStyle, String id, double yPos,
			boolean skipZero) {
		if (ticStyle == TicStyle.NONE)
			return;

		final double yTranslation = ticStyle == TicStyle.BOTH_SIDES ? 12 : 6;
		final double yPosShift = ticStyle == TicStyle.SMALLER_SIDE ? 0 : -6;

		Node xTics = doc.getOrCreateChildGroupById(parent, id);

		for (double pos : cs.xAxis.ticLines()) {
			if (skipZero && Math.abs(pos) < Constants.EPSILON)
				continue;
			Point from = cs.convert(pos, yPos, 0, yPosShift);
			Point to = new Point(from);
			to.translate(0, yTranslation);
			xTics.appendChild(doc.createLine(from, to));
		}
	}

	/**
	 * Create tics for an y axis at the specified y position. The tics are
	 * rendered according to {@code ticStyle}.
	 * 
	 * @param doc
	 *            the SVG document
	 * @param parent
	 *            the parent element of the tic group
	 * @param ticStyle
	 *            the style of the tics
	 * @param id
	 *            the id of the tic group
	 * @param xPos
	 *            the x position where the axis shall lie
	 * @param skipZero
	 *            if true, zero tics are not drawn (used when there is an axis
	 *            in zero position)
	 */
	private void createYTics(SvgDocument doc, Element parent, TicStyle ticStyle, String id, double xPos,
			boolean skipZero) {
		if (ticStyle == TicStyle.NONE)
			return;

		final double xTranslation = ticStyle == TicStyle.BOTH_SIDES ? 12 : 6;
		final double xPosShift = ticStyle == TicStyle.BIGGER_SIDE ? 0 : -6;

		Node yTics = doc.getOrCreateChildGroupById(parent, id);

		for (double pos : cs.yAxis.ticLines()) {
			if (skipZero && Math.abs(pos) < Constants.EPSILON)
				continue;
			Point from = cs.convert(xPos, pos, xPosShift, 0);
			Point to = new Point(from);
			to.translate(xTranslation, 0);
			yTics.appendChild(doc.createLine(from, to));
		}
	}

	private void createAxisLine(SvgDocument doc, Element parent, String id, Point from, Point to) {
		Element yAxisLine = doc.createLine(from, to);
		parent.appendChild(yAxisLine);
		yAxisLine.setAttribute("id", id);
	}

}
