package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.List;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;

public class OverlayList extends ArrayList<Overlay> {

	private static final long serialVersionUID = -1031062986430320978L;
	private final CoordinateSystem cs;

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
		if (overlay.getY() < cs.yAxis.getRange().getFrom() || overlay.getY() > cs.yAxis.getRange().getTo()) {
			return false;
		}

		// Search for the first overlay not being left (x) of the one to add.
		int i = 0;
		while (i < size() && get(i).getX() <= overlay.getX()) {
			i++;
		}

		// Overlay diameter
		double d = 2 * Overlay.RADIUS;

		// Check whether on of the overlays left (x) to the current one
		// intersects with it. If it does, do not add the current one/overwrite.
		int k = i - 1;
		while (k >= 0 && cs.convertXDistance(overlay.getX() - get(k).getX()) < d) {
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
		while (k < size() && cs.convertXDistance(get(k).getX() - overlay.getX()) < d) {
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
	 * Add a new overlay created from a point. Same as calling
	 * {@link OverlayList#add(Point, String, String, boolean) add(point,
	 * dataSetName, color, false)}.
	 * 
	 * @param point
	 * @param dataSetName
	 * @param color
	 * @return
	 */
	public boolean add(Point point, String dataSetName, String color) {
		return add(new Overlay(point, dataSetName, color));
	}

	/**
	 * Add a new overlay created from a point. If overwrite is true, remove
	 * overlays intersecting with the new overlay and then add it.
	 * 
	 * @param point
	 * @param dataSetName
	 * @param color
	 * @param overwrite
	 *            whether to overwrite intersecting overlays
	 * @return
	 */
	public boolean add(Point point, String dataSetName, String color, boolean overwrite) {
		return add(new Overlay(point, dataSetName, color), overwrite);
	}

	/**
	 * Add a list of points. Same as calling
	 * {@link OverlayList#addAll(List, String, String, boolean) add(points,
	 * dataSetName, color false)}.
	 * 
	 * @param points
	 * @param function
	 */
	public void addAll(List<Point> points, String dataSetName, String color) {
		addAll(points, dataSetName, color, false);
	}

	/**
	 * Add a list of points. If overwrite is true, remove overlays intersecting
	 * with each new overlay and then add it.
	 * 
	 * @param points
	 * @param function
	 * @param overwrite
	 *            whether to overwrite intersecting overlays
	 */
	public void addAll(List<Point> points, String dataSetName, String color, boolean overwrite) {
		for (Point point : points) {
			add(point, dataSetName, color, overwrite);
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