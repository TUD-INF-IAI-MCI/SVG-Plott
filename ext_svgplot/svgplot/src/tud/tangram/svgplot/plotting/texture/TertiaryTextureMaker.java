package tud.tangram.svgplot.plotting.texture;

import org.w3c.dom.Element;

import tud.tangram.svgplot.xml.SvgDocument;

public class TertiaryTextureMaker extends TextureMaker {

	@Override
	protected String addTigerEmbosserTexture(SvgDocument doc) {
		String id = "diagonal_line1_SP_T";
		
		Element g = doc.getOrCreateChildGroupById(doc.defs, "patterns");

		Element pattern = createPattern(doc, id, 10.16, 10.16);

		Element rect = createRect(doc, 0., 0., "100%", "100%", "white", "none");

		Element line1 = createLine(doc, 5.85, -1.27, 18.55, 11.43, "black", 0.8);
		Element line2 = createLine(doc, -1.77, 1.27, 8.39, 11.43, "black", 0.8);

		pattern.appendChild(rect);
		pattern.appendChild(line1);
		pattern.appendChild(line2);
		
		g.appendChild(pattern);
	
		return id;
	}

	@Override
	protected String addMicroCapsulePaperTexture(SvgDocument doc) {
		return addTigerEmbosserTexture(doc);
	}

	@Override
	protected String addPinDeviceTexture(SvgDocument doc) {
		String id = "diagonal_line1_PD";
		
		Element g = doc.getOrCreateChildGroupById(doc.defs, "patterns");

		Element pattern = createPattern(doc, id, 10., 10.);

		Element rect = createRect(doc, 0., 0., "100%", "100%", "white", "none");

		Element line1 = createLine(doc, 2.5, -2.5, 17.5, 12.5, "black", 1.3);
		Element line2 = createLine(doc, -2.5, 2.5, 7.5, 12.5, "black", 1.3);

		pattern.appendChild(rect);
		pattern.appendChild(line1);
		pattern.appendChild(line2);
		
		g.appendChild(pattern);
	
		return id;
	}

}
