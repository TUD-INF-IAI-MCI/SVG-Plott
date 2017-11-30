package tud.tangram.svgplot.data.sorting;

import java.util.ArrayList;
import java.util.List;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.PointListList.PointList;

public class CategorialSumSorter extends CategorialPointListListSorter {
	
	public CategorialSumSorter(CategorialPointListList pointListList) {
		super(pointListList);
	}

	@Override
	public void sort(boolean descending) {
		if(pointListList.isEmpty())
			return;
		
		PointList[] pointListArray = pointListList.toArray(new PointList[pointListList.size()]);
		
		List<Double> sums = new ArrayList<>();
		for(int i = 0; i < pointListList.getCategoryNames().size(); i++) {
			sums.add(pointListList.getCategorySum(i));
		}
		
		concurrentSort(descending, sums, pointListList.getCategoryNames(), pointListArray);
		
		updateValues(pointListArray);
	}

}
