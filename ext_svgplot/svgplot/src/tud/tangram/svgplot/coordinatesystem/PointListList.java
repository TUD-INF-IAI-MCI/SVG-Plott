package tud.tangram.svgplot.coordinatesystem;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IStringConverter;
/**
 * 
 * @author Gregor Harlan, Jens Bornschein
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class PointListList{

	public List<PointList> pointLists = new ArrayList<PointList>();
	
	public PointListList(String pointLists) {
		
		//TODO: load from file
		
		pointLists = pointLists.replaceAll("[^\\d.,^\\s+,^\\{^\\}^-]", "");
		String[] lists = pointLists.split("\\}");
		for (String l : lists) {
			PointList pl = new PointList(l);
			if(pl.points.size() > 0) this.pointLists.add(pl);
		}		
	}
	
	public static class Converter implements IStringConverter<PointListList> {
		@Override
		public PointListList convert(String value) {
			return new PointListList(value.trim());
		}
	}
		
	public class PointList{
		
		public List<Point> points = new ArrayList<Point>();
		
		public PointList(String points){
			points = points.replaceAll("[^\\d.,^\\s+,^-]", "");
			String[] s = points.split("\\s+");
			
			for (String string : s) {
				this.points.add((new Point.Converter()).convert(string));
			}			
		}	
		
		public class Converter implements IStringConverter<PointList> {
			@Override
			public PointList convert(String value) {
				return new PointList(value.trim());
			}
		}
	}
}