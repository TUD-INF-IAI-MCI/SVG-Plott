package tud.tangram.svgplot.coordinatesystem;

import tud.tangram.svgplot.SvgPlot;

import com.beust.jcommander.IStringConverter;

public class Point {

	public double x;
	public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void translate(double dx, double dy) {
		x += dx;
		y += dy;
	}

	public String x() {
		return SvgPlot.format2svg(x);
	}

	public String y() {
		return SvgPlot.format2svg(y);
	}

	@Override
	public String toString() {
		return x() + "," + y();
	}

	@Override
	public Point clone() {
		return new Point(x, y);
	}

	public double distance(Point other) {
		return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
	}

	public static class Converter implements IStringConverter<Point> {
		@Override
		public Point convert(String value) {
			String[] s = value.split(",");
			return new Point(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		}
	}
}
