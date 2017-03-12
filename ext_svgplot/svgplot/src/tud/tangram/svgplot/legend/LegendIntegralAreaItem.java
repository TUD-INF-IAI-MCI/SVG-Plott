package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.plotting.IntegralPlot;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendIntegralAreaItem extends LegendItem {

	private static final int areaSymbolVerticalTextAlignment = 12;
	
	private final IntegralPlotSettings integral;
	private final CoordinateSystem cs;
	
	public LegendIntegralAreaItem(IntegralPlotSettings integral, CoordinateSystem cs) {
		this(integral, cs,  0);
	}
	
	public LegendIntegralAreaItem(IntegralPlotSettings integral, CoordinateSystem cs, int priority) {
		super(priority);
		this.integral = integral;
		this.cs = cs;
	}
	
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = startingPosition.clone();
		
		Node integralGroup = getOrCreateGroup(legend, viewbox, "0");

		// append devs
		if (legend.defs != null) {
			legend.defs.appendChild(IntegralPlot.getFillPattern(legend));
		}

		Node iBox = integralGroup.appendChild(legend.createRectangle(currentPosition, 30, 15));
		((Element) iBox).setAttribute("class", "integral-1 box");

		String text = "";
		if (integral.function2 >= 0)
			text = SvgTools.translate("legend.integral_1",
					Math.max(cs.xAxis.range.from, integral.xRange.from),
					Math.min(cs.xAxis.range.to, integral.xRange.to),
					SvgTools.getFunctionName(integral.function1), SvgTools.getFunctionName(integral.function2));
		else
			text = SvgTools.translate("legend.integral_0",
					Math.max(cs.xAxis.range.from, integral.xRange.from),
					Math.min(cs.xAxis.range.to, integral.xRange.to),
					SvgTools.getFunctionName(integral.function1));

		currentPosition.translate(35, areaSymbolVerticalTextAlignment);
		legend.appendChild(legend.createText(currentPosition, text));

		return currentPosition.y + 5;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.AREA;
	}

	@Override
	public String getGroupId() {
		return "integral";
	}

}
