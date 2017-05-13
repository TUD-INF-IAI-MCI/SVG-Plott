package tud.tangram.svgplot.data;

import java.util.ArrayList;
import java.util.Collections;
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
	protected Double maxX = Double.NEGATIVE_INFINITY;
	protected Double maxY = Double.NEGATIVE_INFINITY;
	protected Double minX = Double.POSITIVE_INFINITY;
	protected Double minY = Double.POSITIVE_INFINITY;
	
	public XType getXType() {
		return XType.METRIC;
	}

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
		boolean success = super.add(pl);
		updateMinMax();
		return success;
	}

	public boolean add(List<Point> points) {
		PointList pl = new PointList(points);
		return add(pl);
	}
	
	public void updateMinMax() {
		for(PointList checkPl : this) {
			maxX = Math.max(getMaxX(), checkPl.getMaxX());
			maxY = Math.max(getMaxY(), checkPl.getMaxY());
			minX = Math.min(getMinX(), checkPl.getMinX());
			minY = Math.min(getMinY(), checkPl.getMinY());
		}
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
	public static class PointList extends ArrayList<Point> {

		private static final long serialVersionUID = -2318768874799315111L;
		private Double maxX = Double.NEGATIVE_INFINITY;
		private Double maxY = Double.NEGATIVE_INFINITY;
		private Double minX = Double.POSITIVE_INFINITY;
		private Double minY = Double.POSITIVE_INFINITY;
		private String name = "";

		public PointList(List<Point> points) {
			if (points != null && !points.isEmpty()) {
				for (Point p : points) {
					this.insertSorted(p);
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
						this.insertSorted(p);
					}
				}
			}
		}

		public PointList() {
			this("");
		}

		public void insertSorted(Point p) {
			maxX = Math.max(getMaxX(), p.getX());
			maxY = Math.max(getMaxY(), p.getY());
			minX = Math.min(getMinX(), p.getX());
			minY = Math.min(getMinY(), p.getY());
			super.add(p);
			
			Comparable<Point> cmp = (Comparable<Point>) p;
	        for (int i = size()-1; i > 0 && cmp.compareTo(get(i-1)) < 0; i--)
	            Collections.swap(this, i, i-1);
		}
		

		@Deprecated
		public void add(int index, Point element) {
			throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
		}

		@Deprecated
		public boolean add(Point e) {
			throw new UnsupportedOperationException("Only insertions via insertSorted are allowed");
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

		/**
		 * Gets the first maximum of the data set.
		 * TODO implement multiple maxima with a proper string representation
		 * @return first maximum point
		 */
		public Point getFirstMaximum() {
			if(this.isEmpty())
				return null;
			
			Point maxPoint = get(0);
			
			for(Point p : this) {
				if(maxPoint.getY() < p.getY())
					maxPoint = p;
			}
			
			return maxPoint;
		}
		
		/**
		 * Gets the first minimum of the data set.
		 * TODO implement multiple minimum with a proper string representation
		 * @return first minimum point
		 */
		public Point getFirstMinimum() {
			if(this.isEmpty())
				return null;
			
			Point minPoint = get(0);
			
			for(Point p : this) {
				if(minPoint.getY() > p.getY())
					minPoint = p;
			}
			
			return minPoint;
		}
		
		public class Converter implements IStringConverter<PointList> {
			@Override
			public PointList convert(String value) {
				return new PointList(value.trim());
			}
		}
	}
}