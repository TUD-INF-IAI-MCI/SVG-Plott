package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.options.SvgMultiScatterPlotOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;

public class SvgMultiScatterPlotCreator extends SvgGridCreator {

	protected final SvgMultiScatterPlotOptions options;

	public SvgMultiScatterPlotCreator(SvgMultiScatterPlotOptions options) {
		super(options);
		this.options = options;
	}
	
	public static SvgCreatorInstantiator INSTANTIATOR = new SvgCreatorInstantiator() {
		public SvgCreator instantiateCreator(SvgPlotOptions rawOptions) {
			SvgMultiScatterPlotOptions options = new SvgMultiScatterPlotOptions(rawOptions);
			SvgMultiScatterPlotCreator creator = new SvgMultiScatterPlotCreator(options);
			return creator;
		}
	};
	
	@Override
	protected AxisStyle getXAxisStyle() {
		return AxisStyle.BOX_MIDDLE;
	}

	@Override
	protected AxisStyle getYAxisStyle() {
		return AxisStyle.BOX_MIDDLE;
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
