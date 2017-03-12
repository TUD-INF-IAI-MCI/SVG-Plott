package tud.tangram.svgplot.options;

import java.io.File;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.utils.SvgTools;

public abstract class SvgOptions {
	public String css;
	public File output;
	public String title;
	public String legendTitle;
	public String descTitle;
	public Point size;
	public OutputDevice outputDevice;

	public SvgOptions(SvgPlotOptions options) {
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.css = options.getCss();
		this.output = options.getOutput();
		this.title = options.getTitle();
		this.legendTitle = SvgTools.translate("legend") + ": " + options.getTitle();
		this.descTitle = SvgTools.translate("desc") + ": " + options.getTitle();
		this.size = options.getSize();
		this.outputDevice = options.getOutputDevice();
	};
}
