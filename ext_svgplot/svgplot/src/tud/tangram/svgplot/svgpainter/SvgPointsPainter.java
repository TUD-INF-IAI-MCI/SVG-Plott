package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.coordinatesystem.PointListList.PointList;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Overlay;
import tud.tangram.svgplot.plotting.OverlayList;
import tud.tangram.svgplot.plotting.PointPlot;
import tud.tangram.svgplot.svgcreator.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * Paint points to the SVG document and document them in the legend (TODO).
 * TODO add more styles
 */
public class SvgPointsPainter extends SvgPainter {

	CoordinateSystem cs;
	PointListList points;
	OverlayList overlays;

	public SvgPointsPainter(CoordinateSystem cs, PointListList points) {
		this.cs = cs;
		this.points = points;
	}

	@Override
	protected String getPainterName() {
		return "Points Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		StringBuilder defaultOptions = new StringBuilder();

		defaultOptions.append(".poi_symbol { stroke: black; stroke-dasharray: none; stroke-width:"
				+ (Constants.strokeWidth * 1.5) + ";  fill: black; }").append(System.lineSeparator());
		defaultOptions
				.append("." + Constants.spacerCssClass + " { stroke: white; stroke-dasharray: none; stroke-width:"
						+ (Constants.strokeWidth * 6) + ";  fill: transparent; stroke-linecap: round; }")
				.append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
		
		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		
		overlays = new OverlayList(cs);
		
		// TODO: add scatter plot
		// points or scatter plots
		if (points != null && points.size() > 0) {
			int j = 0;
			Element poiGroup = doc.createElement("g", "points");
			viewbox.appendChild(poiGroup);
			for (PointList pl : points) {
				if (pl != null && pl.size() > 0) {

					Element plGroup = doc.createElement("g", "points_" + j);
					poiGroup.appendChild(plGroup);

					for (Point p : pl) {
						Element symbol = PointPlot.getPointSymbolForIndex(j, doc);
						Element ps = PointPlot.paintPoint(doc, cs.convert(p), symbol,
								plGroup != null ? plGroup : viewbox);
						ps.appendChild(doc.createTitle(SvgTools.formatForSpeech(cs, p)));
						if (pl.name != null && !pl.name.isEmpty())
							ps.appendChild(doc.createDesc(pl.name)); // TODO:
																		// maybe
																		// fine
																		// this

						overlays.add(new Overlay(p), true);
					}
				}
				j++;
			}
		}
	}

	/**
	 * Adds the scatter plot overlays to the specified list.
	 * 
	 * @param overlays
	 *            list where the overlays shall be added to
	 */
	public void addOverlaysToList(OverlayList overlays) {
		overlays.addAll(this.overlays, true);
	}

	@Override
	public void paintToSvgLegend(SvgDocument legend, OutputDevice device) {
		// TODO Auto-generated method stub
		super.paintToSvgLegend(legend, device);
	}

}
