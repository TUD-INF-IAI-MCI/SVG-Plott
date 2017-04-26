package tud.tangram.svgplot.coordinatesystem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import tud.tangram.svgplot.utils.Constants;

/**
 * 
 * @author Gregor Harlan, Jens Bornschein Idea and supervising by Jens
 *         Bornschein jens.bornschein@tu-dresden.de Copyright by Technische
 *         Universität Dresden / MCI 2014
 *
 */
public class Axis {
	public final double ticInterval;
	public final Range ticRange;
	public final double gridInterval;
	public final Range range;
	public final double atom;
	public final int atomCount;
	public final List<Double> intervalSteps;

	private final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Constants.locale);
	// FIXME: use this for publications
	// final private DecimalFormat decimalFormat = (DecimalFormat)
	// DecimalFormat.getInstance(Locale.US);
	// Now changed to use the locale specified in Constants. Kept for reference.

	public Axis(Range axisRange, double size) {
		boolean finished = false;
		double interval = 0;
		range = new Range(0, 0);
		range.setName(axisRange.getName());
		int dimensionExp;
		double dimension;
		double factor;
		do {
			/*
			 * Calculate how many tics there can maximally be without violating
			 * the minimal distance of grid lines constraint.
			 */
			int maxTics = (int) (size / Constants.MIN_GRID_DISTANCE);
			// Calculate which interval (virtual) the tics must minimally have.
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
			factor = getFactorForIntervalAndDimension(interval, dimension);
			finished = true;
			interval = factor * dimension * 2;
			range.setFrom(((int) (axisRange.getFrom() / interval)) * interval);
			range.setTo(((int) (axisRange.getTo() / interval)) * interval);
			if (range.getFrom() > axisRange.getFrom()) {
				axisRange.setFrom(range.getFrom() - interval);
				finished = false;
			}
			if (range.getTo() < axisRange.getTo()) {
				axisRange.setTo(range.getTo() + interval);
				finished = false;
			}
		} while (!finished);

		gridInterval = interval;

		ticInterval = interval; // TODO set this to 2 * interval if needed, maybe create an option
		ticRange = new Range(Math.ceil(range.getFrom() / ticInterval) * ticInterval,
				Math.floor(range.getTo() / ticInterval) * ticInterval);

		decimalFormat.setMaximumFractionDigits(Math.max(0, -dimensionExp + 2));

		atom = dimension / 100;
		atomCount = (int) (range.distance() / atom + 1);

		intervalSteps = new ArrayList<>();
		calculateIntervalSteps(dimension, factor);
	}

	/**
	 * @param dimension
	 * @param factor
	 */
	private void calculateIntervalSteps(double dimension, double factor) {
		int i = 0;
		if (Math.abs(factor - 2.5) < Constants.EPSILON) {
			intervalSteps.add(i++, 2.5 * dimension);
			intervalSteps.add(i++, dimension);
		} else if (Math.abs(factor - 1) < Constants.EPSILON) {
			intervalSteps.add(i++, dimension);
		}

		intervalSteps.add(i++, 0.5 * dimension);
		intervalSteps.add(i++, 0.1 * dimension);
		intervalSteps.add(i++, 0.05 * dimension);
		intervalSteps.add(i, 0.01 * dimension);
	}

	/**
	 * @param interval
	 * @param dimension
	 * @return the calculated factor
	 */
	private double getFactorForIntervalAndDimension(double interval, double dimension) {
		double factor;
		if (interval > dimension) {
			factor = 2.5;
		} else if (interval > 0.5 * dimension) {
			factor = 1;
		} else {
			factor = 0.5;
		}
		return factor;
	}

	public AxisIterator ticLines() {
		return new AxisIterator(ticRange, ticInterval);
	}

	public AxisIterator gridLines() {
		return new AxisIterator(range, gridInterval);
	}

	public String format(double value) {
		String str = decimalFormat.format(value);
		return "-0".equals(str) ? "0" : str;
	}

	protected static class AxisIterator implements java.util.Iterator<Double>, Iterable<Double> {

		private Range range;
		private double interval;
		private double current;

		protected AxisIterator(Range range, double interval) {
			this.range = range;
			this.interval = interval;
			current = range.getFrom();
		}

		/**
		 * Get the next axis value. There used to be a check for skipping the
		 * zero value, but now it is not skipped anymore, because there are axis
		 * configurations where the zero tics and gridlines are needed.
		 */
		@Override
		public boolean hasNext() {
			return current <= range.getTo();
		}

		@Override
		public Double next() {
			if (!hasNext())
				throw new NoSuchElementException();
			double nextCurrent = this.current;
			this.current += interval;
			return nextCurrent;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public AxisIterator iterator() {
			return this;
		}

	}

}
