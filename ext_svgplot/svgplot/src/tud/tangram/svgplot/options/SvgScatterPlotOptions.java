package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.trendline.TrendLineAlgorithm;
import tud.tangram.svgplot.styles.GridStyle;

public class SvgScatterPlotOptions extends SvgGridOptions {

	public PointListList points;
	public TrendLineAlgorithm trendLineAlgorithm;
	public boolean hideOriginalPoints;
	
	public SvgScatterPlotOptions(SvgPlotOptions options) {
		super(options);
		points = options.getPoints();
		
		trendLineAlgorithm = options.getTrendLineAlgorithm();
		
		if(gridStyle == null)
			gridStyle = GridStyle.NONE;
		
		hideOriginalPoints = trendLineAlgorithm != null && options.isHideOriginalPoints();
	}
}
