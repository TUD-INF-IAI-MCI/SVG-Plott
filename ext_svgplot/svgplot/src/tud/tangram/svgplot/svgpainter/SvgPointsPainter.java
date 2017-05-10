package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.legend.LegendPointItem;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.plotting.PointPlot.PointType;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * Paint points to the SVG document and document them in the legend.
 */
public class SvgPointsPainter extends SvgPainter {

	CoordinateSystem cs;
	PointListList points;
	OverlayList overlays;
	PointPlotStyle pointPlotStyle;
	List<Color> colors;

	public SvgPointsPainter(CoordinateSystem cs, PointListList points, PointPlotStyle pointPlotStyle,
			LinkedHashSet<Color> colors) {
		this.cs = cs;
		this.points = points;
		this.pointPlotStyle = pointPlotStyle;
		this.colors = new ArrayList<>(colors);
	}

	@Override
	protected String getPainterName() {
		return "Points Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder defaultOptions = new StringBuilder();

		defaultOptions.append(".poi_symbol { stroke: black; stroke-dasharray: none; stroke-width:"
				+ (Constants.STROKE_WIDTH * 1.5) + ";  fill: black; }").append(System.lineSeparator());
		defaultOptions
				.append("." + Constants.SPACER_CSS_CLASS + " { stroke: white; stroke-dasharray: none; stroke-width:"
						+ (Constants.STROKE_WIDTH * 6) + ";  fill: transparent; stroke-linecap: round; }")
				.append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, defaultOptions.toString());

		StringBuilder screenHighContrastOptions = new StringBuilder();
		screenHighContrastOptions
				.append(".poi_symbol { stroke: white; stroke-dasharray: none; stroke-width:0.75;  fill: white; }")
				.append(System.lineSeparator());
		screenHighContrastOptions
				.append(".poi_symbol_bg { stroke: black; stroke-dasharray: none; stroke-width:3.0;  fill: transparent; }")
				.append(System.lineSeparator());

		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastOptions.toString());

		StringBuilder screenColorCss = new StringBuilder();

		for (int i = 0; i < points.size(); i++) {
			screenColorCss.append("#points_fg_" + i + " .poi_symbol { stroke: " + colors.get(i).getRgbColor() + "; fill: "
					+ colors.get(i).getRgbColor() + ";}").append(System.lineSeparator());
		}
		screenColorCss.append(".linechart_bg { stroke: none; }").append(System.lineSeparator());

		deviceCss.put(OutputDevice.ScreenColor, screenColorCss.toString());

		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		overlays = new OverlayList(cs);

		if (points == null || points.isEmpty()) {
			return;
		}

		int j = 0;

		Element pointMainGroup = doc.createElement("g", "points");
		viewbox.appendChild(pointMainGroup);

		Element backgroundGroup = doc.createElement("g", "points_bg");
		pointMainGroup.appendChild(backgroundGroup);

		Element pointGroup = doc.createElement("g", "points_fg");
		pointMainGroup.appendChild(pointGroup);

		PointPlot pointPlot = new PointPlot(doc, pointPlotStyle);

		for (PointList pl : points) {
			if (pl == null || pl.isEmpty()) {
				j++;
				continue;
			}

			Element backgroundSubGroup = doc.createElement("g", "points_bg_" + j);
			backgroundGroup.appendChild(backgroundSubGroup);

			Element pointSubGroup = doc.createElement("g", "points_fg_" + j);
			pointGroup.appendChild(pointSubGroup);

			for (Point p : pl) {
				for (PointType pt : PointType.values()) {
					Element paintedPoint = pointPlot.paintPoint(pt == PointType.BG ? backgroundSubGroup : pointSubGroup,
							pt, cs.convert(p), j);
					if (paintedPoint != null) {
						paintedPoint.appendChild(doc.createTitle(cs.formatForSpeech(p)));

						// Add a description if the point list has a name. TODO
						// refine this
						if (pl.getName() != null && !pl.getName().isEmpty())
							paintedPoint.appendChild(doc.createDesc(pl.getName()));
					}
				}

				overlays.add(new Overlay(p), true);
			}

			j++;
		}
	}

	/**
	 * Adds the scatter plot overlays to the specified list.
	 * 
	 * @param overlays
	 *            list where the overlays shall be added to
	 */
	public void addOverlaysToList(OverlayList overlays) {
		overlays.addAll(this.overlays, true);
	}

	@Override
	public void prepareLegendRenderer(LegendRenderer renderer, OutputDevice device, int priority) {
		super.prepareLegendRenderer(renderer, device, priority);
		int pointSymbolIndex = 0;
		for (PointList pl : points) {
			if (pl != null && pl.size() > 0) {
				renderer.offer(new LegendPointItem(pl, pointPlotStyle, pointSymbolIndex, priority));
			}
			pointSymbolIndex++;
		}
	}

}
