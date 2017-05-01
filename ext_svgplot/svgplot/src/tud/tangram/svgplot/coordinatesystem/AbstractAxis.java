package tud.tangram.svgplot.coordinatesystem;

import java.text.DecimalFormat;
import java.util.NoSuchElementException;

import tud.tangram.svgplot.utils.Constants;

public abstract class AbstractAxis {

	protected double ticInterval;
	protected Range ticRange;
	protected double gridInterval;
	protected Range range;
	protected double labelInterval;
	protected Range labelRange;
	protected final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Constants.locale);

	public AxisIterator ticLines() {
		return new AxisIterator(ticRange, ticInterval);
	}

	public AxisIterator gridLines() {
		return new AxisIterator(range, gridInterval);
	}
	
	public AxisIterator labelPositions() {
		return new AxisIterator(labelRange, labelInterval);
	}

	public abstract String format(double value);
	
	public abstract String format(String value);

	protected static class AxisIterator implements java.util.Iterator<Double>, Iterable<Double> {
		
		private Range range;
		private double interval;
		private double current;
	
		protected AxisIterator(Range range, double interval) {
			this(range, interval, 0);
		}
		
		protected AxisIterator(Range range, double interval, double offset) {
			this.range = range;
			this.interval = interval;
			current = range.getFrom() + offset;
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
	
	public double getTicInterval() {
		return ticInterval;
	}

	public Range getTicRange() {
		return ticRange;
	}

	public double getGridInterval() {
		return gridInterval;
	}

	public Range getRange() {
		return range;
	}
	
	public double getLabelInterval() {
		return labelInterval;
	}
	
	public Range getLabelRange() {
		return labelRange;
	}
}