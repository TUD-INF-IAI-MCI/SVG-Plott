package tud.tangram.svgplot.data.parse;

import com.beust.jcommander.IStringConverter;

import tud.tangram.svgplot.data.XType;

/**
 * Determines what data is represented how by the CSV file. The values are
 * structural properties, whereas the {@link XType} held by every value
 * determines whether the x values are metric or categorial.
 */
public enum CsvType {
	DOTS(XType.METRIC), X_ALIGNED(XType.METRIC), X_ALIGNED_CATEGORIES(XType.CATEGORIAL);

	public final XType xType;

	private CsvType(XType xType) {
		this.xType = xType;
	}

	public static CsvType fromString(String value) {
		switch (value.toLowerCase()) {
		case "x_aligned":
		case "xa":
			return CsvType.X_ALIGNED;
		case "x_aligned_categories":
		case "xac":
			return CsvType.X_ALIGNED_CATEGORIES;
		case "dots":
		case "d":
		default:
			return DOTS;
		}
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static class CsvTypeConverter implements IStringConverter<CsvType> {

		public CsvTypeConverter() {
			super();
		}

		@Override
		public CsvType convert(String value) {
			CsvType convertedValue = CsvType.fromString(value);
			return convertedValue;
		}

	}
}
