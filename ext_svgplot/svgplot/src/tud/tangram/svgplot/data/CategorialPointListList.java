package tud.tangram.svgplot.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PointListList} storing a list of category names. The x values of the
 * added points should correspond to the index of their category in the category
 * list.
 */
public class CategorialPointListList extends PointListList {

	private static final long serialVersionUID = -1291194891140659342L;

	private List<String> categoryNames;
	private double maxYSum = Double.NEGATIVE_INFINITY;

	public XType getXType() {
		return XType.CATEGORIAL;
	}
	
	public CategorialPointListList() {
		categoryNames = new ArrayList<>();
	}

	public void addCategory(String name) {
		categoryNames.add(name);
	}
	
	public String getCategoryName(int index) {
		try {
			return categoryNames.get(index);
		} catch (Exception e) {
			return "";
		}
	}
	
	public int getCategoryCount() {
		return categoryNames.size();
	}
	
	public List<String> getCategoryNames() {
		return categoryNames;
	}
	
	public double getCategorySum(int index) {
		double sum = 0;
		for(PointList pointList : this) {
			if(pointList.size() > index)
				sum += pointList.get(index).getY();
		}
		return sum;
	}

	@Override
	public void updateMinMax() {
		super.updateMinMax();
		for(int i = 0; i < categoryNames.size(); i++)
			maxYSum = Math.max(maxYSum, getCategorySum(i));
	}

	public double getMaxYSum() {
		return maxYSum;
	}
}
