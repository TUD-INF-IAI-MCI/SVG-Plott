package tud.tangram.svgplot.data.trendline;

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

	/**
	 * Get the name of the algorithm. It is used for the creation of
	 * descriptions and referenced by the keys
	 * {@code desc.trendline_algorithm.<name>}.
	 * 
	 * @return
	 */
	public String getAlgorithmName();

	/**
	 * Get the algorithm params. Should follow the pattern
	 * (param1=...;param2=...) or be an empty {@link String} if there are no
	 * params.
	 * 
	 * @return
	 */
	public String getAlgorithmParams();
}
