package tud.tangram.svgplot.styles;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color definitions, in the order they are used by default.
 */
public enum Color {
	BLUE(0, 74, 115, "blau"),
	ORANGE(255, 159, 0, "orange"),
	GREEN(0, 153, 96, "grün"),
	RED(255, 0, 0, "rot"),
	YELLOW(255, 255, 0, "gelb"),
	BROWN(140, 69, 19, "braun"),
	PINK(255, 0, 255, "pink"),
	PURPLE(169, 0, 255, "violett"),
	GRAY(128, 128, 128, "grau");
	
	private String rgbColor;
	private String name;

	private static final Map<String, Color> NAME_MAP = new HashMap<>();

	static final Logger log = LoggerFactory.getLogger(Color.class);

	static {
		for (Color color : Color.values()) {
			NAME_MAP.put(color.getName(), color);
		}
	}

	/**
	 * Constructor for colors.
	 * 
	 * @param r
	 *            red channel from 0-255
	 * @param g
	 *            green channel from 0-255
	 * @param b
	 *            blue channel from 0-255
	 * @param name
	 *            human readable color name
	 */
	private Color(int r, int g, int b, String name) {
		this.rgbColor = "rgb(" + r + "," + g + "," + b + ")";
		this.name = name;
	}

	/**
	 * Gets the {@link Color} belonging to the {@code name}.
	 * 
	 * @param name
	 *            the name of the {@link Color}
	 * @return the {@link Color} belonging to the {@code name}
	 */
	public static Color fromString(String name) {
		if (name == null)
			throw new NullPointerException("the name cannot be null");
		Color color = NAME_MAP.get(name);
		if (color == null)
			throw new IllegalArgumentException("Not a valid diagram type: " + name);
		return color;
	}

	/**
	 * Create a {@link LinkedHashSet} with the color names provided in order.
	 * Double names will be eliminated and non-existant names will be ignored
	 * outputting a warning.
	 * 
	 * @param names
	 * @return
	 */
	public static LinkedHashSet<Color> fromStrings(List<String> names) {
		LinkedHashSet<Color> colors = new LinkedHashSet<>();
		for (String name : names) {
			try {
				Color color = fromString(name);
				colors.add(color);
			} catch (Exception e) {
				log.warn("Die Farbe {} wird nicht unterstützt.", name);
			}
		}
		return colors;
	}

	/**
	 * Get the ith color in order of their definition.
	 * 
	 * @param i
	 * @return
	 */
	public static Color getColor(int i) {
		if (i >= 0 && i < Color.values().length)
			return Color.values()[i];
		return Color.values()[Color.values().length - 1];
	}

	/**
	 * Takes a {@link LinkedHashSet} of {@link Color Colors} and returns the set
	 * in the same order followed by the remaining defined {@link Color Colors}
	 * in the order of their definition.
	 * 
	 * @param definedColors
	 *            user defined {@link Color} order
	 * @return
	 */
	public static LinkedHashSet<Color> getColorOrder(LinkedHashSet<Color> definedColors) {
		LinkedHashSet<Color> colorOrder = new LinkedHashSet<>(definedColors);
		colorOrder.addAll(Arrays.asList(Color.values()));
		return colorOrder;
	}

	/**
	 * Get the rgb color string used for CSS.
	 * 
	 * @return
	 */
	public String getRgbColor() {
		return rgbColor;
	}

	/**
	 * Get the human readable name of the color.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
