package tud.tangram.svgplot.data.trendline;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;

/**
 * Calculates a trend line using linear regression.
 */
public class LinearRegressionTrendLine implements TrendLineAlgorithm {

	double fromX;
	double toX;

	/**
	 * Constructor for the linear regression trend line algorithm.
	 * 
	 * @param fromX
	 *            from which position to render the trend line
	 * @param toX
	 *            to which position to render the trend line
	 */
	public LinearRegressionTrendLine(double fromX, double toX) {
		this.fromX = fromX;
		this.toX = toX;
	}

	/**
	 * Calculates a trend line using linear regression. At least two points need
	 * to be in {@code originalPoints}, otherwise it fails.
	 */
	@Override
	public PointList calculateTrendLine(PointList originalPoints) {
		double n = originalPoints.size();

		if (n < 2)
			return null;

		double sumXY = 0;
		double sumX = 0;
		double sumY = 0;
		double sumXX = 0;

		for (Point p : originalPoints) {
			sumXY += p.getX() * p.getY();
			sumX += p.getX();
			sumY += p.getY();
			sumXX += p.getX() * p.getX();
		}

		double alpha = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
		double beta = (sumY - alpha * sumX) / n;

		double fromY = alpha * fromX + beta;
		double toY = alpha * toX + beta;

		PointList newPoints = new PointList();
		newPoints.setName(originalPoints.getName() != null ? "Linear Regression Trend Line: " + originalPoints.getName()
				: "Linear Regression Trend Line");
		
		newPoints.insertSorted(new Point(fromX, fromY));
		newPoints.insertSorted(new Point(toX, toY));

		return newPoints;
	}

}
