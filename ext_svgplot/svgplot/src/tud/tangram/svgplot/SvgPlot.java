package tud.tangram.svgplot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;

import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;
import tud.tangram.svgplot.utils.Constants;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 * 
 */
public class SvgPlot {
	
	static final Logger log = LoggerFactory.getLogger(SvgPlot.class);
	
	private SvgPlot(){
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SvgPlotOptions options = new SvgPlotOptions();
		JCommander jc = new JCommander(options);
		jc.addConverterFactory(new SvgPlotOptions.StringConverterFactory());
		
		try (final FileInputStream fis = new FileInputStream(Constants.PROPERTIES_FILENAME)){
			final Properties properties = new Properties();
			properties.load(fis);

			jc.setDefaultProvider(new IDefaultProvider() {
				@Override
				public String getDefaultValueFor(String optionName) {
					return properties.getProperty(optionName.replaceFirst("^-+", ""));
				}
			});
		} catch (FileNotFoundException e) {
			log.info("Keine Konfigurationsdatei {} gefunden", Constants.PROPERTIES_FILENAME);
			log.debug("Stacktrace", e);
		} catch (IOException e) {
			log.error("Datei {} konnte nicht geöffnet werden", Constants.PROPERTIES_FILENAME);
			log.debug("Stacktrace", e);
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
		
		// Create the SvgCreator that is responsible for rendering the selected diagram type
		SvgCreator creator = options.getDiagramType().getInstance(options);
		
		try {
			creator.run();
		} catch (Exception e) {
			log.error("Fehler beim Erstellen der SVG-Datei", e);
		}
	}



}
