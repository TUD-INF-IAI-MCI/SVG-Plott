package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendPointItem extends LegendItem {

	private static final int pointSymbolVerticalTextAlignment = 7;
	
	private Integer pointSymbolIndex;
	private PointList pointList;
	private PointPlotStyle style;
	
	public LegendPointItem(PointList pointList, PointPlotStyle style, int pointSymbolIndex) {
		this(pointList, style, pointSymbolIndex, 0);
	}
	
	public LegendPointItem(PointList pointList, PointPlotStyle style, int pointSymbolIndex, int priority) {
		super(priority);
		this.pointSymbolIndex = pointSymbolIndex;
		this.pointList = pointList;
		this.style = style;
	}
	
	/**
	 * Renders a {@link LegendPointItem} explaining a {@link PointList} symbol.
	 */
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = new Point(startingPosition);
		
		Element group = getOrCreateGroup(legend, viewbox, pointSymbolIndex.toString());
		
		currentPosition.translate(5, 3);
		
		PointPlot pointPlot = new PointPlot(legend, style);
		pointPlot.paintPoint(group != null ? group : viewbox, currentPosition, pointSymbolIndex);
		
		currentPosition.translate(-5, -3);

		String text = pointList.getName().isEmpty() ? SvgTools.translateN("legend.poi", SvgTools.getPointName(pointSymbolIndex), pointList.size())
				: pointList.getName();
		currentPosition.translate(20, pointSymbolVerticalTextAlignment);
		legend.appendChild(legend.createText(currentPosition, text));
		
		return currentPosition.getY();
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
