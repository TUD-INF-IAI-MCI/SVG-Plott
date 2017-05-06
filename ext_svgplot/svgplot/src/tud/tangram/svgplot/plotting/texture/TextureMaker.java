package tud.tangram.svgplot.plotting.texture;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * TextureMaker class having methods for several different texture styles in
 * order to support multiple devices.
 */
public abstract class TextureMaker {
	public String addTexture(SvgDocument doc, OutputDevice device) {
		switch (device) {
		case MicroCapsulePaper:
			return addMicroCapsulePaperTexture(doc);
		case PinDevice:
			return addPinDeviceTexture(doc);
		case TigerEmbosser:
		case Default:
		default:
			return addTigerEmbosserTexture(doc);
		}
	}

	protected abstract String addTigerEmbosserTexture(SvgDocument doc);

	protected abstract String addMicroCapsulePaperTexture(SvgDocument doc);
	
	protected abstract String addPinDeviceTexture(SvgDocument doc);

	protected Element createPattern(SvgDocument doc, String id, Double width, Double height) {
		Element pattern = doc.createElement("pattern");
		pattern.setAttribute("id", id);
		pattern.setAttribute("width", width.toString());
		pattern.setAttribute("height", height.toString());
		pattern.setAttribute("patternUnits", "userSpaceOnUse");
		
		return pattern;
	}
	
	protected Element createRect(SvgDocument doc, Double x, Double y, String width, String height, String fill, String stroke) {
		Element rect = doc.createElement("rect");
		rect.setAttribute("x", x.toString());
		rect.setAttribute("y", y.toString());
		rect.setAttribute("width", width);
		rect.setAttribute("height", height);
		rect.setAttribute("fill", fill);
		rect.setAttribute("stroke", stroke);
		return rect;
	}
	
	protected Element createLine(SvgDocument doc, Double x1, Double y1, Double x2, Double y2, String stroke,
			Double strokeWidth) {
		Element line = doc.createElement("line");
		line.setAttribute("x1", x1.toString());
		line.setAttribute("y1", y1.toString());
		line.setAttribute("x2", x2.toString());
		line.setAttribute("y2", y2.toString());
		line.setAttribute("stroke", stroke);
		line.setAttribute("stroke-width", strokeWidth.toString());
		return line;
	}
	
	protected Element createCircle(SvgDocument doc, Double cx, Double cy, Double r) {
		return doc.createCircle(new Point(cx, cy), r);
	}
}
