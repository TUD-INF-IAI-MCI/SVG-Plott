package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.PointListList;

public class SvgMultiScatterPlotOptions extends SvgGridOptions {

	public PointListList points;
	
	public SvgMultiScatterPlotOptions(SvgPlotOptions options) {
		super(options);
		points = options.getPoints();
	}
}
