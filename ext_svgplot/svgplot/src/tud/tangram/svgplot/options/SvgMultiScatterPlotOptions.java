package tud.tangram.svgplot.options;

import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.styles.GridStyle;

public class SvgMultiScatterPlotOptions extends SvgGridOptions {

	public PointListList points;
	
	public SvgMultiScatterPlotOptions(SvgPlotOptions options) {
		super(options);
		points = options.getPoints();
		
		if(gridStyle == null)
			gridStyle = GridStyle.NONE;
	}
}
