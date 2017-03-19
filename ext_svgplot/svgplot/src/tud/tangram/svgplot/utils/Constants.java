package tud.tangram.svgplot.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import tud.tangram.svgplot.data.Point;

public class Constants {
	public static final String PROPERTIES_FILENAME = "svgplot.properties";
	public static final Locale locale = new Locale("de");
	public static final NumberFormat numberFormat = NumberFormat.getInstance(locale);
	public static final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
	public static final double STROKE_WIDTH = 0.5;
	public static final List<Integer> MARGIN = Collections.unmodifiableList(Arrays.asList(20, 10, 20, 10));
	/** List of letters for function naming */
	public static final List<String> FN_LIST = Collections
			.unmodifiableList(Arrays.asList("f", "g", "h", "i", "j", "k", "l", "m", "o", "p", "q", "r"));
	/** List of letters for point naming */
	public static final List<String> PN_LIST = Collections.unmodifiableList(
			Arrays.asList("A", "B", "C", "D", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "T"));
	public static final String SPACER_CSS_CLASS = "poi_symbol_bg";
	public static final DecimalFormat decimalFormat = getSvgDecimalFormat();
	/** The minimal distance of grid lines in mm */
	public static final int MIN_GRID_DISTANCE = 10;
	public static final Point titlePosition = new Point(Constants.MARGIN.get(3), Constants.MARGIN.get(0) + 10);
	
	// Used for double comparisons
	public static final double EPSILON = 1E-10;

	private Constants() {
	}

	private static final DecimalFormat getSvgDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("0.###");
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(dfs);
		return decimalFormat;
	}
}
