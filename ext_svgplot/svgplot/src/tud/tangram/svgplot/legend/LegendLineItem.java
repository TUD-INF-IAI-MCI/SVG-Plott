package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendLineItem extends LegendItem {

	private static final int lineSymbolVerticalTextAlignment = 7;

	private Integer lineSymbolIndex;
	private String lineTitle;

	public LegendLineItem(String lineTitle, Integer lineSymbolIndex) {
		this(lineTitle, lineSymbolIndex, 0);
	}

	public LegendLineItem(String lineTitle, Integer lineSymbolIndex, int priority) {
		super(priority);
		this.lineTitle = lineTitle;
		this.lineSymbolIndex = lineSymbolIndex;
	}

	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = new Point(startingPosition);

		Element linecharts = getOrCreateGroup(legend, viewbox);

		Element line = legend.createLine(new Point(currentPosition.getX(), currentPosition.getY() + 2),
				new Point(currentPosition.getX() + 26, currentPosition.getY() + 2));
		line.setAttribute("id", "linechart-" + lineSymbolIndex);
		linecharts.appendChild(line);

		currentPosition.translate(35, lineSymbolVerticalTextAlignment);

		legend.appendChild(legend.createText(currentPosition, lineTitle));

		currentPosition.translate(-35, 0);

		return currentPosition.getY();
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.LINE;
	}

	@Override
	public String getGroupId() {
		return "linecharts";
	}

}
