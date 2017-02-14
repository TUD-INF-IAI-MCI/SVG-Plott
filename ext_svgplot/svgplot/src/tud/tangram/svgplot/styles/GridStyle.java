package tud.tangram.svgplot.styles;

/**
 * Style class for setting whether to show x and y grid lines.
 */
public class GridStyle {
	public boolean showX = true;
	public boolean showY = true;
	
	/**
	 * Constructor for showing the whole grid.
	 */
	public GridStyle() {}
	
	/**
	 * Set the whole grid on or off.
	 */
	public GridStyle(boolean show) {
		this.showX = show;
		this.showY = show;
	}
	
	/**
	 * Set the grid lines independently.
	 * @param showX
	 * @param showY
	 */
	public GridStyle(boolean showX, boolean showY) {
		this.showX = showX;
		this.showY = showY;
	}
}
