package tud.tangram.svgplot.data.sorting;

import java.util.ArrayList;
import java.util.List;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList.PointList;

public class MaxFirstDataSetSorter extends CategorialPointListListSorter {
	
	public MaxFirstDataSetSorter(CategorialPointListList pointListList) {
		super(pointListList);
	}

	@Override
	public void sort(boolean descending) {
		if(pointListList.isEmpty())
			return;
		List<Double> firstDataSetValues = new ArrayList<>();
		
		for(Point p : pointListList.get(0))
			firstDataSetValues.add(p.getY());
		
		PointList[] pointListArray = getPointListArray();
		List<String> categories = pointListList.getCategoryNames();
		
		concurrentSort(descending, firstDataSetValues, categories, pointListArray);
		
		updateValues(pointListArray);
	}

}
