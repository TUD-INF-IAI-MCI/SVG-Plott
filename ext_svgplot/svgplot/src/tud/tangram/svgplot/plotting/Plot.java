package tud.tangram.svgplot.plotting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tud.tangram.svgplot.coordinatesystem.Point;

public class Plot implements Iterable<List<Point>> {

	final private Function function;
	final private List<List<Point>> plot;
	final private Map<Plot, List<Point>> intersections = new HashMap<>();
	private List<Point> extrema = null;
	private List<Point> roots = null;

	public Plot(Function function, Gnuplot gnuplot) throws IOException, InterruptedException {
		this.function = function;

		String gf = function.getFunction().replace("^", "**").replaceAll("(\\d|\\))\\s*([a-zA-z(])", "$1*$2");
		plot = gnuplot.plot(gf);
	}

	public Function getFunction() {
		return function;
	}

	public List<Point> getIntersections(Plot otherPlot) {
		if (!intersections.containsKey(otherPlot)) {
			List<Point> intersectionsPoints = new ArrayList<>();
			Iterator<List<Point>> it1 = otherPlot.iterator();
			if (!it1.hasNext()) {
				return intersectionsPoints;
			}
			Iterator<Point> it2 = it1.next().iterator();
			Point other = it2.next();
			for (List<Point> list : plot) {
				double lastDistance = 0;
				Point last = null;
				for (Point point : list) {
					while (other.x < point.x) {
						if (!it2.hasNext()) {
							if (!it1.hasNext()) {
								return intersectionsPoints;
							}
							it2 = it1.next().iterator();
						}
						other = it2.next();
					}
					if (other.x > point.x) {
						continue;
					}
					double distance = other.y - point.y;
					if (distance == 0 || lastDistance != 0 && (lastDistance < 0 ^ distance < 0)) {
						if (Math.abs(lastDistance) < Math.abs(distance)) {
							intersectionsPoints.add(last);
						} else {
							intersectionsPoints.add(point);
						}
					}
					lastDistance = distance;
					last = point;
				}
			}
			intersections.put(otherPlot, intersectionsPoints);
		}
		return intersections.get(otherPlot);
	}

	public List<Point> getExtrema() {
		if (extrema == null) {
			extrema = new ArrayList<>();
			for (List<Point> list : plot) {
				Point last = null;
				int lastDirection = 0;
				int direction = 0;
				for (Point point : list) {
					direction = 0;
					if (last != null) {
						direction = last.y > point.y ? -1 : 1;
						if (lastDirection != 0 && direction != lastDirection) {
							extrema.add(last);
						}
					}
					last = point;
					lastDirection = direction;
				}
			}
		}
		return extrema;
	}

	public List<Point> getRoots() {
		if (roots == null) {
			roots = new ArrayList<>();
			for (List<Point> list : plot) {
				Point last = null;
				for (Point point : list) {
					if (last != null && last.y < 0 ^ point.y < 0) {
						if (Math.abs(last.y) < Math.abs(point.y)) {
							roots.add(last);
						} else {
							roots.add(point);
						}
					}
					last = point;
				}
			}
		}
		return roots;
	}

	public List<Point> getPoints(double interval, double from) {
		List<Point> points = new ArrayList<>();
		double next = from;
		//System.out.println(next);
		for (List<Point> list : plot) {
			Point last = null;
			for (Point point : list) {
				if (last == null) {
					while (next < point.x) {
						next += interval;
						//System.out.print(SvgPlot.format(next) + ", ");
					}
				}
				if (point.x >= next) {
					if (last == null || next - last.x >= point.x - next) {
						points.add(point);
					} else {
						points.add(last);
					}
					next += interval;
					//System.out.print(SvgPlot.format(next) + ", ");
				}
				last = point;
			}
		}
		//System.out.println();
		//System.out.println();
		return points;
	}

	@Override
	public Iterator<List<Point>> iterator() {
		return plot.iterator();
	}

}
