package tud.tangram.svgplot.coordinatesystem;

import java.util.List;

import tud.tangram.svgplot.data.Point;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class CoordinateSystem {
	
	public final Axis xAxis;
	public final Axis yAxis;
	
	public final boolean pi;
	
	/** Origin of the real coordinate system (left upper corner) */
	private final Point origin;
	
	/** Size of the drawing area excluding margins */
	private final Point size;

	public CoordinateSystem(Range xRange, Range yRange, Point size, List<Integer> margin) {
		this(xRange, yRange, size, margin, false);
	}
	
	public CoordinateSystem(Range xRange, Range yRange, Point size, List<Integer> diagramContentMargin, boolean pi) {
		origin = new Point(diagramContentMargin.get(3), diagramContentMargin.get(0));

		this.size = new Point(size);
		this.size.setX(this.size.getX() - (diagramContentMargin.get(1) + diagramContentMargin.get(3)));
		this.size.setY(this.size.getY() - (diagramContentMargin.get(0) + diagramContentMargin.get(2)));
//		this.size.x = Math.min(this.size.x, this.size.y);
//		this.size.y = this.size.x;

		xAxis = new Axis(xRange, this.size.getX());
		yAxis = new Axis(yRange, this.size.getY());
		
		this.pi = pi;
	}

	/**
	 * Converts a point from virtual to real coordinates.
	 * @param x | virtual x coordinate
	 * @param y | virtual y coordinate
	 * @return real point
	 */
	public Point convert(double x, double y) {
		double newX = origin.getX() + (x - xAxis.range.getFrom()) * size.getX() / (xAxis.range.getTo() - xAxis.range.getFrom());
		double newY = origin.getY() + size.getY() - ((y - yAxis.range.getFrom()) * size.getY() / (yAxis.range.getTo() - yAxis.range.getFrom()));
		return new Point(newX, newY);
	}

	/**
	 * Converts a point from virtual to real coordinates.
	 * @param point | virtual coordinates
	 * @return real point
	 */
	public Point convert(Point point) {
		return convert(point.getX(), point.getY());
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
		return convert(point.getX(), point.getY(), dx, dy);
	}

	/**
	 * Converts a distance on the x axis from virtual to real.
	 * @param distance	| virtual distance
	 * @return real distance
	 */
	public double convertXDistance(double distance) {
		return distance * size.getX() / (xAxis.range.getTo() - xAxis.range.getFrom());
	}

	/**
	 * Converts a distance on the y axis from virtual to real.
	 * @param distance	| virtual distance
	 * @return real distance
	 */
	public double convertYDistance(double distance) {
		return distance * size.getY() / (yAxis.range.getTo() - yAxis.range.getFrom());
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
