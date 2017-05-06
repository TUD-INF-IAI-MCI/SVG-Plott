package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.BarPlot;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgBarPainter extends SvgPainter {

	private final CategorialPointListList barPointListList;
	private final CoordinateSystem cs;
	private final BarAccumulationStyle barAccumulationStyle;
	
	public SvgBarPainter(CoordinateSystem cs, BarAccumulationStyle barAccumulationStyle, CategorialPointListList barPointListList) {
		this.barPointListList = barPointListList;
		this.cs = cs;
		this.barAccumulationStyle = barAccumulationStyle;
	}

	@Override
	protected String getPainterName() {
		return "Bar Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		StringBuilder defaultCss = new StringBuilder();
		
		defaultCss.append(".bar-filling { stroke: none }");
		
		deviceCss.put(OutputDevice.Default, defaultCss.toString());
		
		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		
		BarPlot barPlot = new BarPlot(doc, device);
		
		List<Double> upperPositions = new ArrayList<>(Collections.nCopies(barPointListList.getCategoryCount(), 0.));
		
		double availableSpace = cs.convertXDistance(cs.xAxis.getTicInterval());
		
		double convertedWidth;
		if(barAccumulationStyle == BarAccumulationStyle.GROUPED)
			convertedWidth = (availableSpace - 2 * Constants.HALF_BAR_DISTANCE) / barPointListList.size();
		else
			convertedWidth = availableSpace - 2 * Constants.HALF_BAR_DISTANCE;
		
		int dataSetNumber = 0;
		for(PointList pointList : barPointListList) {
			Iterator<String> categoryNames = barPointListList.getCategoryNames().iterator();
			
			int categoryNumber = 0;
			
			Element group = doc.getOrCreateChildGroupById(viewbox, "barchart-" + dataSetNumber);
			
			for(Point point : pointList) {
				String categoryName = "";
				if(categoryNames.hasNext())
					categoryName = categoryNames.next();
				
				double height = point.getY();
				double convertedHeight = cs.convertYDistance(height);
				
				double dx;
				double dy;
				
				if(barAccumulationStyle == BarAccumulationStyle.GROUPED) {
					dx = Constants.HALF_BAR_DISTANCE + dataSetNumber * convertedWidth;
					dy = 0;
				}
				else {
					dx = Constants.HALF_BAR_DISTANCE;
					dy = -upperPositions.get(categoryNumber);
				}
				
				upperPositions.set(categoryNumber, dy + convertedHeight);
				
				Point convertedPosition = cs.convert(point, dx, dy);
				
				Element bar = barPlot.getSingleBar(group, convertedPosition, convertedWidth, convertedHeight, dataSetNumber);

				group.appendChild(bar);
				
				categoryNumber++;
			}			
			dataSetNumber++;
		}
	}

}
