package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.svgcreator.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendPointItem extends LegendItem {

	private static final int pointSymbolVerticalTextAlignment = 7;
	
	private Integer pointSymbolIndex;
	private PointList pointList;
	
	public LegendPointItem(PointList pointList, int pointSymbolIndex) {
		this(pointList, pointSymbolIndex, 0);
	}
	
	public LegendPointItem(PointList pointList, int pointSymbolIndex, int priority) {
		super(priority);
		this.pointSymbolIndex = pointSymbolIndex;
		this.pointList = pointList;
	}
	
	/**
	 * Renders a {@link LegendPointItem} explaining a {@link PointList} symbol.
	 */
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = startingPosition.clone();
		
		Element group = getOrCreateGroup(legend, viewbox, pointSymbolIndex.toString());
		
		currentPosition.translate(5, 3);
		
		Element symbol = PointPlot.getPointSymbolForIndex(pointSymbolIndex, legend);
		PointPlot.paintPoint(legend, currentPosition, symbol, group != null ? group : viewbox);
		
		currentPosition.translate(-5, -3);

		String text = pointList.name.isEmpty() ? SvgTools.translateN("legend.poi", SvgTools.getPointName(pointSymbolIndex), pointList.size())
				: pointList.name;
		currentPosition.translate(20, pointSymbolVerticalTextAlignment);
		legend.appendChild(legend.createText(currentPosition, text));
		
		return currentPosition.y;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.POINT;
	}

	@Override
	public String getGroupId() {
		return "points";
	}

}
