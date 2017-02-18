/**
 * 
 */
package tud.tangram.svgplot.svgcreator;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import tud.tangram.svgplot.Constants;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.options.SvgOptions;
import tud.tangram.svgplot.svgpainter.SvgTitlePainter;
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

	public SvgCreator(SvgOptions options) {
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

		doc = new SvgDocument(options.title, options.size, Constants.margin[1]);
		legend = new SvgDocument(options.legendTitle, options.size, Constants.margin[1]);
		legend.setAttribute("id", "legend");
		desc = new HtmlDocument(options.descTitle);

		beforeCreate();
		create();
		afterCreate();

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
	 * Sets up the diagram SVG title and legend SVG title. Computes margins for
	 * the rest of the document. Should be used for further setup tasks before
	 * anything is printed.
	 */
	protected void beforeCreate() {
		SvgTitlePainter svgTitlePainter = new SvgTitlePainter(options.title, options.legendTitle);

		svgTitlePainter.paintToSvgDocument(doc, null, options.outputDevice);
		svgTitlePainter.paintToSvgLegend(legend, options.outputDevice);

		diagramContentMargin = svgTitlePainter.getDiagramContentMargin();
		diagramTitleLowerEnd = svgTitlePainter.getDiagramTitleLowerEnd();
		legendTitleLowerEnd = svgTitlePainter.getLegendTitleLowerEnd();
	}

	/**
	 * Inputs all the main content into the diagram and legend SVG files.
	 */
	protected void create() {

	}

	/**
	 * Gives final touches to the diagram and legend SVG files. Can be used for
	 * adding accumulated information like overlays.
	 */
	protected void afterCreate() {

	}

	// TODO readd option to append external css
	//protected void appendOptionsCss(SvgDocument doc, String css) throws IOException {
	//	if (options.css != null) {
	//		css += "\n\n/* custom */\n";
	//		if (new File(options.css).isFile()) {
	//			options.css = new String(Files.readAllBytes(Paths.get(options.css)));
	//		}
	//		css += options.css;
	//	}
	//	doc.appendCss(css);
	//}
	
}
