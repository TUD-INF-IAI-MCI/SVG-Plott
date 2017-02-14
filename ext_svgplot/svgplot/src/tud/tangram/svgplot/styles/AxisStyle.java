package tud.tangram.svgplot.styles;

/**
 * Style class for a single axis. Allows to set whether the axis line, the
 * arrow, the tics and the label are to be shown or not.
 */
public class AxisStyle {
	public boolean arrow = true;
	public boolean tics = true;
	public boolean axis = true;
	public boolean label = true;

	/**
	 * Constructor for showing the whole axis.
	 */
	public AxisStyle() {
	}

	/**
	 * Constructor allowing to set whether to show the whole axis or nothing.
	 * 
	 * @param show
	 */
	public AxisStyle(boolean show) {
		this.arrow = show;
		this.axis = show;
		this.tics = show;
		this.label = show;
	}

	/**
	 * Set whether to show axis, arrow, label and tics.
	 * 
	 * @param axis
	 * @param arrow
	 * @param tics
	 * @param label
	 */
	public AxisStyle(boolean axis, boolean arrow, boolean tics, boolean label) {
		this.axis = axis;
		this.arrow = arrow;
		this.tics = tics;
		this.label = label;
	}
}
