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

	public enum PointType {
		BG, FG
	}

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
	private List<Element> poiSymbolBackgroundElements;
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

	/**
	 * Select a point symbol for the given id and places it at a given point.
	 * May return null, if a borderless style is selected and an attempt to
	 * create a border is made.
	 * 
	 * @param pointParent
	 *            | the parent group of the point
	 * @param pointType
	 *            | specify whether a background or foreground is queried
	 * @param position
	 *            | the point to place the symbol
	 * @param index
	 *            | the index of the pointlist, influences which symbol is
	 *            selected
	 * @return the svg 'use' element that visually represents the point
	 */
	public Element paintPoint(Element pointParent, PointType pointType, Point position, int index) {
		Element pointElement = getPointSymbolForIndex(pointType, index);

		if (pointElement == null)
			return null;

		// Get or create a defs section
		Element defs = (Element) doc.defs;
		if (defs == null)
			defs = addDefsToDoc();

		// Add the point symbol to the defs, if it is not already there. Also
		// save its
		// id.
		if (doc.getElementById(pointElement.getAttribute("id")) == null) {
			defs.appendChild(pointElement);
		}
		String pointSymbolId = pointElement.getAttribute("id");

		return paintPointUse(pointParent, position, pointSymbolId);
	}

	/**
	 * Place a symbol at a given point.
	 * 
	 * @param parent
	 *            | the parent group of the symbol
	 * @param p
	 *            | the point to place the symbol
	 * @param symbolId
	 *            | the svg Symbol id to use for the point
	 * @return the svg 'use' element that visually represents the point
	 */
	private Element paintPointUse(Element parent, Point p, String symbolId) {
		// Get the parent of the point
		Element safeParent;
		if (parent == null)
			safeParent = doc.document();
		else
			safeParent = parent;

		// do a use
		Element use = doc.createElement("use");
		use.setAttribute("xlink:href", "#" + symbolId);
		use.setAttribute("x", p.x());
		use.setAttribute("y", p.y());
		safeParent.appendChild(use);

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
	 * Returns a svg point/background Symbol corresponding to the given index.
	 * The symbols are stored in a list sorted by their recognizability. The
	 * first symbols are best to detect and to recognize. If the index is higher
	 * than the amount of available symbols the last symbol will be returned.
	 * 
	 * @param index
	 *            | the symbols list entry to return
	 * @return an svg Symbol Element
	 */
	private Element getPointSymbolForIndex(PointType pointType, int index) {
		List<Element> elements = pointType == PointType.FG ? poiSymbolElements : poiSymbolBackgroundElements;
		if (elements.size() > index)
			return elements.get(index);
		if (elements.isEmpty())
			return null;
		return elements.get(elements.size() - 1);
	}

	/**
	 * Fills the poiSymbolElements list with the svg symbols. Has to add at
	 * least one symbol in any case. For the borderless dot styles no border
	 * elements are added.
	 * 
	 */
	private void initSymbolArray() {
		poiSymbolElements = new ArrayList<>();
		poiSymbolBackgroundElements = new ArrayList<>();

		if (doc == null)
			return;

		if (style == PointPlotStyle.DOTS || style == PointPlotStyle.DOTS_BORDERLESS) {
			poiSymbolElements.add(createFilledCircleSymbol(PointType.FG));
			if (style == PointPlotStyle.DOTS)
				poiSymbolBackgroundElements.add(createFilledCircleSymbol(PointType.BG));
		} else {
			poiSymbolElements.add(createSquareSymbol(PointType.FG));
			poiSymbolElements.add(createCrossSymbol(PointType.FG));
			poiSymbolElements.add(createRhombusSymbol(PointType.FG));
			poiSymbolElements.add(createPlusSymbol(PointType.FG));
			poiSymbolElements.add(createFilledCircleSymbol(PointType.FG));
			poiSymbolElements.add(createCircleSymbol(PointType.FG));
			if (style == PointPlotStyle.MULTI_ROWS) {
				poiSymbolBackgroundElements.add(createSquareSymbol(PointType.BG));
				poiSymbolBackgroundElements.add(createCrossSymbol(PointType.BG));
				poiSymbolBackgroundElements.add(createRhombusSymbol(PointType.BG));
				poiSymbolBackgroundElements.add(createPlusSymbol(PointType.BG));
				poiSymbolBackgroundElements.add(createFilledCircleSymbol(PointType.BG));
				poiSymbolBackgroundElements.add(createCircleSymbol(PointType.BG));
			}
		}
	}

	/**
	 * Creates a cross Symbol with the id 'poi_cross'
	 * 
	 * @return an SVG symbol with the id 'poi_cross' representing a little cross
	 */
	private Element createCrossSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_cross" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round; ");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		symbol.appendChild(group);

		Element line1 = doc.createLine(new Point(-2, 2), new Point(2, -2));
		Element line2 = doc.createLine(new Point(-2, -2), new Point(2, 2));

		group.appendChild(line2);
		group.appendChild(line1);

		return symbol;
	}

	/**
	 * Creates a plus Symbol with the id 'poi_plus'
	 * 
	 * @return an SVG symbol with the id 'poi_plus' representing a little plus
	 */
	private Element createPlusSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_plus" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round; ");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		symbol.appendChild(group);

		Element hLine = doc.createLine(new Point(-2, 0), new Point(2, 0));
		Element vLine = doc.createLine(new Point(0, -2), new Point(0, 2));

		group.appendChild(hLine);
		group.appendChild(vLine);

		return symbol;
	}

	/**
	 * Creates a circle Symbol with the id 'poi_circles'
	 * 
	 * @return an SVG symbol with the id 'poi_circle' representing a little
	 *         circle
	 */
	private Element createCircleSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_circle" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round; fill-opacity:0");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		symbol.appendChild(group);

		Element circle = doc.createCircle(new Point(0, 0), 1.7);
		group.appendChild(circle);

		return symbol;
	}

	/**
	 * Creates a filled circle Symbol with the id 'poi_dot'
	 * 
	 * @return an SVG symbol with the id 'poi_dot' representing a little filled
	 *         dot
	 */
	private Element createFilledCircleSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_dot" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		if (pointType == PointType.FG)
			group.setAttribute("style", "stroke-opacity:0");

		symbol.appendChild(group);

		Element circle = doc.createCircle(new Point(0, 0), 2.2);
		group.appendChild(circle);

		return symbol;
	}

	/**
	 * Creates a square Symbol with the id 'poi_square'
	 * 
	 * @return an SVG symbol with the id 'poi_square' representing a little
	 *         filled square
	 */
	private Element createSquareSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_square" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		if (pointType == PointType.FG)
			group.setAttribute("style", "stroke-opacity:0");

		symbol.appendChild(group);

		Element square = doc.createRectangle(new Point(-2, -2), 4, 4);
		group.appendChild(square);

		return symbol;
	}

	/**
	 * Creates a rhomb Symbol with the id 'poi_rhombus'
	 * 
	 * @return an SVG symbol with the id 'poi_rhombus' representing a little
	 *         filled rhomb
	 */
	private Element createRhombusSymbol(PointType pointType) {

		Element symbol = createSymbol("poi_rhombus" + (pointType == PointType.BG ? "_bg" : ""));
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element group = doc.createElement("g");
		group.setAttribute("class", pointType == PointType.BG ? POI_SYMBOL_SPACER_CLASS : POI_SYMBOL_VISIBLE_CLASS);

		if (pointType == PointType.FG)
			group.setAttribute("style", "stroke-opacity:0");

		symbol.appendChild(group);

		Element rombus = doc.createElement("polygon");
		rombus.setAttribute("points", "0,-2.5 2.5,0 0,2.5 -2.5,0");
		group.appendChild(rombus);

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
