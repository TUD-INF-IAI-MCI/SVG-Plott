/**
 * 
 */
package tud.tangram.svgplot.svgcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.SvgOptions;
import tud.tangram.svgplot.xml.HtmlDocument;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * @author mic
 *
 */
public abstract class SvgCreator {
	protected final SvgOptions options;

	protected Point diagramTitleLowerEnd;
	protected Point legendTitleLowerEnd;

	/** Margin for the content, which is below the title */
	protected int[] diagramContentMargin;

	/** Final diagram svg */
	protected SvgDocument doc;

	/**
	 * Final diagram svg
	 * 
	 * @return
	 */
	public SvgDocument getDoc() {
		return doc;
	}

	/** key to the diagram */
	protected SvgDocument legend;

	/**
	 * key to the diagram
	 * 
	 * @return
	 */
	public SvgDocument getLegend() {
		return legend;
	}

	/** description of the diagram in html format */
	protected HtmlDocument desc;

	/**
	 * description of the diagram in html format
	 * 
	 * @return
	 */
	public HtmlDocument getDesc() {
		return desc;
	}

	/**
	 * Set the description of the diagram
	 * 
	 * @param desc
	 */
	public void setDesc(HtmlDocument desc) {
		this.desc = desc;
	}

	public SvgCreator(SvgOptions options){
		this.options = options;
	}
	
	/**
	 * Main function. Combine all the elements and create all the output files.
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws TransformerException
	 */
	public void run() throws ParserConfigurationException, IOException, InterruptedException, TransformerException {
		if (options.title == null && options.output != null) {
			options.title = options.output.getName();
		}

		createTitles();
		createBackground();
		createCss(doc);
		createCss(legend);
		create();
		createLegend();

		if (options.output != null) {
			doc.writeTo(new FileOutputStream(options.output));
			// TODO fix path bug
			String parent = options.output.getParent() == null ? "" : options.output.getParent() + "\\";
			String legendFile = parent + options.output.getName().replaceFirst("(\\.[^.]*)?$", "_legend$0");
			legend.writeTo(new FileOutputStream(legendFile));
			String descFile = parent + options.output.getName().replaceFirst("(\\.[^.]*)?$", "_desc.html");
			desc.writeTo(new FileOutputStream(descFile));
		} else {
			doc.writeTo(System.out);
		}
	}

	/**
	 * Creates the whole diagram SVG file, legend SVG file and description HTML
	 * file.
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws DOMException
	 * @throws InterruptedException
	 */
	public abstract SvgDocument create()
			throws ParserConfigurationException, IOException, DOMException, InterruptedException;

	/**
	 * Create the titles for the legend and diagram svg files.
	 * 
	 * @throws ParserConfigurationException
	 */
	public void createTitles() throws ParserConfigurationException {
		String legendTitle = SvgTools.translate("legend") + ": " + options.title;

		doc = new SvgDocument(options.title, options.size, Constants.margin[1]);
		legend = new SvgDocument(legendTitle, options.size, Constants.margin[1]);
		legend.setAttribute("id", "legend");
		desc = new HtmlDocument(SvgTools.translate("desc") + ": " + options.title);

		// Create the titles and update the top positions
		diagramTitleLowerEnd = createTitle(doc, options.title);
		legendTitleLowerEnd = createTitle(legend, legendTitle);

		// Set the new margins according to the titles
		diagramContentMargin = Constants.margin.clone();
		diagramContentMargin[0] = (int) diagramTitleLowerEnd.y + 17;
		diagramContentMargin[1] += 20;
		diagramContentMargin[3] += 10;
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
	 * Creates the background SVG object and  adds it to the document.
	 */
	public void createBackground() {
		Element bg = doc.createRectangle(new Point(0, 0), "100%", "100%");
		bg.setAttribute("id", "background");
		doc.appendChild(bg);
		Element bgL = legend.createRectangle(new Point(0, 0), "100%", "100%");
		bgL.setAttribute("id", "background");
		legend.appendChild(bgL);
	}

	/**
	 * Generates the basic css optimized for tactile output on a tiger embosser.
	 * Should call appendOptionsCss.
	 * 
	 * @param doc
	 *            | the svg document in with this css should been added
	 * @throws IOException
	 *             if the set up css file is not readable or does not exist
	 *             anymore.
	 */
	protected abstract void createCss(SvgDocument doc) throws IOException;

	protected void appendOptionsCss(String css) throws IOException {
		if (options.css != null) {
			css += "\n\n/* custom */\n";
			if (new File(options.css).isFile()) {
				options.css = new String(Files.readAllBytes(Paths.get(options.css)));
			}
			css += options.css;
		}
		doc.appendCss(css);
	}

	protected abstract void createLegend();
}
