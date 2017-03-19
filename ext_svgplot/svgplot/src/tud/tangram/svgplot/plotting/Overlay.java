package tud.tangram.svgplot.plotting;

import tud.tangram.svgplot.data.Point;

public class Overlay extends Point {
	public static final double RADIUS = 2.9;

	private Function function = null;

	public Overlay(Point p){
		super(p.getX(), p.getY(), p.getName(), p.getSymbol());
	}
	
	public Overlay(double x, double y) {
		super(x, y);
	}

	public Overlay(Point point, Function function) {
		this(point.getX(), point.getY());
		this.function = function;
	}

	public Function getFunction() {
		return function;
	}
}