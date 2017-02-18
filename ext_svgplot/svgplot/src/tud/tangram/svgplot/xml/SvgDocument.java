package tud.tangram.svgplot.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.svgcreator.SvgTools;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class SvgDocument extends Document {

	final public Node defs;
	final protected double textEnd;

	final protected static int LINE_HEIGHT = 13;

	public SvgDocument(String title, Point size, double marginRight) throws ParserConfigurationException {
		super("svg");

		root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		root.setAttribute("version", "1.1");
		root.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		root.setAttribute("width", SvgTools.format2svg(size.x) + "mm");
		root.setAttribute("height", SvgTools.format2svg(size.y) + "mm");

		defs = root.appendChild(doc.createElement("defs"));

		root.appendChild(createTitle(title));

		textEnd = size.x - marginRight;
	}

	public void setAttribute(String name, String value) {
		root.setAttribute(name, value);
	}

	public Node appendDefsChild(Node child) {
		return defs.appendChild(child);
	}

	public Node appendCss(String css) {
		NodeList styleElements = doc.getElementsByTagName("style");
		
		// TODO make it less messy
		String indent = "            ";
		css = "\n" + indent + css.replace("\n", "\n" + indent) + "\n        ";
		
		if(styleElements.getLength() == 0) {		
			Element style = (Element) defs.appendChild(doc.createElement("style"));
			style.setAttribute("type", "text/css");
			style.appendChild(doc.createCDATASection(css));
			return style;
		}
		else {
			Element style = (Element) styleElements.item(0);
			CDATASection styleData = (CDATASection) style.getFirstChild();
			String completeCss = styleData.getData() + css;
			styleData.setData(completeCss);
			return style;
		}
	}

	public Element createGroup() {
		return createElement("g");
	}

	public Element createGroup(String id) {
		return createElement("g", id);
	}

	public Element createLine(Point from, Point to) {
		Element line = createElement("line");
		line.setAttribute("x1", from.x());
		line.setAttribute("y1", from.y());
		line.setAttribute("x2", to.x());
		line.setAttribute("y2", to.y());
		return line;
	}

	public Element createCircle(Point center, double radius) {
		Element circle = createElement("circle");
		circle.setAttribute("cx", center.x());
		circle.setAttribute("cy", center.y());
		circle.setAttribute("r", SvgTools.format2svg(radius));
		return circle;
	}
	
	public Element createRectangle(Point start, double width, double height) {
		Element rect = createElement("rect");
		rect.setAttribute("x", start.x());
		rect.setAttribute("y", start.y());
		rect.setAttribute("width", SvgTools.format2svg(width));
		rect.setAttribute("height", SvgTools.format2svg(height));
		return rect;
	}
	
	public Element createRectangle(Point start, String width, String height) {
		Element rect = createElement("rect");
		rect.setAttribute("x", start.x());
		rect.setAttribute("y", start.y());
		rect.setAttribute("width", width);
		rect.setAttribute("height", height);
		return rect;
	}
	

	public Element createTitle(String title) {
		return createTextElement("title", title);
	}

	public Element createDesc(String desc) {
		return createTextElement("desc", desc);
	}

	public Element createText(Point point, String line1, String... lines) {
		Element text = createElement("text");
		int charLimit = (int) ((textEnd - point.x) / 6.5);
		if (lines.length == 0 && line1.length() <= charLimit) {
			text.setAttribute("x", point.x() + "mm");
			text.setAttribute("y", point.y() + "mm");
			text.setTextContent(line1);
			point.translate(0, LINE_HEIGHT);
			return text;
		}
		String line = line1;
		for (int i = -1; i < lines.length; i++) {
			if (i >= 0) {
				line = lines[i];
			}
			char[] chars = line.toCharArray();
			boolean endOfString = false;
			int start = 0;
			int end = start;
			while (start < chars.length - 1) {
				Element tspan = createElement("tspan");
				tspan.setAttribute("x", point.x() + "mm");
				tspan.setAttribute("y", point.y() + "mm");
				text.appendChild(tspan);
				point.translate(0, LINE_HEIGHT);
				int charCount = 0;
				int lastSpace = 0;
				while (charCount < charLimit) {
					if (chars[charCount + start] == ' ' || chars[charCount + start] == '-' || chars[charCount + start] == '+') {
						lastSpace = charCount;
					}
					charCount++;
					if (charCount + start == line.length()) {
						endOfString = true;
						break;
					}
				}
				end = endOfString ? line.length() : (lastSpace > 0) ? lastSpace + start + 1 : charCount + start;
				tspan.setTextContent(line.substring(start, end));
				start = end;
			}
		}
		return text;
	}
	
	/**
	 * Creates a textual label and place it in the svg document previous the
	 * viewbox.
	 * 
	 * @param text
	 *            | the textual value that should be diplayed
	 * @param pos
	 *            | pixel position in the svg file where the text should start
	 * @param id
	 *            | XML id of the node
	 * @param cssClass
	 *            | css class for this node
	 * @return an textual Element already placed in the svg file with given text
	 *         at given position.
	 */
	public Element createLabel(String text, Point pos, String id, String cssClass) {
		Element label = createText(pos, text);
		if (id != null && !id.isEmpty())
			label.setAttribute("id", id);
		if (cssClass != null && !cssClass.isEmpty())
			label.setAttribute("class", cssClass);
		// TODO: add underlying background
		return label;
	}

	@Override
	protected void setTransformerProperties(Transformer transformer) {
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
	}
}
