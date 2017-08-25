package tud.tangram.svgplot.data.parse;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.io.IOException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject; 
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import java.util.Iterator;



public class MarkupParser {
	
	private JSONObject jsonMetaData;
	private JSONArray jsonChartDataGroupData; // jsonArray which entries are data rows (each entry = one data variable) 
	private JSONObject jsonChart; // contains all chart data (except from metadata)
	private ArrayList<ArrayList<String>> dataToParse; // values from dataset as strings
	
	
	// enums for type-safety parameters of functions
	enum chartStyle{Width,Height,Left,Top};

		
	
	/**
	 * @author Christin Engel
	 * Initiates the parser for self defined chart Markup.
	 * That Markup contains meta data, style and structural properties of the chart 
	 */
	
	public MarkupParser (String markupPath) throws FileNotFoundException {
		
	 	
	    String stringData = "";
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(markupPath));
			while ((line = br.readLine()) != null) {
				stringData += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		JSONParser jsonParser = new JSONParser();
		
		 try {
			JSONObject jsonObjectData = (JSONObject) jsonParser.parse(stringData);
			this.jsonMetaData = (JSONObject)jsonObjectData.get("MetaData");
			this.jsonChart = (JSONObject)jsonObjectData.get("Chart");
			
			this.jsonChartDataGroupData = (JSONArray)this.jsonChart.get("ChartDataGroups");
	     
			// TODO: Think about whether it is better to return data the data for every dataset, where to save
			// style informations about dataset and other properties and where the datapoints itself
			// 1. idea: in one array where every entry consists of all datapoints from that dataset (order of array is important = order of datasets in json file) 
			// 2. one array per dataset  --> give getChartDataPoints an id for that dataset that data are needed
	        
	
	    
		 }
		
	  	 catch(ParseException pe){
			
	         System.out.println("position: " + pe.getPosition());
	         System.out.println(pe);
	      }
	 
		
	}
	
	// getter for MetaData Author, CreationDate, ExtractionProgram, ExtractionProgramVersion
	public String getMetaDataAuthor() {
		return this.jsonMetaData.get("Author").toString();
	}
	
	public String getMetaDataCreationDate() {
		return this.jsonMetaData.get("CreationDate").toString();
	}
	
	public String getMetaDataExtractionProgram() {
		return this.jsonMetaData.get("ExtractionProgram").toString();
	}
	
	public String getMetaDataExtractionProgramVersion() {
		return this.jsonMetaData.get("ExtractionProgramVersion").toString();
	}
	public String getMetaDataAlternativeText() {
		return this.jsonMetaData.get("AlternativeText").toString();
	}
	
	
	// getter for general chart specifications
	public String getChartType() {
		return this.jsonChart.get("ChartType").toString();
	}
	
	public String getChartHeadline() {
		return this.jsonChart.get("ChartHeadline").toString();
	}
	
	public String getChartTypeCharacteristica() {
		return this.jsonChart.get("ChartTypeCharacteristica").toString();
	}
	
	
	/**
	 * Getter for chart style information (in particular size and position)
	 * @param Specific chart style characteristic type enum
	 */
	public String getChartPosSize(chartStyle chartStyleSpecification) {
		  JSONObject chartStyleObject = (JSONObject) this.jsonChart.get("ChartStyle");
		  return chartStyleObject.get(chartStyleSpecification.toString()).toString();
    }
	
	
	public ArrayList getChartDataPoints() {
		dataToParse = new ArrayList<>();
		
		JSONArray jsonDataPoints = new JSONArray();
		
		//Iterator<String> iterator = this.jsonChartDataGroupData.iterator();
		//while (iterator.hasNext()) {
		//	System.out.println(iterator.next());
		//}
		
      //  System.out.println(jsonDataPoints.toString());
        return dataToParse;
	}
	
}
