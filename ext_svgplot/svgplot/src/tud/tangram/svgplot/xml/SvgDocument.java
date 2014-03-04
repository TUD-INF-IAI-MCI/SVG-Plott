package tud.tangram.svgplot.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.SvgPlot;
import tud.tangram.svgplot.coordinatesystem.Point;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class SvgDocument extends Document {

	final protected Node defs;
	final protected double textEnd;

	final protected static int LINE_HEIGHT = 13;

	public SvgDocument(String title, Point size, double marginRight) throws ParserConfigurationException {
		super("svg");

		root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		root.setAttribute("version", "1.1");
		root.setAttribute("width", SvgPlot.format2svg(size.x) + "mm");
		root.setAttribute("height", SvgPlot.format2svg(size.y) + "mm");

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
		Element style = (Element) defs.appendChild(doc.createElement("style"));
		style.setAttribute("type", "text/css");
		String indent = "            ";
		css = "\n" + indent + css.replace("\n", "\n" + indent) + "\n        ";
		style.appendChild(doc.createCDATASection(css));
		return style;
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
		circle.setAttribute("r", SvgPlot.format2svg(radius));
		return circle;
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

	@Override
	protected void setTransformerProperties(Transformer transformer) {
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.0//EN");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd");
	}
}
