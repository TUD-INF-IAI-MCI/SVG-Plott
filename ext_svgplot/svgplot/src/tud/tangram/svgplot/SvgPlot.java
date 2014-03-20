package tud.tangram.svgplot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.coordinatesystem.PointListList.PointList;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.Gnuplot;
import tud.tangram.svgplot.plotting.IntegralPlot;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;
import tud.tangram.svgplot.plotting.Plot;
import tud.tangram.svgplot.plotting.PlotList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.plotting.PlotList.Overlay;
import tud.tangram.svgplot.plotting.PlotList.OverlayList;
import tud.tangram.svgplot.xml.HtmlDocument;
import tud.tangram.svgplot.xml.SvgDocument;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 * 
 */
@Parameters(separators = "=", resourceBundle = "Bundle")
public class SvgPlot {
	final protected static String[] fnList = new String[] { "f", "g", "h", "i",
			"j", "k", "l", "m", "o", "p", "q", "r" }; 
	public final static String spacerCssClass = "poi_symbol_bg";

	@Parameter(description = "functions")
	private List<Function> functions = new ArrayList<>();

	@Parameter(names = { "--size", "-s" }, descriptionKey = "param.size")
	private Point size = new Point(210, 297);

	@Parameter(names = { "--xrange", "-x" }, descriptionKey = "param.xrange")
	private Range xRange = new Range(-8, 8);

	@Parameter(names = { "--yrange", "-y" }, descriptionKey = "param.yrange")
	private Range yRange = new Range(-8, 8);

	@Parameter(names = { "--pi", "-p" }, descriptionKey = "param.pi")
	private boolean pi = false;

	@Parameter(names = { "--xlines" }, descriptionKey = "param.xlines")
	private String xLines = null;

	@Parameter(names = { "--ylines" }, descriptionKey = "param.ylines")
	private String yLines = null;

	@Parameter(names = { "--title", "-t" }, descriptionKey = "param.title")
	private String title = null;

	@Parameter(names = { "--gnuplot", "-g" }, descriptionKey = "param.gnuplot")
	private String gnuplot = null;

	@Parameter(names = { "--css", "-c" }, descriptionKey = "param.css")
	private String css = null;

	@Parameter(names = { "--output", "-o" }, descriptionKey = "param.output")
	private File output = null;

	@Parameter(names = { "--help", "-h", "-?" }, help = true, descriptionKey = "param.help")
	private boolean help;

	@Parameter(names = { "--integral", "-i" }, descriptionKey = "param.integral")
	private IntegralPlotSettings integral;

	// TODO: add parameter for scatter plot file
	// parameter for marking some points
	@Parameter(names = { "--points", "--pts" }, descriptionKey = "param.points")
	private String pts;

	private PointListList points;

	private SvgDocument doc;

	private Element viewbox;

	private SvgDocument legend;

	private HtmlDocument desc;

	private CoordinateSystem cs;

	final private double strokeWidth = 0.5;

	final private int[] margin = { 20, 10, 20, 10 };

	private static DecimalFormat decimalFormat = null;

	final private static ResourceBundle bundle = ResourceBundle
			.getBundle("Bundle");

	/**
	 * Main function. Combine all the elements and create all the output files.
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws TransformerException
	 */
	public void run() throws ParserConfigurationException, IOException,
			InterruptedException, TransformerException {
		if (title == null && output != null) {
			title = output.getName();
		}
		String legendTitle = translate("legend") + ": " + title;

		doc = new SvgDocument(title, size, margin[1]);
		legend = new SvgDocument(legendTitle, size, margin[1]);
		legend.setAttribute("id", "legend");
		desc = new HtmlDocument(translate("desc") + ": " + title);

		createCss(doc);
		createCss(legend);

		Element bg = doc.createRectangle(new Point(0, 0), "100%", "100%");
		bg.setAttribute("id", "background");
		doc.appendChild(bg);
		Element bgL = legend.createRectangle(new Point(0, 0), "100%", "100%");
		bgL.setAttribute("id", "background");
		legend.appendChild(bgL);

		Point pos = createTitle(doc, title);
		Point legendPos = createTitle(legend, legendTitle);

		xRange.from = Math.min(0, xRange.from);
		xRange.to = Math.max(0, xRange.to);
		yRange.from = Math.min(0, yRange.from);
		yRange.to = Math.max(0, yRange.to);
		int[] margin = this.margin.clone();
		margin[0] = (int) pos.y + 17;
		margin[1] += 20;
		margin[3] += 10;
		cs = new CoordinateSystem(xRange, yRange, size, margin);

		createViewbox();
		createGrid();
		createAxes();

		createReferenceLines();
		createPlots();
		createLegend(legendPos);

		if (output != null) {
			doc.writeTo(new FileOutputStream(output));
			String parent = output.getParent() == null ? "" : output
					.getParent() + "\\";
			String legendFile = parent
					+ output.getName()
							.replaceFirst("(\\.[^.]*)?$", "_legend$0");
			legend.writeTo(new FileOutputStream(legendFile));
			String descFile = parent
					+ output.getName().replaceFirst("(\\.[^.]*)?$",
							"_desc.html");
			desc.writeTo(new FileOutputStream(descFile));
		} else {
			doc.writeTo(System.out);
		}
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
	private void createCss(SvgDocument doc) throws IOException {
		String css = "/* default */\n";
		css += "svg { fill: none; stroke: #000000; stroke-width: "
				+ strokeWidth + "; }\n";
		css += "text { font-family: serif; font-size: 36pt; fill: black; stroke: none; }\n";
		css += "#grid { stroke: #777777; }\n";
		css += "#axes, #reference-lines, .box { stroke: #111111; fill: transparent; }\n";
		double width = 2 * strokeWidth;
		css += "#plots { stroke-width: " + width + "; stroke-dasharray: "
				+ width * 5 + ", " + width * 5 + "; }\n";
		css += "#plot-1 { stroke-dasharray: none; }\n";
		css += "#plot-2 { stroke-dasharray: " + width + ", " + width * 3
				+ "; }\n";
		css += "#overlays { stroke: none; stroke-dasharray: none; fill: transparent; }\n";

		css += ".poi_symbol { stroke: black; stroke-dasharray: none; stroke-width:"
				+ (strokeWidth * 1.5) + ";  fill: black; }\n";
		css += "." + spacerCssClass
				+ " { stroke: white; stroke-dasharray: none; stroke-width:"
				+ (width * 3)
				+ ";  fill: transparent; stroke-linecap: round; }\n";

		css += ".integral { stroke: white; stroke-dasharray: none; stroke-width:"
				+ (width * 3) + "; }\n";
		css += ".integral-1 { fill: url(#diagonal_line1_PD); }\n";
		css += ".integral-2 { fill: white; }\n";
		
		
		css += "@media print{\n";
		css += "text { font-family: 'Braille DE Computer'; font-size: 36pt; fill: black; stroke: none; }\n";
		css += " }\n";
		

		if (this.css != null) {
			css += "\n\n/* custom */\n";
			if (new File(this.css).isFile()) {
				this.css = new String(Files.readAllBytes(Paths.get(this.css)));
			}
			css += this.css;
		}
		doc.appendCss(css);
	}

	/**
	 * Adds the textual readable title to the svg document at a predefined
	 * position in the left top corner of the sheet.
	 * 
	 * @param doc
	 *            | the svg document where the header text should be insert
	 * @param text
	 *            | the textual value
	 * @return the position of the added title node
	 */
	private Point createTitle(SvgDocument doc, String text) {
		Point pos = new Point(margin[3], margin[0] + 10);
		Element title = (Element) doc.appendChild(doc.createText(pos, text));
		title.setAttribute("id", "title");
		return pos;
	}

	private void createViewbox() {
		viewbox = (Element) doc.appendChild(doc.createElement("svg"));
		viewbox.setAttribute("viewBox", "0 0 " + format2svg(size.x) + " "
				+ format2svg(size.y));

		Node defs = viewbox.appendChild(doc.createElement("defs"));

		Node clipPath = defs.appendChild(doc.createElement("clipPath",
				"plot-area"));
		Element rect = (Element) clipPath
				.appendChild(doc.createElement("rect"));
		Point topLeft = cs.convert(cs.xAxis.range.from, cs.yAxis.range.to);
		Point bottomRight = cs.convert(cs.xAxis.range.to, cs.yAxis.range.from);
		rect.setAttribute("x", topLeft.x());
		rect.setAttribute("y", topLeft.y());
		rect.setAttribute("width", format2svg(bottomRight.x - topLeft.x));
		rect.setAttribute("height", format2svg(bottomRight.y - topLeft.y));
	}

	/**
	 * Paint the grid to the svg file.
	 */
	private Node createGrid() {
		Node grid = viewbox.appendChild(doc.createGroup("grid"));

		Element xGrid = (Element) grid.appendChild(doc.createGroup("x-grid"));
		double dotDistance = cs.convertYDistance(cs.yAxis.gridInterval);
		int factor = (int) (dotDistance / 2.3);
		dotDistance = (dotDistance - factor * strokeWidth) / factor;
		xGrid.setAttribute("stroke-dasharray", strokeWidth + ", "
				+ format2svg(dotDistance));
		for (double pos : cs.xAxis.gridLines()) {
			Point from = cs
					.convert(pos, cs.yAxis.range.to, 0, -strokeWidth / 2);
			Point to = cs.convert(pos, cs.yAxis.range.from, 0, strokeWidth / 2);
			xGrid.appendChild(doc.createLine(from, to));
		}

		Element yGrid = (Element) grid.appendChild(doc.createGroup("y-grid"));
		dotDistance = cs.convertXDistance(cs.xAxis.gridInterval);
		factor = (int) (dotDistance / 2.3);
		dotDistance = (dotDistance - factor * strokeWidth) / factor;
		yGrid.setAttribute("stroke-dasharray", strokeWidth + ", "
				+ format2svg(dotDistance));
		for (double pos : cs.yAxis.gridLines()) {
			Point from = cs.convert(cs.xAxis.range.from, pos, -strokeWidth / 2,
					0);
			Point to = cs.convert(cs.xAxis.range.to, pos, strokeWidth / 2, 0);
			yGrid.appendChild(doc.createLine(from, to));
		}
		return grid;
	}

	/**
	 * Paint the axes to the svg file.
	 */
	private Node createAxes() {
		Node axes = viewbox.appendChild(doc.createGroup("axes"));
		Point from, to;
		String points;

		from = cs.convert(xRange.from, 0, -10, 0);
		to = cs.convert(xRange.to, 0, 10, 0);
		Element xAxisLine = doc.createLine(from, to);
		axes.appendChild(xAxisLine);
		xAxisLine.setAttribute("id", "x-axis");
		Element xAxisArrow = doc.createElement("polyline", "x-axis-arrow");
		axes.appendChild(xAxisArrow);
		to.translate(0, -3);
		points = to.toString();
		to.translate(5.2, 3);
		points += " " + to;
		to.translate(-5.2, 3);
		points += " " + to;
		xAxisArrow.setAttribute("points", points);
		xAxisArrow.appendChild(doc.createTitle(translate("xaxis")));

		// create x-label
		Point pos2 = to;
		pos2.translate(0, 13);
		doc.appendChild(createLabel("x", pos2, "x_label", "label"));

		from = cs.convert(0, yRange.from, 0, 10);
		to = cs.convert(0, yRange.to, 0, -10);
		Element yAxisLine = doc.createLine(from, to);
		axes.appendChild(yAxisLine);
		yAxisLine.setAttribute("id", "y-axis");
		Element yAxisArrow = doc.createElement("polyline", "y-axis-arrow");
		axes.appendChild(yAxisArrow);
		to.translate(-3, 0);
		points = to.toString();
		to.translate(3, -5.2);
		points += " " + to;
		to.translate(3, 5.2);
		points += " " + to;
		yAxisArrow.setAttribute("points", points);
		yAxisArrow.appendChild(doc.createTitle(translate("yaxis")));

		// create y-label
		Point pos3 = to;
		pos3.translate(-15, 0);
		doc.appendChild(createLabel("y", pos3, "y_label", "label"));

		Node xTics = axes.appendChild(doc.createGroup("x-tics"));
		for (double pos : cs.xAxis.ticLines()) {
			from = cs.convert(pos, 0, 0, -6);
			to = from.clone();
			to.translate(0, 12);
			xTics.appendChild(doc.createLine(from, to));
		}

		Node yTics = axes.appendChild(doc.createGroup("y-tics"));
		for (double pos : cs.yAxis.ticLines()) {
			from = cs.convert(0, pos, -6, 0);
			to = from.clone();
			to.translate(12, 0);
			yTics.appendChild(doc.createLine(from, to));
		}

		// Duplicate axes and append them as spacers
		Element axesClone = (Element) axes.cloneNode(true);
		axesClone.setAttribute("id", "axes_spacer");
		axesClone.setAttribute(
				"class",
				axesClone.hasAttribute("class") ? axesClone
						.getAttribute("class") + " " + spacerCssClass
						: spacerCssClass);

		axes.insertBefore(axesClone, xAxisLine);

		return axes;
	}

	/**
	 * Creates a textual label and place it in the svg document previous the
	 * viewbox.
	 * 
	 * @param text		| the textual value that should be diplayed
	 * @param pos		| pixel position in the svg file where the text should start
	 * @param id		| XML id of the node
	 * @param cssClass	| css class for this node
	 * @return an textual Element already placed in the svg file with given text
	 *         at given position.
	 */
	private Element createLabel(String text, Point pos, String id,
			String cssClass) {
		Element label = doc.createText(pos, text);
		if (id != null && !id.isEmpty())
			label.setAttribute("id", id);
		if (cssClass != null && !cssClass.isEmpty())
			label.setAttribute("class", cssClass);
		// TODO: add underlying background
		return label;
	}

	/**
	 * Paint the vertical and horizontal additional reference lines to the svg
	 * file.
	 */
	private void createReferenceLines() {
		if (xLines == null && yLines == null && integral == null) {
			return;
		}

		Node referenceLines = viewbox.appendChild(doc
				.createGroup("reference-lines"));
		
		if(integral != null && integral.xRange != null){
			if(xLines == null) xLines = "";
			if(integral.xRange.from > cs.xAxis.range.from) xLines += " " + integral.xRange.from;
			if(integral.xRange.to < cs.xAxis.range.to) xLines += " " + integral.xRange.to;
		}

		if (xLines != null) {
			Node group = referenceLines.appendChild(doc
					.createGroup("x-reference-lines"));
			for (String line : xLines.trim().split("\\s+")) {
				if (line != null && !line.isEmpty()) {
					double pos = Double.parseDouble(line);
					Point from = cs.convert(pos, cs.yAxis.range.to, 0,
							-strokeWidth / 2);
					Point to = cs.convert(pos, cs.yAxis.range.from, 0,
							strokeWidth / 2);
					group.appendChild(doc.createLine(from, to));
				}
			}
		}

		if (yLines != null) {
			Node group = referenceLines.appendChild(doc
					.createGroup("y-reference-lines"));
			for (String line : yLines.trim().split("\\s+")) {
				double pos = Double.parseDouble(line);
				Point from = cs.convert(cs.xAxis.range.from, pos,
						-strokeWidth / 2, 0);
				Point to = cs.convert(cs.xAxis.range.to, pos, strokeWidth / 2,
						0);
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

		Gnuplot gnuplot = new Gnuplot(this.gnuplot);
		gnuplot.setSample(cs.xAxis.atomCount);
		gnuplot.setSample(1300);
		gnuplot.setXRange(cs.xAxis.range, pi);
		gnuplot.setYRange(cs.yAxis.range);

		// functions
		int i = 0;
		PlotList plotList = new PlotList(cs);
		for (Function function : functions) {
			Node graph = plots.appendChild(doc.createGroup("plot-" + ++i));
			Element path = (Element) graph.appendChild(doc
					.createElement("path"));
			path.setAttribute("clip-path", "url(#plot-area)");

			String points = "";
			Plot plot = new Plot(function, gnuplot);
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

		if (integral != null && integral.function1 >= 0
				&& plotList.size() > integral.function1) {

			Element parent = viewbox;
			Element a = doc.getChildElementById(viewbox, "axes");
			if (a != null) {
				Element integralContainer = doc.createGroup("integrals");
				viewbox.insertBefore(integralContainer, a);
				parent = integralContainer;
			}
			Plot p2 = null;
			if (integral.function2 >= 0 && plotList.size() > integral.function2
					&& integral.function2 != integral.function1)
				p2 = plotList.get(integral.function2);
			new IntegralPlot(cs).handlePlotIntergral(
					plotList.get(integral.function1),
					doc,
					parent,
					integral.xRange != null ? Math.max(integral.xRange.from,
							cs.xAxis.range.from) : cs.xAxis.range.from,
					integral.xRange != null ? Math.min(integral.xRange.to,
							cs.xAxis.range.to) : cs.xAxis.range.to, p2);
		}

		// overlays for audio tactile output
		OverlayList overlays = plotList.overlays();

		// TODO: add scatter plot
		// points or scatter plots
		if (points != null && points.size() > 0) {
			int j = 0;
			Element poiGroup = doc.createElement("g", "points");
			viewbox.appendChild(poiGroup);
			for (PointList pl : points) {
				if (pl != null && pl.size() > 0) {

					Element plGroup = doc.createElement("g", "points_" + j);
					poiGroup.appendChild(plGroup);

					for (Point p : pl) {
						Element symbol = PointPlot.getPointSymbolForIndex(j,
								doc);
						Element ps = PointPlot.paintPoint(doc, cs.convert(p),
								symbol, plGroup != null ? plGroup : viewbox);
						ps.appendChild(doc.createTitle(format(p)));
						if (pl.name != null && !pl.name.isEmpty())
							ps.appendChild(doc.createDesc(pl.name)); // TODO: maybe fine this
						// add on top of overlays and avoid collision testing
						overlays.add(overlays.size(), new Overlay(p));
					}
				}
				j++;
			}
		}

		// overlays for audio tactile output
		Node overlaysElement = viewbox.appendChild(doc.createGroup("overlays"));
		for (Function function : functions) {
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
		circle.appendChild(doc.createTitle(format(overlay)));
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

		// TODO: explain the poi points

		// general description
		Node div = desc.appendBodyChild(desc.createDiv("functions"));
		div.appendChild(desc.createP(translateN("desc.intro",
				formatX(cs.xAxis.range.from), formatX(cs.xAxis.range.to),
				formatX(cs.xAxis.ticInterval), formatY(cs.yAxis.range.from),
				formatY(cs.yAxis.range.to), formatY(cs.yAxis.ticInterval),
				formatName(cs.xAxis.range.name),
				formatName(cs.yAxis.range.name), functions.size())));

		// functions
		if (!functions.isEmpty()) {
			Node ol = div.appendChild(desc.createElement("ul"));
			int f = 0;
			for (Function function : functions) {
				Element li = (Element) ol.appendChild(desc.createElement("li"));
				li.appendChild(desc.createTextElement("span",
						getFunctionName(f++) + "(x) = "));
				if (function.hasTitle()) {
					li.appendChild(desc.createTextElement("strong",
							function.getTitle() + ":"));
					li.appendChild(desc.createTextNode(" "
							+ function.getFunction() + nl + tab + tab));
				} else {
					li.appendChild(desc.createTextElement("span",
							function.getFunction()));
				}
			}

			// intersections between functions
			if (functions.size() > 1) {
				div = desc.appendBodyChild(desc.createDiv("intersections"));
				boolean hasIntersections = false;
				for (int i = 0; i < plotList.size() - 1; i++) {
					for (int k = i + 1; k < plotList.size(); k++) {
						List<Point> intersections = plotList.get(i)
								.getIntersections(plotList.get(k));
						if (!intersections.isEmpty()) {
							hasIntersections = true;
							div.appendChild(desc.createP(translateN(
									"desc.intersections", getFunctionName(i),
									getFunctionName(k), intersections.size())));
							div.appendChild(createPointList(intersections, "S",
									s));
							s += intersections.size();
						}
					}
				}
				if (!hasIntersections) {
					div.appendChild(desc
							.createP(translate("desc.intersections_0")));
				}
			}
		}

		// extreme points & zero
		for (int i = 0; i < plotList.size(); i++) {
			div = desc.appendBodyChild(desc.createDiv("function-"
					+ getFunctionName(i)));
			List<Point> extrema = plotList.get(i).getExtrema();
			String f = plotList.size() == 1 ? "" : " " + getFunctionName(i);
			div.appendChild(desc.createP(translateN("desc.extrema", f,
					extrema.size())));
			if (!extrema.isEmpty()) {
				div.appendChild(createPointList(extrema, "E", e));
				e += extrema.size();
			}
			List<Point> roots = plotList.get(i).getRoots();
			div.appendChild(desc.createP(translateN("desc.roots", roots.size())));
			if (!roots.isEmpty()) {
				div.appendChild(createXPointList(roots, r));
				r += roots.size();
			}
		}
		
		// integral
		if(integral != null && integral.function1 >= 0){
			div = desc.appendBodyChild(desc.createDiv("integral-"));
			if(integral.function2 >= 0)			
				div.appendChild(desc.createP(translate("desc.integral_1", 
						Math.max(cs.xAxis.range.from, integral.xRange.from),
						Math.min(cs.xAxis.range.to, integral.xRange.to),
						getFunctionName(integral.function1),
						getFunctionName(integral.function2))));
			else
				div.appendChild(desc.createP(translate("desc.integral_0", 
						Math.max(cs.xAxis.range.from, integral.xRange.from),
						Math.min(cs.xAxis.range.to, integral.xRange.to),
						getFunctionName(integral.function1))));			
		}

		desc.appendBodyChild(desc.createP(translate("desc.note")));
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
				list.appendChild(desc.createTextElement("li",
						formatForText(point, cap + "_" + ++i)));
			} else {
				list.appendChild(desc.createTextElement("li", format(point)));
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
				list.appendChild(desc.createTextElement("li", cap + "_" + ++i
						+ " = " + formatX(point.x)));
			} else {
				list.appendChild(desc.createTextElement("li", formatX(point.x)));
			}
		}
		return list;
	}

	// TODO: generate textual braille key
	/**
	 * Paint the key as an svg file.
	 * 
	 * @param pos
	 *            | ??? //TODO: find out what this means
	 */
	private void createLegend(Point pos) {
		int distance = 7;
		pos.y += 2 * distance;

		Element viewbox = (Element) legend.appendChild(legend
				.createElement("svg"));
		viewbox.setAttribute("viewBox", "0 0 " + format2svg(size.x) + " "
				+ format2svg(size.y));

		Node plots = viewbox.appendChild(legend.createGroup("plots"));
		int i = 0;
		for (Function function : functions) {
			Node plot = plots
					.appendChild(legend.createGroup("plot-" + (i + 1)));
			plot.appendChild(legend.createLine(new Point(pos.x, pos.y - 5),
					new Point(pos.x + 26, pos.y - 5)));

			pos.translate(35, 0);
			if (function.hasTitle()) {
				legend.appendChild(legend.createText(pos, getFunctionName(i)
						+ "(x) = " + function.getTitle() + ":",
						function.getFunction()));
			} else {
				legend.appendChild(legend.createText(pos, getFunctionName(i)
						+ "(x) = " + function.getFunction()));
			}
			pos.translate(-35, distance);
			i++;
		}

		//integrals
		if(integral != null && integral.function1 >= 0){
			pos.translate(0, -10);
			Node integrals = viewbox.appendChild(legend.createGroup("integral"));
			Node integralGroup = integrals.appendChild(legend.createGroup("integral-0"));
			
			//append devs
			if (legend.defs != null) {
				legend.defs.appendChild(IntegralPlot.getFillPattern(legend));
			}
			
			Node iBox = integralGroup.appendChild(legend.createRectangle(pos, 30, 15));
			((Element)iBox).setAttribute("class", "integral-1 box");
			
			String text = "";
			if(integral.function2 >= 0)			
				text = translate("legend.integral_1", 
						Math.max(cs.xAxis.range.from, integral.xRange.from),
						Math.min(cs.xAxis.range.to, integral.xRange.to),
						getFunctionName(integral.function1),
						getFunctionName(integral.function2));
			else
				text = translate("legend.integral_0", 
						Math.max(cs.xAxis.range.from, integral.xRange.from),
						Math.min(cs.xAxis.range.to, integral.xRange.to),
						getFunctionName(integral.function1));
			
			
			pos.translate(35, distance + 5);
			legend.appendChild(legend.createText(pos, text));
			
			pos.translate(-35, distance);
		}		
		
		pos.translate(0, distance);
		legend.appendChild(legend.createText(
				pos,
				translate("legend.xrange", formatX(cs.xAxis.range.from),
						formatX(cs.xAxis.range.to),
						formatName(cs.xAxis.range.name)),
				translate("legend.xtic", formatX(cs.xAxis.ticInterval))));

		pos.translate(0, distance);
		legend.appendChild(legend.createText(
				pos,
				translate("legend.yrange", formatY(cs.yAxis.range.from),
						formatY(cs.yAxis.range.to),
						formatName(cs.yAxis.range.name)),
				translate("legend.ytic", formatY(cs.yAxis.ticInterval))));
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
		if (pi && !"0".equals(str)) {
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
	public String format(Point point) {
		return ((point.name != null && !point.name.isEmpty()) ? point.name
				+ " " : "")
				+ formatX(point.x) + " / " + formatY(point.y);
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
	 * Formats a additional Name of an object. Checks if the name is set. If
	 * name is set the name is packed into brackets and prepend with an
	 * whitespace
	 * 
	 * @param name
	 *            | optional name of an object or NULL
	 * @return empty string or the name of the object packed into brackets and
	 *         prepend with a whitespace e.g. ' (optional name)'
	 */
	public static String formatName(String name) {
		return (name == null || name.isEmpty()) ? "" : " (" + name + ")";
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value
	 * @return a localized string for the given PropertyResourceBundle key,
	 *         filled with the set arguments
	 */
	public static String translate(String key, Object... arguments) {
		return MessageFormat.format(bundle.getString(key), arguments);
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file. This function is optimized for differing
	 * sentences depending on the amount of results.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value. The last argument gives the
	 *            count and decide which value will be returned.
	 * @return a localized string for the given amount depending
	 *         PropertyResourceBundle key, filled with the set arguments
	 */
	public static String translateN(String key, Object... arguments) {
		int last = (int) arguments[arguments.length - 1];
		String suffix = last == 0 ? "_0" : last == 1 ? "_1" : "_n";
		return translate(key + suffix, arguments);
	}

	public static String format2svg(double value) {
		if (decimalFormat == null) {
			decimalFormat = new DecimalFormat("0.###");
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			decimalFormat.setDecimalFormatSymbols(dfs);
		}
		return decimalFormat.format(value);
	}

	/**
	 * Try to translate the function index into an continuous literal starting
	 * with the char 'f'. If the given index is not valid it returns the name as
	 * an combination of 'f' + the given number.
	 * 
	 * @param f
	 *            | the index if the function
	 * @return a literal representation to the given function index e.g. 'f',
	 *         'g', 'h' or 'f1000'.
	 */
	public static String getFunctionName(int f) {
		if (f < 0 || f > (fnList.length - 1))
			return "f" + String.valueOf(f);
		return fnList[f];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SvgPlot plot = new SvgPlot();
		JCommander jc = new JCommander(plot);
		jc.addConverterFactory(new SvgPlot.StringConverterFactory());

		try {
			final Properties properties = new Properties();
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream("svgplot.properties"));
			properties.load(stream);
			stream.close();

			jc.setDefaultProvider(new IDefaultProvider() {
				@Override
				public String getDefaultValueFor(String optionName) {
					return properties.getProperty(optionName.replaceFirst(
							"^-+", ""));
				}
			});
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("\\s*-[^-][^=:,]+")) {
				args[i] = "\\" + args[i].trim();
			}
		}

		jc.parse(args);		

		if (plot.help) {
			jc.setProgramName("java -jar svgplot.jar");
			jc.usage();
			return;
		}

		plot.points = (new PointListList.Converter()).convert(plot.pts);
		// if(plot.xRange.from > plot.points.minX) plot.xRange.from =
		// plot.points.minX + plot.xRange.
		// TODO: adopt the view range to the set points.

		try {
			plot.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a converter for the special class-types of this project for
	 * JCommander interpretation.
	 */
	public static class StringConverterFactory implements
			IStringConverterFactory {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<? extends IStringConverter<?>> getConverter(Class forType) {
			if (forType.equals(Point.class))
				return Point.Converter.class;
			else if (forType.equals(Range.class))
				return Range.Converter.class;
			else if (forType.equals(Function.class))
				return Function.Converter.class;
			else if (forType.equals(PointListList.class))
				return PointListList.Converter.class;
			else if (forType.equals(IntegralPlotSettings.class))
				return IntegralPlotSettings.Converter.class;
			else
				return null;
		}
	}

}
