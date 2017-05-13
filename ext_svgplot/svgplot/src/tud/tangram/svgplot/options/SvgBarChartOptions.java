package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

public class SvgBarChartOptions extends SvgGridOptions {

	public CategorialPointListList points;
	public BarAccumulationStyle barAccumulationStyle;
	public SortingType sortingType;
	public boolean descending;
	
	public SvgBarChartOptions(SvgPlotOptions options) {
		super(options);
		this.points = (CategorialPointListList) options.getPoints();
		this.barAccumulationStyle = options.getBarAccumulationStyle();
		this.sortingType = options.getSortingType();
		this.descending = options.isSortDescending();
	}

}
