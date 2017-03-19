package tud.tangram.svgplot.plotting;

import java.util.ArrayList;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class PlotList extends ArrayList<Plot> {

	private static final long serialVersionUID = 2449399739807644999L;
	private final CoordinateSystem cs;

	public PlotList(CoordinateSystem cs) {
		super();
		this.cs = cs;
	}

	public OverlayList overlays() {
		OverlayList overlays = new OverlayList(cs);

		// Intersections
		for (int i = 0; i < size() - 1; i++) {
			Plot plot1 = get(i);
			for (int k = i + 1; k < size(); k++) {
				overlays.addAll(plot1.getIntersections(get(k)), plot1.getFunction());
			}
		}

		// Extrema
		for (Plot plot : this) {
			overlays.addAll(plot.getExtrema(), plot.getFunction());
		}

		// Roots
		for (Plot plot : this) {
			overlays.addAll(plot.getRoots(), plot.getFunction());
		}

		// Other points
		for (double interval : cs.xAxis.intervalSteps) {
			for (Plot plot : this) {
				overlays.addAll(plot.getPoints(interval, cs.xAxis.range.getFrom()), plot.getFunction());
			}
		}

		return overlays;
	}

}
