package tud.tangram.svgplot.plotting.texture;

import org.w3c.dom.Element;

import tud.tangram.svgplot.xml.SvgDocument;

public class PrimaryTextureMaker extends TextureMaker {

	public PrimaryTextureMaker() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String addTigerEmbosserTexture(SvgDocument doc) {
		String id = "black";
		
		Element g = doc.getOrCreateChildGroupById(doc.defs, "patterns");
		
		Element pattern = createPattern(doc, id, 10., 10.);
		Element rect = createRect(doc, 0., 0., "100%", "100%", "black", "none");
		
		pattern.appendChild(rect);
		
		g.appendChild(pattern);
		
		return id;
	}

	@Override
	protected String addMicroCapsulePaperTexture(SvgDocument doc) {
		String id = "full_pattern";
		
		Element g = doc.getOrCreateChildGroupById(doc.defs, "patterns");
		
		Element pattern = createPattern(doc, id, 2.5, 2.5);
		
		Element rect = createRect(doc, 0., 0., "100%", "100%", "white", "none");
		Element circle = createCircle(doc, 1.25, 1.25, 0.6);
		
		pattern.appendChild(rect);
		pattern.appendChild(circle);
		
		g.appendChild(pattern);
		
		return id;
	}

	@Override
	protected String addPinDeviceTexture(SvgDocument doc) {
		return addTigerEmbosserTexture(doc);
	}

}
