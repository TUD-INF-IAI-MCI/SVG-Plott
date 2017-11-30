package tud.tangram.svgplot.styles;

import com.beust.jcommander.IStringConverter;

public enum BarAccumulationStyle {
	STACKED, GROUPED;

	public static BarAccumulationStyle fromString(String barAccumulationStyle) {
		switch (barAccumulationStyle.toLowerCase()) {
		case "stacked":
			return STACKED;
		case "grouped":
		default:
			return GROUPED;
		}
	}

	public class BarAccumulationStyleConverter implements IStringConverter<BarAccumulationStyle> {

		@Override
		public BarAccumulationStyle convert(String value) {
			return BarAccumulationStyle.fromString(value);
		}
		
	}
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}