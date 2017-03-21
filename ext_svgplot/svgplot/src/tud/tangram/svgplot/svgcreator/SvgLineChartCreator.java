package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.options.SvgLineChartOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.svgpainter.SvgLinesPainter;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;

public class SvgLineChartCreator extends SvgGridCreator {

	private SvgLineChartOptions options;
	
	public SvgLineChartCreator(SvgLineChartOptions options) {
		super(options);
		this.options = options;
	}
	
	public static final SvgCreatorInstantiator INSTANTIATOR = new SvgCreatorInstantiator() {		
		@Override
		public SvgCreator instantiateCreator(SvgPlotOptions rawOptions) {
			SvgLineChartOptions lcOptions = new SvgLineChartOptions(rawOptions);
			return new SvgLineChartCreator(lcOptions);
		}
	};
	
	@Override
	protected void create() {
		super.create();
		
		// Paint the lines into the svg file
		SvgLinesPainter svgLinesPainter = new SvgLinesPainter(cs, options.points);
		svgLinesPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		
		// Paint the scatter plot points to the SVG file
		SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points, PointPlotStyle.DOTS);
		svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgPointsPainter.addOverlaysToList(overlays);

		// Add the legend items for the scatter plot
		svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);
	}
	
	@Override
	protected AxisStyle getXAxisStyle() {
		return AxisStyle.BOX;
	}

	@Override
	protected AxisStyle getYAxisStyle() {
		return AxisStyle.BOX;
	}

}
