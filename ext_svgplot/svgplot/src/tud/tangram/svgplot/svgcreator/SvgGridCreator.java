package tud.tangram.svgplot.svgcreator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.SvgGridOptions;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.svgpainter.SvgGridPainter;
import tud.tangram.svgplot.svgpainter.SvgOverlayPainter;
import tud.tangram.svgplot.svgpainter.SvgViewboxPainter;

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
		
		SvgViewboxPainter svgViewboxPainter = new SvgViewboxPainter(cs, options.size);
		svgViewboxPainter.paintToSvgDocument(doc, null, options.outputDevice);
		viewbox = svgViewboxPainter.getViewbox();
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
	
}
