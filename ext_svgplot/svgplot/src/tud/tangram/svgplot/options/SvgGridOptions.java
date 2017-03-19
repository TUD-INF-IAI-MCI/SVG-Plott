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
		xRange = options.getxRange();
		yRange = options.getyRange();
		
		xRange.setFrom(Math.min(0, xRange.getFrom()));
		xRange.setTo(Math.max(0, xRange.getTo()));
		yRange.setFrom(Math.min(0, yRange.getFrom()));
		yRange.setTo(Math.max(0, yRange.getTo()));
		
		this.pi = options.isPi();
		
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
	}
	
}
