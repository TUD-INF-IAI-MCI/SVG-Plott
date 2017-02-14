package tud.tangram.svgplot.svgcreator;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.PointListList.PointList;
import tud.tangram.svgplot.options.SvgGraphOptions;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.Gnuplot;
import tud.tangram.svgplot.plotting.IntegralPlot;
import tud.tangram.svgplot.plotting.Plot;
import tud.tangram.svgplot.plotting.PlotList;
import tud.tangram.svgplot.plotting.PlotList.Overlay;
import tud.tangram.svgplot.plotting.PlotList.OverlayList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgGraphCreator extends SvgGridCreator {
	protected final SvgGraphOptions options;

	public SvgGraphCreator(SvgGraphOptions options) {
		super(options);
		this.options = options;
	}

	/**
	 * Creates the whole diagram SVG file, legend SVG file and description HTML
	 * file.
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws DOMException
	 * @throws InterruptedException
	 */
	public SvgDocument create() throws ParserConfigurationException, IOException, DOMException, InterruptedException {
		super.create();

		createReferenceLines();
		createPlots();

		return doc;
	}

	/**
	 * Generates the basic css optimized for tactile output on a tiger embosser.
	 * 
	 * @param doc
	 *            | the svg document in with this css should been added
	 * @throws IOException
	 *             if the set up css file is not readable or does not exist
	 *             anymore.
	 */
	protected void createCss(SvgDocument doc) throws IOException {
		String css = "/* default */\n";
		css += "svg { fill: none; stroke: #000000; stroke-width: " + Constants.strokeWidth + "; }\n";
		css += "text { font-family: serif; font-size: 36pt; fill: black; stroke: none; }\n";
		css += "#grid { stroke: #777777; }\n";
		css += "#axes, #reference-lines, .box { stroke: #111111; fill: transparent; }\n";
		double width = 2 * Constants.strokeWidth;
		css += "#plots { stroke-width: " + width + "; stroke-dasharray: " + width * 5 + ", " + width * 5 + "; }\n";
		css += "#plot-1 { stroke-dasharray: none; }\n";
		css += "#plot-2 { stroke-dasharray: " + width + ", " + width * 3 + "; }\n";
		css += "#overlays { stroke: none; stroke-dasharray: none; fill: transparent; }\n";

		css += ".poi_symbol { stroke: black; stroke-dasharray: none; stroke-width:" + (Constants.strokeWidth * 1.5)
				+ ";  fill: black; }\n";
		css += "." + Constants.spacerCssClass + " { stroke: white; stroke-dasharray: none; stroke-width:" + (width * 3)
				+ ";  fill: transparent; stroke-linecap: round; }\n";

		css += ".integral { stroke: white; stroke-dasharray: none; stroke-width:" + (width * 3) + "; }\n";
		css += ".integral-1 { fill: url(#diagonal_line1_PD); }\n";
		css += ".integral-2 { fill: white; }\n";

		css += "@media print{\n";
		css += "text { font-family: 'Braille DE Computer'; font-size: 36pt; fill: black; stroke: none; }\n";
		css += " }\n";

		appendOptionsCss(css);
	}

	/**
	 * Paint the vertical and horizontal additional reference lines to the svg
	 * file.
	 */
	private void createReferenceLines() {
		if (options.xLines == null && options.yLines == null && options.integral == null) {
			return;
		}

		Node referenceLines = viewbox.appendChild(doc.createGroup("reference-lines"));

		if (options.integral != null && options.integral.xRange != null) {
			if (options.xLines == null)
				options.xLines = "";
			if (options.integral.xRange.from > cs.xAxis.range.from)
				options.xLines += " " + options.integral.xRange.from;
			if (options.integral.xRange.to < cs.xAxis.range.to)
				options.xLines += " " + options.integral.xRange.to;
		}

		if (options.xLines != null) {
			Node group = referenceLines.appendChild(doc.createGroup("x-reference-lines"));
			for (String line : options.xLines.trim().split("\\s+")) {
				if (line != null && !line.isEmpty()) {
					double pos = Double.parseDouble(line.replace("\\", ""));
					Point from = cs.convert(pos, cs.yAxis.range.to, 0, -Constants.strokeWidth / 2);
					Point to = cs.convert(pos, cs.yAxis.range.from, 0, Constants.strokeWidth / 2);
					group.appendChild(doc.createLine(from, to));
				}
			}
		}

		if (options.yLines != null) {
			Node group = referenceLines.appendChild(doc.createGroup("y-reference-lines"));
			for (String line : options.yLines.trim().split("\\s+")) {
				double pos = Double.parseDouble(line);
				Point from = cs.convert(cs.xAxis.range.from, pos, -Constants.strokeWidth / 2, 0);
				Point to = cs.convert(cs.xAxis.range.to, pos, Constants.strokeWidth / 2, 0);
				group.appendChild(doc.createLine(from, to));
			}
		}
	}

	/**
	 * Paint the function-plots in the svg file.
	 * 
	 * @throws IOException
	 *             if gnuplot could not be found
	 * @throws InterruptedException
	 *             if gnuplot causes some exceptions
	 */
	private void createPlots() throws IOException, InterruptedException {
		Node plots = viewbox.appendChild(doc.createGroup("plots"));

		Gnuplot gnuplot = new Gnuplot(options.gnuplot);
		gnuplot.setSample(cs.xAxis.atomCount);
		gnuplot.setSample(1300);
		gnuplot.setXRange(cs.xAxis.range, options.pi);
		gnuplot.setYRange(cs.yAxis.range);

		// functions
		int i = 0;
		PlotList plotList = new PlotList(cs);
		for (Function function : options.functions) {
			Node graph = plots.appendChild(doc.createGroup("plot-" + ++i));
			Element path = (Element) graph.appendChild(doc.createElement("path"));
			path.setAttribute("clip-path", "url(#plot-area)");

			String points = "";
			Plot plot = new Plot(function, gnuplot);
			plot.Name = getFunctionName(i - 1);
			plotList.add(plot);
			for (List<Point> list : plot) {
				String op = "M";
				for (Point point : list) {
					points += op + cs.convert(point) + " ";
					op = "L";
				}
			}
			path.setAttribute("d", points);
		}

		if (options.integral != null && options.integral.function1 >= 0
				&& plotList.size() > options.integral.function1) {

			Element parent = viewbox;
			Element a = doc.getChildElementById(viewbox, "axes");
			if (a != null) {
				Element integralContainer = doc.createGroup("integrals");
				viewbox.insertBefore(integralContainer, a);
				parent = integralContainer;
			}
			Plot p2 = null;
			if (options.integral.function2 >= 0 && plotList.size() > options.integral.function2
					&& options.integral.function2 != options.integral.function1)
				p2 = plotList.get(options.integral.function2);
			new IntegralPlot(cs).handlePlotIntergral(plotList.get(options.integral.function1), doc, parent,
					options.integral.xRange != null ? Math.max(options.integral.xRange.from, cs.xAxis.range.from)
							: cs.xAxis.range.from,
					options.integral.xRange != null ? Math.min(options.integral.xRange.to, cs.xAxis.range.to)
							: cs.xAxis.range.to,
					p2);
		}

		// overlays for audio tactile output
		OverlayList overlays = plotList.overlays();

		// TODO: add scatter plot
		// points or scatter plots
		if (options.points != null && options.points.size() > 0) {
			int j = 0;
			Element poiGroup = doc.createElement("g", "points");
			viewbox.appendChild(poiGroup);
			for (PointList pl : options.points) {
				if (pl != null && pl.size() > 0) {

					Element plGroup = doc.createElement("g", "points_" + j);
					poiGroup.appendChild(plGroup);

					for (Point p : pl) {
						Element symbol = PointPlot.getPointSymbolForIndex(j, doc);
						Element ps = PointPlot.paintPoint(doc, cs.convert(p), symbol,
								plGroup != null ? plGroup : viewbox);
						ps.appendChild(doc.createTitle(formatForSpeech(p)));
						if (pl.name != null && !pl.name.isEmpty())
							ps.appendChild(doc.createDesc(pl.name)); // TODO:
																		// maybe
																		// fine
																		// this
						// add on top of overlays and avoid collision testing
						overlays.add(overlays.size(), new Overlay(p));
					}
				}
				j++;
			}
		}

		// overlays for audio tactile output
		Node overlaysElement = viewbox.appendChild(doc.createGroup("overlays"));
		for (Function function : options.functions) {
			for (Overlay overlay : overlays) {
				if (function.equals(overlay.getFunction())) {
					overlaysElement.appendChild(createOverlay(overlay));
				}
			}
		}
		for (Overlay overlay : overlays) {
			if (overlay.getFunction() == null) {
				overlaysElement.appendChild(createOverlay(overlay));
			}
		}

		createDesc(plotList);
	}

	/**
	 * Paints an overlay in the svg file
	 * 
	 * @param overlay
	 *            | the overlay that should be insert, containing position and
	 *            additional informations about the point underneath it.
	 * @return a DOM Element representing the already inserted overlay node.
	 */
	private Element createOverlay(Overlay overlay) {
		Element circle = doc.createCircle(cs.convert(overlay), Overlay.RADIUS);
		circle.appendChild(doc.createTitle(formatForSpeech(overlay)));
		if (overlay.getFunction() != null) {
			circle.appendChild(doc.createDesc(overlay.getFunction().toString()));
		}
		return circle;
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
		div.appendChild(desc.createP(SvgTools.translateN("desc.intro", formatX(cs.xAxis.range.from),
				formatX(cs.xAxis.range.to), formatX(cs.xAxis.ticInterval), formatY(cs.yAxis.range.from),
				formatY(cs.yAxis.range.to), formatY(cs.yAxis.ticInterval), SvgTools.formatName(cs.xAxis.range.name),
				SvgTools.formatName(cs.yAxis.range.name), options.functions.size())));

		// functions
		if (!options.functions.isEmpty()) {
			Node ol = div.appendChild(desc.createElement("ul"));
			int f = 0;
			for (Function function : options.functions) {
				Element li = (Element) ol.appendChild(desc.createElement("li"));
				li.appendChild(desc.createTextElement("span", getFunctionName(f++) + "(x) = "));
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
							div.appendChild(desc.createP(SvgTools.translateN("desc.intersections", getFunctionName(i),
									getFunctionName(k), intersections.size())));
							div.appendChild(createPointList(intersections, "S", s));
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
			div = desc.appendBodyChild(desc.createDiv("function-" + getFunctionName(i)));
			List<Point> extrema = plotList.get(i).getExtrema();
			String f = plotList.size() == 1 ? "" : " " + getFunctionName(i);
			div.appendChild(desc.createP(SvgTools.translateN("desc.extrema", f, extrema.size())));
			if (!extrema.isEmpty()) {
				div.appendChild(createPointList(extrema, "E", e));
				e += extrema.size();
			}
			List<Point> roots = plotList.get(i).getRoots();
			div.appendChild(desc.createP(SvgTools.translateN("desc.roots", roots.size())));
			if (!roots.isEmpty()) {
				div.appendChild(createXPointList(roots, r));
				r += roots.size();
			}
		}

		if (options.points != null && options.points.size() > 0) {
			div = desc.appendBodyChild(desc.createDiv("points"));
			div.appendChild(desc.createP(SvgTools.translateN("legend.poi.intro", options.points.size())));

			int j = 0;
			for (PointList pts : options.points) {
				if (pts != null && pts.size() > 0) {
					String text = pts.name.isEmpty() ? getPointName(j) : pts.name;
					div.appendChild(desc.createP(SvgTools.translateN("legend.poi.list", text, pts.size())));

					div.appendChild(createPointList(pts, getPointName(j), 0));
					j++;
				}
			}
		}

		// integral
		if (options.integral != null && options.integral.function1 >= 0) {
			div = desc.appendBodyChild(desc.createDiv("integral-"));
			if (options.integral.function2 >= 0)
				div.appendChild(desc.createP(SvgTools.translate("desc.integral_1",
						Math.max(cs.xAxis.range.from, options.integral.xRange.from),
						Math.min(cs.xAxis.range.to, options.integral.xRange.to),
						getFunctionName(options.integral.function1), getFunctionName(options.integral.function2))));
			else
				div.appendChild(desc.createP(SvgTools.translate("desc.integral_0",
						Math.max(cs.xAxis.range.from, options.integral.xRange.from),
						Math.min(cs.xAxis.range.to, options.integral.xRange.to),
						getFunctionName(options.integral.function1))));
		}

		desc.appendBodyChild(desc.createP(SvgTools.translate("desc.note")));
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @return ul element with points as a list
	 */
	@SuppressWarnings("unused")
	private Element createPointList(List<Point> points) {
		return createPointList(points, null, 0);
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @return ul element with points as a list
	 */
	@SuppressWarnings("unused")
	private Element createPointList(List<Point> points, String cap) {
		return createPointList(points, cap, 0);
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries packed in the
	 * given caption string and brackets. E.g. E(x|y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	private Element createPointList(List<Point> points, String cap, int offset) {
		Element list = desc.createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(desc.createTextElement("li", formatForText(point, cap + "_" + ++i)));
			} else {
				list.appendChild(desc.createTextElement("li", formatForSpeech(point)));
			}
		}
		return list;
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @return ul element with points as a list
	 */
	@SuppressWarnings("unused")
	private Element createXPointList(List<Point> points) {
		return createXPointList(points, "x", 0);
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	private Element createXPointList(List<Point> points, int offset) {
		return createXPointList(points, "x", offset);
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries packed in the
	 * given caption string and brackets. E.g. E(x|y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	private Element createXPointList(List<Point> points, String cap, int offset) {
		Element list = desc.createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(desc.createTextElement("li", cap + "_" + ++i + " = " + formatX(point.x)));
			} else {
				list.appendChild(desc.createTextElement("li", formatX(point.x)));
			}
		}
		return list;
	}

	// TODO: generate textual braille key
	/**
	 * Paint the key as an svg file.
	 */
	protected void createLegend() {
		Point currentPosition = legendTitleLowerEnd.clone();

		int distance = 7;
		currentPosition.y += 2 * distance;

		Element viewbox = (Element) legend.appendChild(legend.createElement("svg"));
		viewbox.setAttribute("viewBox",
				"0 0 " + SvgTools.format2svg(options.size.x) + " " + SvgTools.format2svg(options.size.y));

		Node plots = viewbox.appendChild(legend.createGroup("plots"));
		int i = 0;
		for (Function function : options.functions) {
			Node plot = plots.appendChild(legend.createGroup("plot-" + (i + 1)));
			plot.appendChild(legend.createLine(new Point(currentPosition.x, currentPosition.y - 5),
					new Point(currentPosition.x + 26, currentPosition.y - 5)));

			currentPosition.translate(35, 0);
			if (function.hasTitle()) {
				legend.appendChild(legend.createText(currentPosition,
						getFunctionName(i) + "(x) = " + function.getTitle() + ":", function.getFunction()));
			} else {
				legend.appendChild(
						legend.createText(currentPosition, getFunctionName(i) + "(x) = " + function.getFunction()));
			}
			currentPosition.translate(-35, distance);
			i++;
		}

		// points
		if (options.points != null && options.points.size() > 0) {
			currentPosition.translate(0, -10);
			if (options.points != null && options.points.size() > 0) {
				int j = 0;
				Element poiGroup = legend.createElement("g", "points");
				viewbox.appendChild(poiGroup);
				for (PointList pl : options.points) {
					if (pl != null && pl.size() > 0) {
						Element plGroup = legend.createElement("g", "points_" + j);
						poiGroup.appendChild(plGroup);

						currentPosition.translate(5, 3);

						Element symbol = PointPlot.getPointSymbolForIndex(j, legend);
						PointPlot.paintPoint(legend, currentPosition, symbol, plGroup != null ? plGroup : viewbox);

						currentPosition.translate(-5, -3);

						String text = pl.name.isEmpty() ? SvgTools.translateN("legend.poi", getPointName(j), pl.size())
								: pl.name;
						currentPosition.translate(20, distance);
						legend.appendChild(legend.createText(currentPosition, text));

						currentPosition.translate(-20, 0);
					}
					j++;
				}
			}
		}

		// integrals
		if (options.integral != null && options.integral.function1 >= 0) {
			currentPosition.translate(0, -10);
			Node integrals = viewbox.appendChild(legend.createGroup("integral"));
			Node integralGroup = integrals.appendChild(legend.createGroup("integral-0"));

			// append devs
			if (legend.defs != null) {
				legend.defs.appendChild(IntegralPlot.getFillPattern(legend));
			}

			Node iBox = integralGroup.appendChild(legend.createRectangle(currentPosition, 30, 15));
			((Element) iBox).setAttribute("class", "integral-1 box");

			String text = "";
			if (options.integral.function2 >= 0)
				text = SvgTools.translate("legend.integral_1",
						Math.max(cs.xAxis.range.from, options.integral.xRange.from),
						Math.min(cs.xAxis.range.to, options.integral.xRange.to),
						getFunctionName(options.integral.function1), getFunctionName(options.integral.function2));
			else
				text = SvgTools.translate("legend.integral_0",
						Math.max(cs.xAxis.range.from, options.integral.xRange.from),
						Math.min(cs.xAxis.range.to, options.integral.xRange.to),
						getFunctionName(options.integral.function1));

			currentPosition.translate(35, distance + 5);
			legend.appendChild(legend.createText(currentPosition, text));

			currentPosition.translate(-35, distance);
		}

		// footnote
		currentPosition.translate(0, distance);
		legend.appendChild(legend.createText(currentPosition,
				SvgTools.translate("legend.xrange", formatX(cs.xAxis.range.from), formatX(cs.xAxis.range.to),
						SvgTools.formatName(cs.xAxis.range.name)),
				SvgTools.translate("legend.xtic", formatX(cs.xAxis.ticInterval))));

		currentPosition.translate(0, distance);
		legend.appendChild(legend.createText(currentPosition,
				SvgTools.translate("legend.yrange", formatY(cs.yAxis.range.from), formatY(cs.yAxis.range.to),
						SvgTools.formatName(cs.yAxis.range.name)),
				SvgTools.translate("legend.ytic", formatY(cs.yAxis.ticInterval))));
	}

	/**
	 * Formats the x value of a point with respect to if Pi is set.
	 * 
	 * @param x
	 *            | x-value
	 * @return formated string for the point
	 */
	public String formatX(double x) {
		String str = cs.xAxis.format(x);
		if (options.pi && !"0".equals(str)) {
			str += " pi";
		}
		return str;
	}

	public String formatY(double y) {
		return cs.yAxis.format(y);
	}

	/**
	 * Formats a Point that it is optimized for speech output. E.g. (x / y)
	 * 
	 * @param point
	 *            | The point that should be transformed into a textual
	 *            representation
	 * @return formated string for the point with '/' as delimiter
	 */
	public String formatForSpeech(Point point) {
		return ((point.name != null && !point.name.isEmpty()) ? point.name + " " : "") + formatX(point.x) + " / "
				+ formatY(point.y);
	}

	/**
	 * Formats a Point that it is optimized for textual output and packed into
	 * the caption with brackets. E.g. E(x | y)
	 * 
	 * @param point
	 *            | The point that should be transformed into a textual
	 *            representation
	 * @param cap
	 *            | The caption sting without brackets
	 * @return formated string for the point with '/' as delimiter if now
	 *         caption is set, otherwise packed in the caption with brackets and
	 *         the '|' as delimiter
	 */
	public String formatForText(Point point, String cap) {
		String p = formatX(point.x) + " | " + formatY(point.y);
		cap = cap.trim();
		return (cap != null && !cap.isEmpty()) ? cap + "(" + p + ")" : p;
	}

	/**
	 * Try to translate the function index into a continuous literal starting
	 * with the char 'f'. If the given index is not valid it returns the name as
	 * a combination of 'f' + the given number.
	 * 
	 * @param f
	 *            | the index if the function
	 * @return a literal representation to the given function index e.g. 'f',
	 *         'g', 'h' or 'f1000'.
	 */
	public static String getFunctionName(int f) {
		if (f < 0 || f > (Constants.fnList.length - 1))
			return "f" + String.valueOf(f);
		return Constants.fnList[f];
	}

	public static String getPointName(int p) {
		if (p < 0 || p > (Constants.pnList.length - 1))
			return "P" + String.valueOf(p);
		return Constants.pnList[p];
	}
}
