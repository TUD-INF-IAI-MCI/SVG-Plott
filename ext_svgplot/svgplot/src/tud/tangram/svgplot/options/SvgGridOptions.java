package tud.tangram.svgplot.options;

import tud.tangram.svgplot.coordinatesystem.Range;

public class SvgGridOptions extends SvgOptions {
	public Range xRange;
	public Range yRange;
	public boolean pi;
	
	public String xLines;
	public String yLines;
	
	public SvgGridOptions(SvgPlotOptions options) {
		super(options);
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.xRange = options.getxRange();
		this.yRange = options.getyRange();
		
		this.xRange.from = Math.min(0, this.xRange.from);
		this.xRange.to = Math.max(0, this.xRange.to);
		this.yRange.from = Math.min(0, this.yRange.from);
		this.yRange.to = Math.max(0, this.yRange.to);
		
		this.pi = options.isPi();
		
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
	}
	
}
