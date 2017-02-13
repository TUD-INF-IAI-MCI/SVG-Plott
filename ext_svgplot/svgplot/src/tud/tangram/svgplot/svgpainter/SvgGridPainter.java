package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgGridPainter extends SvgPainter {

	private Range xRange, yRange;
	
	public SvgGridPainter(Range xRange, Range yRange) {
		super();
		this.xRange = xRange;
		this.yRange = yRange;
	}
	
	@Override
	protected void paintToSvgDocument(SvgDocument doc, Element viewbox) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintToSvgLegend(SvgDocument legend) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setupDeviceCss() {
		deviceCss = new HashMap<>();
		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions.append("#grid { stroke: #777777; }").append(System.lineSeparator());
		defaultOptions.append("#axes, #reference-lines, .box { stroke: #111111; fill: transparent; }").append(System.lineSeparator());
		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
	}
	
	/**
	 * Paint the grid to the svg file.
	 */
	/*private Node createGrid(SvgDocument doc, Element viewbox) {
		Node grid = viewbox.appendChild(doc.createGroup("grid"));

		Element xGrid = (Element) grid.appendChild(doc.createGroup("x-grid"));
		double dotDistance = cs.convertYDistance(cs.yAxis.gridInterval);
		int factor = (int) (dotDistance / 2.3);
		dotDistance = (dotDistance - factor * Constants.strokeWidth) / factor;
		xGrid.setAttribute("stroke-dasharray", Constants.strokeWidth + ", " + format2svg(dotDistance));
		for (double pos : cs.xAxis.gridLines()) {
			Point from = cs.convert(pos, cs.yAxis.range.to, 0, -Constants.strokeWidth / 2);
			Point to = cs.convert(pos, cs.yAxis.range.from, 0, Constants.strokeWidth / 2);
			xGrid.appendChild(doc.createLine(from, to));
		}

		Element yGrid = (Element) grid.appendChild(doc.createGroup("y-grid"));
		dotDistance = cs.convertXDistance(cs.xAxis.gridInterval);
		factor = (int) (dotDistance / 2.3);
		dotDistance = (dotDistance - factor * Constants.strokeWidth) / factor;
		yGrid.setAttribute("stroke-dasharray", Constants.strokeWidth + ", " + format2svg(dotDistance));
		for (double pos : cs.yAxis.gridLines()) {
			Point from = cs.convert(cs.xAxis.range.from, pos, -Constants.strokeWidth / 2, 0);
			Point to = cs.convert(cs.xAxis.range.to, pos, Constants.strokeWidth / 2, 0);
			yGrid.appendChild(doc.createLine(from, to));
		}
		return grid;
	}*/

	/**
	 * Paint the axes to the svg file.
	 */
	/*private Node createAxes(SvgDocument doc, Element viewbox) {
		Node axes = viewbox.appendChild(doc.createGroup("axes"));
		Point from, to;
		String points;

		from = cs.convert(xRange.from, 0, -15, 0);
		to = cs.convert(xRange.to, 0, 10, 0);
		Element xAxisLine = doc.createLine(from, to);
		axes.appendChild(xAxisLine);
		xAxisLine.setAttribute("id", "x-axis");
		Element xAxisArrow = doc.createElement("polyline", "x-axis-arrow");
		axes.appendChild(xAxisArrow);
		to.translate(0, -3);
		points = to.toString();
		to.translate(5.2, 3);
		points += " " + to;
		to.translate(-5.2, 3);
		points += " " + to;
		xAxisArrow.setAttribute("points", points);
		xAxisArrow.appendChild(doc.createTitle(translate("xaxis")));

		// create x-label
		Point pos2 = to;
		pos2.translate(0, 13);
		doc.appendChild(createLabel("x", pos2, "x_label", "label"));

		from = cs.convert(0, yRange.from, 0, 15);
		to = cs.convert(0, yRange.to, 0, -10);
		Element yAxisLine = doc.createLine(from, to);
		axes.appendChild(yAxisLine);
		yAxisLine.setAttribute("id", "y-axis");
		Element yAxisArrow = doc.createElement("polyline", "y-axis-arrow");
		axes.appendChild(yAxisArrow);
		to.translate(-3, 0);
		points = to.toString();
		to.translate(3, -5.2);
		points += " " + to;
		to.translate(3, 5.2);
		points += " " + to;
		yAxisArrow.setAttribute("points", points);
		yAxisArrow.appendChild(doc.createTitle(translate("yaxis")));

		// create y-label
		Point pos3 = to;
		pos3.translate(-15, 0);
		doc.appendChild(createLabel("y", pos3, "y_label", "label"));

		Node xTics = axes.appendChild(doc.createGroup("x-tics"));
		for (double pos : cs.xAxis.ticLines()) {
			from = cs.convert(pos, 0, 0, -6);
			to = from.clone();
			to.translate(0, 12);
			xTics.appendChild(doc.createLine(from, to));
		}

		Node yTics = axes.appendChild(doc.createGroup("y-tics"));
		for (double pos : cs.yAxis.ticLines()) {
			from = cs.convert(0, pos, -6, 0);
			to = from.clone();
			to.translate(12, 0);
			yTics.appendChild(doc.createLine(from, to));
		}

		// Duplicate axes and append them as spacers
		Element axesClone = (Element) axes.cloneNode(true);
		axesClone.setAttribute("id", "axes_spacer");
		axesClone.setAttribute("class", axesClone.hasAttribute("class")
				? axesClone.getAttribute("class") + " " + Constants.spacerCssClass : Constants.spacerCssClass);

		axes.insertBefore(axesClone, xAxisLine);

		return axes;
	}*/

}
