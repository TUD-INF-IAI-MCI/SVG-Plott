package tud.tangram.svgplot.options;

import java.util.List;

import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

public class SvgGraphOptions extends SvgOptions {
	public List<Function> functions;
	public Range xRange;
	public Range yRange;
	public boolean pi;
	public String xLines;
	public String yLines;
	public String gnuplot;
	public IntegralPlotSettings integral;
	public String pts;
	public PointListList points;
	
	public SvgGraphOptions(SvgPlotOptions options) {
		createFromSvgPlotOptions(options);
	}
	
	public void createFromSvgPlotOptions(SvgPlotOptions options) {
		super.createFromSvgPlotOptions(options);
		this.functions = options.getFunctions();
		this.xRange = options.getxRange();
		this.yRange = options.getyRange();
		this.pi = options.isPi();
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
		this.gnuplot = options.getGnuplot();
		this.integral = options.getIntegral();
		this.pts = options.getPts();
		this.points = options.getPoints();
	}
}
