package tud.tangram.svgplot.options;

import com.beust.jcommander.IStringConverter;

public enum CsvOrientation {
	
	HORIZONTAL, VERTICAL;
	
    public static CsvOrientation fromString(String code) {
        if(code.equals("vertical") || code.equals("v"))
        	return CsvOrientation.VERTICAL;
        else
        	return CsvOrientation.HORIZONTAL;
    }

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
	public static class CsvOrientationConverter implements IStringConverter<CsvOrientation> {

		public CsvOrientationConverter() {
			super();
		}
		
		@Override
		public CsvOrientation convert(String value) {
			CsvOrientation convertedValue = CsvOrientation.fromString(value);
			return convertedValue;
		}
		
	}
}
