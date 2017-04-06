package tud.tangram.svgplot.data;

/**
 * A nominal date consisting of a name and value.
 */
public class Nominal implements Comparable<Nominal> {

	private final String name;
	private final double value;
	
	public Nominal(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Compare the values of two nominals.
	 */
	@Override
	public int compareTo(Nominal o) {
		if(this.value < o.getValue())
			return -1;
		if(this.value > o.getValue())
			return 1;
		return 0;
	}
	
	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

}
