package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.coordinatesystem.MetricAxis;
import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.legend.LegendFunctionLineItem;
import tud.tangram.svgplot.legend.LegendIntegralAreaItem;
import tud.tangram.svgplot.legend.LegendRenderer;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.Gnuplot;
import tud.tangram.svgplot.plotting.IntegralPlot;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;
import tud.tangram.svgplot.plotting.Plot;
import tud.tangram.svgplot.plotting.PlotList;
import tud.tangram.svgplot.utils.Constants;
import tud.tangram.svgplot.utils.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class SvgPlotPainter extends SvgPainter {

	private CoordinateSystem cs;
	private List<Function> functions;
	private Gnuplot gnuplot;
	private IntegralPlotSettings integral;
	private PlotList plotList;
	
	public SvgPlotPainter(CoordinateSystem cs, List<Function> functions, Gnuplot gnuplot, IntegralPlotSettings integral) {
		this.cs = cs;
		this.functions = functions;
		this.gnuplot = gnuplot;
		this.integral = integral;
	}
	
	@Override
	protected String getPainterName() {
		return "Plot Painter";
	}

	@Override
	protected HashMap<OutputDevice, String> getDeviceCss() {
		HashMap<OutputDevice, String> deviceCss = new HashMap<>();
		
		double width = 2 * Constants.STROKE_WIDTH;
		
		StringBuilder defaultCss = new StringBuilder();
		defaultCss.append(".integral { stroke: white; stroke-dasharray: none; stroke-width:" + 3 * width + "; }").append(System.lineSeparator());
		defaultCss.append(".integral-1 { fill: url(#diagonal_line1_PD); }").append(System.lineSeparator());
		defaultCss.append(".integral-2 { fill: white; }").append(System.lineSeparator());
		
		defaultCss.append("#plots { stroke-width: " + width + "; stroke-dasharray: " + width * 5 + ", " + width * 5 + "; }").append(System.lineSeparator());
		defaultCss.append("#plot-1 { stroke-dasharray: none; }").append(System.lineSeparator());
		defaultCss.append("#plot-2 { stroke-dasharray: " + width + ", " + width * 3 + "; }").append(System.lineSeparator());
		
		deviceCss.put(OutputDevice.Default, defaultCss.toString());
		
		StringBuilder screenHighContrastCss = new StringBuilder();
		screenHighContrastCss.append("#plots { stroke-width: 1.5; stroke-dasharray: none; stroke: yellow;}").append(System.lineSeparator());
		screenHighContrastCss.append("#plot-2 { stroke-dasharray: 2.0, 3.0; stroke: #00ffff;}").append(System.lineSeparator());
		screenHighContrastCss.append("#plot-3 { stroke-dasharray: 5.0, 5.0; stroke: #ff00ff;}").append(System.lineSeparator());
		
		screenHighContrastCss.append(".integral { stroke: black; stroke-dasharray: none; stroke-width:3.0; }").append(System.lineSeparator());
		screenHighContrastCss.append(".integral-1 { fill: url(#diagonal_line1_PD); fill-opacity: 0.5; }").append(System.lineSeparator());
		screenHighContrastCss.append(".integral-2 { fill: white; }").append(System.lineSeparator());
		
		deviceCss.put(OutputDevice.ScreenHighContrast, screenHighContrastCss.toString());
		
		return deviceCss;
	}

	@Override
	public void paintToSvgDocument(SvgDocument doc, Element viewbox, OutputDevice device) {
		super.paintToSvgDocument(doc, viewbox, device);
		Node plots = viewbox.appendChild(doc.createGroup("plots"));

		gnuplot.setSample(((MetricAxis)cs.xAxis).atomCount);
		// TODO why was this here?
		//gnuplot.setSample(1300);
		gnuplot.setXRange(cs.xAxis.getRange(), cs.pi);
		gnuplot.setYRange(cs.yAxis.getRange());

		// functions
		int i = 0;
		plotList = new PlotList(cs);
		for (Function function : functions) {
			Node graph = plots.appendChild(doc.createGroup("plot-" + ++i));
			Element path = (Element) graph.appendChild(doc.createElement("path"));
			path.setAttribute("clip-path", "url(#plot-area)");

			String points = "";
			
			Plot plot;
			
			try{
				plot = new Plot(function, gnuplot);
			}
			catch (Exception e) {
				System.out.println("Gnuplot exception");
				System.out.println(e);
				continue;
			}
				
			plot.Name = SvgTools.getFunctionName(i - 1);
			plotList.add(plot);
			for (List<Point> list : plot) {
				String op = "M";
				for (Point point : list) {
					points += op + cs.convertWithOffset(point) + " ";
					op = "L";
				}
			}
			path.setAttribute("d", points);
		}

		if (integral != null && integral.function1 >= 0
				&& plotList.size() > integral.function1) {

			Element parent = viewbox;
			Element a = doc.getChildElementById(viewbox, "axes");
			if (a != null) {
				Element integralContainer = doc.createGroup("integrals");
				viewbox.insertBefore(integralContainer, a);
				parent = integralContainer;
			}
			Plot p2 = null;
			if (integral.function2 >= 0 && plotList.size() > integral.function2
					&& integral.function2 != integral.function1)
				p2 = plotList.get(integral.function2);
			new IntegralPlot(cs).handlePlotIntegral(plotList.get(integral.function1), doc, parent,
					integral.xRange != null ? Math.max(integral.xRange.getFrom(), cs.xAxis.getRange().getFrom())
							: cs.xAxis.getRange().getFrom(),
					integral.xRange != null ? Math.min(integral.xRange.getTo(), cs.xAxis.getRange().getTo())
							: cs.xAxis.getRange().getTo(),
					p2);
		}
	}
	
	/**
	 * Get the plot list after calling {@link #paintToSvgDocument(SvgDocument, Element, OutputDevice) paintToSvgDocument}.
	 * @return the plot list
	 */
	public PlotList getPlotList() {
		return plotList;
	}

	@Override
	public void prepareLegendRenderer(LegendRenderer renderer, OutputDevice device, int priority) {
		super.prepareLegendRenderer(renderer, device, priority);
		
		int functionSymbolIndex = 0;
		for(Function function : functions) {
			renderer.add(new LegendFunctionLineItem(function, functionSymbolIndex, priority));
			functionSymbolIndex++;
		}
		
		if (integral != null && integral.function1 >= 0) {
			renderer.add(new LegendIntegralAreaItem(integral, cs, priority + 1));
		}
	}
	
	

}
