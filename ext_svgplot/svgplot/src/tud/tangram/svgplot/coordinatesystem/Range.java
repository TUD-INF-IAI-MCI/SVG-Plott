package tud.tangram.svgplot.coordinatesystem;

import com.beust.jcommander.IStringConverter;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Range {
	public double from;
	public double to;
	public String name;

	public Range(double from, double to, String name) {
		this.from = from;
		this.to = to;
		this.name = name;
	}
	
	public Range(double from, double to)  { 
		this(from, to, "");
	}

	public double distance() {
		return to - from;
	}

	@Override
	public String toString() {
		return name + " " + from + ":" + to;
	}

	public static class Converter implements IStringConverter<Range> {
		@Override
		public Range convert(String value) {
			String[] s = value.split(":");	
			if(s.length < 2) return new Range(-8,8);
			return s.length > 2 ? new Range(Double.parseDouble(s[0]), Double.parseDouble(s[1]), s[2]) : new Range(Double.parseDouble(s[0]), Double.parseDouble(s[1]));
		}
	}
}
