package tud.tangram.svgplot.options;

import java.util.List;

import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.XType;
import tud.tangram.svgplot.styles.GridStyle;

public class SvgGridOptions extends SvgOptions {
	public Range xRange;
	public Range yRange;
	public boolean pi;
	
	public String xLines;
	public String yLines;
	
	public String xUnit;
	public String yUnit;
	
	public List<String> xCategories;
	
	public GridStyle gridStyle;
	public String showDoubleAxes;
	
	public boolean dotsBorderless;
	
	public DiagramType diagramType;
	
	public SvgGridOptions(SvgPlotOptions options) {
		super(options);
		createFromSvgPlotOptions(options);
	}
	
	private void createFromSvgPlotOptions(SvgPlotOptions options) {
		xRange = options.getxRange();
		yRange = options.getyRange();
		
		this.pi = options.isPi();
		
		this.xLines = options.getxLines();
		this.yLines = options.getyLines();
		
		this.xUnit = options.getxUnit();
		this.yUnit = options.getyUnit();
		
		if(options.getPoints().getXType() == XType.CATEGORIAL)
			this.xCategories = ((CategorialPointListList) options.getPoints()).getCategoryNames();
		
		this.gridStyle = GridStyle.fromStrings(options.getShowHorizontalGrid(), options.getShowVerticalGrid());
		this.showDoubleAxes = options.getShowDoubleAxes();
		
		this.dotsBorderless = options.isPointsBorderless();
		
		this.diagramType = options.getDiagramType();
	}
	
}
