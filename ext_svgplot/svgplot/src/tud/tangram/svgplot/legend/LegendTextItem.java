package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendTextItem extends LegendItem {

	private static final int textVerticalTextAlignment = 7;
	
	private final String line1;
	private final String[] lines;
	
	public LegendTextItem(String line1, String... lines) {
		this(0, line1, lines);
	}
	
	public LegendTextItem(int priority, String line1, String... lines) {
		super(priority);
		this.line1 = line1;
		this.lines = lines;
	}
	
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = startingPosition.clone();
		currentPosition.translate(0, textVerticalTextAlignment);
		legend.appendChild(legend.createText(currentPosition, line1, lines));
		return currentPosition.y;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.TEXT;
	}

	/**
	 * Text labels do not need to be in a group.
	 */
	@Override
	public String getGroupId() {
		return null;
	}

}
