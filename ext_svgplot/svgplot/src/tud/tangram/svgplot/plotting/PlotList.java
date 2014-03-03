package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;

public class PlotList extends ArrayList<Plot> {
	private static final long serialVersionUID = 1L;

	final private CoordinateSystem cs;

	public PlotList(CoordinateSystem cs) {
		super();
		this.cs = cs;
	}

	public OverlayList overlays() {
		OverlayList overlays = new OverlayList();

		// Intersections
		for (int i = 0; i < size() - 1; i++) {
			Plot plot1 = get(i);
			for (int k = i + 1; k < size(); k++) {
				overlays.add(plot1.getIntersections(get(k)), plot1.getFunction());
			}
		}

		// Extrema
		for (Plot plot : this) {
			overlays.add(plot.getExtrema(), plot.getFunction());
		}

		// Roots
		for (Plot plot : this) {
			overlays.add(plot.getRoots(), plot.getFunction());
		}

		// Other points
		for (double interval : cs.xAxis.intervalSteps) {
			for (Plot plot : this) {
				overlays.add(plot.getPoints(interval, cs.xAxis.range.from), plot.getFunction());
			}
		}

		overlays.add(new Overlay(0, 0));
		for (double tic : cs.xAxis.ticLines()) {
			overlays.add(new Overlay(tic, 0));
		}
		for (double tic : cs.yAxis.ticLines()) {
			overlays.add(new Overlay(0, tic));
		}

		return overlays;
	}

	public class OverlayList extends ArrayList<Overlay> {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(Overlay overlay) {
			if (overlay.y < cs.yAxis.range.from || overlay.y > cs.yAxis.range.to) {
				return false;
			}
			int i = 0;
			while (i < size() && get(i).x <= overlay.x) {
				i++;
			}
			double d = 2 * Overlay.RADIUS;
			int k = i - 1;
			while (k >= 0 && cs.convertXDistance(overlay.x - get(k).x) < d) {
				if (cs.convertDistance(get(k), overlay) < d) {
					return false;
				}
				k--;
			}
			k = i;
			while (k < size() && cs.convertXDistance(get(k).x - overlay.x) < d) {
				if (cs.convertDistance(get(k), overlay) < d) {
					return false;
				}
				k++;
			}
			super.add(i, overlay);
			return true;
		}

		public boolean add(Point point, Function function) {
			return add(new Overlay(point, function));
		}

		public void add(List<Point> points, Function function) {
			for (Point point : points) {
				add(point, function);
			}
		}

	}

	public static class Overlay extends Point {
		final public static double RADIUS = 2.9;

		private Function function = null;

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

}
