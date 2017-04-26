package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.legend.LegendLineItem;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgLinesPainter extends SvgPainter {

	PointListList points;
	CoordinateSystem cs;

	public SvgLinesPainter(CoordinateSystem cs, PointListList pointListList) {
		this.points = pointListList;
		this.cs = cs;
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
		defaultCss.append(".linechart_bg { stroke: black; stroke-dasharray: none; stroke-width:3.0;  fill: transparent; stroke-linecap: round; }").append(System.lineSeparator());

		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastCss.toString());

		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		if (points == null || points.isEmpty()) {
			return;
		}

		int j = 0;

		Element linechartGroup = doc.getOrCreateChildGroupById(viewbox, "linecharts");

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
				Point convertedPoint = cs.convert(point);
				polyLinePointsBuilder.append(convertedPoint.x());
				polyLinePointsBuilder.append(",");
				polyLinePointsBuilder.append(convertedPoint.y());
				polyLinePointsBuilder.append(" ");
			}

			polyLine.setAttribute("points", polyLinePointsBuilder.toString());
			polyLineBg.setAttribute("points", polyLinePointsBuilder.toString());

			linechartGroup.appendChild(polyLineBg);
			linechartGroup.appendChild(polyLine);
		}
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
