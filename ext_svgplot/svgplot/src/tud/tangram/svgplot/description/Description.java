package tud.tangram.svgplot.description;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.MetricAxis;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.data.trendline.TrendLineAlgorithm;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.PlotList;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.GridStyle;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.HtmlDocument;

public class Description extends HtmlDocument {

	private static final String TAB = "    ";
	private static final String NL = "\n" + TAB + TAB;

	public Description(String title) throws ParserConfigurationException {
		super(title);
	}

	/**
	 * Create a description text for the diagram type and title. If a barchart
	 * is described also adds a more precise description of its accumulation
	 * style (stacked,...).
	 * 
	 * @param diagramType
	 * @param barAccumulationStyle
	 * @param title
	 * @param dataSetCount
	 * @return
	 */
	public Node createDiagramTypeDescription(DiagramType diagramType, BarAccumulationStyle barAccumulationStyle,
			String title, int dataSetCount) {
		Node descriptionDiv = createDiv("diagram");

		Node typeAndTitle;
		if (title == null || title.length() < 1) {
			typeAndTitle = createP(SvgTools.translate("desc.diagramtype_notitle",
					SvgTools.translate("desc.diagramtype." + diagramType)));
		} else {
			String typeAndTitleText = SvgTools.translate("desc.diagramtype_title",
					SvgTools.translate("desc.diagramtype." + diagramType), title);
			if (diagramType == DiagramType.BarChart) {
				typeAndTitleText += TAB + SvgTools.translate("desc.diagramtype_barcharttype", SvgTools.translate(
						"desc.diagramtype_barcharttype." + (dataSetCount < 2 ? "single" : barAccumulationStyle)));
			}
			typeAndTitle = createP(typeAndTitleText);
		}
		descriptionDiv.appendChild(typeAndTitle);

		return descriptionDiv;
	}

	/**
	 * Creates a general description of the axis layout.
	 * 
	 * @param diagramType
	 * @param cs
	 * @param xAxisStyle
	 * @param yAxisStyle
	 * @return
	 */
	public Node createAxisPositionDescription(DiagramType diagramType, CoordinateSystem cs, AxisStyle xAxisStyle,
			AxisStyle yAxisStyle) {
		Node axisPosition = createDiv("axisposition");

		String axisText = SvgTools.translate("desc.axis_position_first");
		if (diagramType != DiagramType.BarChart) {
			Point intersect = new Point(cs.xAxis.getRange().getFrom(), cs.yAxis.getRange().getFrom());
			axisText += TAB + SvgTools.translate("desc.axis_position_first.intersect", cs.formatForSpeech(intersect));
		}
		axisText += ".";

		if (yAxisStyle == AxisStyle.BOX) {
			String axisSecondSelection = xAxisStyle == AxisStyle.BOX ? "both" : "vertical";
			axisText += TAB;
			axisText += SvgTools.translate("desc.axis_position_second_start." + axisSecondSelection) + TAB;
			axisText += SvgTools.translate("desc.axis_position_second");
		}

		axisPosition.appendChild(createP(axisText));

		return axisPosition;
	}

	public Node createAxisDetailDescription(CoordinateSystem cs, GridStyle gridStyle) {
		Node axisDetails = createDiv("axisdetails");

		// x axis
		if (cs.xAxis instanceof MetricAxis) {
			String horizontal = SvgTools.translate("desc.horizontal_det");

			String xUnit = cs.xAxis.getUnit();
			String xAxisUnit = xUnit != null && xUnit.length() > 0
					? TAB + SvgTools.translate("desc.axis_detail_unit", xUnit) : "";

			String xTitle = cs.xAxis.getTitle();
			String xAxisTitle = xTitle != null && xTitle.length() > 0
					? TAB + SvgTools.translate("desc.axis_detail_title", xTitle) : "";

			String xAxisDetails = SvgTools.translate("desc.axis_detail", horizontal, xAxisTitle, xAxisUnit,
					cs.formatX(cs.xAxis.getRange().getFrom()), cs.formatX(cs.xAxis.getRange().getTo()),
					cs.formatX(cs.xAxis.getTicInterval()));

			horizontal = SvgTools.translate("desc.horizontal_neutral");
			if (gridStyle == GridStyle.HORIZONTAL || gridStyle == GridStyle.FULL)
				xAxisDetails += TAB + SvgTools.translate("desc.axis_grid", horizontal);

			axisDetails.appendChild(createP(xAxisDetails));
		}

		// y axis
		String vertical = SvgTools.translate("desc.vertical_det");

		String yUnit = cs.yAxis.getUnit();
		String yAxisUnit = yUnit != null && yUnit.length() > 0
				? TAB + SvgTools.translate("desc.axis_detail_unit", yUnit) : "";

		String yTitle = cs.yAxis.getTitle();
		String yAxisTitle = yTitle != null && yTitle.length() > 0
				? TAB + SvgTools.translate("desc.axis_detail_title", yTitle) : "";

		String yAxisDetails = SvgTools.translate("desc.axis_detail", vertical, yAxisTitle, yAxisUnit,
				cs.formatY(cs.yAxis.getRange().getFrom()), cs.formatY(cs.yAxis.getRange().getTo()),
				cs.formatY(cs.yAxis.getTicInterval()));

		vertical = SvgTools.translate("desc.vertical_neutral");
		if (gridStyle == GridStyle.VERTICAL || gridStyle == GridStyle.FULL)
			yAxisDetails += TAB + SvgTools.translate("desc.axis_grid", vertical);

		axisDetails.appendChild(createP(yAxisDetails));

		return axisDetails;
	}

	/**
	 * Describe a point data set, including the point counts for each subset.
	 * 
	 * @param points
	 * @return
	 */
	public Node createPointDataSetDescription(PointListList points) {
		Node pointDataSet = createDiv("pointdataset");

		// If no data are displayed, only create a text stating this fact.
		if (points == null || points.isEmpty()) {
			pointDataSet.appendChild(createP(SvgTools.translate("desc.datacount_0")));
			return pointDataSet;
		}

		// Data set count
		if (points.size() == 1)
			pointDataSet.appendChild(createP(SvgTools.translate("desc.datacount_1", points.size()) + ":"));
		else
			pointDataSet.appendChild(createP(SvgTools.translate("desc.datacount_n", points.size()) + ":"));

		// Data sets with their point counts
		Node ul = createElement("ul");

		for (PointList pl : points) {
			Node li = createElement("li");

			li.appendChild(createTextElement("strong", pl.getName() + ":"));

			String text;
			if (pl.size() < 2)
				text = pl.isEmpty() ? SvgTools.translate("desc.data_0") : SvgTools.translate("desc.data_1");
			else
				text = SvgTools.translate("desc.data_n", pl.size());
			li.appendChild(createTextElement("span", " " + text));

			ul.appendChild(li);
		}

		pointDataSet.appendChild(ul);

		return pointDataSet;
	}

	/**
	 * Describe a line data set including maxima and minima for each subset.
	 * 
	 * @param points
	 * @return
	 */
	public Node createLineDataSetDescription(CoordinateSystem cs, PointListList points) {
		Node lineDataSet = createDiv("linedataset");

		// If no data are displayed, only create a text stating this fact.
		if (points == null || points.isEmpty()) {
			lineDataSet.appendChild(createP(SvgTools.translate("desc.datacount_0")));
			return lineDataSet;
		}

		// Data set count and description of minima and maxima format
		if (points.size() == 1)
			lineDataSet.appendChild(
					createP(SvgTools.translate("desc.datacount_1") + ". " + SvgTools.translate("desc.line.minmax")));
		else
			lineDataSet.appendChild(createP(SvgTools.translate("desc.datacount_n", points.size()) + ". "
					+ SvgTools.translate("desc.line.minmax")));

		// Data sets with their maxima and minima
		Node ul = createElement("ul");

		for (PointList pl : points) {
			Node li = createElement("li");

			li.appendChild(createTextElement("strong", pl.getName() + ":"));

			if (pl.isEmpty()) {
				li.appendChild(createTextElement("span", " " + SvgTools.translate("desc.data_0")));
			} else if (pl.size() == 1) {
				li.appendChild(createTextElement("span",
						" " + SvgTools.translate("desc.data_1") + ": " + cs.formatForSpeech(pl.get(0))));
			} else {
				// TODO support multiple maxima and minima
				Point max = pl.getFirstMaximum();
				Point min = pl.getFirstMinimum();

				StringBuilder textBuilder = new StringBuilder(" ");
				textBuilder.append(SvgTools.formatForText(cs, max, "max"));
				textBuilder.append("; ");
				textBuilder.append(SvgTools.formatForText(cs, min, "min"));

				li.appendChild(createTextElement("span", " " + textBuilder));
			}

			ul.appendChild(li);
		}

		lineDataSet.appendChild(ul);

		return lineDataSet;
	}

	public Node createBarSortingDescription(CategorialPointListList points, SortingType sortingType,
			boolean descending) {
		Node barSorting = createDiv("barsorting");

		// Initial sorting description
		String sortingText;
		if (sortingType == SortingType.None)
			sortingText = SvgTools.translate("desc.barchart.sorting.notsorted");
		else
			sortingText = SvgTools.translate("desc.barchart.sorting.sortingstring",
					SvgTools.translate("desc.barchart.sorting." + sortingType),
					SvgTools.translate("desc.barchart.sorting." + (descending ? "desc" : "asc")),
					SvgTools.translate("desc.barchart.sorting.sorted"));

		barSorting.appendChild(createP(SvgTools.translate("desc.barchart.sorting", sortingText)));

		Node ul = createElement("ul");

		for (String name : points.getCategoryNames()) {
			Node li = createElement("li");

			li.setTextContent(name);

			ul.appendChild(li);
		}

		barSorting.appendChild(ul);

		return barSorting;
	}

	public Node createBarDatasetDescription(PointListList points) {
		Node barDataSet = createDiv("bardataset");

		// If no data are displayed, only create a text stating this fact.
		if (points == null || points.isEmpty()) {
			barDataSet.appendChild(createP(SvgTools.translate("desc.datacount_0")));
			return barDataSet;
		}

		// Data set count
		if (points.size() == 1)
			barDataSet.appendChild(createP(SvgTools.translate("desc.datacount_1") + ":"));
		else
			barDataSet.appendChild(createP(SvgTools.translate("desc.datacount_n", points.size()) + ":"));

		Node ul = createElement("ul");

		for (PointList pl : points) {
			Node li = createElement("li");
			li.appendChild(createTextNode(pl.getName()));
			ul.appendChild(li);
		}

		barDataSet.appendChild(ul);

		return barDataSet;
	}

	public Node createTrendlinePointsDescription(PointListList points, TrendLineAlgorithm trendLineAlgorithm) {
		Node trendlineDescription = createDiv("trendlinedescription");

		trendlineDescription.appendChild(createP(SvgTools.translate("desc.trendline_points",
				SvgTools.translate("desc.trendline_points_" + (points.size() == 1 ? "1" : "n")),
				SvgTools.translate("desc.trendline_algorithm." + trendLineAlgorithm.getAlgorithmName()),
				trendLineAlgorithm.getAlgorithmParams())));

		return trendlineDescription;
	}

	public Node createTrendlineOnlyDescription(PointListList points, TrendLineAlgorithm trendLineAlgorithm) {
		Node trendlineDescription = createDiv("trendlinedescription");

		if (points.size() == 1) {
			trendlineDescription.appendChild(createP(SvgTools.translate("desc.trendline_only_1",
					SvgTools.translate("desc.trendline_algorithm." + trendLineAlgorithm.getAlgorithmName()),
					trendLineAlgorithm.getAlgorithmParams(), points.get(0).getName())));
		}
		else {
			trendlineDescription.appendChild(createP(SvgTools.translate("desc.trendline_only_n",
					SvgTools.translate("desc.trendline_algorithm." + trendLineAlgorithm.getAlgorithmName()),
					trendLineAlgorithm.getAlgorithmParams(), points.size())));
			
			Node ul = createElement("ul");
			
			for(PointList pl : points) {
				ul.appendChild(createTextElement("li", pl.getName()));
			}
			
			trendlineDescription.appendChild(ul);
		}

		return trendlineDescription;
	}

	/**
	 * Create a function intro node stating how many functions are displayed and
	 * explaining the coordinate system.
	 * 
	 * @param cs
	 * @param functionCount
	 * @return
	 */
	public Node createFunctionIntro(CoordinateSystem cs, int functionCount) {
		Range xAxisRange = cs.xAxis.getRange();
		Range yAxisRange = cs.yAxis.getRange();
		return createP(SvgTools.translateN("desc.intro", cs.formatX(xAxisRange.getFrom()),
				cs.formatX(xAxisRange.getTo()), cs.formatX(cs.xAxis.getTicInterval()), cs.formatY(yAxisRange.getFrom()),
				cs.formatY(yAxisRange.getTo()), cs.formatY(cs.yAxis.getTicInterval()),
				SvgTools.formatName(xAxisRange.getName()), SvgTools.formatName(yAxisRange.getName()), functionCount));
	}

	/**
	 * Create a list of functions node.
	 * 
	 * @param functions
	 *            | at least one element has to be in the list
	 * @return function list description element
	 */
	public Node createFunctionList(List<Function> functions) {
		if (functions == null || functions.isEmpty())
			throw new IllegalArgumentException(
					"Cannot create a function list from an empty or non-existant functions parameter.");
		Node ol = createElement("ul");
		int f = 0;
		for (Function function : functions) {
			Element li = (Element) ol.appendChild(createElement("li"));
			li.appendChild(createTextElement("span", SvgTools.getFunctionName(f++) + "(x) = "));
			if (function.hasTitle()) {
				li.appendChild(createTextElement("strong", function.getTitle() + ":"));
				li.appendChild(createTextNode(" " + function.getFunction() + NL + TAB + TAB));
			} else {
				li.appendChild(createTextElement("span", function.getFunction()));
			}
		}
		return ol;
	}

	/**
	 * Create a list of intersections between all functions. At least two
	 * elements have to be in the {@link PlotList}.
	 * 
	 * @param cs
	 * @param plotList
	 * @return
	 */
	public Node createFunctionIntersectionList(CoordinateSystem cs, PlotList plotList) {
		if (plotList.size() < 2)
			throw new IllegalArgumentException(
					"The PlotList needs to have at least two elements in order to calculate intersections.");
		int s = 0; // intersection offset counter
		Node intersectionDiv = createDiv("intersections");
		boolean hasIntersections = false;
		for (int i = 0; i < plotList.size() - 1; i++) {
			for (int k = i + 1; k < plotList.size(); k++) {
				List<Point> intersections = plotList.get(i).getIntersections(plotList.get(k));
				if (!intersections.isEmpty()) {
					hasIntersections = true;
					intersectionDiv.appendChild(createP(SvgTools.translateN("desc.intersections",
							SvgTools.getFunctionName(i), SvgTools.getFunctionName(k), intersections.size())));
					intersectionDiv.appendChild(createPointList(cs, intersections, "S", s));
					s += intersections.size();
				}
			}
		}
		if (!hasIntersections) {
			intersectionDiv.appendChild(createP(SvgTools.translate("desc.intersections_0")));
		}
		return intersectionDiv;
	}

	/**
	 * Create explanations for extrema and zeros for each function.
	 * 
	 * @param cs
	 * @param plotList
	 * @return
	 */
	public Node createFunctionExtremaAndZero(CoordinateSystem cs, PlotList plotList) {
		int e = 0; // extreme point offset counter
		int r = 0; // root point offset counter

		Node extremaDiv = createDiv("extrema_zeros");

		for (int i = 0; i < plotList.size(); i++) {

			Node div = createDiv("function-" + SvgTools.getFunctionName(i));

			List<Point> extrema = plotList.get(i).getExtrema();

			String f = plotList.size() == 1 ? "" : " " + SvgTools.getFunctionName(i);
			div.appendChild(createP(SvgTools.translateN("desc.extrema", f, extrema.size())));

			if (!extrema.isEmpty()) {
				div.appendChild(createPointList(cs, extrema, "E", e));
				e += extrema.size();
			}

			List<Point> roots = plotList.get(i).getRoots();
			div.appendChild(createP(SvgTools.translateN("desc.roots", roots.size())));
			if (!roots.isEmpty()) {
				div.appendChild(createXPointList(cs, roots, r));
				r += roots.size();
			}

			extremaDiv.appendChild(div);
		}

		return extremaDiv;
	}

	public Node createFunctionPointsDescription(CoordinateSystem cs, PointListList points) {
		if (points == null || points.isEmpty())
			throw new IllegalArgumentException("There have to be points specified in order to create a description.");
		Node pointsDiv = createDiv("points");
		pointsDiv.appendChild(createP(SvgTools.translateN("legend.poi.intro", points.size())));

		int j = 0;
		for (PointList pts : points) {
			if (pts != null && pts.size() > 0) {
				String text = pts.getName().isEmpty() ? SvgTools.getPointName(j) : pts.getName();
				pointsDiv.appendChild(createP(SvgTools.translateN("legend.poi.list", text, pts.size())));

				pointsDiv.appendChild(createPointList(cs, pts, SvgTools.getPointName(j), 0));
				j++;
			}
		}

		return pointsDiv;
	}

	/**
	 * Generates an HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @return ul element with points as a list
	 */
	public Element createPointList(CoordinateSystem cs, List<Point> points) {
		return createPointList(cs, points, null, 0);
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
	public Element createPointList(CoordinateSystem cs, List<Point> points, String cap) {
		return createPointList(cs, points, cap, 0);
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
	public Element createPointList(CoordinateSystem cs, List<Point> points, String cap, int offset) {
		Element list = createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(createTextElement("li", SvgTools.formatForText(cs, point, cap + "_" + ++i)));
			} else {
				list.appendChild(createTextElement("li", cs.formatForSpeech(point)));
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
	public Element createXPointList(CoordinateSystem cs, List<Point> points) {
		return createXPointList(cs, points, "x", 0);
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
	public Element createXPointList(CoordinateSystem cs, List<Point> points, int offset) {
		return createXPointList(cs, points, "x", offset);
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
	public Element createXPointList(CoordinateSystem cs, List<Point> points, String cap, int offset) {
		Element list = createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(createTextElement("li", cap + "_" + ++i + " = " + cs.formatX(point.getX())));
			} else {
				list.appendChild(createTextElement("li", cs.formatX(point.getX())));
			}
		}
		return list;
	}
}
