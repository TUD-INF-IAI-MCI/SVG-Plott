SVG-Plott
=========

**Import!** This is not the git of [JavaScript API - SVGPlot of SVGKIT][svgkit] 

SVG-Plott is a Java-program which uses *[gnuplot]*. Gregor Harlan started the development during his assignment supervised by Jens Bornschein and Denise Prescher. The Tangram team of the TU Dresden extend this project with several new features. 
This program generates and creates an svg-file for blind users. After the creation the file can be printed on a tactile printer. Our system just generates the svg-file. If you want the see the svg, you have to open the file in a browser, e.g. Internet Explorer, Chrome, Mozilla Firefox.

The program generates 3 files:

- title.svg
- title_desc.html
- title_legend.svg

**Hint** Title can be set by using the option --title name (see [options](#options))

## Requirements

- download and install gnuplot v4.6 [gnuplot] or higher
- download and install Java
- add the Java and gnuplot to PATH-Variable
- Braille Font **Braille DE Computer** by [Viewplus Website][viewplus]

## Commands

For creating an SVG-File 

	java -jar svg-plott.jar [options] function1 [function2 [function3]]  

### Options

By using the following options the output can be configured.

Option:

| option       |                         description                          |
|:-------------|:-------------------------------------------------------------|
|  --title, -t |                    title of the graphic                      |
|  --size, -s  |  size of the graphic in millimeter (Default: A4 = 210,297)   |
| --xrange, -x | range of x-axis (Default: -8:8) and alternative title Titel, e.g. [--xrange "Years::-3:5"]|
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

Here you can find some example program calls and the resulting images.


###Example 1

	java -jar svg-plott.jar -o example_1.svg -t "example 1" "(x^3-3x^2-10x+12)/6" "-0.25x^2+1"
	
	
	
![two graphs (x^3-3x^2-10x+12)/6 and  -0.25x^2+1](./examples/graphs/generated_graph/example_1_graph/example_1.svg){:height="25%" width="25%"}

###Example 2

	java -jar svg-plott.jar -o example2.svg -t "example 2" -x -4:8 -y -6:6 "(x^5-12x^4+35x^3+20x^2-156x+168)/56" "-x+3" "-(x-2)^2+3"
 
 ![](./examples/graphs/generated_graph/example_2_graph/example_2.svg){:height="25%" width="25%"}

###Example 3

	java -jar svg-plott.jar -o example_3.svg -t "example 3" -x -6:10 -y -6:10 "1/(x-2)+2" "-0.3(x-3)^2+6"
 
 ![](./examples/graphs/generated_graph/example_3_graph/example_3.svg){:height="100px" width="50px"}




[gnuplot]: http://gnuplot.info
[svgkit]: http://svgkit.sourceforge.net/SVGPlot.html
[viewplus]: http://viewplus.com
