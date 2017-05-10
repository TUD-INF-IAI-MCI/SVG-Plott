package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.texture.PrimaryTextureMaker;
import tud.tangram.svgplot.plotting.texture.SecondaryTextureMaker;
import tud.tangram.svgplot.plotting.texture.TertiaryTextureMaker;
import tud.tangram.svgplot.plotting.texture.TextureMaker;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class BarPlot {

	static final Logger log = LoggerFactory.getLogger(BarPlot.class);
	
	private List<String> textureIds;
	private OutputDevice device;
	private SvgDocument doc;

	public BarPlot(SvgDocument doc, OutputDevice device) {
		textureIds = new ArrayList<>();
		this.device = device;
		this.doc = doc;
	}

	public String getTextureId(int dataSetId) {
		if (textureIds.size() > dataSetId)
			return textureIds.get(dataSetId);

		TextureMaker textureMaker;

		// TODO extend by other texture makers
		// In any case at least one texture has to present here
		switch (dataSetId) {
		case 0:
			textureMaker = new PrimaryTextureMaker();
			break;
		case 1:
			textureMaker = new SecondaryTextureMaker();
			break;
		case 2:
			textureMaker = new TertiaryTextureMaker();
			break;
		default:
			return textureIds.get(textureIds.size() - 1);
		}

		String textureId = textureMaker.addTexture(doc, device);
		textureIds.add(textureId);

		return textureId;
	}

	/**
	 * Get a single bar as a group. All values are real values.
	 * 
	 * @param parentGroup
	 * @param position
	 * @param width
	 * @param height
	 * @param dataSetId
	 * @return
	 */
	public Element getSingleBar(Element parentGroup, Point position, double width, double height, int dataSetId) {
		Element group = doc.createGroup();
		String baseClass = "bar-" + dataSetId;
		group.setAttribute("class", baseClass + " bar");

		Element border = doc.createRectangle(position, width, height);
		border.setAttribute("class", baseClass + "-border  bar-border");
		group.appendChild(border);
		
		if (height - 2 * Constants.TEXTURE_BORDER_DISTANCE >= Constants.TEXTURE_MIN_HEIGHT) {
			Point fillingPosition = new Point(position.getX() + Constants.TEXTURE_BORDER_DISTANCE,
					position.getY() + Constants.TEXTURE_BORDER_DISTANCE);
			Element filling = doc.createRectangle(fillingPosition, width - 2 * Constants.TEXTURE_BORDER_DISTANCE,
					height - 2 * Constants.TEXTURE_BORDER_DISTANCE);
			filling.setAttribute("class", baseClass + "-filling bar-filling");
			filling.setAttribute("fill", "url(#" + getTextureId(dataSetId) + ")");
			group.appendChild(filling);
		}

		return group;
	}
}
