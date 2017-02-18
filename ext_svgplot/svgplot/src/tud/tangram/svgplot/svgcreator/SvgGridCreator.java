package tud.tangram.svgplot.svgcreator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.SvgGridOptions;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.svgpainter.SvgGridPainter;
import tud.tangram.svgplot.svgpainter.SvgOverlayPainter;

public class SvgGridCreator extends SvgCreator {
	protected final SvgGridOptions options;
	//protected SvgGridPainter svgGridPainter;
	
	protected Element viewbox;
	
	protected CoordinateSystem cs;

	public CoordinateSystem getCs() {
		return cs;
	}
	
	protected OverlayList overlays;
	
	public SvgGridCreator(SvgGridOptions options) {
		super(options);
		this.options = options;
	}
		
	@Override
	protected void beforeCreate() {
		super.beforeCreate();
		cs = new CoordinateSystem(options.xRange, options.yRange, options.size, diagramContentMargin, options.pi);
		overlays = new OverlayList(cs);
		
		createViewbox();
	}

	@Override
	protected void create() {
		super.create();
		
		SvgGridPainter svgGridPainter = new SvgGridPainter(cs, options.xRange, options.yRange, null, null, null);
		
		svgGridPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgGridPainter.paintToSvgLegend(legend, options.outputDevice);
		
		svgGridPainter.addOverlaysToList(overlays);
	}
	
	@Override
	protected void afterCreate() {
		super.afterCreate();
		
		SvgOverlayPainter svgOverlayPainter = new SvgOverlayPainter(cs, overlays);
		svgOverlayPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
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
