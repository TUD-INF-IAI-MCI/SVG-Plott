package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.xml.SvgDocument;

public class PointPlot {

	public static Element paintPoint(SvgDocument doc, Point p, Element symbol,
			Element parent) {

		Element defs = (Element) doc.defs;
		if (defs == null)
			defs = addDevsToDoc(doc);

		String symbolId = "";

		if (defs != null) {
			if (symbol != null && symbol.hasAttribute("id")) {
				if (doc.getElementById(symbol.getAttribute("id")) == null) {
					defs.appendChild(symbol);
				}
				symbolId = symbol.getAttribute("id");
			}
		}
		return paintPoint(doc, p, symbolId, parent);

	}

	public static Element paintPoint(SvgDocument doc, Point p, String symbolId,
			Element parent) {

		if (parent == null)
			parent = doc.document();
		if (symbolId == null || symbolId.isEmpty()
				|| doc.getElementById(symbolId) == null) {
			Element defs = (Element) doc.defs;
			if (defs == null)
				defs = addDevsToDoc(doc);

			if (defs != null) { // TODO: set default
				Element symbol = createCrossSymbol(doc);
				// Element cross = createPlusSymbol(doc);
				// Element cross = createCircleSymbol(doc);
				// Element cross = createDotSymbol(doc);
				// Element cross = createSquareSymbol(doc);
				// Element cross = createRombusSymbol(doc);

				if (symbol != null && symbol.hasAttribute("id")) {
					if (doc.getChildElementById((Element) doc.defs,
							symbol.getAttribute("id")) == null) {
						defs.appendChild(symbol);
					}
					symbolId = symbol.getAttribute("id");
				} else
					return null;
			}
		}
		// do a use
		Element use = doc.createElement("use");
		use.setAttribute("xlink:href", "#" + symbolId);

		use.setAttribute("x", p.x());
		use.setAttribute("y", p.y());

		parent.appendChild(use);

		return use;
	}

	private static Element addDevsToDoc(SvgDocument doc) {
		Element devs = doc.createElement("devs");
		doc.appendChild(devs);
		return devs;
	}

	// TODO: make this more generic for using more point symbols
	public static Element getPointSymbolForIndex(int index, SvgDocument doc) {
		if (doc != null) {
			if (initSymbolArray(doc)) {
				if (POI_SYMBOL_ELEMNTS.size() > index)
					return POI_SYMBOL_ELEMNTS.get(index);
				else if (POI_SYMBOL_ELEMNTS.size() > 0)
					return POI_SYMBOL_ELEMNTS
							.get(POI_SYMBOL_ELEMNTS.size() - 1);
			}
		}
		return null;
	}

	private static List<Element> POI_SYMBOL_ELEMNTS = new ArrayList<Element>();

	private static boolean initSymbolArray(SvgDocument doc) {
		if (doc == null)
			return false;
		if (POI_SYMBOL_ELEMNTS == null || POI_SYMBOL_ELEMNTS.size() < 1) {
			POI_SYMBOL_ELEMNTS.add(createCrossSymbol(doc));
			POI_SYMBOL_ELEMNTS.add(createPlusSymbol(doc));
			POI_SYMBOL_ELEMNTS.add(createCircleSymbol(doc));
			POI_SYMBOL_ELEMNTS.add(createDotSymbol(doc));
			POI_SYMBOL_ELEMNTS.add(createSquareSymbol(doc));
			POI_SYMBOL_ELEMNTS.add(createRombusSymbol(doc));
		}
		return true;
	}

	public static final String POI_SYMBOL_VISSIBLE_CLASS = "poi_symbol";
	public static final String POI_SYMBOL_SPACER_CLASS = "poi_symbol_bg";

	private static Element createCrossSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_cross");
		symbol.setAttribute("style", "stroke-linecap:round; ");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISSIBLE_CLASS);
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

	private static Element createPlusSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_plus");
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

	private static Element createCircleSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_circle");
		symbol.setAttribute("style", "stroke-linecap:round; fill-opacity:0");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISSIBLE_CLASS);
		Element bgGroup = doc.createElement("g");
		bgGroup.setAttribute("class", POI_SYMBOL_SPACER_CLASS);

		symbol.appendChild(bgGroup);
		symbol.appendChild(vGroup);

		Element circle = doc.createCircle(new Point(0, 0), 1.7);
		bgGroup.appendChild(circle);
		vGroup.appendChild(circle.cloneNode(true));

		return symbol;
	}

	private static Element createDotSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_dot");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISSIBLE_CLASS);
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

	private static Element createSquareSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_square");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISSIBLE_CLASS);
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

	private static Element createRombusSymbol(SvgDocument doc) {

		Element symbol = createSymbol(doc, "poi_rombus");
		symbol.setAttribute("style", "stroke-linecap:round;");

		Element vGroup = doc.createElement("g");
		vGroup.setAttribute("class", POI_SYMBOL_VISSIBLE_CLASS);
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

	public static Element createSymbol(SvgDocument doc, String id) {
		Element symbol = doc.createElement("symbol", id);
		symbol.setAttribute("preserveAspectRatio", "xMidYMid slice");
		symbol.setAttribute("overflow", "visible");
		return symbol;
	}

}
