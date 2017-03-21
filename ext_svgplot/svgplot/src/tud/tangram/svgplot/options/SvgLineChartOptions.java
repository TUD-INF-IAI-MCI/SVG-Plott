package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.PointListList;

public class SvgLineChartOptions extends SvgGridOptions {
	
	public PointListList points;
	
	public SvgLineChartOptions(SvgPlotOptions options) {
		super(options);
		points = options.getPoints();
	}
}
