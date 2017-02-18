package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.svgcreator.SvgTools;
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
		viewbox = createViewbox(doc);
	}

	private Element createViewbox(SvgDocument doc) {
		Element viewbox = (Element) doc.appendChild(doc.createElement("svg"));
		viewbox.setAttribute("viewBox",
				"0 0 " + SvgTools.format2svg(size.x) + " " + SvgTools.format2svg(size.y));

		Node defs = viewbox.appendChild(doc.createElement("defs"));

		Node clipPath = defs.appendChild(doc.createElement("clipPath", "plot-area"));
		Element rect = (Element) clipPath.appendChild(doc.createElement("rect"));
		Point topLeft = cs.convert(cs.xAxis.range.from, cs.yAxis.range.to);
		Point bottomRight = cs.convert(cs.xAxis.range.to, cs.yAxis.range.from);
		rect.setAttribute("x", topLeft.x());
		rect.setAttribute("y", topLeft.y());
		rect.setAttribute("width", SvgTools.format2svg(bottomRight.x - topLeft.x));
		rect.setAttribute("height", SvgTools.format2svg(bottomRight.y - topLeft.y));
		
		return viewbox;
	}
}
