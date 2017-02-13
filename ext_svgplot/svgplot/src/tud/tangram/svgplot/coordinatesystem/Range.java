package tud.tangram.svgplot.coordinatesystem;

import com.beust.jcommander.IStringConverter;
/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Range {
	/** Start of the range */
	public double from;
	/** End of the range */
	public double to;
	
	public String name;

	/**
	 * Constructor with name.
	 * @param from	|	start of the range
	 * @param to	|	end of the range
	 * @param name
	 */
	public Range(double from, double to, String name) {
		this.from = from;
		this.to = to;
		this.name = name;
	}
	
	/**
	 * Constructor without name.
	 * @param from	|	start of the range
	 * @param to	|	end of the range
	 */
	public Range(double from, double to)  { 
		this(from, to, "");
	}

	/**
	 * Calculates the distance covered by the range.
	 * @return distance
	 */
	public double distance() {
		return to - from;
	}

	@Override
	public String toString() {
		return name + " " + from + ":" + to;
	}

	/**
	 * Converter class for parsing ranges from strings.
	 */
	public static class Converter implements IStringConverter<Range> {
		/**
		 * Converts a range specified by a string to a {@link Range} instance.
		 * The syntax is: {@code [["]<name>["]::]<from>:<to>[:<name>]}.
		 * The second name parameter is preferred.
		 * The from and to parameters should be parsable as Double.
		 * 
		 * @param value	|	correctly formatted range string
		 */
		@Override
		public Range convert(String value) {
			String[] parts = value.split("::");
			String[] s;
			String name = "";
			
			// Extract the name if specified and remove quotations
			if(parts.length > 1){
				name = parts[0].replace("\"", "").trim();
				s = parts[1].split(":");
			}else{
				s = parts[0].split(":");
			}			
			
			// There were not enough parameters specified.
			if (s.length < 2)
				return new Range(-8, 8);
			
			/*
			 * If there are two parameters, use the first name string,
			 * if there are more, use the second one.
			 */
			return s.length > 2 ? new Range(Double.parseDouble(s[0]),
					Double.parseDouble(s[1]), s[2]) : new Range(
					Double.parseDouble(s[0]), Double.parseDouble(s[1]), name);				
			}
		
	}
}
