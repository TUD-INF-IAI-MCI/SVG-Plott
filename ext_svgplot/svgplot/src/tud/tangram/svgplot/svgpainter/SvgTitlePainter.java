package tud.tangram.svgplot.svgpainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.legend.LegendTitleItem;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * This class is used for the very basic setup of the SVG files. It adds a
 * background, the titles and computes the available space for the rest of the
 * files.
 */
public class SvgTitlePainter extends SvgPainter {

	private String title;
	private String legendTitle;
	private List<Integer> diagramContentMargin;
	private Point diagramTitleLowerEnd;
	private String xAxisLabel;
	private String yAxislabel;
	private Point size;

	/**
	 * 
	 * @param title
	 *            | the title of the graphics document
	 * @param legendTitle
	 *            | the title of the legend document
	 * @param xAxisLabel
	 *            | the label for a potential x axis
	 * @param yAxislabel
	 *            |the label for a potential y axis
	 */
	public SvgTitlePainter(Point size, String title, String legendTitle, String xAxisLabel, String yAxisLabel) {
		this.title = title;
		this.legendTitle = legendTitle;
		this.diagramContentMargin = new ArrayList<>(Constants.MARGIN);
		this.xAxisLabel = xAxisLabel;
		this.yAxislabel = yAxisLabel;
		this.size = size;
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder defaultOptions = new StringBuilder();

		// Set the background style
		defaultOptions.append("svg { fill: none; stroke: #000000; stroke-width: " + Constants.STROKE_WIDTH + "; }")
				.append(System.lineSeparator());

		// Set the text style
		defaultOptions.append("text { font-family: serif; font-size: 36pt; fill: black; stroke: none; }")
				.append(System.lineSeparator());

		// Set the text style for printing (tiger braille font)
		defaultOptions.append("@media print{").append(System.lineSeparator());
		defaultOptions
				.append("\ttext { font-family: 'Braille DE Computer'; font-size: 36pt; fill: black; stroke: none; }")
				.append(System.lineSeparator());
		defaultOptions.append("}").append(System.lineSeparator());

		StringBuilder screenHighContrastCss = new StringBuilder();
		screenHighContrastCss.append("#background { fill: black; }").append(System.lineSeparator());
		screenHighContrastCss.append("svg { fill: none; stroke: #000000; stroke-width: 0.5; }")
				.append(System.lineSeparator());
		screenHighContrastCss.append("text { font-family: sans-serif; font-size: 36pt; fill: white; stroke: none; }")
				.append(System.lineSeparator());
		screenHighContrastCss.append("@media print{").append(System.lineSeparator());
		screenHighContrastCss
				.append("\ttext { font-family: 'sans-serif'; font-size: 36pt; fill: white; stroke: none; }")
				.append(System.lineSeparator());
		screenHighContrastCss.append("}").append(System.lineSeparator());

		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastCss.toString());

		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		doc.paintBackground();

		diagramTitleLowerEnd = doc.createTitleText(title, Constants.titlePosition);
		diagramTitleLowerEnd.translate(7, 18);

		// Create the yAxislabel further moving the lower end
		if (yAxislabel != null && yAxislabel.length() > 0)
			doc.appendChild(doc.createText(diagramTitleLowerEnd, yAxislabel));
		
		diagramTitleLowerEnd.translate(-7, -10);

		// Create the xAxisLabel
		int lowerEndShift = 0;
		if (xAxisLabel != null && xAxisLabel.length() > 0) {
			Point xAxisTitlePosition = new Point(diagramTitleLowerEnd);
			xAxisTitlePosition.translate(25, 0);
			xAxisTitlePosition.setY(size.getY() - diagramContentMargin.get(3));

			doc.appendChild(doc.createText(xAxisTitlePosition, xAxisLabel));

			// TODO calculate multi line title size, place it appropriately and
			// calculate the right shift
			lowerEndShift = 18;
		}

		// Set the new margins according to the title
		diagramContentMargin.set(0, (int) diagramTitleLowerEnd.getY() + 9);
		diagramContentMargin.set(1, diagramContentMargin.get(1) + 20);
		diagramContentMargin.set(2, diagramContentMargin.get(2) + lowerEndShift);
		diagramContentMargin.set(3, diagramContentMargin.get(3) + 10);
	}

	@Override
	public void prepareLegendRenderer(LegendRenderer renderer, OutputDevice device, int priority) {
		super.prepareLegendRenderer(renderer, device, priority);
		renderer.offer(new LegendTitleItem(legendTitle));
	}

	/**
	 * Get the lower end of the diagram title. Call after
	 * {@link #paintToSvgDocument(SvgDocument, Element, OutputDevice)
	 * paintToSvgDocument}.
	 * 
	 * @return
	 */
	public Point getDiagramTitleLowerEnd() {
		return diagramTitleLowerEnd;
	}

	/**
	 * Get the margins for the content after title placement. Call after
	 * {@link #paintToSvgDocument(SvgDocument, Element, OutputDevice)
	 * paintToSvgDocument}.
	 * 
	 * @return
	 */
	public List<Integer> getDiagramContentMargin() {
		return diagramContentMargin;
	}

	@Override
	protected String getPainterName() {
		return "Title Painter";
	}
}
