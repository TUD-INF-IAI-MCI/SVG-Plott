package tud.tangram.svgplot.options;

import java.io.File;

import tud.tangram.svgplot.coordinatesystem.Point;

public abstract class SvgOptions {
	public String css;
	public File output;
	public String title;
	public Point size;

	public SvgOptions(SvgPlotOptions options) {
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.css = options.getCss();
		this.output = options.getOutput();
		this.title = options.getTitle();
		this.size = options.getSize();
	};
}
