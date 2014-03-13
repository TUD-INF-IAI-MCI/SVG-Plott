package tud.tangram.svgplot.plotting;

import com.beust.jcommander.IStringConverter;
/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Function {

	private String title;
	private String function;

	public Function(String title, String function) {
		this.title = title;
		this.function = function;
	}

	public Function(String function) {
		this.title = null;
		this.function = function;
	}

	public boolean hasTitle() {
		return title != null;
	}

	public String getTitle() {
		return title;
	}

	public String getFunction() {
		return function;
	}

	@Override
	public String toString() {
		if (hasTitle()) {
			return getTitle() + ": " + getFunction();
		}
		return getFunction();
	}

	public static class Converter implements IStringConverter<Function> {
		@Override
		public Function convert(String value) {
			//TODO: add file or table for functions
			
			String[] s = value.replaceFirst("^\\\\", "").split("::");
			if (s.length == 1) {
				return new Function(s[0].trim());
			}
			return new Function(s[0].trim(), s[1].trim());
		}
	}
}
