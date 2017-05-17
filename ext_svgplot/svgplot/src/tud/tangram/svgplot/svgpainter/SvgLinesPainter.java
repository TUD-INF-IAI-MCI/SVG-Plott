package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.legend.LegendLineItem;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgLinesPainter extends SvgPainter {

	private PointListList points;
	private CoordinateSystem cs;
	private LinkedHashMap<PointList, String> polyLinePoints;
	private final List<Color> colors;

	public SvgLinesPainter(CoordinateSystem cs, PointListList pointListList, LinkedHashSet<Color> colors) {
		this.points = pointListList;
		this.cs = cs;
		this.colors = new ArrayList<>(colors);
	}

	@Override
	protected String getPainterName() {
		return "Line Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		double width = 2 * Constants.STROKE_WIDTH;

		StringBuilder defaultCss = new StringBuilder();

		defaultCss.append(
				"#linecharts { stroke-width: " + width + "; stroke-dasharray: " + width * 5 + ", " + width * 5 + "; }")
				.append(System.lineSeparator());
		defaultCss.append("#linechart-1 { stroke-dasharray: none; }").append(System.lineSeparator());
		defaultCss.append("#linechart-2 { stroke-dasharray: " + width + ", " + width * 3 + "; }")
				.append(System.lineSeparator());
		defaultCss.append(".linechart_bg { stroke: white; stroke-dasharray: none; stroke-width:" + (width * 3)
				+ ";  fill: transparent; stroke-linecap: round; }").append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, defaultCss.toString());

		StringBuilder screenHighContrastCss = new StringBuilder();
		screenHighContrastCss.append("#linecharts { stroke-width: 1.5; stroke-dasharray: none; stroke: yellow;}")
				.append(System.lineSeparator());
		screenHighContrastCss.append("#linechart-2 { stroke-dasharray: 2.0, 3.0; stroke: #00ffff;}")
				.append(System.lineSeparator());
		screenHighContrastCss.append("#linechart-3 { stroke-dasharray: 5.0, 5.0; stroke: #ff00ff;}")
				.append(System.lineSeparator());
		screenHighContrastCss
				.append(".linechart_bg { stroke: black; stroke-dasharray: none; stroke-width:3.0;  fill: transparent; stroke-linecap: round; }")
				.append(System.lineSeparator());

		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastCss.toString());

		StringBuilder screenColorCss = new StringBuilder();

		for (int i = 0; i < points.size(); i++) {
			screenColorCss.append("#linechart-" + (i + 1) + " { stroke: " + colors.get(i).getRgbColor() + ";}")
					.append(System.lineSeparator());
		}
		screenColorCss.append(".linechart_bg { stroke: none; }").append(System.lineSeparator());

		deviceCss.put(OutputDevice.ScreenColor, screenColorCss.toString());

		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		this.polyLinePoints = new LinkedHashMap<>();

		if (points == null || points.isEmpty()) {
			return;
		}

		int j = 0;

		Element linechartGroup = doc.getOrCreateChildGroupById(viewbox, "linecharts");

		// Used to insert the third line before the second one, as
		// the third is stronger - the second one therefore needs to lie above
		// it.
		Element lastBg = null;

		for (PointList pointList : points) {
			if (pointList.size() <= 1) {
				j++;
				continue;
			}

			StringBuilder polyLinePointsBuilder = new StringBuilder();
			Element polyLine = doc.createElement("polyline", "linechart-" + ++j);
			Element polyLineBg = doc.createElement("polyline");
			polyLineBg.setAttribute("class", "linechart_bg");

			for (Point point : pointList) {
				Point convertedPoint = cs.convertWithOffset(point);
				polyLinePointsBuilder.append(convertedPoint.x());
				polyLinePointsBuilder.append(",");
				polyLinePointsBuilder.append(convertedPoint.y());
				polyLinePointsBuilder.append(" ");
			}

			String polyLinePointsString = polyLinePointsBuilder.toString();

			polyLinePoints.put(pointList, polyLinePointsString);
			
			polyLine.setAttribute("points", polyLinePointsString);
			polyLineBg.setAttribute("points", polyLinePointsString);
		
			// Insert the third line before the second
			if(j == 3 && lastBg != null){
				linechartGroup.insertBefore(polyLine, lastBg);
				linechartGroup.insertBefore(polyLineBg, polyLine);
			}
			else {
				linechartGroup.appendChild(polyLineBg);
				linechartGroup.appendChild(polyLine);
			}
			
			lastBg = polyLineBg;
		}
	}

	/**
	 * Get the line data used for overlay creation. The data can be set as a the
	 * {@code points} attribute of an SVG polyline. Call after
	 * {@link paintToSvgDocument(SvgDocument, Element, OutputDevice)
	 * paintToSvgDocument}.
	 * 
	 * @return SVG polyline data for overlay creation
	 */
	public LinkedHashMap<PointList, String> getLineDataForOverlayCreation() {
		return polyLinePoints;
	}

	@Override
	public void prepareLegendRenderer(LegendRenderer renderer, OutputDevice device) {
		super.prepareLegendRenderer(renderer, device);

		int j = 0;

		for (PointList pointList : points) {
			if (pointList.size() <= 1) {
				j++;
				continue;
			}

			renderer.add(new LegendLineItem(pointList.getName(), ++j));
		}
	}

}
