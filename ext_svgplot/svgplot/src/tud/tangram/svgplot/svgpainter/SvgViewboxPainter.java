package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgViewboxPainter extends SvgPainter {

	CoordinateSystem cs;
	Point size;
	Element viewbox;
	
	public SvgViewboxPainter(CoordinateSystem cs, Point size) {
		this.cs = cs;
		this.size = size;
	}
	
	@Override
	protected String getPainterName() {
		return "Viewbox Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		return new HashMap<>();
	}
	
	public Element getViewbox() {
		return viewbox;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		this.viewbox = createViewbox(doc);
	}

	private Element createViewbox(SvgDocument doc) {
		Element newViewbox = (Element) doc.appendChild(doc.createElement("svg"));
		newViewbox.setAttribute("viewBox",
				"0 0 " + SvgTools.format2svg(size.getX()) + " " + SvgTools.format2svg(size.getY()));

		Node defs = newViewbox.appendChild(doc.createElement("defs"));

		Node clipPath = defs.appendChild(doc.createElement("clipPath", "plot-area"));
		Element rect = (Element) clipPath.appendChild(doc.createElement("rect"));
		Point topLeft = cs.convert(cs.xAxis.getRange().getFrom(), cs.yAxis.getRange().getTo());
		Point bottomRight = cs.convert(cs.xAxis.getRange().getTo(), cs.yAxis.getRange().getFrom());
		rect.setAttribute("x", topLeft.x());
		rect.setAttribute("y", topLeft.y());
		rect.setAttribute("width", SvgTools.format2svg(bottomRight.getX() - topLeft.getX()));
		rect.setAttribute("height", SvgTools.format2svg(bottomRight.getY() - topLeft.getY()));
		
		return newViewbox;
	}
}
