 package tud.tangram.svgplot.plotting.texture;

import org.w3c.dom.Element;

import tud.tangram.svgplot.xml.SvgDocument;

public class TertiaryTextureMaker extends TextureMaker {

	@Override
	protected String addTigerEmbosserTexture(SvgDocument doc) {
		String id = "dotted_pattern";
		
		Element g = doc.getOrCreateChildGroupById(doc.defs, "patterns");
		
		Element pattern = createPattern(doc, id, 10., 10.);
		pattern.setAttribute("fill", "black");
		
		Element rect = createRect(doc, 0., 0., "100%", "100%", "white", "none");
		Element circle1 = createCircle(doc, 1.25, 1.25, 0.6);
		Element circle2 = createCircle(doc, 6.25, 6.25, 0.6);
		
		pattern.appendChild(rect);
		pattern.appendChild(circle1);
		pattern.appendChild(circle2);
		
		g.appendChild(pattern);
		
		return id;
	}

	@Override
	protected String addMicroCapsulePaperTexture(SvgDocument doc) {
		return addTigerEmbosserTexture(doc);
	}

	@Override
	protected String addPinDeviceTexture(SvgDocument doc) {
		return addTigerEmbosserTexture(doc);
	}

}
