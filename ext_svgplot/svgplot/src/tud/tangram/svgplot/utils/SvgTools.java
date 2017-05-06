package tud.tangram.svgplot.utils;

import java.text.MessageFormat;

import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;

public class SvgTools {
	private SvgTools() {
	}
	
	/**
	 * Format a number for svg usage according to the constant decimalFormat
	 * 
	 * @param value
	 * @return
	 */
	public static String format2svg(double value) {
		return Constants.decimalFormat.format(value);
	}

	/**
	 * Formats an additional Name of an object. Checks if the name is set. If
	 * name is set, the name is packed into brackets and prepend with an
	 * whitespace
	 * 
	 * @param name
	 *            | optional name of an object or NULL
	 * @return empty string or the name of the object packed into brackets and
	 *         prepend with a whitespace e.g. ' (optional name)'
	 */
	public static String formatName(String name) {
		return (name == null || name.isEmpty()) ? "" : " (" + name + ")";
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value
	 * @return a localized string for the given PropertyResourceBundle key,
	 *         filled with the set arguments
	 */
	public static String translate(String key, Object... arguments) {
		return MessageFormat.format(Constants.bundle.getString(key), arguments);
	}

	/**
	 * Try to translate a key in the localized version defined in the
	 * PropertyResourceBundle file. This function is optimized for differing
	 * sentences depending on the amount of results.
	 * 
	 * @param key
	 *            | PropertyResourceBundle key
	 * @param arguments
	 *            | arguments that should fill the placeholder in the returned
	 *            PropertyResourceBundle value. The last argument gives the
	 *            count and decide which value will be returned.
	 * @return a localized string for the given amount depending
	 *         PropertyResourceBundle key, filled with the set arguments
	 */
	public static String translateN(String key, Object... arguments) {
		int last = (int) arguments[arguments.length - 1];
		String suffix = last == 0 ? "_0" : last == 1 ? "_1" : "_n";
		return translate(key + suffix, arguments);
	}

	/**
	 * Formats a Point that it is optimized for textual output and packed into
	 * the caption with brackets. E.g. E(x | y)
	 * 
	 * @param cs
	 *            the coordinate system
	 * @param point
	 *            The point that should be transformed into a textual
	 *            representation
	 * @param cap
	 *            The caption string without brackets
	 * @return formated string for the point with '/' as delimiter if now
	 *         caption is set, otherwise packed in the caption with brackets and
	 *         the '|' as delimiter
	 */
	public static String formatForText(CoordinateSystem cs, Point point, String cap) {
		String p = cs.formatX(point.getX()) + " | " + cs.formatY(point.getY());
		String capTrimmed = cap.trim();
		return (capTrimmed != null && !capTrimmed.isEmpty()) ? capTrimmed + "(" + p + ")" : p;
	}

	/**
	 * Try to translate the function index into a continuous literal starting
	 * with the char 'f'. If the given index is not valid it returns the name as
	 * a combination of 'f' + the given number.
	 * 
	 * @param f
	 *            | the index if the function
	 * @return a literal representation to the given function index e.g. 'f',
	 *         'g', 'h' or 'f1000'.
	 */
	public static String getFunctionName(int f) {
		if (f < 0 || f > (Constants.FN_LIST.size() - 1))
			return "f" + f;
		return Constants.FN_LIST.get(f);
	}

	public static String getPointName(int p) {
		if (p < 0 || p > (Constants.PN_LIST.size() - 1))
			return "P" + p;
		return Constants.PN_LIST.get(p);
	}
}
