package tud.tangram.svgplot.data;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.opencsv.CSVReader;

import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.utils.Constants;

public class CsvParser {

	private ArrayList<ArrayList<String>> csvData;

	/**
	 * Initiates the parser. The parser reads from the specified {@code reader}
	 * and populates {@link #csvData}.
	 * 
	 * @param reader
	 *            a reader, like {@link FileReader}
	 * @param separator
	 * @param quoteChar
	 * @throws IOException
	 *             if the {@link CSVReader} has problems parsing
	 */
	public CsvParser(Reader reader, char separator, char quoteChar) throws IOException {
		CSVReader csvReader = new CSVReader(reader, separator, quoteChar);

		csvData = new ArrayList<>();

		String[] nextLine;
		while ((nextLine = csvReader.readNext()) != null) {
			csvData.add(new ArrayList<String>(Arrays.asList(nextLine)));
		}

		csvReader.close();
	}

	/**
	 * Parses scattered point data in horizontal rows, alternating x and y. The
	 * first column contains the row name in the x row.
	 * 
	 * @return the parsed data
	 */
	public PointListList parseAsScatterDataHorizontalRows() {
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

	public PointListList parseAsScatterDataVerticalRows() {
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
			ArrayList<String> fields = csvData.get(row);
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
	private void addPointToPointListList(PointListList pointListList, int listIndex, Point point) {
		while (pointListList.size() < listIndex) {
			pointListList.add(new PointList());
		}

		pointListList.get(listIndex).insertSorted(point);
	}
}
