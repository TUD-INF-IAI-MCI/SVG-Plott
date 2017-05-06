package tud.tangram.svgplot.options;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.IStringConverter;

import tud.tangram.svgplot.svgcreator.SvgBarChartCreator;
import tud.tangram.svgplot.svgcreator.SvgCreator;
import tud.tangram.svgplot.svgcreator.SvgCreatorInstantiator;
import tud.tangram.svgplot.svgcreator.SvgGraphCreator;
import tud.tangram.svgplot.svgcreator.SvgLineChartCreator;
import tud.tangram.svgplot.svgcreator.SvgScatterPlotCreator;

/**
 * Special enum used for retrieving the desired plot from the command line and
 * also for retrieving the appropriate {@link SvgCreator} and {@link SvgOptions}
 * classes.
 * 
 * @author mic
 *
 */
public enum DiagramType {
	/**
	 * All the enum values together with their instantiators and synonyms.
	 */
	FunctionPlot(SvgGraphCreator.INSTANTIATOR, "fp", "function", "f", "functions"),
	ScatterPlot(SvgScatterPlotCreator.INSTANTIATOR, "scatter", "scatterplot", "s", "sp", "point", "points", "pointchart"),
	LineChart(SvgLineChartCreator.INSTANTIATOR, "lc", "linechart", "l", "lines", "line"),
	BarChart(SvgBarChartCreator.INSTANTIATOR, "bc", "b", "barchart", "bar", "bars");

	/**
	 * A map for easy retrieval of the right {@link DiagramType} belonging to
	 * the {@link #synonyms}.
	 */
	private static final Map<String, DiagramType> SYNONYM_MAP = new HashMap<>();
	static {
		for (DiagramType diagramType : DiagramType.values()) {
			SYNONYM_MAP.put(diagramType.name().toLowerCase(), diagramType);
			for (String synonym : diagramType.getSynonyms()) {
				SYNONYM_MAP.put(synonym.toLowerCase(), diagramType);
			}
		}
	}

	/**
	 * Stores the synonyms for each plot type.
	 */
	private final List<String> synonyms;

	private final SvgCreatorInstantiator instantiator;

	/**
	 * Constructor allowing to store the synonyms and instantiator for each plot
	 * type.
	 * 
	 * @param instantiator
	 *            an instantiator for the plot type
	 * @param synonyms
	 *            the synonyms for the plot type
	 */
	DiagramType(SvgCreatorInstantiator instantiator, String... synonyms) {
		this.synonyms = Arrays.asList(synonyms);
		this.instantiator = instantiator;
	}

	/**
	 * Get an {@link SvgCreator} instance fitting to create the current
	 * {@link DiagramType}.
	 * 
	 * @param rawOptions
	 *            the raw options used by the {@link SvgOptions} instance of the
	 *            {@link SvgCreator}
	 * @return
	 */
	public SvgCreator getInstance(SvgPlotOptions rawOptions) {
		return instantiator.instantiateCreator(rawOptions);
	}

	/**
	 * Gets the synonyms of the plot type
	 * 
	 * @return synonyms of the plot type
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * Gets the {@link DiagramType} belonging to the {@code name}.
	 * 
	 * @param name
	 *            the name of the {@link DiagramType}
	 * @return the {@link DiagramType} belonging to the {@code name}
	 */
	public static DiagramType fromString(String name) {
		if (name == null)
			throw new NullPointerException("the name cannot be null");
		DiagramType diagramType = SYNONYM_MAP.get(name);
		if (diagramType == null)
			throw new IllegalArgumentException("Not a valid diagram type: " + name);
		return diagramType;
	}

	/**
	 * Class for converting a {@link String} to the appropriate
	 * {@link DiagramType}.
	 */
	public static class DiagramTypeConverter implements IStringConverter<DiagramType> {

		@Override
		public DiagramType convert(String name) {
			return DiagramType.fromString(name);
		}
	}
}
