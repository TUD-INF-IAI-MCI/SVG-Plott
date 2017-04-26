package tud.tangram.svgplot.data.trendline;

import java.util.Iterator;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;

public class ExponentialSmoothingTrendline implements TrendLineAlgorithm {

	private double alpha;

	public ExponentialSmoothingTrendline(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public PointList calculateTrendLine(PointList originalPoints) {
		Iterator<Point> it = originalPoints.iterator();
		if (!it.hasNext())
			return null;

		PointList newPoints = new PointList();
		newPoints.setName(originalPoints.getName() != null
				? "Exponential Smoothing Trend Line: " + originalPoints.getName() : "Exponential Smoothing Trend Line");

		Point currentPoint = it.next();
		double sum = currentPoint.getY();

		newPoints.insertSorted(new Point(currentPoint.getX(), sum));

		while (it.hasNext()) {
			currentPoint = it.next();
			sum = (1 - alpha) * sum + alpha * currentPoint.getY();

			newPoints.insertSorted(new Point(currentPoint.getX(), sum));
		}

		return newPoints;
	}

}
