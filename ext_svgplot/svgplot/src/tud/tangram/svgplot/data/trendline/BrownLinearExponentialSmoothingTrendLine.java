package tud.tangram.svgplot.data.trendline;

import java.util.Iterator;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.utils.SvgTools;

public class BrownLinearExponentialSmoothingTrendLine implements TrendLineAlgorithm {

	private double alpha;
	private int forecast;

	public BrownLinearExponentialSmoothingTrendLine(double alpha, int forecast) {
		this.alpha = alpha;
		this.forecast = forecast;
	}

	@Override
	public PointList calculateTrendLine(PointList originalPoints) {
		Iterator<Point> it = originalPoints.iterator();

		if (!it.hasNext())
			return null;

		PointList newPoints = new PointList();
		newPoints.setName(originalPoints.getName() != null ? "Brown LES Trend Line: " + originalPoints.getName()
				: "Brown LES Trend Line");

		Point currentPoint = it.next();

		if (!it.hasNext())
			return null;

		double s1 = currentPoint.getY();
		double s2 = currentPoint.getY();

		double distance;
		double x = currentPoint.getX();

		do {
			currentPoint = it.next();
			double newX = currentPoint.getX();
			distance = newX - x;
			x = newX;

			double y = currentPoint.getY();
			s1 = getNewS1(y, s1);
			s2 = getNewS2(y, s1, s2);

			newPoints.insertSorted(new Point(x, getF(s1, s2, 1)));
		} while (it.hasNext());

		x += distance;

		for (int i = 2; i < forecast + 2; i++, x += distance) {
			newPoints.insertSorted(new Point(x, getF(s1, s2, i)));
		}

		return newPoints;
	}

	private double getNewS1(double y, double oldS1) {
		return alpha * y + (1 - alpha) * oldS1;
	}

	private double getNewS2(double y, double currentS1, double oldS2) {
		return alpha * currentS1 + (1 - alpha) * oldS2;
	}

	private double getF(double s1, double s2, int currentForecast) {
		double a = 2 * s1 - s2;
		double b = alpha / (1 - alpha) * (s1 - s2);
		return a + currentForecast * b;
	}

	@Override
	public String getAlgorithmName() {
		return "BrownLES";
	}

	@Override
	public String getAlgorithmParams() {
		return  "(alpha=" + SvgTools.format2svg(alpha) + ";forecast=" + forecast + ")";
	}
}
