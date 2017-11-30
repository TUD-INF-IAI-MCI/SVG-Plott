package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendTitleItem extends LegendItem {

	private String legendTitle;

	/**
	 * Construct the LegendTitleItem and set its priority to the minimal
	 * {@link Integer} value, so that it always appears on top.
	 * 
	 * @param legendTitle
	 *            the title text for the legend
	 */
	public LegendTitleItem(String legendTitle) {
		super(Integer.MIN_VALUE);
		this.legendTitle = legendTitle;
	}

	/**
	 * Renders the title as well as the background element.
	 */
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		legend.paintBackground();
		Point legendTitleLowerEnd = legend.createTitleText(legendTitle, startingPosition);
		return legendTitleLowerEnd.getY() + 5;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.TITLE;
	}

	/**
	 * No group id is needed for the title.
	 */
	@Override
	public String getGroupId() {
		return null;
	}

}
