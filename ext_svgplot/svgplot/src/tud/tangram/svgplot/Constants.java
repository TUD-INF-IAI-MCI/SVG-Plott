package tud.tangram.svgplot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Constants {
	final public static Locale locale = new Locale("de");
	final public static double strokeWidth = 0.5;
	final public static int[] margin = { 20, 10, 20, 10 };
	/** List of letters for function naming */
	final public static String[] fnList = new String[] { "f", "g", "h", "i", "j", "k", "l", "m", "o", "p", "q", "r" };
	/** List of letters for point naming */
	final public static String[] pnList = new String[] { "A", "B", "C", "D", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "T" };
	final public static String spacerCssClass = "poi_symbol_bg";
	final public static DecimalFormat decimalFormat = new DecimalFormat("0.###", new DecimalFormatSymbols(locale));
	/** The minimal distance of grid lines in mm */
	final public static int minGridDistance = 10;
}
