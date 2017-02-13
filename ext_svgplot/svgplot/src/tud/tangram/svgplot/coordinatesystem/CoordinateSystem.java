package tud.tangram.svgplot.coordinatesystem;
/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class CoordinateSystem {
	/** Origin of the real coordinate system (left upper corner) */
	final private Point origin;
	
	/** Size of the drawing area excluding margins */
	final private Point size;
	
	final public Axis xAxis;
	final public Axis yAxis;

	public CoordinateSystem(Range xRange, Range yRange, Point size, int[] margin) {
		origin = new Point(margin[3], margin[0]);

		this.size = size.clone();
		this.size.x -= margin[1] + margin[3];
		this.size.y -= margin[0] + margin[2];
//		this.size.x = Math.min(this.size.x, this.size.y);
//		this.size.y = this.size.x;

		xAxis = new Axis(xRange, this.size.x);
		yAxis = new Axis(yRange, this.size.y);
	}

	/**
	 * Converts a point from virtual to real coordinates.
	 * @param x | virtual x coordinate
	 * @param y | virtual y coordinate
	 * @return real point
	 */
	public Point convert(double x, double y) {
		x = origin.x + (x - xAxis.range.from) * size.x / (xAxis.range.to - xAxis.range.from);
		y = origin.y + size.y - ((y - yAxis.range.from) * size.y / (yAxis.range.to - yAxis.range.from));
		return new Point(x, y);
	}

	/**
	 * Converts a point from virtual to real coordinates.
	 * @param point | virtual coordinates
	 * @return real point
	 */
	public Point convert(Point point) {
		return convert(point.x, point.y);
	}

	/**
	 * Converts a point from virtual coordinates and translates it
	 * in real space.
	 * @param x | virtual x coordinate
	 * @param y | virtual y coordinate
	 * @param dx | real x transformation
	 * @param dy | real y transformation
	 * @return real point
	 */
	public Point convert(double x, double y, double dx, double dy) {
		Point real = convert(x, y);
		real.translate(dx, dy);
		return real;
	}

	/**
	 * Converts a point from virtual coordinates and translates it
	 * in real space.
	 * @param point | virtual coordinates
	 * @param dx | real x transformation
	 * @param dy | real y transformation
	 * @return real point
	 */
	public Point convert(Point point, double dx, double dy) {
		return convert(point.x, point.y, dx, dy);
	}

	/**
	 * Converts a distance on the x axis from virtual to real.
	 * @param distance	| virtual distance
	 * @return real distance
	 */
	public double convertXDistance(double distance) {
		return distance * size.x / (xAxis.range.to - xAxis.range.from);
	}

	/**
	 * Converts a distance on the y axis from virtual to real.
	 * @param distance	| virtual distance
	 * @return real distance
	 */
	public double convertYDistance(double distance) {
		return distance * size.y / (yAxis.range.to - yAxis.range.from);
	}

	/**
	 * Converts two virtual points and calculates their real distance
	 * @param point1
	 * @param point2
	 * @return real distance
	 */
	public double convertDistance(Point point1, Point point2) {
		return convert(point1).distance(convert(point2));
	}
}
