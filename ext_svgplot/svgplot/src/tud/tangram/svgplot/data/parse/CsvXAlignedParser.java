package tud.tangram.svgplot.data.parse;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.utils.Constants;

public class CsvXAlignedParser extends CsvParseAlgorithm {

	@Override
	public PointListList parseAsHorizontalDataSets(List<? extends List<String>> csvData) {
		PointListList pointListList = new PointListList();
		List<Number> xValues = new ArrayList<>();
		Iterator<? extends List<String>> rowIterator = csvData.iterator();
		
		if(!rowIterator.hasNext())
			return pointListList;
		
		Iterator<String> lineIterator = rowIterator.next().iterator();
		
		// Move the iterator to the x value
		if(!lineIterator.hasNext())
			return pointListList;
		lineIterator.next();
		if(!lineIterator.hasNext())
			return pointListList;
		
		// Store all x values, if one is not specified store NaN
		while(lineIterator.hasNext()) {
			Number xValue;
			try {
				xValue = Constants.numberFormat.parse(lineIterator.next());
			} catch (ParseException e) {
				xValue = Double.NaN;
			}
			xValues.add(xValue);
		}
		
		// Store each row's data set
		while(rowIterator.hasNext()) {
			lineIterator = rowIterator.next().iterator();
			
			// Create a PointList with the title of the data set
			if(!lineIterator.hasNext())
				continue;
			PointList pointList = new PointList();
			pointList.setName(lineIterator.next());
			pointListList.add(pointList);
			
			// Add all the points
			int colPosition = 0;
			while (lineIterator.hasNext()) {
				if(colPosition >= xValues.size())
					break;
				Number xValue = xValues.get(colPosition);
				if(xValue.equals(Double.NaN)) {
					lineIterator.next();
					colPosition++;
					continue;
				}
				
				// Find out the y value
				Number yValue;
				try {
					yValue = Constants.numberFormat.parse(lineIterator.next());
				} catch (ParseException e) {
					colPosition++;
					continue;
				}
				
				// Add the new point
				Point newPoint = new Point(xValue.doubleValue(), yValue.doubleValue());
				pointList.insertSorted(newPoint);
				colPosition++;
			}
		}
		
		return pointListList;
	}

	@Override
	public PointListList parseAsVerticalDataSets(List<? extends List<String>> csvData) {
		PointListList pointListList = new PointListList();
		Iterator<? extends List<String>> rowIterator = csvData.iterator();
		
		if(!rowIterator.hasNext())
			return pointListList;
		
		Iterator<String> lineIterator = rowIterator.next().iterator();
		
		// Move the iterator to the first title
		if(!lineIterator.hasNext())
			return pointListList;
		lineIterator.next();
		if(!lineIterator.hasNext())
			return pointListList;
		
		// Add a PointList for each title
		while(lineIterator.hasNext()) {
			PointList pointList = new PointList();
			pointList.setName(lineIterator.next());
			pointListList.add(pointList);
		}
		
		// Add the data
		while(rowIterator.hasNext()) {
			lineIterator = rowIterator.next().iterator();
			if(!lineIterator.hasNext())
				continue;
			
			// Find out the x value
			Number xValue;
			try {
				xValue = Constants.numberFormat.parse(lineIterator.next());
			} catch (ParseException e) {
				continue;
			}
			
			// Find out the y values and add the points to the respective lists
			int currentDataSet = 0;
			while(lineIterator.hasNext()) {
				Number yValue;
				try {
					yValue = Constants.numberFormat.parse(lineIterator.next());
				} catch (ParseException e) {
					currentDataSet++;
					continue;
				}
				
				Point newPoint = new Point(xValue.doubleValue(), yValue.doubleValue());
				addPointToPointListList(pointListList, currentDataSet, newPoint);
				currentDataSet++;
			}
			
		}
		
		return pointListList;		
	}

}
