package tud.tangram.svgplot.coordinatesystem;
/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class CoordinateSystem {

	final private Point origin;
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

	public Point convert(double x, double y) {
		x = origin.x + (x - xAxis.range.from) * size.x / (xAxis.range.to - xAxis.range.from);
		y = origin.y + size.y - ((y - yAxis.range.from) * size.y / (yAxis.range.to - yAxis.range.from));
		return new Point(x, y);
	}

	public Point convert(Point point) {
		return convert(point.x, point.y);
	}

	public Point convert(double x, double y, double dx, double dy) {
		Point real = convert(x, y);
		real.translate(dx, dy);
		return real;
	}

	public Point convert(Point point, double dx, double dy) {
		return convert(point.x, point.y, dx, dy);
	}

	public double convertXDistance(double distance) {
		return distance * size.x / (xAxis.range.to - xAxis.range.from);
	}

	public double convertYDistance(double distance) {
		return distance * size.y / (yAxis.range.to - yAxis.range.from);
	}

	public double convertDistance(Point point1, Point point2) {
		return convert(point1).distance(convert(point2));
	}
}
