package tud.tangram.svgplot.plotting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tud.tangram.svgplot.data.Point;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Plot implements Iterable<List<Point>> {

	final private Function function;
	final private List<List<Point>> plot;
	final private Map<Plot, List<Point>> intersections = new HashMap<>();
	private List<Point> extrema = null;
	private List<Point> roots = null;
	public String Name = "f";

	public Plot(Function function, Gnuplot gnuplot) throws IOException, InterruptedException {
		this.function = function;
		//replacing uncommon signing to gnu plot format 
		String gf = function.getFunction().replace("^", "**").replaceAll("(\\d|\\))\\s*([a-zA-z(])", "$1*$2");
		plot = gnuplot.plot(gf);
		if(plot != null){
			for (List<Point> pList : plot) {
				Collections.sort(pList);
			}
		}	
	}

	public Function getFunction() {
		return function;
	}

	public List<Point> getIntersections(Plot otherPlot) {
		if (!intersections.containsKey(otherPlot)) {
			List<Point> intersectionsPoints = new ArrayList<>();
			Iterator<List<Point>> it1 = otherPlot.iterator();
			if (!it1.hasNext()) {
				Collections.sort(intersectionsPoints);
				return intersectionsPoints;
			}
			Iterator<Point> it2 = it1.next().iterator();
			Point other = it2.next();
			for (List<Point> list : plot) {
				double lastDistance = 0;
				Point last = null;
				for (Point point : list) {
					while (other.getX() < point.getX()) {
						if (!it2.hasNext()) {
							if (!it1.hasNext()) {
								Collections.sort(intersectionsPoints);
								return intersectionsPoints;
							}
							it2 = it1.next().iterator();
						}
						other = it2.next();
					}
					if (other.getX() > point.getX()) {
						continue;
					}
					double distance = other.getY() - point.getY();
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
						direction = last.getY() > point.getY() ? -1 : 1;
						if (lastDirection != 0 && direction != lastDirection) {
							extrema.add(last);
						}
					}
					last = point;
					lastDirection = direction;
				}
			}
			Collections.sort(extrema);
		}		
		return extrema;
	}

	public List<Point> getRoots() {
		if (roots == null) {
			roots = new ArrayList<>();
			for (List<Point> list : plot) {
				Point last = null;
				for (Point point : list) {
					if (last != null && last.getY() < 0 ^ point.getY() < 0) {
						if (Math.abs(last.getY()) < Math.abs(point.getY())) {
							roots.add(last);
						} else {
							roots.add(point);
						}
					}
					last = point;
				}
			}
			Collections.sort(roots);
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
					while (next < point.getX()) {
						next += interval;
						//System.out.print(SvgPlot.format(next) + ", ");
					}
				}
				if (point.getX() >= next) {
					if (last == null || next - last.getX() >= point.getX() - next) {
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
