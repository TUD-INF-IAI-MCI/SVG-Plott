package tud.tangram.svgplot.legend;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.BarPlot;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendBarAreaItem extends LegendItem {

	private static final int areaSymbolVerticalTextAlignment = 12;
	
	private final Integer barDataSetId;
	private final String title;
	private final OutputDevice device;
	
	public LegendBarAreaItem(OutputDevice device, int priority, Integer barDataSetId, String title) {
		super(priority);
		this.barDataSetId = barDataSetId;
		this.title = title;
		this.device = device;
	}

	public LegendBarAreaItem(OutputDevice device, Integer barDataSetId, String title) {
		this(device, 0, barDataSetId, title);
	}

	@Override
	public double render(SvgDocument legend, Element viewbox, Point startingPosition) {
		Point currentPosition = new Point(startingPosition);
		
		currentPosition.translate(0, Constants.TEXTURE_BORDER_DISTANCE);
		
		Element group = getOrCreateGroup(legend, viewbox, barDataSetId.toString());
		
		BarPlot barPlot = new BarPlot(legend, device);
		Element bar = barPlot.getSingleBar(currentPosition, 30, 15 + 2 * Constants.TEXTURE_BORDER_DISTANCE, barDataSetId);
		group.appendChild(bar);
		
		currentPosition.translate(35, areaSymbolVerticalTextAlignment);
		
		legend.appendChild(legend.createText(currentPosition, title));

		return currentPosition.getY() + 5;
	}

	@Override
	public LegendItemType getType() {
		return LegendItemType.AREA;
	}

	@Override
	public String getGroupId() {
		return "barchart";
	}

}
