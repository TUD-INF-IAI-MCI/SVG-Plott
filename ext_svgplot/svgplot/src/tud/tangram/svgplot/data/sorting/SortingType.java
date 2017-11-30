package tud.tangram.svgplot.data.sorting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.IStringConverter;

public enum SortingType {
	/**
	 * Important: The description keys {@code desc.barchart.sorting.*} rely on the enum key.
	 * If the enum key change, the descriptions keys have to change as well.
	 */
	MaxFirstDataSet("mf", "maxfirst", "maxfirstdataset"),
	Alphabetical("a", "alpha", "alphabetical"),
	CategorialSum("cs", "sum", "categorialsum"),
	None;
	
	private static Map<String, SortingType> mapping;
	static {
		mapping = new HashMap<>();
		for(SortingType sortingType : SortingType.values())
			for(String value : sortingType.values)
				mapping.put(value.toLowerCase(), sortingType);
	}
	
	private List<String> values;
	
	private SortingType(String... values) {
		this.values = Arrays.asList(values);
	}
	
	public static SortingType fromString(String value) {
		if(mapping.containsKey(value))
			return mapping.get(value.toLowerCase());
		else
			return None;
	}
	
	public static class SortingTypeConverter implements IStringConverter<SortingType> {

		@Override
		public SortingType convert(String value) {
			return SortingType.fromString(value);
		}
		
	}
}
