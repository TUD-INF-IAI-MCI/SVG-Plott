package tud.tangram.svgplot.data.trendline;

import java.util.Iterator;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;

/**
 * Calculates a trend line using the moving average filter. The average is
 * calculated from each point and it {@code n} neighbours to the left and to the
 * right. Assumes that all points have the same distance.
 */
public class MovingAverageTrendline implements TrendLineAlgorithm {

	private final int n;
	private final int avgCount;

	/**
	 * Constructor setting the size of the filter.
	 * 
	 * @param n
	 *            size of the filter to the left and to the right
	 */
	public MovingAverageTrendline(int n) {
		this.n = n;
		this.avgCount = 2 * n + 1;
	}

	/**
	 * Calculate a trend line from the given points using the moving average
	 * filter. Fails if there are less than {@code 2n+2} points specified.
	 */
	@Override
	public PointList calculateTrendLine(PointList originalPoints) {
		if (originalPoints.size() < avgCount)
			return null;
		Point[] points = new Point[avgCount];
		AtomicModuloInteger arrayPos = new AtomicModuloInteger(0, avgCount);
		Iterator<Point> it = originalPoints.iterator();

		double ySum = 0;

		PointList newPoints = new PointList();
		newPoints.setName(originalPoints.getName() != null ? "Moving Average Trend Line: " + originalPoints.getName()
				: "Moving Average Trend Line");

		// Populate the filter start
		for (int i = 0; i < avgCount - 1; i++) {
			Point newPoint = it.next();
			ySum += newPoint.getY();
			points[arrayPos.getAndIncrementMod()] = newPoint;
		}

		// Calculate the average and move the iterator
		while (it.hasNext()) {
			int newPos = arrayPos.getModDelta(-2 * n - 1);
			if (points[newPos] != null)
				ySum -= points[arrayPos.getModDelta(-2 * n - 1)].getY();
			Point newPointInList = it.next();
			ySum += newPointInList.getY();
			points[arrayPos.getAndIncrementMod()] = newPointInList;

			double currentX = points[arrayPos.getModDelta(-n - 1)].getX();
			Point newPoint = new Point(currentX, ySum / avgCount);
			newPoints.add(newPoint);
		}

		return newPoints;
	}
}
