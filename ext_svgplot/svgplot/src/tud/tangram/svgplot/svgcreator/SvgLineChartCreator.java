package tud.tangram.svgplot.svgcreator;

import java.util.Map;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.SvgLineChartOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.svgpainter.SvgLineOverlayPainter;
import tud.tangram.svgplot.svgpainter.SvgLinesPainter;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;
import tud.tangram.svgplot.utils.Constants;

public class SvgLineChartCreator extends SvgGridCreator {

	private SvgLineChartOptions options;
	private Map<PointList, String> polyLineStrings;

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
		svgLinesPainter.prepareLegendRenderer(getLegendRenderer(), options.outputDevice);

		// Save the data needed for the overlays
		polyLineStrings = svgLinesPainter.getLineDataForOverlayCreation();

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

		// Paint the scatter plot points to the SVG file if possible.
		if (drawPoints) {
			SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points, PointPlotStyle.DOTS);
			svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
			svgPointsPainter.addOverlaysToList(overlays);

			// The legend items for the scatter plot are not added
		}
		// If only the lines and no visible data plot points can be drawn, add overlays for the data points.
		else {
			for(PointList pointList : options.points) {
				for(Point point : pointList)
					overlays.add(new Overlay(point), true);
			}
		}
	}

	/**
	 * Paint the line overlays
	 */
	@Override
	protected void afterCreate() {
		// Paint the line overlays before the point overlays are painted in the super class
		SvgLineOverlayPainter svgLineOverlayPainter = new SvgLineOverlayPainter(cs, polyLineStrings);
		svgLineOverlayPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		
		super.afterCreate();
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
