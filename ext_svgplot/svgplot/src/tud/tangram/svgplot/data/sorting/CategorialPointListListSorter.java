package tud.tangram.svgplot.data.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.PointListList.PointList;

public abstract class CategorialPointListListSorter {

	protected final CategorialPointListList pointListList;

	public CategorialPointListListSorter(CategorialPointListList pointListList) {
		this.pointListList = pointListList;
	}

	public abstract void sort(boolean descending);

	public static CategorialPointListListSorter getSorter(SortingType sortingType,
			CategorialPointListList pointListList) {
		if (sortingType != null) {
			switch (sortingType) {
			case MaxFirstDataSet:
				return new MaxFirstDataSetSorter(pointListList);
			case Alphabetical:
				return new AlphabeticalSorter(pointListList);
			case CategorialSum:
				return new CategorialSumSorter(pointListList);
			default:
				break;
			}
		}

		// return a dummy sorter doing nothing
		return new CategorialPointListListSorter(pointListList) {
			@Override
			public void sort(boolean descending) {
				return;
			}
		};
	}

	public static <T extends Comparable<T>> void concurrentSort(final boolean descending, final List<T> key,
			final List<String> categories, List<?>... lists) {
		// Do validation
		if (key == null || lists == null)
			throw new NullPointerException("key cannot be null.");

		if (categories != null && categories.size() != key.size())
			throw new IllegalArgumentException("all lists must be the same size");

		for (List<?> list : lists)
			if (list.size() != key.size())
				throw new IllegalArgumentException("all lists must be the same size");

		// Lists are size 0 or 1, nothing to sort
		if (key.size() < 2)
			return;

		// Create a List of indices
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < key.size(); i++)
			indices.add(i);

		// Sort the indices list based on the key
		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return (descending ? -1 : 1) * key.get(i).compareTo(key.get(j));
			}
		});

		Map<Integer, Integer> swapMap = new HashMap<>(indices.size());

		// create a mapping that allows sorting of the List by N swaps.
		for (int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while (swapMap.containsKey(k))
				k = swapMap.get(k);

			swapMap.put(i, k);
		}

		// for each list, swap elements to sort according to key list
		for (Map.Entry<Integer, Integer> e : swapMap.entrySet()) {
			if (categories != null)
				Collections.swap(categories, e.getKey(), e.getValue());
			for (List<?> list : lists) {
				Collections.swap(list, e.getKey(), e.getValue());
			}
		}
	}

	protected PointList[] getPointListArray() {
		return pointListList.toArray(new PointList[pointListList.size()]);
	}

	protected void updateValues(PointList[] pointListArray) {
		for (PointList pl : pointListArray) {
			for (int i = 0; i < pl.size(); i++)
				pl.get(i).setX(i);
		}
	}
}
