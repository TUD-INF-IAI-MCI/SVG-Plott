package tud.tangram.svgplot.plotting;

import java.util.ArrayList;

public class ReferenceLine {
	public enum Direction {
		X_LINE,
		Y_LINE
	};
	
	public final Direction direction;
	
	public double position;
	
	public ReferenceLine(Direction direction, double position) {
		this.direction = direction;
		this.position = position;
	}
	
	public static ArrayList<ReferenceLine> fromString(Direction direction, String lines) {
		ArrayList<ReferenceLine> linesParsed = new ArrayList<>();
		
		if(lines == null) {
			return linesParsed;
		}
		
		for (String line : lines.trim().split("\\s+")) {
			if (line != null && !line.isEmpty()) {
				double position = Double.parseDouble(line.replace("\\", ""));
				linesParsed.add(new ReferenceLine(direction, position));
			}
		}
		return linesParsed;
	}
}
