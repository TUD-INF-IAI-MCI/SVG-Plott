package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * Paint overlays to the SVG document. Leaves the legend untouched.
 *
 */
public class SvgPointOverlayPainter extends SvgPainter {

	CoordinateSystem cs;
	OverlayList overlays;

	public SvgPointOverlayPainter(CoordinateSystem cs, OverlayList overlays) {
		this.overlays = overlays;
		this.cs = cs;
	}
	
	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		StringBuilder defaultOptions = new StringBuilder();
		defaultOptions.append("#overlayPoints { stroke: none; stroke-dasharray: none; fill: transparent; }")
				.append(System.lineSeparator());
	
		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
		
		return deviceCss;
	}

	@Override
	protected String getPainterName() {
		return "Point Overlay Painter";
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		// Create an overlay node
		Element overlaysElement = doc.getOrCreateChildGroupById(viewbox, "overlays");
		Element overlaysPointsElement = doc.getOrCreateChildGroupById(overlaysElement, "overlayPoints");

		// Add the overlays -- TODO order by function
		for (Overlay overlay : overlays) {
			// if (overlay.getFunction() == null) {
			overlaysPointsElement.appendChild(createOverlay(doc, overlay));
			// }
		}
	}

	/**
	 * Paints an overlay in the svg file
	 * 
	 * @param doc
	 *            the SVG document
	 * @param overlay
	 *            the overlay that should be insert, containing position and
	 *            additional informations about the point underneath it.
	 * @return a DOM Element representing the already inserted overlay node.
	 */
	protected Element createOverlay(SvgDocument doc, Overlay overlay) {
		Element circle = doc.createCircle(cs.convert(overlay), Overlay.RADIUS);
		circle.appendChild(doc.createTitle(SvgTools.formatForSpeech(cs, overlay)));
		if (overlay.getFunction() != null) {
			circle.appendChild(doc.createDesc(overlay.getFunction().toString()));
		}
		return circle;
	}

}
