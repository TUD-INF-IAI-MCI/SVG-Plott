package tud.tangram.svgplot.svgcreator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.SvgGraphOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.Gnuplot;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.plotting.PlotList;
import tud.tangram.svgplot.plotting.ReferenceLine;
import tud.tangram.svgplot.plotting.ReferenceLine.Direction;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.svgpainter.SvgPlotPainter;
import tud.tangram.svgplot.svgpainter.SvgPointsPainter;
import tud.tangram.svgplot.svgpainter.SvgReferenceLinesPainter;
import tud.tangram.svgplot.utils.SvgTools;

public class SvgGraphCreator extends SvgGridCreator {
	protected final SvgGraphOptions options;

	public SvgGraphCreator(SvgGraphOptions options) {
		super(options);
		this.options = options;
	}

	public static SvgCreatorInstantiator INSTANTIATOR = new SvgCreatorInstantiator() {
		public SvgCreator instantiateCreator(SvgPlotOptions rawOptions) {
			SvgGraphOptions options = new SvgGraphOptions(rawOptions);
			SvgGraphCreator creator = new SvgGraphCreator(options);
			return creator;
		}
	};

	/**
	 * Creates the reference lines and graph/integral/scatter plots and puts
	 * according information into the legend.
	 */
	public void create() {
		super.create();

		// Paint reference lines
		if (options.integral != null && options.integral.xRange != null) {
			ArrayList<ReferenceLine> lines = new ArrayList<>();

			/*
			 * If the integral start and/or end is within the axis range, paint
			 * reference lines for them
			 */
			if (options.integral.xRange.getFrom() > cs.xAxis.range.getFrom())
				lines.add(new ReferenceLine(Direction.X_LINE, options.integral.xRange.getFrom()));
			if (options.integral.xRange.getTo() < cs.xAxis.range.getTo())
				lines.add(new ReferenceLine(Direction.X_LINE, options.integral.xRange.getTo()));

			if (!lines.isEmpty()) {
				SvgReferenceLinesPainter svgReferenceLinesPainter = new SvgReferenceLinesPainter(cs, lines);
				svgReferenceLinesPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
			}
		}

		try {
			createPlots();
		} catch (Exception e) {
			System.out.println("Could not create the plots.");
			System.out.println(e);
		}
	}
	
	@Override
	protected AxisStyle getXAxisStyle() {
		return AxisStyle.GRAPH;
	}

	@Override
	protected AxisStyle getYAxisStyle() {
		return AxisStyle.GRAPH;
	}

	/**
	 * Paint the function/integral/scatter plots into the svg file and add
	 * according information into the legend.
	 */
	private void createPlots() {
		/*
		 * Call gnuplot in order to calculate and paint the function graphs and
		 * integrals
		 */
		Gnuplot gnuplot = new Gnuplot(options.gnuplot);
		SvgPlotPainter svgPlotPainter = new SvgPlotPainter(cs, options.functions, gnuplot, options.integral);
		svgPlotPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);

		// Add the legend items for the graph lines and the integral area with a
		// high priority
		svgPlotPainter.prepareLegendRenderer(legendRenderer, options.outputDevice, -100);

		// Get the list of the plots for reference in the description
		PlotList plotList = svgPlotPainter.getPlotList();

		// Overlays for audio tactile output
		OverlayList overlays = plotList.overlays();

		// Paint the scatter plot points to the SVG file
		SvgPointsPainter svgPointsPainter = new SvgPointsPainter(cs, options.points);
		svgPointsPainter.paintToSvgDocument(doc, viewbox, options.outputDevice);
		svgPointsPainter.addOverlaysToList(overlays);

		// Add the legend items for the scatter plot
		svgPointsPainter.prepareLegendRenderer(legendRenderer, options.outputDevice);

		// Add all overlays to the main overlay list
		this.overlays.addAll(overlays, true);

		createDesc(plotList);
	}

	/**
	 * Writes the external HTML description document
	 * 
	 * @param plotList
	 *            | ???
	 */
	private void createDesc(PlotList plotList) {
		String tab = "    ";
		String nl = "\n" + tab + tab;
		int s = 0; // intersection offset counter
		int e = 0; // extreme point offset counter
		int r = 0; // root point offset counter

		// general description
		Node div = desc.appendBodyChild(desc.createDiv("functions"));
		div.appendChild(desc.createP(SvgTools.translateN("desc.intro", SvgTools.formatX(cs, cs.xAxis.range.getFrom()),
				SvgTools.formatX(cs, cs.xAxis.range.getTo()), SvgTools.formatX(cs, cs.xAxis.ticInterval),
				SvgTools.formatY(cs, cs.yAxis.range.getFrom()), SvgTools.formatY(cs, cs.yAxis.range.getTo()),
				SvgTools.formatY(cs, cs.yAxis.ticInterval), SvgTools.formatName(cs.xAxis.range.getName()),
				SvgTools.formatName(cs.yAxis.range.getName()), options.functions.size())));

		// functions
		if (!options.functions.isEmpty()) {
			Node ol = div.appendChild(desc.createElement("ul"));
			int f = 0;
			for (Function function : options.functions) {
				Element li = (Element) ol.appendChild(desc.createElement("li"));
				li.appendChild(desc.createTextElement("span", SvgTools.getFunctionName(f++) + "(x) = "));
				if (function.hasTitle()) {
					li.appendChild(desc.createTextElement("strong", function.getTitle() + ":"));
					li.appendChild(desc.createTextNode(" " + function.getFunction() + nl + tab + tab));
				} else {
					li.appendChild(desc.createTextElement("span", function.getFunction()));
				}
			}

			// intersections between functions
			if (options.functions.size() > 1) {
				div = desc.appendBodyChild(desc.createDiv("intersections"));
				boolean hasIntersections = false;
				for (int i = 0; i < plotList.size() - 1; i++) {
					for (int k = i + 1; k < plotList.size(); k++) {
						List<Point> intersections = plotList.get(i).getIntersections(plotList.get(k));
						if (!intersections.isEmpty()) {
							hasIntersections = true;
							div.appendChild(desc.createP(SvgTools.translateN("desc.intersections",
									SvgTools.getFunctionName(i), SvgTools.getFunctionName(k), intersections.size())));
							div.appendChild(desc.createPointList(cs, intersections, "S", s));
							s += intersections.size();
						}
					}
				}
				if (!hasIntersections) {
					div.appendChild(desc.createP(SvgTools.translate("desc.intersections_0")));
				}
			}
		}

		// extreme points & zero
		for (int i = 0; i < plotList.size(); i++) {
			div = desc.appendBodyChild(desc.createDiv("function-" + SvgTools.getFunctionName(i)));
			List<Point> extrema = plotList.get(i).getExtrema();
			String f = plotList.size() == 1 ? "" : " " + SvgTools.getFunctionName(i);
			div.appendChild(desc.createP(SvgTools.translateN("desc.extrema", f, extrema.size())));
			if (!extrema.isEmpty()) {
				div.appendChild(desc.createPointList(cs, extrema, "E", e));
				e += extrema.size();
			}
			List<Point> roots = plotList.get(i).getRoots();
			div.appendChild(desc.createP(SvgTools.translateN("desc.roots", roots.size())));
			if (!roots.isEmpty()) {
				div.appendChild(desc.createXPointList(cs, roots, r));
				r += roots.size();
			}
		}

		if (options.points != null && options.points.size() > 0) {
			div = desc.appendBodyChild(desc.createDiv("points"));
			div.appendChild(desc.createP(SvgTools.translateN("legend.poi.intro", options.points.size())));

			int j = 0;
			for (PointList pts : options.points) {
				if (pts != null && pts.size() > 0) {
					String text = pts.getName().isEmpty() ? SvgTools.getPointName(j) : pts.getName();
					div.appendChild(desc.createP(SvgTools.translateN("legend.poi.list", text, pts.size())));

					div.appendChild(desc.createPointList(cs, pts, SvgTools.getPointName(j), 0));
					j++;
				}
			}
		}

		// integral
		if (options.integral != null && options.integral.function1 >= 0) {
			div = desc.appendBodyChild(desc.createDiv("integral-"));
			if (options.integral.function2 >= 0)
				div.appendChild(desc.createP(SvgTools.translate("desc.integral_1",
						Math.max(cs.xAxis.range.getFrom(), options.integral.xRange.getFrom()),
						Math.min(cs.xAxis.range.getTo(), options.integral.xRange.getTo()),
						SvgTools.getFunctionName(options.integral.function1),
						SvgTools.getFunctionName(options.integral.function2))));
			else
				div.appendChild(desc.createP(SvgTools.translate("desc.integral_0",
						Math.max(cs.xAxis.range.getFrom(), options.integral.xRange.getFrom()),
						Math.min(cs.xAxis.range.getTo(), options.integral.xRange.getTo()),
						SvgTools.getFunctionName(options.integral.function1))));
		}

		desc.appendBodyChild(desc.createP(SvgTools.translate("desc.note")));
	}

}
