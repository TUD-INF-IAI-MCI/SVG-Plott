package tud.tangram.svgplot.svgcreator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.SvgGraphOptions;
import tud.tangram.svgplot.options.SvgGridOptions;
import tud.tangram.svgplot.svgpainter.SvgGridPainter;
import tud.tangram.svgplot.xml.SvgDocument;

public abstract class SvgGridCreator extends SvgCreator {
	protected final SvgGridOptions options;
	protected SvgGridPainter svgGridPainter;
	
	protected Element viewbox;
	
	protected CoordinateSystem cs;

	public CoordinateSystem getCs() {
		return cs;
	}
	
	public SvgGridCreator(SvgGridOptions options) {
		super(options);
		this.options = options;
	}
	
	protected void trySetupGridPainter() {
		if(svgGridPainter == null) {
			svgGridPainter = new SvgGridPainter(cs, options.xRange, options.yRange, null, null, null);
		}
	}
	
	@Override
	public SvgDocument create() throws ParserConfigurationException, IOException, DOMException, InterruptedException {

		options.xRange.from = Math.min(0, options.xRange.from);
		options.xRange.to = Math.max(0, options.xRange.to);
		options.yRange.from = Math.min(0, options.yRange.from);
		options.yRange.to = Math.max(0, options.yRange.to);

		cs = new CoordinateSystem(options.xRange, options.yRange, options.size, diagramContentMargin);
		
		createViewbox();
		
		trySetupGridPainter();
		svgGridPainter.paintToSvgDocument(doc, viewbox);
		
		return doc;
	}

	@Override
	protected void createCss(SvgDocument doc) throws IOException {
		trySetupGridPainter();
	}
	
	private void createViewbox() {
		viewbox = (Element) doc.appendChild(doc.createElement("svg"));
		viewbox.setAttribute("viewBox",
				"0 0 " + SvgTools.format2svg(options.size.x) + " " + SvgTools.format2svg(options.size.y));

		Node defs = viewbox.appendChild(doc.createElement("defs"));

		Node clipPath = defs.appendChild(doc.createElement("clipPath", "plot-area"));
		Element rect = (Element) clipPath.appendChild(doc.createElement("rect"));
		Point topLeft = cs.convert(cs.xAxis.range.from, cs.yAxis.range.to);
		Point bottomRight = cs.convert(cs.xAxis.range.to, cs.yAxis.range.from);
		rect.setAttribute("x", topLeft.x());
		rect.setAttribute("y", topLeft.y());
		rect.setAttribute("width", SvgTools.format2svg(bottomRight.x - topLeft.x));
		rect.setAttribute("height", SvgTools.format2svg(bottomRight.y - topLeft.y));
	}
	
}
