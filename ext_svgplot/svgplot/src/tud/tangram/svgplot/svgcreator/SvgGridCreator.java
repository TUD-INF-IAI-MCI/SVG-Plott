package tud.tangram.svgplot.svgcreator;

import java.util.ArrayList;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgGridOptions;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.plotting.ReferenceLine;
import tud.tangram.svgplot.plotting.ReferenceLine.Direction;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.svgpainter.SvgGridPainter;
import tud.tangram.svgplot.svgpainter.SvgPointOverlayPainter;
import tud.tangram.svgplot.svgpainter.SvgReferenceLinesPainter;
import tud.tangram.svgplot.svgpainter.SvgViewboxPainter;

public abstract class SvgGridCreator extends SvgCreator {
	protected final SvgGridOptions options;

	protected Element viewbox;

	protected CoordinateSystem cs;

	protected OverlayList overlays;

	public SvgGridCreator(SvgGridOptions options) {
		super(options);
		this.options = options;
	}

	/**
	 * Gets the {@link AxisStyle} in which the X axis should be rendered.
	 * 
	 * @return x axis style
	 */
	protected abstract AxisStyle getXAxisStyle();

	/**
	 * Gets the {@link AxisStyle} in which the y axis should be rendered.
	 * 
	 * @return y axis style
	 */
	protected abstract AxisStyle getYAxisStyle();

	@Override
	protected void beforeCreate() {
		super.beforeCreate();

		// Add extra margins for label placing for all diagrams except function
		// plots.
		if (options.diagramType != DiagramType.FunctionPlot) {
			diagramContentMargin.set(2, diagramContentMargin.get(2) + 10);
			diagramContentMargin.set(3, diagramContentMargin.get(3) + 20);
		}

		// Prepare either a coordinate system with metric or with categorial x axis
		if(options.xCategories == null)
			cs = new CoordinateSystem(options.xRange, options.yRange, options.size, diagramContentMargin, options.pi);
		else
			cs = new CoordinateSystem(options.xCategories, options.yRange, options.size, diagramContentMargin);
		overlays = new OverlayList(cs);

		SvgViewboxPainter svgViewboxPainter = new SvgViewboxPainter(cs, options.size);
		svgViewboxPainter.paintToSvgDocument(doc, null, options.outputDevice);
		viewbox = svgViewboxPainter.getViewbox();
	}

	@Override
	protected void create() {
		super.create();

		SvgGridPainter svgGridPainter = new SvgGridPainter(cs, getXAxisStyle(), getYAxisStyle(), options.gridStyle);

		svgGridPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgGridPainter.prepareLegendRenderer(legendRenderer, options.outputDevice, Integer.MAX_VALUE);

		svgGridPainter.addOverlaysToList(overlays);

		if (options.xLines != null || options.yLines != null) {
			ArrayList<ReferenceLine> lines = ReferenceLine.fromString(Direction.X_LINE, options.xLines);
			lines.addAll(ReferenceLine.fromString(Direction.Y_LINE, options.yLines));

			SvgReferenceLinesPainter svgReferenceLinesPainter = new SvgReferenceLinesPainter(cs, lines);
			svgReferenceLinesPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		}
	}

	@Override
	protected void afterCreate() {
		super.afterCreate();

		SvgPointOverlayPainter svgPointOverlayPainter = new SvgPointOverlayPainter(cs, overlays);
		svgPointOverlayPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
	}

	public CoordinateSystem getCs() {
		return cs;
	}

}
