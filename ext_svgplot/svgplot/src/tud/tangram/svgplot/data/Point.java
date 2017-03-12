package tud.tangram.svgplot.data;

import org.w3c.dom.Element;

import com.beust.jcommander.IStringConverter;

import tud.tangram.svgplot.utils.SvgTools;

/**
 * 
 * @author Gregor Harlan Idea and supervising by Jens Bornschein
 *         jens.bornschein@tu-dresden.de Copyright by Technische Universität
 *         Dresden / MCI 2014
 *
 */
public class Point implements Comparable<Point> {

	public double x;
	public double y;
	public String name;
	public Element symbol;

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
		this.x = x;
		this.y = y;
		this.name = name;
		this.symbol = symbol;
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
		x += dx;
		y += dy;
	}

	/**
	 * formats the x value as an svg compatible decimal value.
	 * 
	 * @return
	 */
	public String x() {
		return SvgTools.format2svg(x);
	}

	/**
	 * formats the y value as an svg compatible decimal value.
	 * 
	 * @return
	 */
	public String y() {
		return SvgTools.format2svg(y);
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

	@Override
	public Point clone() {
		return new Point(x, y, name, symbol);
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
		return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
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
	 * Compares with x priority.
	 * Returns -1 if p2 is null.
	 * @param p2	|	other point
	 * @return
	 */
	@Override
	public int compareTo(Point p2) {
		if (p2 != null) {
			if (p2.x == x) {
				return y < p2.y ? -1 : 1;
			} else
				return x < p2.x ? -1 : 1;
		}
		return -1;
	}

	/**
	 * Compare the y values of two points.
	 * Returns -1 if p2 is null.
	 * @param p2	|	other point
	 * @return
	 */
	public int compareToY(Point p2) {
		if (p2 != null) {
			return y < p2.y ? -1 : 1;
		}
		return -1;
	}

	/**
	 * Compare the x values of two points.
	 * Returns -1 if p2 is null.
	 * @param p2	|	other point
	 * @return
	 */
	public int compareToX(Point p2) {
		if (p2 != null) {
			return x < p2.x ? -1 : 1;
		}
		return -1;
	}

}
