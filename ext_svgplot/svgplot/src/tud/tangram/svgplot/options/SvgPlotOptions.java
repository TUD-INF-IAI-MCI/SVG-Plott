package tud.tangram.svgplot.options;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.CsvParser;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.trendline.BrownLinearExponentialSmoothingTrendLine;
import tud.tangram.svgplot.data.trendline.ExponentialSmoothingTrendline;
import tud.tangram.svgplot.data.trendline.LinearRegressionTrendLine;
import tud.tangram.svgplot.data.trendline.MovingAverageTrendline;
import tud.tangram.svgplot.data.trendline.TrendLineAlgorithm;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

@Parameters(separators = "=", resourceBundle = "Bundle")
public class SvgPlotOptions {

	@Parameter(names = { "--as", "--autoscale" }, descriptionKey = "param.autoscale")
	private boolean autoScale = false;

	public boolean hasAutoScale() {
		return autoScale;
	}

	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
	}

	@Parameter(names = { "--diagramtype", "--dt" }, descriptionKey = "param.diagramtype", required = true)
	private DiagramType diagramType;

	public DiagramType getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}

	@Parameter(names = { "--device", "-d" }, descriptionKey = "param.device")
	private OutputDevice outputDevice = OutputDevice.Default;

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public void setOutputDevice(OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}

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
	private Range xRange;

	public Range getxRange() {
		return xRange;
	}

	public void setxRange(Range xRange) {
		this.xRange = xRange;
	}

	@Parameter(names = { "--yrange", "-y" }, descriptionKey = "param.yrange")
	private Range yRange;

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

	@Parameter(names = { "--points", "--pts" }, descriptionKey = "param.points")
	private String pts;

	public String getPts() {
		return pts;
	}

	public void setPts(String pts) {
		this.pts = pts;
	}

	@Parameter(names = { "--csvpath", "--csv" }, descriptionKey = "param.csvpath")
	private String csvPath = null;

	public String getCsvPath() {
		return csvPath;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	@Parameter(names = { "--csvorientation", "--csvo" }, descriptionKey = "param.csvorientation")
	private CsvOrientation csvOrientation = CsvOrientation.HORIZONTAL;

	public CsvOrientation getCsvOrientation() {
		return csvOrientation;
	}

	public void setCsvOrientation(CsvOrientation csvOrientation) {
		this.csvOrientation = csvOrientation;
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

	@Parameter(names = { "--hgrid",
			"--horizontalgrid" }, descriptionKey = "param.showhorizontalgrid", validateWith = OnOffParameterValidator.class)
	private String showHorizontalGrid;

	public String getShowHorizontalGrid() {
		return showHorizontalGrid;
	}

	public void setShowHorizontalGrid(String showHorizontalGrid) {
		this.showHorizontalGrid = showHorizontalGrid;
	}

	@Parameter(names = { "--vgrid",
			"--verticalgrid" }, descriptionKey = "param.showverticalgrid", validateWith = OnOffParameterValidator.class)
	private String showVerticalGrid;

	public String getShowVerticalGrid() {
		return showVerticalGrid;
	}

	public void setShowVerticalGrid(String showVerticalGrid) {
		this.showVerticalGrid = showVerticalGrid;
	}

	@Parameter(names = { "--daxes",
			"--doubleaxes" }, descriptionKey = "param.showdoubleaxes", validateWith = OnOffParameterValidator.class)
	private String showDoubleAxes;

	public String getShowDoubleAxes() {
		return showDoubleAxes;
	}

	public void setShowDoubleAxes(String showDoubleAxes) {
		this.showDoubleAxes = showDoubleAxes;
	}

	@Parameter(names = { "--linepoints",
			"--lp" }, descriptionKey = "param.showlinepoints", validateWith = OnOffParameterValidator.class)
	private String showLinePoints;

	public String getShowLinePoints() {
		return showLinePoints;
	}

	public void setShowLinePoints(String showLinePoints) {
		this.showLinePoints = showLinePoints;
	}
	
	@Parameter(names = { "--pointsborderless", "--dbl"}, descriptionKey = "param.pointsborderless")
	private boolean pointsBorderless = false;
	
	public boolean isPointsBorderless() {
		return pointsBorderless;
	}
	
	public void setPointsBorderless(boolean pointsBorderless) {
		this.pointsBorderless = pointsBorderless;
	}

	@Parameter(names = "--trendline", descriptionKey = "param.trendline", variableArity = true)
	public List<String> trendLine = new ArrayList<>();

	public List<String> getTrendLine() {
		return trendLine;
	}

	public void setTrendLine(List<String> trendLine) {
		this.trendLine = trendLine;
	}

	private TrendLineAlgorithm trendLineAlgorithm;

	public TrendLineAlgorithm getTrendLineAlgorithm() {
		return trendLineAlgorithm;
	}

	@Parameter(names = { "--hideoriginalpoints", "--hop" }, descriptionKey = "param.hideoriginalpoints")
	private boolean hideOriginalPoints = false;

	public boolean isHideOriginalPoints() {
		return hideOriginalPoints;
	}

	public void setHideOriginalPoints(boolean hideOriginalPoints) {
		this.hideOriginalPoints = hideOriginalPoints;
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
			else if (forType.equals(CsvOrientation.class))
				return CsvOrientation.CsvOrientationConverter.class;
			else if (forType.equals(DiagramType.class))
				return DiagramType.DiagramTypeConverter.class;
			else
				return null;
		}
	}

	public void finalizeOptions() {
		boolean xRangeSpecified = true;
		boolean yRangeSpecified = true;
		if (xRange == null) {
			xRangeSpecified = false;
			xRange = new Range(-8, 8);
		}
		if (yRange == null) {
			yRangeSpecified = false;
			yRange = new Range(-8, 8);
		}
		if (csvPath != null) {
			try {
				CsvParser parser = new CsvParser(new FileReader(csvPath), ',', '"');
				if (csvOrientation == CsvOrientation.VERTICAL)
					points = parser.parseAsScatterDataVerticalRows();
				else
					points = parser.parseAsScatterDataHorizontalRows();

			} catch (IOException e) {
				points = (new PointListList.Converter()).convert(pts);
			}
		} else {
			points = (new PointListList.Converter()).convert(pts);
		}
		points.updateMinMax();
		if (autoScale && !points.isEmpty() && points.hasValidMinMaxValues()) { // TODO
																				// add
																				// margin
																				// option
			double xPointRangeMargin = 0; // 0.01 * (points.getMaxX() -
											// points.getMinX());
			double yPointRangeMargin = 0; // 0.01 * (points.getMaxY() -
											// points.getMinY());
			if (!xRangeSpecified || xRange.getFrom() > points.getMinX())
				xRange.setFrom(points.getMinX() - xPointRangeMargin);
			if (!xRangeSpecified || xRange.getTo() < points.getMaxX())
				xRange.setTo(points.getMaxX() + xPointRangeMargin);
			if (!yRangeSpecified || yRange.getFrom() > points.getMinY())
				yRange.setFrom(points.getMinY() - yPointRangeMargin);
			if (!yRangeSpecified || yRange.getTo() < points.getMaxY())
				yRange.setTo(points.getMaxY() + yPointRangeMargin);
		}
		parseTrendLine();
	}

	private void parseTrendLine() {
		if (trendLine.isEmpty() || trendLine.get(0) == null)
			return;

		String trendlineAlgorithm = trendLine.get(0).toLowerCase();
		int paramCount = trendLine.size() - 1;

		switch (trendlineAlgorithm) {
		case "movingaverage":
			int n = paramCount == 0 ? 1 : Integer.valueOf(trendLine.get(1));
			this.trendLineAlgorithm = new MovingAverageTrendline(n);
			break;

		case "exponentialsmoothing":
			double expAlpha = paramCount == 0 ? 0.3 : Double.valueOf(trendLine.get(1));
			this.trendLineAlgorithm = new ExponentialSmoothingTrendline(expAlpha);
			break;

		case "brownles":
			double brownAlpha = paramCount == 0 ? 0.2 : Double.valueOf(trendLine.get(1));
			int forecast = paramCount < 2 ? 5 : Integer.valueOf(trendLine.get(2));
			this.trendLineAlgorithm = new BrownLinearExponentialSmoothingTrendLine(brownAlpha, forecast);
			break;

		case "linearregression":
			this.trendLineAlgorithm = new LinearRegressionTrendLine(xRange.getFrom(), xRange.getTo());
			break;

		default:
			return;
		}
	}

	public static class OnOffParameterValidator implements IParameterValidator {

		@Override
		public void validate(String name, String value) throws ParameterException {
			String lowerCaseValue = value.toLowerCase();
			if ("on".equals(lowerCaseValue) || "off".equals(lowerCaseValue))
				return;
			ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
			throw new ParameterException(bundle.getString("error.onoffparameter"));
		}

	}
}
