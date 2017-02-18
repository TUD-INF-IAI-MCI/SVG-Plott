package tud.tangram.svgplot.plotting;

import tud.tangram.svgplot.coordinatesystem.Point;

public class Overlay extends Point {
	final public static double RADIUS = 2.9;

	private Function function = null;

	public Overlay(Point p){
		super(p.x, p.y, p.name, p.symbol);
	}
	
	public Overlay(double x, double y) {
		super(x, y);
	}

	public Overlay(Point point, Function function) {
		this(point.x, point.y);
		this.function = function;
	}

	public Function getFunction() {
		return function;
	}
}