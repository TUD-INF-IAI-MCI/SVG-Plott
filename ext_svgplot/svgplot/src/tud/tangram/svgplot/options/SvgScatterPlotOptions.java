package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.PointListList;

public class SvgScatterPlotOptions extends SvgGridOptions {

	public PointListList points;
	
	public SvgScatterPlotOptions(SvgPlotOptions options) {
		super(options);
		points = options.getPoints();
	}
}
