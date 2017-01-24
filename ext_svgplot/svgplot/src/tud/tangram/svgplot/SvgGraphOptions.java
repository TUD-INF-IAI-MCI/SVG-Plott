package tud.tangram.svgplot;

import java.io.File;
import java.util.List;

import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

public class SvgGraphOptions {
	public List<Function> functions;
	public Point size;
	public Range xRange;
	public Range yRange;
	public boolean pi;
	public String xLines;
	public String yLines;
	public String title;
	public String gnuplot;
	public String css;
	public File output;
	public IntegralPlotSettings integral;
	public String pts;
	public PointListList points;
	
	public SvgGraphOptions(SvgPlotOptions options) {
		createFromSvgPlotOptions(options);
	}
	
	public void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.functions = options.getFunctions();
		this.size = options.getSize();
		this.xRange = options.getxRange();
		this.yRange = options.getyRange();
		this.pi = options.isPi();
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
		this.title = options.getTitle();
		this.gnuplot = options.getGnuplot();
		this.css = options.getCss();
		this.output = options.getOutput();
		this.integral = options.getIntegral();
		this.pts = options.getPts();
		this.points = options.getPoints();
	}
}
