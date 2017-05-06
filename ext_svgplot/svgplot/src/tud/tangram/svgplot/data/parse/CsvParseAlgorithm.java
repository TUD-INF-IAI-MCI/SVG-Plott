package tud.tangram.svgplot.data.parse;

import java.util.List;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;

/**
 * An algorithm for parsing CSV data. Contains implementations for two
 * orientations of the data in the file.
 */
public abstract class CsvParseAlgorithm {
	/**
	 * If the data sets are oriented horizontally, i.e. in rows, parse the rows
	 * into {@link PointList PointLists}.
	 * 
	 * @param csvData
	 * @return
	 */
	public abstract PointListList parseAsHorizontalDataSets(List<? extends List<String>> csvData);

	/**
	 * If the data sets are oriented vertically, i.e. in columns, parse the
	 * columns into {@link PointList PointLists}.
	 * 
	 * @param csvData
	 * @return
	 */
	public abstract PointListList parseAsVerticalDataSets(List<? extends List<String>> csvData);

	/**
	 * Adds a {@code point} to a {@link PointList} in a {@link PointListList},
	 * specified by {@code listIndex}. Adds more {@link PointList PointLists} if
	 * needed.
	 * 
	 * @param pointListList
	 *            the {@link PointListList} to which the point shall be added
	 * @param listIndex
	 *            the index of the list to which the point shall be added
	 * @param point
	 *            the point which shall be added
	 */
	protected void addPointToPointListList(PointListList pointListList, int listIndex, Point point) {
		while (pointListList.size() < listIndex) {
			pointListList.add(new PointList());
		}

		pointListList.get(listIndex).insertSorted(point);
	}
}
