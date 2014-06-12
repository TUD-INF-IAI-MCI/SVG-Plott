SVG-Plott
=========

**Import!** This is not the git of [JavaScript API - SVGPlot of SVGKIT] [svgkit] 

SVG-Plott is a Java-program which uses *[gnuplot]*. Gregor Harlan started the development during his assignment. 
This program generates and creates a svg-file for blind user. After the creation the file can be printed on a tactile printer. Our system just generates the svg-file. If you want the see the svg, you has to open the file in a browser, e.g. Internet Explorer, Chrome, Mozilla Firefox.

## Requirements

- download and install gnuplot v4.6 [gnuplot]
- download and install Java
- add the Java and gnuplat to PATH-Variable

## Commands

For creation a SVG-File 

	java -jar svgplot.jar [options] function1 [function2 [function3]]  

### Options

By using the following options the output can be configured.

Option:

| option       |                         description                          |
|:-------------|:-------------------------------------------------------------|
|  --title, -t |                    title of the graphic                      |
|  --size, -s  |  size of the graphic in millimeter (Default: A4 = 210,297)   |
| --xrange, -x | range of x-axis (Default: -8:8) and alternative title Titel, e.g. [--xrange "Jahre::-35"]|
|--yrange, -y  | range of y-axis (Default: -8:8) and alternative title (e.g. [--yrange -3:5])|											  |
|--pi, -p  	   | division of the x-axis by multiples of pi (Default: false)|
|--xlines	   | help lines of the x-axis, seperated by spaces (e.g. [--xlines "1 3.5"])|
|--ylines	   | help lines of the y-axis, seperated by spaces				   |
|--css, -c	   | direct input of css.code or path to a css-file (e.g. [--css "#grid { stroke: #444444 }"] or [--css stylesheet.css])|			
|--points, --pts|	point list, which should be marked in the graphic. Multiple lists seperated by { }. Points are separated by spaces, x and y are separated by **,** . List can be named (e.g. ["list 1"::{1.2,3 4.5,6}{-1,-2.3}])|
|--integral, -i|draws a integral area of the functions or of the x-axis (e.g. [--integral "probability::1,2[-2:2]" ])
|--gnuplot, -g | path of gnuplot (e.g. [--gnuplot "C:\gnuplot.exe"])    	  |
|--output, -o  | output path 												  |
|--help, -h	   | Help	 													  |


## Examples

Coming soon ... 

[gnuplot]: http://gnuplot.info
[svgkit]: http://svgkit.sourceforge.net/SVGPlot.html
