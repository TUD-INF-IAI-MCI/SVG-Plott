package tud.tangram.svgplot.svgpainter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.GridStyle;
import tud.tangram.svgplot.svgcreator.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * Paints a grid and axes to the SVG Document. Leaves the legend untouched.
 */
public class SvgGridPainter extends SvgPainter {

	private Range xRange, yRange;
	private CoordinateSystem cs;
	private AxisStyle xAxisStyle;
	private AxisStyle yAxisStyle;
	private GridStyle gridStyle;

	@Override
	protected String getPainterName() {
		return "Grid Painter";
	}

	/**
	 * Input all needed parameters. The {@link AxisStyle} and {@link GridStyle}
	 * params may be <code>null</code> leading to display everything.
	 * 
	 * @param cs
	 * @param xRange
	 * @param yRange
	 * @param xAxisStyle
	 *            may be <code>null</code>
	 * @param yAxisStyle
	 *            may be <code>null</code>
	 * @param gridStyle
	 *            may be <code>null</code>
	 */
	public SvgGridPainter(CoordinateSystem cs, Range xRange, Range yRange, AxisStyle xAxisStyle, AxisStyle yAxisStyle,
			GridStyle gridStyle) {
		super();
		this.xRange = xRange;
		this.yRange = yRange;
		this.cs = cs;
		this.gridStyle = gridStyle != null ? gridStyle : new GridStyle();
		this.yAxisStyle = yAxisStyle != null ? yAxisStyle : new AxisStyle();
		this.xAxisStyle = xAxisStyle != null ? xAxisStyle : new AxisStyle();
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
	public void setupDeviceCss() {
		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions.append("#grid { stroke: #777777; }").append(System.lineSeparator());
		defaultOptions.append("#axes, #reference-lines, .box { stroke: #111111; fill: transparent; }")
				.append(System.lineSeparator());
		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
	}

	/**
	 * Adds the grid overlays to the specified list.
	 * 
	 * @param overlays
	 *            list where the overlays shall be added to
	 */
	public void addOverlaysToList(OverlayList overlays) {
		overlays.add(new Overlay(0, 0));
		for (double tic : cs.xAxis.ticLines()) {
			overlays.add(new Overlay(tic, 0));
		}
		for (double tic : cs.yAxis.ticLines()) {
			overlays.add(new Overlay(0, tic));
		}
	}

	/**
	 * Paint the grid to the svg file.
	 */
	private Node createGrid(SvgDocument doc, Element viewbox) {
		double dotDistance;
		int factor;

		Node grid = viewbox.appendChild(doc.createGroup("grid"));

		if (gridStyle.showX) {
			Element xGrid = (Element) grid.appendChild(doc.createGroup("x-grid"));
			dotDistance = cs.convertYDistance(cs.yAxis.gridInterval);
			factor = (int) (dotDistance / 2.3);
			dotDistance = (dotDistance - factor * Constants.strokeWidth) / factor;
			xGrid.setAttribute("stroke-dasharray", Constants.strokeWidth + ", " + SvgTools.format2svg(dotDistance));
			for (double pos : cs.xAxis.gridLines()) {
				Point from = cs.convert(pos, cs.yAxis.range.to, 0, -Constants.strokeWidth / 2);
				Point to = cs.convert(pos, cs.yAxis.range.from, 0, Constants.strokeWidth / 2);
				xGrid.appendChild(doc.createLine(from, to));
			}
		}

		if (gridStyle.showY) {
			Element yGrid = (Element) grid.appendChild(doc.createGroup("y-grid"));
			dotDistance = cs.convertXDistance(cs.xAxis.gridInterval);
			factor = (int) (dotDistance / 2.3);
			dotDistance = (dotDistance - factor * Constants.strokeWidth) / factor;
			yGrid.setAttribute("stroke-dasharray", Constants.strokeWidth + ", " + SvgTools.format2svg(dotDistance));
			for (double pos : cs.yAxis.gridLines()) {
				Point from = cs.convert(cs.xAxis.range.from, pos, -Constants.strokeWidth / 2, 0);
				Point to = cs.convert(cs.xAxis.range.to, pos, Constants.strokeWidth / 2, 0);
				yGrid.appendChild(doc.createLine(from, to));
			}
		}

		return grid;
	}

	/**
	 * Paint the axes to the svg file.
	 */
	private Node createAxes(SvgDocument doc, Element viewbox) {
		Node axes = viewbox.appendChild(doc.createGroup("axes"));
		Point from, to;
		String points;

		from = cs.convert(xRange.from, 0, -15, 0);
		to = cs.convert(xRange.to, 0, 10, 0);

		// Create the x axis line
		if (xAxisStyle.axis) {
			Element xAxisLine = doc.createLine(from, to);
			axes.appendChild(xAxisLine);
			xAxisLine.setAttribute("id", "x-axis");
		}

		// Create the x arrow
		if (xAxisStyle.arrow) {
			Element xAxisArrow = doc.createElement("polyline", "x-axis-arrow");
			axes.appendChild(xAxisArrow);
			to.translate(0, -3);
			points = to.toString();
			to.translate(5.2, 3);
			points += " " + to;
			to.translate(-5.2, 3);
			points += " " + to;
			xAxisArrow.setAttribute("points", points);
			xAxisArrow.appendChild(doc.createTitle(SvgTools.translate("xaxis")));
		}

		// Create the x label
		if (xAxisStyle.label) {
			Point pos2 = to;
			pos2.translate(0, 13);
			doc.appendChild(doc.createLabel("x", pos2, "x_label", "label"));
		}

		from = cs.convert(0, yRange.from, 0, 15);
		to = cs.convert(0, yRange.to, 0, -10);

		// Create the y axis line
		if (yAxisStyle.axis) {
			Element yAxisLine = doc.createLine(from, to);
			axes.appendChild(yAxisLine);
			yAxisLine.setAttribute("id", "y-axis");
		}

		// Create the y arrow
		if (yAxisStyle.arrow) {
			Element yAxisArrow = doc.createElement("polyline", "y-axis-arrow");
			axes.appendChild(yAxisArrow);
			to.translate(-3, 0);
			points = to.toString();
			to.translate(3, -5.2);
			points += " " + to;
			to.translate(3, 5.2);
			points += " " + to;
			yAxisArrow.setAttribute("points", points);
			yAxisArrow.appendChild(doc.createTitle(SvgTools.translate("yaxis")));
		}

		// Create the y label
		if (yAxisStyle.label) {
			Point pos3 = to;
			pos3.translate(-15, 0);
			doc.appendChild(doc.createLabel("y", pos3, "y_label", "label"));
		}

		// Create the x tics
		if (xAxisStyle.tics) {
			Node xTics = axes.appendChild(doc.createGroup("x-tics"));
			for (double pos : cs.xAxis.ticLines()) {
				from = cs.convert(pos, 0, 0, -6);
				to = from.clone();
				to.translate(0, 12);
				xTics.appendChild(doc.createLine(from, to));
			}
		}

		if (yAxisStyle.tics) {
			Node yTics = axes.appendChild(doc.createGroup("y-tics"));
			for (double pos : cs.yAxis.ticLines()) {
				from = cs.convert(0, pos, -6, 0);
				to = from.clone();
				to.translate(12, 0);
				yTics.appendChild(doc.createLine(from, to));
			}
		}

		// Duplicate axes and append them as spacers
		Element axesClone = (Element) axes.cloneNode(true);
		axesClone.setAttribute("id", "axes_spacer");
		axesClone.setAttribute("class", axesClone.hasAttribute("class")
				? axesClone.getAttribute("class") + " " + Constants.spacerCssClass : Constants.spacerCssClass);

		axes.insertBefore(axesClone, axes.getFirstChild());

		return axes;
	}

}
