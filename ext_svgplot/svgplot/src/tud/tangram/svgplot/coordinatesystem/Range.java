package tud.tangram.svgplot.coordinatesystem;

import com.beust.jcommander.IStringConverter;

public class Range {
	public double from;
	public double to;

	public Range(double from, double to) {
		this.from = from;
		this.to = to;
	}

	public double distance() {
		return to - from;
	}

	@Override
	public String toString() {
		return from + ":" + to;
	}

	public static class Converter implements IStringConverter<Range> {
		@Override
		public Range convert(String value) {
			String[] s = value.split(":");
			return new Range(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		}
	}
}
