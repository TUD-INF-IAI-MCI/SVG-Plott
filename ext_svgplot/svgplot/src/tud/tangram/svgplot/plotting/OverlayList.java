package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;

public class OverlayList extends ArrayList<Overlay> {

	private static final long serialVersionUID = -1031062986430320978L;
	final private CoordinateSystem cs;

	public OverlayList(CoordinateSystem cs) {
		super();
		this.cs = cs;
	}

	/**
	 * Add an overlay. Same as calling {@link OverlayList#add(Overlay, boolean)
	 * add(overlay, false)}.
	 * 
	 * @param overlay
	 *            the overlay which shall be added
	 */
	@Override
	public boolean add(Overlay overlay) {
		return add(overlay, false);
	}

	/**
	 * Add an overlay. If overwrite is false, only add it when there is no other
	 * overlay intersecting with it. If overwrite is true, remove overlays
	 * intersecting with the new overlay and then add it.
	 * 
	 * @param overlay
	 *            the overlay which shall be added
	 * @param overwrite
	 *            true, if intersecting overlays shall be overwritten
	 * @return true, if added, false if not added
	 */
	public boolean add(Overlay overlay, boolean overwrite) {
		// Do not add the overlay if would not be shown anyway.
		if (overlay.y < cs.yAxis.range.from || overlay.y > cs.yAxis.range.to) {
			return false;
		}

		// Search for the first overlay not being left (x) of the one to add.
		int i = 0;
		while (i < size() && get(i).x <= overlay.x) {
			i++;
		}

		// Overlay diameter
		double d = 2 * Overlay.RADIUS;

		// Check whether on of the overlays left (x) to the current one
		// intersects with it. If it does, do not add the current one/overwrite.
		int k = i - 1;
		while (k >= 0 && cs.convertXDistance(overlay.x - get(k).x) < d) {
			if (cs.convertDistance(get(k), overlay) < d) {
				if (overwrite) {
					i--;
					remove(k);
				} else {
					return false;
				}
			}
			k--;
		}

		// Check whether one of the overlays right (x) to the current one
		// intersects with it. If it does, do not add the current one/overwrite.
		k = i;
		while (k < size() && cs.convertXDistance(get(k).x - overlay.x) < d) {
			if (cs.convertDistance(get(k), overlay) < d) {
				if (overwrite) {
					remove(k);
					k--;
				} else {
					return false;
				}
			}
			k++;
		}

		// Otherwise add the overlay, sorted by the x value.
		super.add(i, overlay);
		return true;
	}

	/**
	 * Add a new overlay created from a point and a function. Same as calling
	 * {@link OverlayList#add(Point, Function, boolean) add(point, function, false)}.
	 * 
	 * @param point
	 * @param function
	 * @return
	 */
	public boolean add(Point point, Function function) {
		return add(new Overlay(point, function));
	}

	/**
	 * Add a new overlay created from a point and a function. If overwrite is
	 * true, remove overlays intersecting with the new overlay and then add it.
	 * 
	 * @param point
	 * @param function
	 * @param overwrite
	 *            whether to overwrite intersecting overlays
	 * @return
	 */
	public boolean add(Point point, Function function, boolean overwrite) {
		return add(new Overlay(point, function), overwrite);
	}

	/**
	 * Add a list of points together with a function. Same as calling
	 * {@link OverlayList#addAll(List, Function, boolean) add(points, function,
	 * false)}.
	 * 
	 * @param points
	 * @param function
	 */
	public void addAll(List<Point> points, Function function) {
		addAll(points, function, false);
	}

	/**
	 * Add a list of points together with a function. If overwrite is true,
	 * remove overlays intersecting with each new overlay and then add it.
	 * 
	 * @param points
	 * @param function
	 * @param overwrite
	 *            whether to overwrite intersecting overlays
	 */
	public void addAll(List<Point> points, Function function, boolean overwrite) {
		for (Point point : points) {
			add(point, function, overwrite);
		}
	}

	/**
	 * Add a list of overlays. Same as calling {@link #addAll(List, boolean)
	 * addAll(overlays, false)}.
	 * 
	 * @param overlays
	 */
	public void addAll(List<Overlay> overlays) {
		addAll(overlays, false);
	}

	/**
	 * Add a list of overlays. If overwrite is true, intersecting overlays are
	 * overwritten. Else the new overlay is not added.
	 * 
	 * @param overlays
	 * @param overwrite
	 *            whether to overwrite intersecting overlays
	 */
	public void addAll(List<Overlay> overlays, boolean overwrite) {
		for (Overlay overlay : overlays) {
			add(overlay, overwrite);
		}
	}

}