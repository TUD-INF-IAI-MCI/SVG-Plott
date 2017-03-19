package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.options.SvgMultiScatterPlotOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;

public class SvgMultiScatterPlotCreator extends SvgGridCreator {

	protected final SvgMultiScatterPlotOptions options;

	public SvgMultiScatterPlotCreator(SvgMultiScatterPlotOptions options) {
		super(options);
		this.options = options;
	}
	
	public static final SvgCreatorInstantiator INSTANTIATOR = new SvgCreatorInstantiator() {
		public SvgCreator instantiateCreator(SvgPlotOptions rawOptions) {
			SvgMultiScatterPlotOptions msOptions = new SvgMultiScatterPlotOptions(rawOptions);
			return new SvgMultiScatterPlotCreator(msOptions);
		}
	};
	
	@Override
	protected AxisStyle getXAxisStyle() {
		if(getPointPlotStyle() == PointPlotStyle.DOTS)
			return AxisStyle.BOX;
		else
			return AxisStyle.BOX_MIDDLE;
	}

	@Override
	protected AxisStyle getYAxisStyle() {
		if(getPointPlotStyle() == PointPlotStyle.DOTS)
			return AxisStyle.BOX;
		else
			return AxisStyle.BOX_MIDDLE;
	}

	@Override
	protected void create() {
		super.create();

		// Paint the scatter plot points to the SVG file
		SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points, getPointPlotStyle());
		svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgPointsPainter.addOverlaysToList(overlays);

		// Add the legend items for the scatter plot
		svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);
	}
	
	private PointPlotStyle getPointPlotStyle() {
		if (options.points.size() <= 1 && options.points.get(0) != null && options.points.get(0).size() > 15)
			return PointPlotStyle.DOTS;
		else
			return PointPlotStyle.MULTI_ROWS;
	}
}
