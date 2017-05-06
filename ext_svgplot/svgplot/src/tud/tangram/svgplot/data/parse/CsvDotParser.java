package tud.tangram.svgplot.data.parse;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.utils.Constants;

public class CsvDotParser extends CsvParseAlgorithm {

	/**
	 * Parses scattered point data in horizontal data sets, alternating x and y. The
	 * first column contains the row name in the x row.
	 * 
	 * @return the parsed data
	 */
	public PointListList parseAsHorizontalDataSets(List<? extends List<String>> csvData) {
		int row = 0;

		PointListList pointListList = new PointListList();

		// Continue as long as there are at least two further rows left
		while (csvData.size() >= row + 2) {
			PointList rowPoints = new PointList();

			Iterator<String> xRowIterator = csvData.get(row).iterator();
			Iterator<String> yRowIterator = csvData.get(row + 1).iterator();

			row += 2;

			// Get the row name
			if (xRowIterator.hasNext() && yRowIterator.hasNext()) {
				rowPoints.setName(xRowIterator.next());
				yRowIterator.next();
			} else {
				continue;
			}

			// Get the row values
			while (xRowIterator.hasNext() && yRowIterator.hasNext()) {
				Number xValue;
				Number yValue;
				try {
					xValue = Constants.numberFormat.parse(xRowIterator.next());
					yValue = Constants.numberFormat.parse(yRowIterator.next());
				} catch (ParseException e) {
					continue;
				}
				Point newPoint = new Point(xValue.doubleValue(), yValue.doubleValue());
				rowPoints.insertSorted(newPoint);
			}

			// If there were no points found, do not add the row to the list
			if (!rowPoints.isEmpty())
				pointListList.add(rowPoints);
		}

		return pointListList;
	}

	/**
	 * Parses scattered point data in vertical data sets, alternating x and y. The
	 * first row contains the column name in the x column.
	 * 
	 * @return the parsed data
	 */
	@Override
	public PointListList parseAsVerticalDataSets(List<? extends List<String>> csvData) {
		int row = 0;

		PointListList pointListList = new PointListList();

		if (csvData.isEmpty())
			return pointListList;

		// Iterate over the first row in order to get the headers
		int col = 0;
		for (String header : csvData.get(0)) {
			if (col % 2 == 0) {
				PointList pointList = new PointList();
				pointList.setName(header);
				pointListList.add(pointList);
			}
			col++;
		}

		row++;

		// Continue as long as there is at least one further rows left
		while (csvData.size() >= row + 1) {
			List<String> fields = csvData.get(row);
			Iterator<String> fieldIterator = fields.iterator();

			col = -1;

			while (fieldIterator.hasNext()) {
				String xRaw = fieldIterator.next();
				String yRaw;

				col++;

				if (!fieldIterator.hasNext())
					break;
				
				yRaw = fieldIterator.next();

				Number xValue;
				Number yValue;

				try {
					xValue = Constants.numberFormat.parse(xRaw);
					yValue = Constants.numberFormat.parse(yRaw);
				} catch (ParseException e) {
					col++;
					continue;
				}

				Point point = new Point(xValue.doubleValue(), yValue.doubleValue());

				addPointToPointListList(pointListList, col / 2, point);

				col++;
			}

			row++;
		}

		return pointListList;
	}
}
