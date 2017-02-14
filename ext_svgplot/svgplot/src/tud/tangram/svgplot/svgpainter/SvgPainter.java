package tud.tangram.svgplot.svgpainter;

import java.util.HashMap;

import org.w3c.dom.Element;

import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.xml.SvgDocument;

public abstract class SvgPainter {
	public SvgPainter() {
		setupDeviceCss();
	}
	
	/**
	 * Here the CSS for the specific output devices is specified.
	 * {@link OutputDevice#Default} should be used for data which is not
	 * specific for any device and will always be added, while the other options
	 * can be used for overwriting the values.
	 */
	protected HashMap<OutputDevice, String> deviceCss;

	/**
	 * The name of the painter. For example: "Scatter plot painter"
	 */
	protected String painterName;

	/**
	 * Output the default CSS joint with the device CSS. Also inserts basic
	 * comments.
	 * 
	 * @param outputDevice
	 *            | The device, for which the CSS shall be selected.
	 * @return Complete CSS string including comments
	 */
	public String composeDeviceSpecificCss(OutputDevice outputDevice) {
		StringBuilder deviceCssString = new StringBuilder();
		if (deviceCss.containsKey(OutputDevice.Default)) {
			deviceCssString.append(System.lineSeparator());
			deviceCssString.append("// Default CSS for " + painterName);
			deviceCssString.append(System.lineSeparator());
			deviceCssString.append(deviceCss.get(OutputDevice.Default));
			deviceCssString.append(System.lineSeparator());
		}
		if (deviceCss.containsKey(outputDevice)) {
			deviceCssString.append(System.lineSeparator());
			deviceCssString.append("// Device specific CSS (" + outputDevice.toString() + ")  for " + painterName);
			deviceCssString.append(System.lineSeparator());
			deviceCssString.append(deviceCss.get(outputDevice));
			deviceCssString.append(System.lineSeparator());
		}
		return deviceCssString.toString();
	}
	
	/**
	 * Setup the device specific CSS. Should populate {@link SvgPainter#deviceCss}.
	 */
	protected abstract void setupDeviceCss();
	
	protected abstract void paintToSvgDocument(SvgDocument doc, Element viewbox);
	
	protected abstract void paintToSvgLegend(SvgDocument legend);
}
