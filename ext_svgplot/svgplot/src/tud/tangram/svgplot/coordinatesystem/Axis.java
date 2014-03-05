package tud.tangram.svgplot.coordinatesystem;

import java.text.DecimalFormat;
import java.util.Locale;
/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Axis {
	final private static int minGridDistance = 10;

	final public double ticInterval;
	final public Range ticRange;
	final public double gridInterval;
	final public Range range;
	final public double atom;
	final public int atomCount;
	final public double[] intervalSteps;

	final private DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
	// FIXME: use this for publications
	//final private DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
		
	public Axis(Range axisRange, double size) {
		boolean finished = false;
		double interval = 0;
		range = new Range(0, 0);
		int dimensionExp;
		double dimension;
		double factor;
		do {
			int maxTics = (int) (size / minGridDistance);
			interval = axisRange.distance() / maxTics;
			dimensionExp = 0;
			int direction = interval < 1 ? -1 : 1;
			while (direction * 0.5 * Math.pow(10, dimensionExp) < direction * interval) {
				dimensionExp += direction;
			}
			if (direction == 1) {
				dimensionExp--;
			}
			dimension = Math.pow(10, dimensionExp);
			if (interval > dimension) {
				factor = 2.5;
			} else if (interval > 0.5 * dimension) {
				factor = 1;
			} else {
				factor = 0.5;
			}
			finished = true;
			interval = factor * dimension;
			range.from = ((int) (axisRange.from / interval)) * interval;
			range.to = ((int) (axisRange.to / interval)) * interval;
			range.name = axisRange.name;
			if (range.from > axisRange.from) {
				axisRange.from = range.from - interval;
				finished = false;
			}
			if (range.to < axisRange.to) {
				axisRange.to = range.to + interval;
				finished = false;
			}
		} while (!finished);

		gridInterval = interval;

		ticInterval = 2 * interval;
		ticRange = new Range(((int) (range.from / ticInterval)) * ticInterval, ((int) (range.to / ticInterval)) * ticInterval);

		decimalFormat.setMaximumFractionDigits(Math.max(0, -dimensionExp + 2));

		atom = dimension / 100;
		atomCount = (int) (range.distance() / atom + 1);

		int i = 0;
		if (factor == 2.5) {
			intervalSteps = new double[6];
			intervalSteps[i++] = 2.5 * dimension;
			intervalSteps[i++] = dimension;
		} else if (factor == 1) {
			intervalSteps = new double[5];
			intervalSteps[i++] = dimension;
		} else {
			intervalSteps = new double[4];
		}
		intervalSteps[i++] = 0.5 * dimension;
		intervalSteps[i++] = 0.1 * dimension;
		intervalSteps[i++] = 0.05 * dimension;
		intervalSteps[i++] = 0.01 * dimension;
	}

	public Iterator ticLines() {
		return new Iterator(ticRange, ticInterval);
	}

	public Iterator gridLines() {
		return new Iterator(range, gridInterval);
	}

	public String format(double value) {
		String str = decimalFormat.format(value);
		return "-0".equals(str) ? "0" : str;
	}

	protected static class Iterator implements java.util.Iterator<Double>, Iterable<Double> {

		private Range range;
		private double interval;
		private double current;

		protected Iterator(Range range, double interval) {
			this.range = range;
			this.interval = interval;
			current = range.from;
		}

		@Override
		public boolean hasNext() {
			if (current == 0) {
				current += interval;
			}
			return current <= range.to;
		}

		@Override
		public Double next() {
			double current = this.current;
			this.current += interval;
			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator iterator() {
			return this;
		}

	}

}
