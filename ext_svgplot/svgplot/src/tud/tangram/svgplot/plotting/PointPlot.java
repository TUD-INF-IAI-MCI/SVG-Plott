package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.styles.PointPlotStyle;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.xml.SvgDocument;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 *
 */
public class PointPlot {

	/**
	 * The css class for the visual part of a POI Symbol
	 */
	public static final String POI_SYMBOL_VISIBLE_CLASS = "poi_symbol";
	/**
	 * The css class for the underlying part of a POI Symbol, should result i a
	 * kind of outline.
	 */
	public static final String POI_SYMBOL_SPACER_CLASS = Constants.SPACER_CSS_CLASS;

	/**
	 * The list of available POI point symbols
	 */
	private List<Element> poiSymbolElements;
	// private SvgDocument poiSymbolDoc; FIXME When is this used?
	private PointPlotStyle style;
	private SvgDocument doc;

	public PointPlot(SvgDocument doc, PointPlotStyle style) {
		this.style = style;
		this.doc = doc;

		if (doc == null)
			throw new IllegalArgumentException("You must supply an SvgDocument");
		if (style == null)
			throw new IllegalArgumentException("You must supply a PointPlotStyle");

		initSymbolArray();
	}

	public Element paintPoint(Element parent, Point position, int index) {
		Element pointElement = getPointSymbolForIndex(index);

		// Get or create a defs section
		Element defs = (Element) doc.defs;
		if (defs == null)
			defs = addDefsToDoc();

		// Add the symbol to the defs, if it is not already there. Also save its
		// id.
		if (doc.getElementById(pointElement.getAttribute("id")) == null) {
			defs.appendChild(pointElement);
		}
		String symbolId = pointElement.getAttribute("id");

		return paintPointUse(parent, position, symbolId);
	}

	/**
	 * Place a symbol at a given point.
	 * 
	 * @param p
	 *            | the point to place the symbol
	 * @param symbolId
	 *            | the svg Symbol id to use for the point
	 * @return the svg 'use' element that visually represents the point
	 */
	private Element paintPointUse(Element parent, Point p, String symbolId) {
		// Get the parent of the point
		Element pointParent;
		if (parent == null)
			pointParent = doc.document();
		else
			pointParent = parent;

		// do a use
		Element use = doc.createElement("use");
		use.setAttribute("xlink:href", "#" + symbolId);
		use.setAttribute("x", p.x());
		use.setAttribute("y", p.y());
		pointParent.appendChild(use);

		return use;
	}

	/**
	 * Adds a SVG defs Element to the svg document
	 * 
	 * @param doc
	 *            | The SVG document
	 * @return the defs section Element already inserted into the svg document
	 */
	private Element addDefsToDoc() {
		Element defs = doc.createElement("defs");
		doc.appendChild(defs);
		return defs;
	}

	/**
	 * Returns a svg Symbol corresponding to the given index. The symbols are
	 * stored in a list sorted by here recognizability. The first symbols are
	 * best to detect and to recognize. If the index is higher than the amount
	 * of available symbols the last symbol will be returned.
	 * 
	 * @param index
	 *            | the symbols list entry to return
	 * @return an svg Symbol Element
	 */
	private Element getPointSymbolForIndex(int index) {
		if (poiSymbolElements.size() > index)
			return poiSymbolElements.get(index);
		return poiSymbolElements.get(poiSymbolElements.size() - 1);
	}

	/**
	 * Fills the poiSymbolElements list with the svg symbols. Has to add at
	 * least one symbol in any case.
	 */
	private void initSymbolArray() {
		poiSymbolElements = new ArrayList<>();

		if (doc == null)
			return;

		if (style == PointPlotStyle.DOTS)
			poiSymbolElements.add(createDotSymbol());
		else {
			poiSymbolElements.add(createSquareSymbol());
			poiSymbolElements.add(createCrossSymbol());
			poiSymbolElements.add(createRhombusSymbol());
			poiSymbolElements.add(createPlusSymbol());
			poiSymbolElements.add(createFilledCircleSymbol());
			poiSymbolElements.add(createCircleSymbol());
		}
	}

	/**
	 * Creates a cross Symbol with the id 'poi_cross'
	 * 
	 * @return an SVG symbol with the id 'poi_cross' representing a little cross
	 */
	private Element createCrossSymbol() {

		Element symbol = createSymbol("poi_cross");
		symbol.setAttribute("style", "stroke-linecap:round; ");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element line1 = doc.createLine(new Point(-2, 2), new Point(2, -2));
		Element line2 = doc.createLine(new Point(-2, -2), new Point(2, 2));

		bgGroup.appendChild(line2);
		bgGroup.appendChild(line1);

		vGroup.appendChild(line2.cloneNode(true));
		vGroup.appendChild(line1.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a plus Symbol with the id 'poi_plus'
	 * 
	 * @return an SVG symbol with the id 'poi_plus' representing a little plus
	 */
	private Element createPlusSymbol() {

		Element symbol = createSymbol("poi_plus");
		symbol.setAttribute("style", "stroke-linecap:round; ");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", "poi_symbol");
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", "poi_symbol_bg");

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element hLine = doc.createLine(new Point(-2, 0), new Point(2, 0));
		Element vLine = doc.createLine(new Point(0, -2), new Point(0, 2));

		bgGroup.appendChild(hLine);
		bgGroup.appendChild(vLine);

		vGroup.appendChild(hLine.cloneNode(true));
		vGroup.appendChild(vLine.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a circle Symbol with the id 'poi_circles'
	 * 
	 * @return an SVG symbol with the id 'poi_circle' representing a little
	 *         circle
	 */
	private Element createCircleSymbol() {

		Element symbol = createSymbol("poi_circle");
		symbol.setAttribute("style", "stroke-linecap:round; fill-opacity:0");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element circle = doc.createCircle(new Point(0, 0), 1.7);
		bgGroup.appendChild(circle);
		vGroup.appendChild(circle.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a filled circle Symbol with the id 'poi_dot'
	 * 
	 * @return an SVG symbol with the id 'poi_dot' representing a little filled
	 *         dot
	 */
	private Element createFilledCircleSymbol() {

		Element symbol = createSymbol("poi_dot");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		vGroup.setAttribute("style", "stroke-opacity:0");
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element circle = doc.createCircle(new Point(0, 0), 2.2);
		bgGroup.appendChild(circle);
		vGroup.appendChild(circle.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a filled circle Symbol with the id 'poi_dot'
	 * 
	 * @return an SVG symbol with the id 'poi_dot' representing a little filled
	 *         dot
	 */
	private Element createDotSymbol() {

		Element symbol = createSymbol("poi_dot");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		vGroup.setAttribute("style", "stroke-opacity:0");

		symbol.appendChild(vGroup);

		Element circle = doc.createCircle(new Point(0, 0), 2.2);
		vGroup.appendChild(circle.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a square Symbol with the id 'poi_square'
	 * 
	 * @return an SVG symbol with the id 'poi_square' representing a little
	 *         filled square
	 */
	private Element createSquareSymbol() {

		Element symbol = createSymbol("poi_square");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		vGroup.setAttribute("style", "stroke-opacity:0");
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element square = doc.createRectangle(new Point(-2, -2), 4, 4);
		bgGroup.appendChild(square);
		vGroup.appendChild(square.cloneNode(true));

		return symbol;
	}

	/**
	 * Creates a rhomb Symbol with the id 'poi_rhombus'
	 * 
	 * @return an SVG symbol with the id 'poi_rhombus' representing a little
	 *         filled rhomb
	 */
	private Element createRhombusSymbol() {

		Element symbol = createSymbol("poi_rhombus");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISIBLE_CLASS);
		vGroup.setAttribute("style", "stroke-opacity:0");
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element rombus = doc.createElement("polygon");
		rombus.setAttribute("points", "0,-2.5 2.5,0 0,2.5 -2.5,0");
		bgGroup.appendChild(rombus);
		vGroup.appendChild(rombus.cloneNode(true));

		return symbol;
	}

	/**
	 * Represent the basic structure of an svg Symbol. Setting
	 * 'preserveAspectRatio' to 'xMidYMid slice' and 'overflow' to 'visible';
	 * 
	 * @param id
	 *            | the id-attribute the symbol should get
	 * @return the symbol skeleton
	 */
	public Element createSymbol(String id) {
		Element symbol = doc.createElement("symbol", id);
		symbol.setAttribute("preserveAspectRatio", "xMidYMid slice");
		symbol.setAttribute("overflow", "visible");
		return symbol;
	}
}
