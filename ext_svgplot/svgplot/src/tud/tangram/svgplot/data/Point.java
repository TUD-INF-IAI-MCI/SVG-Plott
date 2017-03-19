package tud.tangram.svgplot.data;

import org.w3c.dom.Element;

import com.beust.jcommander.IStringConverter;

import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.utils.SvgTools;

/**
 * A point in a coordinate system specified by an x and y coordinate. Can also
 * have a name and an SVG symbol. Provides helper methods, e.g. for calculating
 * the distance between two points.
 * 
 * @author Gregor Harlan Idea and supervising by Jens Bornschein
 *         jens.bornschein@tu-dresden.de Copyright by Technische Universität
 *         Dresden / MCI 2014
 *
 */
public class Point implements Comparable<Point> {

	private double x;
	private double y;
	private String name;
	private Element symbol;

	/**
	 * Copy constructor
	 * 
	 * @param otherPoint
	 *            the point to copy
	 */
	public Point(Point otherPoint) {
		this(otherPoint.getX(), otherPoint.getY(), otherPoint.getName(),
				(Element) otherPoint.getSymbol().cloneNode(true));
	}

	/**
	 * Represents a two dimensional Point in the plot
	 * 
	 * @param x
	 *            | x (horizontal) position of the point
	 * @param y
	 *            | y (vertical) position of the point
	 */
	public Point(double x, double y) {
		this(x, y, "", null);
	}

	/**
	 * Represents a two dimensional Point in the plot
	 * 
	 * @param x
	 *            | x (horizontal) position of the point
	 * @param y
	 *            | y (vertical) position of the point
	 * @param name
	 *            | the name of the point
	 */
	public Point(double x, double y, String name) {
		this(x, y, name, null);
	}

	/**
	 * Represents a two dimensional Point in the plot
	 * 
	 * @param x
	 *            | x (horizontal) position of the point
	 * @param y
	 *            | y (vertical) position of the point
	 * @param symbol
	 *            | the symbol to use for the point
	 */
	public Point(double x, double y, Element symbol) {
		this(x, y, "", symbol);
	}

	/**
	 * Represents a two dimensional Point in the plot
	 * 
	 * @param x
	 *            | x (horizontal) position of the point
	 * @param y
	 *            | y (vertical) position of the point
	 * @param name
	 *            | the name of the point
	 * @param symbol
	 *            | the symbol to use for the point
	 */
	public Point(double x, double y, String name, Element symbol) {
		this.setX(x);
		this.setY(y);
		this.setName(name);
		this.setSymbol(symbol);
	}

	/**
	 * Move the point
	 * 
	 * @param dx
	 *            | movement in x (horizontal) direction
	 * @param dy
	 *            | movement in y (vertical) direction
	 */
	public void translate(double dx, double dy) {
		setX(getX() + dx);
		setY(getY() + dy);
	}

	/**
	 * formats the x value as an svg compatible decimal value.
	 * 
	 * @return
	 */
	public String x() {
		return SvgTools.format2svg(getX());
	}

	/**
	 * formats the y value as an svg compatible decimal value.
	 * 
	 * @return
	 */
	public String y() {
		return SvgTools.format2svg(getY());
	}

	@Override
	/**
	 * formats the x and y values as svg compatible decimal values and combine
	 * them by a comma.
	 * 
	 * @return x,y
	 */
	public String toString() {
		return x() + "," + y();
	}

	/**
	 * computes the two dimensional euclidean distance of two points.
	 * 
	 * @param other
	 *            | second point
	 * @return the two dimensional euclidean distance between this and the other
	 *         point.
	 */
	public double distance(Point other) {
		return Math.sqrt(Math.pow(other.getX() - getX(), 2) + Math.pow(other.getY() - getY(), 2));
	}

	public static class Converter implements IStringConverter<Point> {
		/**
		 * Convert a formatted string to a point. The format is:
		 * {@code [<x>][,<y>]} Omitted values will default to 0.
		 * 
		 * @param value
		 *            | formatted string
		 */
		@Override
		public Point convert(String value) {
			String[] s = value.split(",");
			return new Point(s.length > 0 ? Double.parseDouble(s[0]) : 0, s.length > 1 ? Double.parseDouble(s[1]) : 0);
		}
	}

	/**
	 * Compares with x priority. Returns -1 if p2 is null.
	 * 
	 * @param p2
	 *            | other point
	 * @return
	 */
	@Override
	public int compareTo(Point p2) {
		if (p2 != null) {
			if (Math.abs(p2.getX() - getX()) < Constants.EPSILON) {
				return getY() < p2.getY() ? -1 : 1;
			} else
				return getX() < p2.getX() ? -1 : 1;
		}
		return -1;
	}

	/**
	 * Compare the y values of two points. Returns -1 if p2 is null.
	 * 
	 * @param p2
	 *            | other point
	 * @return
	 */
	public int compareToY(Point p2) {
		if (p2 != null) {
			return getY() < p2.getY() ? -1 : 1;
		}
		return -1;
	}

	/**
	 * Compare the x values of two points. Returns -1 if p2 is null.
	 * 
	 * @param p2
	 *            | other point
	 * @return
	 */
	public int compareToX(Point p2) {
		if (p2 != null) {
			return getX() < p2.getX() ? -1 : 1;
		}
		return -1;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Element getSymbol() {
		return symbol;
	}

	public void setSymbol(Element symbol) {
		this.symbol = symbol;
	}

}
