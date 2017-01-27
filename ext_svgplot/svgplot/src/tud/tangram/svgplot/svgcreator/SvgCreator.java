/**
 * 
 */
package tud.tangram.svgplot.svgcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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
	protected SvgOptions options;
	final public static ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
	
	protected Point diagramTitleLowerEnd;
	protected Point legendTitleLowerEnd;

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
	 * @return
	 */
	public SvgDocument getLegend() {
		return legend;
	}
	
	/** description of the diagram in html format */
	protected HtmlDocument desc;

	/**
	 * description of the diagram in html format
	 * @return
	 */
	public HtmlDocument getDesc() {
		return desc;
	}

	/**
	 * Set the description of the diagram
	 * @param desc
	 */
	public void setDesc(HtmlDocument desc) {
		this.desc = desc;
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
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws DOMException
	 * @throws InterruptedException
	 */
	public abstract SvgDocument create() throws ParserConfigurationException, IOException, DOMException, InterruptedException;

	public static String format2svg(double value) {
		return Constants.decimalFormat.format(value);
	}
	
	/**
	 * Create the titles for the legend and diagram svg files.
	 * @throws ParserConfigurationException
	 */
	public void createTitles() throws ParserConfigurationException {
		String legendTitle = translate("legend") + ": " + options.title;

		doc = new SvgDocument(options.title, options.size, Constants.margin[1]);
		legend = new SvgDocument(legendTitle, options.size, Constants.margin[1]);
		legend.setAttribute("id", "legend");
		desc = new HtmlDocument(translate("desc") + ": " + options.title);
		
		// Create the titles and update the top positions
		diagramTitleLowerEnd = createTitle(doc, options.title);
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
	
	/**
	 * Formats a additional Name of an object. Checks if the name is set. If
	 * name is set the name is packed into brackets and prepend with an
	 * whitespace
	 * 
	 * @param name
	 *            | optional name of an object or NULL
	 * @return empty string or the name of the object packed into brackets and
	 *         prepend with a whitespace e.g. ' (optional name)'
	 */
	public static String formatName(String name) {
		return (name == null || name.isEmpty()) ? "" : " (" + name + ")";
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value
	 * @return a localized string for the given PropertyResourceBundle key,
	 *         filled with the set arguments
	 */
	public static String translate(String key, Object... arguments) {
		return MessageFormat.format(bundle.getString(key), arguments);
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file. This function is optimized for differing
	 * sentences depending on the amount of results.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value. The last argument gives the
	 *            count and decide which value will be returned.
	 * @return a localized string for the given amount depending
	 *         PropertyResourceBundle key, filled with the set arguments
	 */
	public static String translateN(String key, Object... arguments) {
		int last = (int) arguments[arguments.length - 1];
		String suffix = last == 0 ? "_0" : last == 1 ? "_1" : "_n";
		return translate(key + suffix, arguments);
	}
}
