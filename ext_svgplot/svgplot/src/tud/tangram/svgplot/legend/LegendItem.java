package tud.tangram.svgplot.legend;

import java.util.concurrent.atomic.AtomicLong;

import org.w3c.dom.Element;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.xml.SvgDocument;

public abstract class LegendItem {

	/**
	 * Priority determines, which elements are rendered first. The smaller it
	 * is, the earlier the element gets rendered. The default priority is 0. The
	 * second order is the same as for normal items.
	 */
	public final int priority;

	/**
	 * Atomic number used for preserving the sequence of items added to
	 * {@link LegendRenderer}.
	 */
	public final static AtomicLong seq = new AtomicLong();

	/**
	 * The sequence number used for preserving the sequence of items added to
	 * {@link LegendRenderer}.
	 */
	public final long seqNum;

	/**
	 * Default constructor specifying the {@link #priority}.
	 * 
	 * @param priority
	 *            default priority is 0, the smaller the priority, the earlier
	 *            the element gets rendered
	 */
	public LegendItem(int priority) {
		seqNum = seq.getAndIncrement();
		this.priority = priority;
	}

	/**
	 * Sets {@link #priority} to 0, causing the item to be rendered in
	 * normal order.
	 */
	public LegendItem() {
		this(0);
	}

	/**
	 * Render the item to the {@link SvgDocument}.
	 * 
	 * @param legend
	 *            The legend document
	 * @param viewbox
	 *            the viewbox where the item shall be rendered to
	 * @param startingPosition
	 *            the position at which to start rendering
	 * @return the end y position of the element
	 */
	public abstract double render(SvgDocument legend, Element viewbox, Point startingPosition);

	/**
	 * Get the type of the {@link LegendItem}.
	 * 
	 * @return the type of the {@link LegendItem}
	 */
	public abstract LegendItemType getType();

	/**
	 * Get the id of the SVG group element.
	 * 
	 * @return id of the SVG group element or null
	 */
	public abstract String getGroupId();

	/**
	 * Gets or creates the SVG group element.
	 * 
	 * @param legend
	 *            the legend
	 * @param viewbox
	 *            the viewbox where the group is a child element
	 * @return the SVG group element
	 */
	protected Element getOrCreateGroup(SvgDocument legend, Element viewbox) {
		Element group = legend.getOrCreateChildGroupById(viewbox, getGroupId());
		return group;
	}

	/**
	 * Gets or creates an SVG subgroup element.
	 * 
	 * @param legend
	 *            the legend
	 * @param viewbox
	 *            the viewbox where the group is a child element
	 * @param suffix
	 *            a suffix to place after the group id, determining the subgroup
	 * @return the SVG group element
	 */
	protected Element getOrCreateGroup(SvgDocument legend, Element viewbox, String suffix) {
		Element group = getOrCreateGroup(legend, viewbox);
		Element subgroup = legend.getOrCreateChildGroupById(group, getGroupId() + "_" + suffix);
		return subgroup;
	}
}
