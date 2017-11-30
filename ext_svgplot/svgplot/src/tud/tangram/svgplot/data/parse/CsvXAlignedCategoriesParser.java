package tud.tangram.svgplot.data.parse;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import tud.tangram.svgplot.data.CategorialPointListList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.PointListList.PointList;
import tud.tangram.svgplot.utils.Constants;

public class CsvXAlignedCategoriesParser extends CsvParseAlgorithm {

	@Override
	public PointListList parseAsHorizontalDataSets(List<? extends List<String>> csvData) {
		CategorialPointListList pointListList = new CategorialPointListList();

		Iterator<? extends List<String>> rowIterator = csvData.iterator();
		
		if(!rowIterator.hasNext())
			return pointListList;
		
		Iterator<String> lineIterator = rowIterator.next().iterator();
		
		// Move the iterator to the first category name
		if(!lineIterator.hasNext())
			return pointListList;
		lineIterator.next();
		if(!lineIterator.hasNext())
			return pointListList;
		
		// Store all categories
		while(lineIterator.hasNext()) {
			pointListList.addCategory(lineIterator.next());
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
				if(colPosition >= pointListList.getCategoryCount())
					break;
				
				// Find out the y value
				Number yValue;
				try {
					yValue = Constants.numberFormat.parse(lineIterator.next());
				} catch (ParseException e) {
					colPosition++;
					continue;
				}
				
				// Add the new point
				Point newPoint = new Point(colPosition, yValue.doubleValue());
				pointList.insertSorted(newPoint);
				colPosition++;
			}
		}
		
		return pointListList;
	}

	@Override
	public PointListList parseAsVerticalDataSets(List<? extends List<String>> csvData) {
		CategorialPointListList pointListList = new CategorialPointListList();
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
		int categoryCounter = 0;
		while(rowIterator.hasNext()) {
			lineIterator = rowIterator.next().iterator();
			if(!lineIterator.hasNext()) {
				categoryCounter++;
				continue;
			}
			
			// Find out the category title
			String currentCategory = lineIterator.next();
			pointListList.addCategory(currentCategory);
			
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
				
				Point newPoint = new Point(categoryCounter, yValue.doubleValue());
				addPointToPointListList(pointListList, currentDataSet, newPoint);
				currentDataSet++;
			}
			
			categoryCounter++;
		}
		
		return pointListList;		
	}

}
