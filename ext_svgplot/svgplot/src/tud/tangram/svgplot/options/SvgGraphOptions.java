package tud.tangram.svgplot.options;

import java.util.List;

import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

public class SvgGraphOptions extends SvgGridOptions {
	public List<Function> functions;
	public String xLines;
	public String yLines;
	public String gnuplot;
	public IntegralPlotSettings integral;
	public String pts;
	public PointListList points;
	
	public SvgGraphOptions(SvgPlotOptions options) {
		super(options);
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		this.functions = options.getFunctions();
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
		this.gnuplot = options.getGnuplot();
		this.integral = options.getIntegral();
		this.pts = options.getPts();
		this.points = options.getPoints();
	}
}
