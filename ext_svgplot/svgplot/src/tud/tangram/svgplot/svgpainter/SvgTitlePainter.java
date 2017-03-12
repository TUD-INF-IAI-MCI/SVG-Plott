package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

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
	private int[] diagramContentMargin;
	private Point diagramTitleLowerEnd;

	@Override
	protected String getPainterName() {
		return "Title Painter";
	}

	/**
	 * 
	 * @param title
	 *            | the title of the graphics document
	 * @param legendTitle
	 *            | the title of the legend document
	 */
	public SvgTitlePainter(String title, String legendTitle) {
		this.title = title;
		this.legendTitle = legendTitle;
		this.diagramContentMargin = Constants.margin.clone();
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();

		StringBuilder defaultOptions = new StringBuilder();

		// Set the background style
		defaultOptions.append("svg { fill: none; stroke: #000000; stroke-width: " + Constants.strokeWidth + "; }")
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

		// Set the new margins according to the title
		diagramContentMargin[0] = (int) diagramTitleLowerEnd.y + 17;
		diagramContentMargin[1] += 20;
		diagramContentMargin[3] += 10;
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
	public int[] getDiagramContentMargin() {
		return diagramContentMargin;
	}
}
