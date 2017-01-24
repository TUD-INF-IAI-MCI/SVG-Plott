package tud.tangram.svgplot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.Parameter;

import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.PointListList;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

public class SvgPlotOptions {

	@Parameter(description = "functions")
	private List<Function> functions = new ArrayList<>();

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	@Parameter(names = { "--size", "-s" }, descriptionKey = "param.size")
	private Point size = new Point(210, 297);

	public Point getSize() {
		return size;
	}

	/**
	 * Page size in mm
	 * 
	 * @param size
	 */
	public void setSize(Point size) {
		this.size = size;
	}

	@Parameter(names = { "--xrange", "-x" }, descriptionKey = "param.xrange")
	private Range xRange = new Range(-8, 8);

	public Range getxRange() {
		return xRange;
	}

	public void setxRange(Range xRange) {
		this.xRange = xRange;
	}

	@Parameter(names = { "--yrange", "-y" }, descriptionKey = "param.yrange")
	private Range yRange = new Range(-8, 8);

	public Range getyRange() {
		return yRange;
	}

	public void setyRange(Range yRange) {
		this.yRange = yRange;
	}

	@Parameter(names = { "--pi", "-p" }, descriptionKey = "param.pi")
	private boolean pi = false;

	public boolean isPi() {
		return pi;
	}

	public void setPi(boolean pi) {
		this.pi = pi;
	}

	@Parameter(names = { "--xlines" }, descriptionKey = "param.xlines")
	private String xLines = null;

	public String getxLines() {
		return xLines;
	}

	public void setxLines(String xLines) {
		this.xLines = xLines;
	}

	@Parameter(names = { "--ylines" }, descriptionKey = "param.ylines")
	private String yLines = null;

	public String getyLines() {
		return yLines;
	}

	public void setyLines(String yLines) {
		this.yLines = yLines;
	}

	@Parameter(names = { "--title", "-t" }, descriptionKey = "param.title")
	private String title = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Parameter(names = { "--gnuplot", "-g" }, descriptionKey = "param.gnuplot")
	private String gnuplot = null;
	
	public String getGnuplot() {
		return this.gnuplot;
	}

	@Parameter(names = { "--css", "-c" }, descriptionKey = "param.css")
	private String css = null;

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	/** Output path */
	@Parameter(names = { "--output", "-o" }, descriptionKey = "param.output")
	private File output = null;

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	@Parameter(names = { "--help", "-h", "-?" }, help = true, descriptionKey = "param.help")
	private boolean help;
	
	public boolean getHelp() {
		return help;
	}

	@Parameter(names = { "--integral", "-i" }, descriptionKey = "param.integral")
	private IntegralPlotSettings integral;

	public IntegralPlotSettings getIntegral() {
		return integral;
	}

	public void setIntegral(IntegralPlotSettings integral) {
		this.integral = integral;
	}

	// TODO: add parameter for scatter plot file
	// parameter for marking some points
	@Parameter(names = { "--points", "--pts" }, descriptionKey = "param.points")
	private String pts;

	public String getPts() {
		return pts;
	}

	public void setPts(String pts) {
		this.pts = pts;
	}

	/**
	 * interpreted List of list of points parsed from the 'pts' property
	 */
	private PointListList points;

	/**
	 * interpreted List of list of points parsed from the 'pts' property
	 */
	public PointListList getPoints() {
		return points;
	}

	/**
	 * interpreted List of list of points parsed from the 'pts' property
	 */
	public void setPoints(PointListList points) {
		this.points = points;
	}

	/**
	 * Returns a converter for the special class-types of this project for
	 * JCommander interpretation.
	 */
	public static class StringConverterFactory implements IStringConverterFactory {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<? extends IStringConverter<?>> getConverter(Class forType) {
			if (forType.equals(Point.class))
				return Point.Converter.class;
			else if (forType.equals(Range.class))
				return Range.Converter.class;
			else if (forType.equals(Function.class))
				return Function.Converter.class;
			else if (forType.equals(PointListList.class))
				return PointListList.Converter.class;
			else if (forType.equals(IntegralPlotSettings.class))
				return IntegralPlotSettings.Converter.class;
			else
				return null;
		}
	}
	
	public void finalizeOptions() {
		this.points = (new PointListList.Converter()).convert(this.pts);
		if (this.points != null) {
			if (this.xRange.from > this.points.minX)
				this.xRange.from = this.points.minX * 1.05;
			if (this.xRange.to < this.points.maxX)
				this.xRange.to = this.points.maxX * 1.05;
			if (this.yRange.from > this.points.minY)
				this.yRange.from = this.points.minY * 1.05;
			if (this.yRange.to < this.points.maxY)
				this.yRange.to = this.points.maxY * 1.05;
		}
	}
}