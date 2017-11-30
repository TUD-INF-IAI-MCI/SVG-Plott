package tud.tangram.svgplot.data.sorting;

import java.util.List;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.PointListList.PointList;

public class AlphabeticalSorter extends CategorialPointListListSorter {

	
	public AlphabeticalSorter(CategorialPointListList pointListList) {
		super(pointListList);
	}

	@Override
	public void sort(boolean descending) {
		if(pointListList.isEmpty())
			return;
		
		PointList[] pointListArray = pointListList.toArray(new PointList[pointListList.size()]);
		List<String> categories = pointListList.getCategoryNames();
		
		concurrentSort(descending, categories, categories, pointListArray);
		
		updateValues(pointListArray);
	}

}
