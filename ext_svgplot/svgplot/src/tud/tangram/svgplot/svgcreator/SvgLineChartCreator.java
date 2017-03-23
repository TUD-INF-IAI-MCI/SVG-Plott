package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.SvgLineChartOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.svgpainter.SvgLinesPainter;
import tud.tangram.svgplot.svgpainter.SvgOverlayPainter;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;
import tud.tangram.svgplot.utils.Constants;

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

		// Calculate whether it is possible to paint dots according to the
		// minimal line section length and the point count. Only reliable for
		// regular lines going from left to right.
		double realWidth = cs.convertXDistance(cs.xAxis.range.distance());
		double maxAllowedPointCount = realWidth / Constants.MIN_LINE_LENGTH;
		double maxPointCount = Double.NEGATIVE_INFINITY;
		for (PointList pointList : options.points) {
			maxPointCount = Math.max(maxPointCount, pointList.size());
		}
		boolean drawPoints = maxPointCount <= maxAllowedPointCount;

		// Paint the scatter plot points to the SVG file if possible, else only add overlays.
		if (drawPoints) {
			SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points, PointPlotStyle.DOTS);
			svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
			svgPointsPainter.addOverlaysToList(overlays);
			
			// Add the legend items for the scatter plot
			svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);
		}
		else {
			OverlayList overlayList = new OverlayList(cs);
			for(PointList pointList : options.points) {
				for(Point point : pointList)
					overlays.add(new Overlay(point), true);
			}
			
			SvgOverlayPainter svgOverlayPainter = new SvgOverlayPainter(cs, overlayList);
			svgOverlayPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		}
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
