package tud.tangram.svgplot.legend;

import java.util.PriorityQueue;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.svgcreator.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class LegendRenderer extends PriorityQueue<LegendItem> {

	private static final long serialVersionUID = 3344044325106436185L;

	private String legendCss;

	public LegendRenderer() {
		super(new LegendItemComparator());
		legendCss = "";
	}

	/**
	 * Render all the {@link LegendItem LegendItems} to the legend and sets the
	 * css.
	 * 
	 * @param legend
	 *            the legend, to which the {@link LegendItem LegendItems} should
	 *            be rendered
	 * @param size
	 *            the size of the viewbox
	 * @param startingPosition
	 *            where to start rendering
	 */
	public void render(SvgDocument legend, Point size, Point startingPosition) {
		legend.appendCss(legendCss);

		Element viewbox = (Element) legend.appendChild(legend.createElement("svg"));
		viewbox.setAttribute("viewBox", "0 0 " + SvgTools.format2svg(size.x) + " " + SvgTools.format2svg(size.y));

		double x = startingPosition.x;
		double currentY = startingPosition.y;

		while (!isEmpty()) {
			LegendItem item = poll();
			currentY = item.render(legend, viewbox, new Point(x, currentY));
		}
	}

	/**
	 * Append css to the legend
	 * 
	 * @param css
	 *            the css which shall be added
	 */
	public void appendCss(String css) {
		legendCss = legendCss.concat(System.lineSeparator()).concat(css);
	}

}
