package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

public class SvgBarChartOptions extends SvgGridOptions {

	public CategorialPointListList points;
	public BarAccumulationStyle barAccumulationStyle;
	
	public SvgBarChartOptions(SvgPlotOptions options) {
		super(options);
		this.points = (CategorialPointListList) options.getPoints();
		this.barAccumulationStyle = options.getBarAccumulationStyle();
	}

}
