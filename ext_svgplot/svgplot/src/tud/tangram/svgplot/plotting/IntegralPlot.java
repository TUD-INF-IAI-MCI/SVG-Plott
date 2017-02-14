package tud.tangram.svgplot.plotting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.tangram.svgplot.SvgPlot;
import tud.tangram.svgplot.coordinatesystem.CoordinateSystem;
import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.svgcreator.SvgGraphCreator;
import tud.tangram.svgplot.svgcreator.SvgTools;
import tud.tangram.svgplot.xml.SvgDocument;

public class IntegralPlot {

	private CoordinateSystem cs;

	public IntegralPlot(CoordinateSystem cs) {
		this.cs = cs;
	}

	public void handlePlotIntergral(Plot plot, SvgDocument doc, Element parent,
			double from, double to) {
		handlePlotIntergral(plot, doc, parent, from, to, null);
	}

	public void handlePlotIntergral(Plot plot, SvgDocument doc, Element parent,
			double from, double to, Plot intersectionPlot) {

		Element integralContainer = doc.createGroup("integrals");
		parent.appendChild(integralContainer);

		if (doc.defs != null) {
			doc.defs.appendChild(getFillPattern(doc));
		}
		int i = 0;

		String title = "integral";
//				+ plot.getFunction().getTitle()
//				+ " mit "
//				+ (intersectionPlot != null ? intersectionPlot.getFunction()
//						.getTitle() : "");
		
		if(intersectionPlot != null){
			title = SvgTools.translate("legend.integral_1", from, to, plot.Name, intersectionPlot.Name);
		}else{
			title = SvgTools.translate("legend.integral_0", from, to, plot.Name);
		}
		
		
		
		String css = "integral-";

		// crate container for Element
		Node graph = parent
				.appendChild(doc.createGroup("plot_integral-" + ++i));
		// create path element
		Element path = (Element) graph.appendChild(doc.createElement("path"));

		// find the root points
		List<Point> zero = plot.getRoots();

		// find the intersection points
		List<Point> ispts = new ArrayList<Point>();

		if (intersectionPlot != null) {
			if (!intersectionPlot.equals(plot)) {
				ispts = plot.getIntersections(intersectionPlot);	
				Collections.sort(ispts);
			}
		}
		

		String points = "";
		String backPoints = "";

		for (List<Point> list : plot) {

			/******* START PAINTING EACH PLOT *********/
			if (list != null && list.size() > 0) {
				String op = "M"; // "moveto" command - start path

				// check if the path should start at the x axes
				if (intersectionPlot == null
						&& (zero == null || zero.size() < 2)
						&& list.get(0) != null
						&& (list.get(0).x > cs.xAxis.range.from || list.get(0).y != 0)) {
					points += op + cs.convert(Math.max(cs.xAxis.range.from, from), 0) + " ";
					op = "L";

				}

				// plot the function
				boolean started = false;
				/******* START PAINTING EACH POINTOF A PLOT *********/
				for (Point point : list) {
					
					// start integrals after first visible intersection
					if (!started) { 
						if (!ispts.contains(point) && intersectionPlot != null)
							continue;
						else{
							started = true;
						}
					}

					/******************* INTERSECION START ***********************/
					if (ispts.size() > 0 && (ispts.contains(point))) {

						int j = 0;
						// try to find the index of the intersection point
						for (j = 0; j < ispts.size(); j++) {
							if (ispts.get(j) != null
									&& ispts.get(j).equals(point))
								break;
						}

						Point nextIspt = null;
						List<Point> otherPath = new ArrayList<Point>();
						if (j >= 0 && j < ispts.size()) {
							
							Point start = ispts.get(j);
							if(start.x <= from) start = new Point(from, 0);
							
							// get next point
							if (j + 1 < (ispts.size())) {
								nextIspt = ispts.get(j + 1);	
								// next intersection point is out of range
								if(nextIspt == null || nextIspt.x > to) nextIspt = new Point(to, 0);
								otherPath = getFunctionAreaPoints(start,
										nextIspt, intersectionPlot);
							}
						}

						if (otherPath != null && otherPath.size() > 0) {

							if (op.equals("L")) {
								// end the path
								points += "Z";
								path = finalizePath(path, points, css + i,
										"", title, null);
								// create new path
								path = (Element) graph.appendChild(doc
										.createElement("path"));

								// clear points
								points = "";
								op = "M";
							}

							Collections.reverse(otherPath);
							for (Point point2 : otherPath) {
								backPoints += op + cs.convert(point2) + " ";
								op = "L";
							}
							points += backPoints;
							backPoints = "";
						}
					}
					/******************* INTERSECION END ***********************/

					// add the point to the path list
					if(point.x <= to && point.x >= from) points += op + cs.convert(point) + " ";
					else{
						if(point.x >= from){
							points += op + cs.convert(new Point(to,intersectionPlot != null ? point.y : 0)) + " ";
							break;
						}						
					}

					// if point is the last intersection point exit the handling
					if (ispts != null && ispts.size() > 0
							&& point.equals(ispts.get(ispts.size() - 1)))
						break;

					/******************* ROOT START ***********************/
					if (intersectionPlot == null && zero.contains(point)) {
						points += "Z";
						path = finalizePath(path, points, css + i + " root", "", title,
								null);
						// create new path
						path = (Element) graph.appendChild(doc
								.createElement("path"));

						// clear points
						points = "M" + cs.convert(point) + " ";
					}
					/******************* ROOT END ***********************/
					op = "L";
				}

				// check if the path should end at the x axes
				if (intersectionPlot == null
						&& (zero == null || zero.size() < 2)
						&& list.get(list.size() - 1) != null
						&& (list.get(list.size() - 1).x < cs.xAxis.range.to || list
								.get(list.size() - 1).y != 0)) {
					points += op + cs.convert(Math.min(cs.xAxis.range.to, to), 0) + " ";
				}
				points += " Z";
			}
		}
		path = finalizePath(path, points, css + i, "", title, null);
	}

	private Element finalizePath(Element path, String d, String cssClass,
			String id, String title, String desc) {

		cssClass = "integral " + cssClass;

		if (path != null) {
			Node doc = path.getOwnerDocument();

			if (d != null && !d.isEmpty()) {
				d = d.trim();
				if (!d.endsWith("Z"))
					d += " Z";
				path.setAttribute("d", d);
			}
			if (cssClass != null && !cssClass.isEmpty())
				path.setAttribute("class", cssClass);
			if (id != null && !id.isEmpty())
				path.setAttribute("id", id);
			path.setAttribute("clip-path", "url(#plot-area)");

			if (doc != null && doc instanceof Document) {
				if (title != null && !title.isEmpty()) {
					Node t = ((Document) doc).createElement("title");
					if (t != null) {
						t.setTextContent(title);
						path.appendChild(t);
					}
				}

				if (desc != null && !desc.isEmpty()) {
					Node dn = ((Document) doc).createElement("desc");
					if (dn != null) {
						dn.setTextContent(desc);
						path.appendChild(dn);
					}
				}
			}
		}

		return path;
	}

	/**
	 * Try to get the function points between the given points
	 * 
	 * @param from
	 *            | Point to start
	 * @param to
	 *            | Point to end
	 * @param plot
	 *            | the function to search
	 * @return List of points
	 */
	private static List<Point> getFunctionAreaPoints(Point from, Point to,
			Plot plot) {
		List<Point> pl = new ArrayList<Point>();
		if (from != null && to != null && plot != null) {
			for (List<Point> list : plot) {
				if (list != null && list.size() > 0) {
					for (Point point : list) {
						if (point.compareToX(from) >= 0
								&& point.compareTo(to) <= 0) {
							pl.add(point);
						}
					}
				}
			}
		}
		return pl;
	}

	@SuppressWarnings("unused")
	private static Point findFirstPointInList(List<Point> ip, Point object) {
		if (ip != null && ip.size() > 0 && object != null) {
			for (Point o : ip) {
				if (o.equals(object))
					return o;
			}
		}
		return null;
	}


	@SuppressWarnings("unused")
	private static Point getListPointAtX(double x, List<Point> list) {

		Point lp = null;
		if (list != null && list.size() > 0) {

			for (Point point : list) {
				if (x == point.x)
					return point;
				else if (x > point.x)
					lp = point;
				else if (x < point.x)
					return lp;
			}
		}
		return lp;
	}

	public static Element getFillPattern(SvgDocument doc) {

		/**
		 * <pattern id="diagonal_line1_SP_T" width="7.18420489685mm"
		 * height="0.5mm" patternUnits="userSpaceOnUse"
		 * patternTransform="rotate(-45) translate(-2 -2)"> <rect x="0" y="0"
		 * width="100%" height="100%" fill="white" stroke="none"/> <line
		 * x1="1.8288mm" y1="0" x2="1.8288mm" y2="0.5mm" stroke="black"
		 * stroke-width="0.8mm"/> </pattern>
		 * 
		 * <pattern id="diagonal_line2_PD" width="3.535533905932738mm"
		 * height="0.5mm" patternUnits="userSpaceOnUse"
		 * patternTransform="rotate(45)"> <rect x="0" y="0" width="100%"
		 * height="100%" fill="white" stroke="none"/> <line x1="1.8mm" y1="0"
		 * x2="1.8mm" y2="0.5mm" stroke="black" stroke-width="1.3mm"/>
		 * </pattern>
		 * 
		 * <pattern id="diagonal_line2_SP_T" width="3.5921024484mm"
		 * height="0.5mm" patternUnits="userSpaceOnUse"
		 * patternTransform="rotate(45) translate(-2 )"> <rect x="0" y="0"
		 * width="100%" height="100%" fill="white" stroke="none"/> <line
		 * x1="1.016mm" y1="0" x2="1.016mm" y2="0.5mm" stroke="black"
		 * stroke-width="0.8mm"/> </pattern>
		 */

		/**
		 * <pattern id="diagonal_line1_PD" width="7.071067811865475mm"
		 * height="0.5mm" patternUnits="userSpaceOnUse"
		 * patternTransform="rotate(-45)"> <rect x="0" y="0" width="100%"
		 * height="100%" fill="white" stroke="none"/> <line x1="1.8mm" y1="0"
		 * x2="1.8mm" y2="0.5mm" stroke="black" stroke-width="1.3mm"/>
		 * </pattern>
		 */
		Element g = doc.createGroup();
		
		
		Element pattern1 = doc.createElement("pattern");
		pattern1.setAttribute("id", "diagonal_line1_PD");
		pattern1.setAttribute("width", "7.071067811865475");
		pattern1.setAttribute("height", "0.5");
		pattern1.setAttribute("patternUnits", "userSpaceOnUse");
		pattern1.setAttribute("patternTransform", "rotate(-45)");

		Element rect = doc.createElement("rect");
		rect.setAttribute("x", "0");
		rect.setAttribute("y", "0");
		rect.setAttribute("width", "100%");
		rect.setAttribute("height", "100%");
		rect.setAttribute("fill", "white");
		rect.setAttribute("stroke", "none");

		Element line = doc.createElement("line");
		line.setAttribute("x1", "1.8");
		line.setAttribute("y1", "0");
		line.setAttribute("x2", "1.8");
		line.setAttribute("y2", "0.5");
		line.setAttribute("stroke", "black");
		line.setAttribute("stroke-width", "1.3");

		pattern1.appendChild(rect);
		pattern1.appendChild(line);
				
		
		Element pattern2 = doc.createElement("pattern");
		pattern2.setAttribute("id", "diagonal_line1_PD_");
		pattern2.setAttribute("width", "7.071067811865475");
		pattern2.setAttribute("height", "0.5");
		pattern2.setAttribute("patternUnits", "userSpaceOnUse");
		pattern2.setAttribute("patternTransform", "rotate(45)");

		Element rect2 = doc.createElement("rect");
		rect2.setAttribute("x", "0");
		rect2.setAttribute("y", "0");
		rect2.setAttribute("width", "100%");
		rect2.setAttribute("height", "100%");
		rect2.setAttribute("fill", "white");
		rect2.setAttribute("stroke", "none");

		Element line2 = doc.createElement("line");
		line2.setAttribute("x1", "1.8");
		line2.setAttribute("y1", "0");
		line2.setAttribute("x2", "1.8");
		line2.setAttribute("y2", "0.5");
		line2.setAttribute("stroke", "black");
		line2.setAttribute("stroke-width", "1.3");

		pattern2.appendChild(rect2);
		pattern2.appendChild(line2);
		
		g.appendChild(pattern1);
		g.appendChild(pattern2);
		
		
		return g;
	}
}