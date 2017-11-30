package tud.tangram.svgplot.options;

import java.util.List;

import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

public class SvgGraphOptions extends SvgGridOptions {
	public List<Function> functions;
	public String gnuplot;
	public IntegralPlotSettings integral;
	public String pts;
	public PointListList points;

	public SvgGraphOptions(SvgPlotOptions options) {
		super(options);
		createFromSvgPlotOptions(options);
	}

	private void createFromSvgPlotOptions(SvgPlotOptions options) {

		// Force the ranges to include zero in order to ensure that the axes
		// always are in the frame
		xRange.setFrom(Math.min(0, xRange.getFrom()));
		xRange.setTo(Math.max(0, xRange.getTo()));
		yRange.setFrom(Math.min(0, yRange.getFrom()));
		yRange.setTo(Math.max(0, yRange.getTo()));

		this.functions = options.getFunctions();
		this.gnuplot = options.getGnuplot();
		this.integral = options.getIntegral();
		this.pts = options.getPts();
		this.points = options.getPoints();
	}
}
