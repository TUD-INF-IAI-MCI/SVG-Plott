package tud.tangram.svgplot.plotting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;

import tud.tangram.svgplot.coordinatesystem.Point;
import tud.tangram.svgplot.coordinatesystem.Range;

/**
 * 
 * @author Gregor Harlan
 * Idea and supervising by Jens Bornschein jens.bornschein@tu-dresden.de
 * Copyright by Technische Universität Dresden / MCI 2014
 *
 */
public class Gnuplot {

	private String executable;

	private int sample = 100;
	private Range xrange = new Range(10, 10);
	private Range yrange = new Range(10, 10);
	private boolean pi = false;

	public Gnuplot(String executable) {
		if (executable == null) {
			this.executable = findExecutable();
		} else {
			this.executable = executable;
		}
	}

	public void setSample(int sample) {
		this.sample = sample;
	}

	public void setXRange(Range xrange, boolean pi) {
		this.xrange = xrange;
		this.pi = pi;
	}

	public void setYRange(Range yrange) {
		this.yrange = yrange;
	}

	public List<List<Point>> plot(String function) throws IOException, InterruptedException {
		String command = "set table; \n";
		command += "set sample " + sample + "; ";
		String pi = this.pi ? "*pi" : "";
		command += "set xrange [" + xrange.from + pi + ":" + xrange.to + pi + "]; ";
		command += "set yrange [" + yrange.from + ":" + yrange.to + "]; ";
		//call gnuplot with command
		Process p = Runtime.getRuntime().exec(new String[] { executable, "-e", command, "-e", "plot " + function });
		BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;

		Point point;
		Point previous = null;
		Scanner scanner;
		List<List<Point>> list = new ArrayList<>();
		List<Point> sublist = null;
		boolean newList = true;
		boolean nextNewList = true;
		
		/*
		 * When table mode is enabled, ‘plot‘ and ‘splot‘ commands print out a multicolumn 
		 * ASCII table of X Y {Z} R values rather than creating an actual plot on the current 
		 * terminal. The character R takes on one of three values: "i" if the point is in the 
		 * active range, "o" if it is out-of-range, or "u" if it is undefined. The data 
		 * format is determined by the format of the axis labels (see ‘set format‘), and the 
		 * columns are separated by single spaces.
		 */
		
		//read gnuplot
		while ((line = bri.readLine()) != null) {
			//System.out.println(line);
			
			if (line.trim().length() == 0 || line.startsWith("#"))
				continue;
			scanner = new Scanner(line);
			scanner.useLocale(Locale.ENGLISH);
			point = new Point(scanner.nextDouble(), scanner.nextDouble());
			if (this.pi) {
				point.x /= Math.PI;
			}
			String type = scanner.next();
			if (type.equals("u")) {
				newList = true;
				previous = null;
				continue;
			}
			newList = nextNewList;
			if (type.equals("o")) {
				if (nextNewList) {
					previous = point;
					continue;
				}
				nextNewList = true;
			} else {
				if (previous != null) {
					if (newList) {
						sublist = new ArrayList<>();
						list.add(sublist);
					}
					sublist.add(previous);
					newList = false;
					previous = null;
				}
				nextNewList = false;
			}
			if (newList) {
				sublist = new ArrayList<>();
				list.add(sublist);
			}
			sublist.add(point);
			scanner.close();
		}
		bri.close();
		if (!list.isEmpty() && list.get(list.size() - 1).isEmpty()) {
			list.remove(list.size() - 1);
		}

		while ((line = bre.readLine()) != null) {
			System.err.println(line);
		}
		bre.close();

		p.waitFor();

		return list;
	}

	private String findExecutable() {
		// copied from https://github.com/marcushere/JavaPlot/blob/master/src/com/panayotis/io/FileUtils.java

		String[] xtrapath = { "/bin", "/usr/bin", "/usr/local/bin", "/sbin", "/usr/sbin", "/usr/local/sbin", "/opt/bin", "/opt/local/bin", "/opt/sbin", "/opt/local/sbin", "/sw/bin", "c:\\cygwin\\bin", "." };

		String pathsep = System.getProperty("path.separator");
		String fileexec = System.getProperty("file.separator") + "gnuplot";

		/* Create enriched path */
		StringBuffer path = new StringBuffer();
		path.append(System.getenv("PATH"));
		for (int i = 0; i < xtrapath.length; i++) {
			path.append(pathsep).append(xtrapath[i]);
		}

		StringTokenizer st = new StringTokenizer(path.toString(), pathsep);
		while (st.hasMoreTokens()) {
			String filepath = st.nextToken() + fileexec;
			File file = new File(filepath);
			if (file.isFile() && file.canRead())
				return filepath;
			file = new File(filepath + ".exe");
			if (file.isFile() && file.canRead())
				return filepath + ".exe";
		}
		return null;
	}

}
