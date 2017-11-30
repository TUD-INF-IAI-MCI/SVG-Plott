package tud.tangram.svgplot.legend;

import java.util.Comparator;

public class LegendItemComparator implements Comparator<LegendItem> {

	/**
	 * Compares two {@link LegendItem LegendItems}. The order is first specified
	 * by {@link LegendItem#priority}, if the are equal by the
	 * {@link LegendItemType} enum and if they are equal, by a running number of
	 * the {@link LegendItem} class.
	 */
	@Override
	public int compare(LegendItem o1, LegendItem o2) {
		if (o1.priority == o2.priority) {
			if (o1.getType().ordinal() != o2.getType().ordinal())
				return o1.getType().ordinal() < o2.getType().ordinal() ? -1 : 1;
			
			if(o1.seqNum == o2.seqNum)
				return 0;
			
			return o1.seqNum < o2.seqNum ? -1 : 1;
		} else {
			return o1.priority < o2.priority ? -1 : 1;
		}
	}

}
