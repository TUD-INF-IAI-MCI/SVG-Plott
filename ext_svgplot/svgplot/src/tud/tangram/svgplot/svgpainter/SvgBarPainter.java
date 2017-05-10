package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.BarPlot;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgBarPainter extends SvgPainter {

	static final Logger log = LoggerFactory.getLogger(SvgPainter.class);
	
	private final CategorialPointListList barPointListList;
	private final CoordinateSystem cs;
	private final BarAccumulationStyle barAccumulationStyle;
	private final List<Color> colors;
	
	public SvgBarPainter(CoordinateSystem cs, BarAccumulationStyle barAccumulationStyle, CategorialPointListList barPointListList, LinkedHashSet<Color> colors) {
		this.barPointListList = barPointListList;
		this.cs = cs;
		this.barAccumulationStyle = barAccumulationStyle;
		this.colors = new ArrayList<>(colors);
	}

	@Override
	protected String getPainterName() {
		return "Bar Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		StringBuilder defaultCss = new StringBuilder();
		
		defaultCss.append(".bar-filling { stroke: none }").append(System.lineSeparator());
		defaultCss.append(".bar-border { fill: white }");
		
		deviceCss.put(OutputDevice.Default, defaultCss.toString());
		
		StringBuilder screenColorCss = new StringBuilder();
		
		screenColorCss.append(".bar-filling { display: none }").append(System.lineSeparator());
		screenColorCss.append(".bar-border { fill: white }");
		
		for(int i = 0; i < barPointListList.size(); i++) {
			screenColorCss.append(System.lineSeparator());
			
			Color color = colors.get(i);
			screenColorCss.append(".bar-" + i + "-border { fill: " + color.getRgbColor() + "}");
		}
		
		deviceCss.put(OutputDevice.ScreenColor, screenColorCss.toString());
		
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
		
		if(convertedWidth < Constants.TEXTURE_MIN_WIDTH) {
			log.warn("Die Balken sind zu schmal, um ihre Textur gut erkennen zu können. Die Textur wird dennoch dargestellt.");
		}
		
		int dataSetNumber = 0;
		for(PointList pointList : barPointListList) {
			Iterator<String> categoryNames = barPointListList.getCategoryNames().iterator();
			
			int categoryNumber = 0;
			
			Element group = doc.getOrCreateChildGroupById(viewbox, "barchart-" + dataSetNumber);
			
			for(Point point : pointList) {
				// TODO Audio label
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
					dy = upperPositions.get(categoryNumber);
				}
				
				upperPositions.set(categoryNumber, dy + convertedHeight);
				
				Point convertedPosition = cs.convert(point, dx, -dy);
				
				if (convertedHeight - 2 * Constants.TEXTURE_BORDER_DISTANCE < Constants.TEXTURE_MIN_HEIGHT) {
					log.warn("Der Balken {} ist zu niedrig und wird daher ohne Textur dargestellt.", cs.formatForSpeech(point));
				}
				
				Element bar = barPlot.getSingleBar(group, convertedPosition, convertedWidth, convertedHeight, dataSetNumber);
				Element title = doc.createElement("title");
				title.setTextContent(pointList.getName() + ": " + cs.formatForSpeech(point));
				
				bar.appendChild(title);
				
				group.appendChild(bar);
				
				categoryNumber++;
			}			
			dataSetNumber++;
		}
	}

}
