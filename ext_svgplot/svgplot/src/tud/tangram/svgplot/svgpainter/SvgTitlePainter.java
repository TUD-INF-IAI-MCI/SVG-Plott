package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.OutputDevice;
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
	private Point legendTitleLowerEnd;

	
	
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

		deviceCss.put(OutputDevice.Default, defaultOptions.toString());
		
		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);

		paintBackground(doc);
		diagramTitleLowerEnd = createTitle(doc, title);

		// Set the new margins according to the title
		diagramContentMargin[0] = (int) diagramTitleLowerEnd.y + 17;
		diagramContentMargin[1] += 20;
		diagramContentMargin[3] += 10;
	}

	@Override
	public void paintToSvgLegend(SvgDocument legend, OutputDevice device) {
		super.paintToSvgLegend(legend, device);

		paintBackground(legend);
		legendTitleLowerEnd = createTitle(legend, legendTitle);
	}

	/**
	 * Adds the textual readable title to the svg document at a predefined
	 * position in the left top corner of the sheet.
	 * 
	 * @param doc
	 *            | the svg document where the header text should be insert
	 * @param text
	 *            | the textual value
	 * @return the position of the added title node
	 */
	private Point createTitle(SvgDocument doc, String text) {
		Point pos = new Point(Constants.margin[3], Constants.margin[0] + 10);
		Element title = (Element) doc.appendChild(doc.createText(pos, text));
		title.setAttribute("id", "title");
		return pos;
	}

	/**
	 * Creates the background SVG object and adds it to the document.
	 */
	private void paintBackground(SvgDocument doc) {
		Element bg = doc.createRectangle(new Point(0, 0), "100%", "100%");
		bg.setAttribute("id", "background");
		doc.appendChild(bg);
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
	 * Get the lower end of the legend title. Call after
	 * {@link #paintToSvgLegend(SvgDocument, OutputDevice) paintToSvgLegend}.
	 * 
	 * @return
	 */
	public Point getLegendTitleLowerEnd() {
		return legendTitleLowerEnd;
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
