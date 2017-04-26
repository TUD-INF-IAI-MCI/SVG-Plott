package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgLineOverlayPainter extends SvgPainter {

	CoordinateSystem cs;
	Map<PointList, String> polyLineStrings;

	public SvgLineOverlayPainter(CoordinateSystem cs, Map<PointList, String> polyLineStrings) {
		this.polyLineStrings = polyLineStrings;
		this.cs = cs;
	}
	
	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions.append("#overlayLines { stroke: transparent; stroke-dasharray: none; fill: none; stroke-width: 6.0}")
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

		// Add the overlays -- TODO order by function
		for (Entry<PointList, String> lineData : polyLineStrings.entrySet()) {
			overlayLinesElement.appendChild(createOverlay(doc, lineData.getKey(), lineData.getValue()));
		}
	}

	protected Element createOverlay(SvgDocument doc, PointList pointList, String polyLineString) {
		Element polyLine = doc.createElement("polyline");
		polyLine.setAttribute("points", polyLineString);
		polyLine.appendChild(doc.createTitle(pointList.getName()));
		return polyLine;
	}
}
