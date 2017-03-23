package tud.tangram.svgplot.data;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 *
 */
public class PointListList extends ArrayList<PointListList.PointList> {

	private static final long serialVersionUID = 6902232865786868851L;
	private Double maxX = Double.NEGATIVE_INFINITY;
	private Double maxY = Double.NEGATIVE_INFINITY;
	private Double minX = Double.POSITIVE_INFINITY;
	private Double minY = Double.POSITIVE_INFINITY;

	public PointListList() {
		this("");
	}

	public PointListList(String pointLists) {
		if (pointLists == null || pointLists.isEmpty())
			return;

		// TODO: load from file

		// pointLists = pointLists.replaceAll("[^\\d.,^\\s+,^\\{^\\}^-]", "");
		String[] lists = pointLists.split("\\}");
		for (String l : lists) {
			PointList pl = new PointList(l);
			if (!pl.isEmpty()) {
				this.add(pl);
			}
		}
	}

	@Override
	public boolean add(PointListList.PointList pl) {
		maxX = Math.max(getMaxX(), pl.getMaxX());
		maxY = Math.max(getMaxY(), pl.getMaxY());
		minX = Math.min(getMinX(), pl.getMinX());
		minY = Math.min(getMinY(), pl.getMinY());
		return super.add(pl);
	}

	public boolean add(List<Point> points) {
		PointList pl = new PointList(points);
		maxX = Math.max(getMaxX(), pl.getMaxX());
		maxY = Math.max(getMaxY(), pl.getMaxY());
		minX = Math.min(getMinX(), pl.getMinX());
		minY = Math.min(getMinY(), pl.getMinY());
		return super.add(pl);
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}
	
	public boolean hasValidMinMaxValues() {
		return maxX > minX && maxY > minY;
	}

	public static class Converter implements IStringConverter<PointListList> {
		@Override
		public PointListList convert(String value) {
			return new PointListList(value);
		}
	}

	/**
	 * List of Points including max values
	 * 
	 * @author Jens Bornschein
	 * 
	 */
	public class PointList extends ArrayList<Point> {

		private static final long serialVersionUID = -2318768874799315111L;
		private Double maxX = Double.NEGATIVE_INFINITY;
		private Double maxY = Double.NEGATIVE_INFINITY;
		private Double minX = Double.POSITIVE_INFINITY;
		private Double minY = Double.POSITIVE_INFINITY;
		private String name = "";

		public PointList(List<Point> points) {
			if (points != null && !points.isEmpty()) {
				for (Point p : points) {
					this.add(p);
				}
			}
		}

		public PointList(String points) {
			if (points == null || points.isEmpty())
				return;

			String[] pl = points.split("::");

			if (pl != null && pl.length > 0) {

				String pts;
				if (pl.length > 1) {
					setName(pl[0].trim());
					pts = pl[1].replaceAll("[^\\d.,^\\s+,^-]", "");
				} else {
					pts = pl[0].replaceAll("[^\\d.,^\\s+,^-]", "");
				}
				String[] s = pts.split("\\s+");

				for (String string : s) {
					if (string != null && !string.isEmpty()) {
						Point p = (new Point.Converter()).convert(string);
						this.add(p);
					}
				}
			}
		}

		public PointList() {
			this("");
		}

		@Override
		public boolean add(Point p) {
			maxX = Math.max(getMaxX(), p.getX());
			maxY = Math.max(getMaxY(), p.getY());
			minX = Math.min(getMinX(), p.getX());
			minY = Math.min(getMinY(), p.getY());
			return super.add(p);
		}

		public double getMaxX() {
			return maxX;
		}

		public double getMaxY() {
			return maxY;
		}

		public double getMinX() {
			return minX;
		}

		public double getMinY() {
			return minY;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public class Converter implements IStringConverter<PointList> {
			@Override
			public PointList convert(String value) {
				return new PointList(value.trim());
			}
		}
	}
}