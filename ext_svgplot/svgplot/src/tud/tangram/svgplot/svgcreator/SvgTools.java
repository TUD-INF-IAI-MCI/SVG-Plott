package tud.tangram.svgplot.svgcreator;

import java.text.MessageFormat;

import tud.tangram.svgplot.Constants;

public class SvgTools {
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
}
