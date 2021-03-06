package tud.tangram.svgplot.plotting;

import tud.tangram.svgplot.data.Point;

public class Overlay extends Point {
	public static final double RADIUS = 2.9;

	private String description;

	/**
	 * if an overlay is an axis overlay of a metric axis it gets a more
	 * appropriate title text
	 */
	private boolean isAxisOverlay = false;

	public Overlay(Point p) {
		super(p.getX(), p.getY(), p.getName(), p.getSymbol());
	}

	public Overlay(double x, double y) {
		super(x, y);
	}
	
	public Overlay(double x, double y, boolean isAxisOverlay) {
		super(x, y);
		this.isAxisOverlay = isAxisOverlay;
	}

	public Overlay(Point point, String dataSetName) {
		this(point);
		this.description = dataSetName;
	}

	public Overlay(Point point, String dataSetName, String color) {
		this(point);
		if (color == null)
			this.description = dataSetName;
		else
			this.description = dataSetName + ", " + color;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isAxisOverlay() {
		return isAxisOverlay;
	}
}