/**
 * 
 */
package tud.tangram.svgplot.svgcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.description.Description;
import tud.tangram.svgplot.legend.LegendItem;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.options.SvgOptions;
import tud.tangram.svgplot.svgpainter.SvgTitlePainter;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * @author mic
 *
 */
public abstract class SvgCreator {

	protected final SvgOptions options;

	protected Point diagramTitleLowerEnd;

	/** Margin for the content, which is below the title */
	protected List<Integer> diagramContentMargin;

	/** Final diagram svg */
	protected SvgDocument doc;

	/** key to the diagram */
	protected SvgDocument legend;

	protected LegendRenderer legendRenderer;

	/** description of the diagram in html format */
	protected Description desc;

	private static final Logger log = LoggerFactory.getLogger(SvgCreator.class);

	public SvgCreator(SvgOptions options) {
		this.options = options;
	}

	/**
	 * Final diagram svg
	 * 
	 * @return
	 */
	public SvgDocument getDoc() {
		return doc;
	}

	/**
	 * key to the diagram
	 * 
	 * @return
	 */
	public SvgDocument getLegend() {
		return legend;
	}

	public LegendRenderer getLegendRenderer() {
		return legendRenderer;
	}

	/**
	 * Add an item to the legend.
	 * 
	 * @param item
	 */
	public void addLegendItem(LegendItem item) {
		legendRenderer.offer(item);
	}

	/**
	 * description of the diagram in html format
	 * 
	 * @return
	 */
	public Description getDesc() {
		return desc;
	}

	/**
	 * Set the description of the diagram
	 * 
	 * @param desc
	 */
	public void setDesc(Description desc) {
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

		doc = new SvgDocument(options.title, options.size, Constants.MARGIN.get(1));
		legend = new SvgDocument(options.legendTitle, options.size, Constants.MARGIN.get(1));
		legend.setAttribute("id", "legend");
		legendRenderer = new LegendRenderer();
		desc = new Description(options.descTitle);

		beforeCreate();
		create();
		afterCreate();

		// TODO do not write to any file here, support getting the results and
		// provide file writing in main

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
		SvgTitlePainter svgTitlePainter = new SvgTitlePainter(options.size, options.title, options.legendTitle,
				getxAxisTitle(), getyAxisTitle());

		svgTitlePainter.paintToSvgDocument(doc, null, options.outputDevice);
		svgTitlePainter.prepareLegendRenderer(legendRenderer, options.outputDevice);

		diagramContentMargin = svgTitlePainter.getDiagramContentMargin();
		diagramTitleLowerEnd = svgTitlePainter.getDiagramTitleLowerEnd();
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
		legendRenderer.render(legend, options.size, Constants.titlePosition);
		appendOptionsCss(doc, legend);
	}

	protected void appendOptionsCss(SvgDocument... documents) {
		String css = "";
		if (options.css != null) {
			css += "\n\n/* custom */\n";
			if (new File(options.css).isFile()) {
				try {
					options.css = new String(Files.readAllBytes(Paths.get(options.css)));
				} catch (IOException e) {
					log.warn("Die CSS-Datei {} kann nicht gelesen werden. Fahre ohne zusätzliche CSS-Inhalte fort.",
							options.css);
					log.debug("Stacktrace", e);
				}
			} else {
				log.warn("Die CSS-Datei {} existiert nicht. Fahre ohne zusätzliche CSS-Inhalte fort.", options.css);
			}
			css += options.css;
		}
		for (SvgDocument document : documents)
			document.appendCss(css);
	}

	/**
	 * Get the title of the x axis. FIXME: It is not semantically correct to
	 * have it here, but it's a requirement of {@link SvgTitlePainter} for now
	 * 
	 * @return
	 */
	protected abstract String getxAxisTitle();

	/**
	 * Get the title of the y axis. FIXME: It is not semantically correct to
	 * have it here, but it's a requirement of {@link SvgTitlePainter} for now
	 * 
	 * @return
	 */
	protected abstract String getyAxisTitle();

}
