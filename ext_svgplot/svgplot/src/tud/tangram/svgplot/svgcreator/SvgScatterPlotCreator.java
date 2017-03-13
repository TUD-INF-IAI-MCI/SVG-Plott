package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.options.SvgScatterPlotOptions;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;

public class SvgScatterPlotCreator extends SvgGridCreator {

	protected final SvgScatterPlotOptions options;

	public SvgScatterPlotCreator(SvgScatterPlotOptions options) {
		super(options);
		this.options = options;
	}

	@Override
	protected void create() {
		super.create();

		// Paint the scatter plot points to the SVG file
		SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points);
		svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgPointsPainter.addOverlaysToList(overlays);

		// Add the legend items for the scatter plot
		svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);
	}
}
