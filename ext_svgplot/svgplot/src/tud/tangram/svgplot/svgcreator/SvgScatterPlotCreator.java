package tud.tangram.svgplot.svgcreator;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.options.SvgScatterPlotOptions;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.svgpainter.SvgLineOverlayPainter;
import tud.tangram.svgplot.svgpainter.SvgLinesPainter;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;

public class SvgScatterPlotCreator extends SvgGridCreator {

	static final Logger log = LoggerFactory.getLogger(SvgScatterPlotCreator.class);

	protected final SvgScatterPlotOptions options;

	/**
	 * Used for caching the string for the line generation in order to reuse
	 * them for audio labels
	 */
	private Map<PointList, String> polyLineStrings;

	public SvgScatterPlotCreator(SvgScatterPlotOptions options) {
		super(options);
		this.options = options;
	}

	public static final SvgCreatorInstantiator INSTANTIATOR = new SvgCreatorInstantiator() {
		public SvgCreator instantiateCreator(SvgPlotOptions rawOptions) {
			SvgScatterPlotOptions spOptions = new SvgScatterPlotOptions(rawOptions);
			return new SvgScatterPlotCreator(spOptions);
		}
	};

	/**
	 * Default: show box axis, overridable with options.
	 */
	@Override
	protected AxisStyle getXAxisStyle() {
		return options.showDoubleAxes == null || "on".equals(options.showDoubleAxes) ? AxisStyle.BOX : AxisStyle.EDGE;
	}

	/**
	 * Default: show box axis, overridable with options.
	 */
	@Override
	protected AxisStyle getYAxisStyle() {
		return options.showDoubleAxes == null || "on".equals(options.showDoubleAxes) ? AxisStyle.BOX : AxisStyle.EDGE;
	}

	@Override
	protected void create() {
		super.create();

		if (options.points == null || options.points.isEmpty()) {
			log.error("Ein leeres Diagramm wird erstellt, da keine Punkte angegeben wurden.");
			return;
		}

		if (!options.hideOriginalPoints) {
			// Paint the scatter plot points to the SVG file
			SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points, getPointPlotStyle());
			svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
			svgPointsPainter.addOverlaysToList(overlays);

			// Add the legend items for the scatter plot
			svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);
		}

		// Create trend lines if an algorithm is specified
		if (options.trendLineAlgorithm != null) {
			PointListList trendLinePoints = new PointListList();
			for (PointList pl : options.points) {
				PointList trendLine = options.trendLineAlgorithm.calculateTrendLine(pl);
				trendLinePoints.add(trendLine);
			}

			SvgLinesPainter svgLinesPainter = new SvgLinesPainter(cs, trendLinePoints);
			svgLinesPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
			svgLinesPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);

			// Save the neccessary data for creating trendline overlays
			polyLineStrings = svgLinesPainter.getLineDataForOverlayCreation();
		}
	}

	/**
	 * Paint the line overlays
	 */
	@Override
	protected void afterCreate() {
		super.afterCreate();

		// Paint the line overlays after the point overlays are painted in the
		// super class, in order to give the line audio-labels priority
		if (polyLineStrings != null) {
			SvgLineOverlayPainter svgLineOverlayPainter = new SvgLineOverlayPainter(cs, polyLineStrings);
			svgLineOverlayPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		}
	}

	private PointPlotStyle getPointPlotStyle() {
		if (options.points.size() == 1 && options.points.get(0) != null && options.points.get(0).size() > 15) {
			if (options.dotsBorderless)
				return PointPlotStyle.DOTS_BORDERLESS;
			else
				return PointPlotStyle.DOTS;
		} else {
			if (options.dotsBorderless)
				return PointPlotStyle.MULTI_ROWS_BORDERLESS;
			else
				return PointPlotStyle.MULTI_ROWS;
		}
	}
}
