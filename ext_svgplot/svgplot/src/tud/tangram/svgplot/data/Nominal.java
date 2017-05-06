package tud.tangram.svgplot.data;

import org.w3c.dom.Element;

/**
 * A nominal point holding the same data as the {@link Point} class, having different behaviour.
 */
public class Nominal extends Point{

	public Nominal(double x, double y, String name, Element symbol) {
		super(x, y, name, symbol);
	}

	public Nominal(double x, double y, String name) {
		super(x, y, name);
	}
	
	public Nominal(Nominal nominal) {
		super(nominal);
	}

	@Override
	public String x() {
		return name;
	}	
}
