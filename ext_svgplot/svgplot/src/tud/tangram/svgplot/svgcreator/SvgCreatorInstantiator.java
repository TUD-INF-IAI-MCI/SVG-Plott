package tud.tangram.svgplot.svgcreator;

import tud.tangram.svgplot.options.SvgOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;

/**
 * This interface is used for instantiating an {@link SvgCreator}. Each subclass
 * should provide an instatiator.
 */
public interface SvgCreatorInstantiator {
	/**
	 * Instantiate the specific {@link SvgOptions} implementation and the
	 * specific {@link SvgCreator}.
	 * 
	 * @param rawOptions
	 *            the raw options
	 * @return the {@link SvgCreator} instance
	 */
	public SvgCreator instantiateCreator(SvgPlotOptions rawOptions);
}