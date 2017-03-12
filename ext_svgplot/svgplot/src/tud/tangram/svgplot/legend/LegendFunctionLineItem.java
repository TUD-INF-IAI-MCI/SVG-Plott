package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendFunctionLineItem extends LegendItem {

	private static final int lineSymbolVerticalTextAlignment = 7;
	
	private Integer functionSymbolIndex;
	private Function function;
	
	public LegendFunctionLineItem(Function function, int functionSymbolIndex) {
		this(function, functionSymbolIndex, 0);
	}
	
	public LegendFunctionLineItem(Function function, int functionSymbolIndex, int priority) {
		super(priority);
		this.functionSymbolIndex = functionSymbolIndex;
		this.function = function;
	}
	
	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = startingPosition.clone();
		
		Node plot = getOrCreateGroup(legend, viewbox, new Integer(functionSymbolIndex + 1).toString());
		plot.appendChild(legend.createLine(new Point(currentPosition.x, currentPosition.y + 2),
				new Point(currentPosition.x + 26, currentPosition.y + 2)));

		currentPosition.translate(35, lineSymbolVerticalTextAlignment);
		
		if (function.hasTitle()) {
			legend.appendChild(legend.createText(currentPosition,
					SvgTools.getFunctionName(functionSymbolIndex) + "(x) = " + function.getTitle() + ":", function.getFunction()));
		} else {
			legend.appendChild(
					legend.createText(currentPosition, SvgTools.getFunctionName(functionSymbolIndex) + "(x) = " + function.getFunction()));
		}
		
		currentPosition.translate(-35, 0);
		
		return currentPosition.y;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.LINE;
	}

	@Override
	public String getGroupId() {
		return "plots";
	}

}
