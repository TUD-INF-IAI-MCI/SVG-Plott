package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgLineOverlayPainter extends SvgPainter {

	CoordinateSystem cs;
	LinkedHashMap<PointList, String> polyLineStrings;
	List<Color> colors;

	public SvgLineOverlayPainter(CoordinateSystem cs, LinkedHashMap<PointList, String> polyLineStrings, LinkedHashSet<Color> colors) {
		this.polyLineStrings = polyLineStrings;
		this.cs = cs;
		this.colors = new ArrayList<>(colors);
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions
				.append("#overlayLines { stroke: transparent; stroke-dasharray: none; fill: none; stroke-width: 6.0; stroke-linecap: round; }")
				.append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, defaultOptions.toString());

		return deviceCss;
	}

	@Override
	protected String getPainterName() {
		return "Line Overlay Painter";
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		// Create an overlay node
		Element overlaysElement = doc.getOrCreateChildGroupById(viewbox, "overlays");
		Element overlayLinesElement = doc.getOrCreateChildGroupById(overlaysElement, "overlayLines");

		// Add the overlays
		int i = 0;
		for (Entry<PointList, String> lineData : polyLineStrings.entrySet()) {
			overlayLinesElement.appendChild(createOverlay(doc, lineData.getKey(), i, lineData.getValue()));
			i++;
		}
	}

	protected Element createOverlay(SvgDocument doc, PointList pointList, int index, String polyLineString) {
		Element polyLine = doc.createElement("polyline");
		polyLine.setAttribute("points", polyLineString);
		polyLine.appendChild(doc.createTitle(pointList.getName()));
		polyLine.appendChild(doc.createDesc(colors.get(index).toString()));
		return polyLine;
	}
}
