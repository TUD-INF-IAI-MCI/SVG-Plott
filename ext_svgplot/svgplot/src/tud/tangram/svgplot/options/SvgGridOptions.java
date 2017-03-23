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
		
		this.pi = options.isPi();
		
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
	}
	
}
