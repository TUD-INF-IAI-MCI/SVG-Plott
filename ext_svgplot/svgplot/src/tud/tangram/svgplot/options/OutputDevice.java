package tud.tangram.svgplot.options;

public enum OutputDevice {
	Default,
	TigerEmbosser,
	ScreenColor,
	ScreenHighContrast,
	PinDevice,
	MicroCapsulePaper;
	
    public static OutputDevice fromString(String code) {
    	 
        for(OutputDevice device : OutputDevice.values()) {
            if(device.toString().equalsIgnoreCase(code)) {
                return device;
            }
        }
 
        return OutputDevice.Default;
    }
}
