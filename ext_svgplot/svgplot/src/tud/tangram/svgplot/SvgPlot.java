package tud.tangram.svgplot;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;
import tud.tangram.svgplot.options.SvgGraphOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.options.SvgScatterPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgGraphCreator;
import tud.tangram.svgplot.svgcreator.SvgScatterPlotCreator;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 * 
 */
public class SvgPlot {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SvgPlotOptions options = new SvgPlotOptions();
		JCommander jc = new JCommander(options);
		jc.addConverterFactory(new SvgPlotOptions.StringConverterFactory());

		try {
			final Properties properties = new Properties();
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream("svgplot.properties"));
			properties.load(stream);
			stream.close();

			jc.setDefaultProvider(new IDefaultProvider() {
				@Override
				public String getDefaultValueFor(String optionName) {
					return properties.getProperty(optionName.replaceFirst("^-+", ""));
				}
			});
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("\\s*-[^-][^=:,]+")) {
				args[i] = "\\" + args[i].trim();
			}
		}

		jc.parse(args);

		if (options.getHelp()) {
			jc.setProgramName("java -jar svgplot.jar");
			jc.usage();
			return;
		}
		
		options.finalizeOptions();
		
		//SvgGraphOptions graphOptions = new SvgGraphOptions(options);
		
		//SvgGraphCreator creator = new SvgGraphCreator(graphOptions);
		
		SvgScatterPlotOptions scatterPlotOptions = new SvgScatterPlotOptions(options);
		SvgScatterPlotCreator creator = new SvgScatterPlotCreator(scatterPlotOptions);
		
		try {
			creator.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
