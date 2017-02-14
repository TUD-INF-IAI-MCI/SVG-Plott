package tud.tangram.svgplot.options;

import tud.tangram.svgplot.coordinatesystem.Range;

public class SvgGridOptions extends SvgOptions {
	public Range xRange;
	public Range yRange;
	
	public SvgGridOptions(SvgPlotOptions options) {
		super(options);
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.xRange = options.getxRange();
		this.yRange = options.getyRange();
	}
	
}
