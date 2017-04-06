package tud.tangram.svgplot.data.trendline;

import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;

public interface TrendLineAlgorithm {
	/**
	 * Calculate a trend line as a new point list.
	 * 
	 * @param originalPoints
	 *            the original points
	 * @return a new {@link PointList} if successful or null
	 */
	public PointList calculateTrendLine(PointList originalPoints);
}
