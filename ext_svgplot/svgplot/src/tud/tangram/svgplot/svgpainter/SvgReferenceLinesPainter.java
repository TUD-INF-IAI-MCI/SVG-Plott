package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.ReferenceLine;
import tud.tangram.svgplot.plotting.ReferenceLine.Direction;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgReferenceLinesPainter extends SvgPainter {

	private ArrayList<ReferenceLine> lines;
	private CoordinateSystem cs;

	public SvgReferenceLinesPainter(CoordinateSystem cs, ArrayList<ReferenceLine> lines) {
		this.lines = lines;
		this.cs = cs;
	}

	@Override
	protected String getPainterName() {
		return "Reference Lines Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder css = new StringBuilder();
		css.append("#reference-lines { stroke-width: " + Constants.strokeWidth + "; }").append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, css.toString());

		StringBuilder screenHighContrastCss = new StringBuilder();
		screenHighContrastCss.append("#reference-lines { stroke: white; fill: transparent; }")
				.append(System.lineSeparator());
		
		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastCss.toString());

		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		paintLines(doc, viewbox);
	}

	private void paintLines(SvgDocument doc, Element viewbox) {
		Element xGroup, yGroup;
		Element linesNode = doc.getOrCreateChildGroupById(viewbox, "reference-lines");

		if (linesNode == null) {
			System.err.println("There exists already a non-group element with the id \"reference-lines\"");
		}

		for (ReferenceLine line : lines) {
			if (line.direction == Direction.X_LINE) {
				xGroup = doc.getOrCreateChildGroupById(linesNode, "x-reference-lines");

				if (xGroup == null) {
					System.err.println("There exists already a non-group element with the id \"x-reference-lines\"");
				}

				Point from = cs.convert(line.position, cs.yAxis.range.to, 0, -Constants.strokeWidth / 2);
				Point to = cs.convert(line.position, cs.yAxis.range.from, 0, Constants.strokeWidth / 2);
				xGroup.appendChild(doc.createLine(from, to));
			} else if (line.direction == Direction.Y_LINE) {
				yGroup = doc.getOrCreateChildGroupById(linesNode, "y-reference-lines");

				if (yGroup == null) {
					System.err.println("There exists already a non-group element with the id \"y-reference-lines\"");
				}

				Point from = cs.convert(cs.xAxis.range.from, line.position, -Constants.strokeWidth / 2, 0);
				Point to = cs.convert(cs.xAxis.range.to, line.position, Constants.strokeWidth / 2, 0);
				yGroup.appendChild(doc.createLine(from, to));
			}
		}
	}
}
