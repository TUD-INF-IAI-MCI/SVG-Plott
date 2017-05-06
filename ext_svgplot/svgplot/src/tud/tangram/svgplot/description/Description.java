package tud.tangram.svgplot.description;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.HtmlDocument;

public class Description extends HtmlDocument{
	
	public Description(String title) throws ParserConfigurationException {
		super(title);
	}
	
	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @return ul element with points as a list
	 */
	public Element createPointList(CoordinateSystem cs, List<Point> points) {
		return createPointList(cs, points, null, 0);
	}
	
	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @return ul element with points as a list
	 */
	public Element createPointList(CoordinateSystem cs, List<Point> points, String cap) {
		return createPointList(cs, points, cap, 0);
	}
	
	/**
	 * Generates a HTML ul list with the Points as li list entries packed in the
	 * given caption string and brackets. E.g. E(x|y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	public Element createPointList(CoordinateSystem cs, List<Point> points, String cap, int offset) {
		Element list = createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(createTextElement("li", SvgTools.formatForText(cs, point, cap + "_" + ++i)));
			} else {
				list.appendChild(createTextElement("li", cs.formatForSpeech(point)));
			}
		}
		return list;
	}
	
	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @return ul element with points as a list
	 */
	public Element createXPointList(CoordinateSystem cs, List<Point> points) {
		return createXPointList(cs, points, "x", 0);
	}

	/**
	 * Generates a HTML ul list with the Points as li list entries (x / y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	public Element createXPointList(CoordinateSystem cs, List<Point> points, int offset) {
		return createXPointList(cs, points, "x", offset);
	}
	
 	/**
	 * Generates a HTML ul list with the Points as li list entries packed in the
	 * given caption string and brackets. E.g. E(x|y)
	 * 
	 * @param points
	 *            | list of points to put in the listing
	 * @param cap
	 *            | caption for the points
	 * @param offset
	 *            | counter offset
	 * @return ul element with points as a list
	 */
	public Element createXPointList(CoordinateSystem cs, List<Point> points, String cap, int offset) {
		Element list = createElement("ul");
		int i = offset;
		for (Point point : points) {
			if (cap != null && !cap.isEmpty()) {
				list.appendChild(createTextElement("li", cap + "_" + ++i + " = " + cs.formatX(point.getX())));
			} else {
				list.appendChild(createTextElement("li", cs.formatX(point.getX())));
			}
		}
		return list;
	}
}
