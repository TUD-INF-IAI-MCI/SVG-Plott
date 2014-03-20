package tud.tangram.svgplot.plotting;

import com.beust.jcommander.IStringConverter;

import tud.tangram.svgplot.coordinatesystem.Range;

public class IntegralPlotSettings {

	public Range xRange;
	public int function1;
	public int function2;
	public String name;
	
	public IntegralPlotSettings(int f1, int f2, String name, Range xRange) {
		this.function1 = f1;this.function2 = f2; this.xRange = xRange; this.name = name;
	}

	public static class Converter implements IStringConverter<IntegralPlotSettings> {
	
		public IntegralPlotSettings convert(String value) {				
			if(value != null && !value.isEmpty()){
				String name = "";
				int f1 = -1, f2 = -1;
				Range r = null;
				
				// "Wahrscheinlichkeit::1,2[-2:2]"
				String[] s = value.split("::");
				if(s.length > 0 ){						
					if(s.length > 1){
						name = s[0];
						value = s[1];
					}
				}
				
				s = value.split("\\[");
				if(s.length > 0 ){						
					if(s.length > 1){							
						value = s[1];
						}							
						
						s[0] = s[0].replaceAll("[^\\d.,]", "");
						String[] f = s[0].split(",");
						
						if(f.length >0){
							try {
								if (f[0] != "" && !f[0].isEmpty())
									f1 = Integer.parseInt(f[0]) -1;									
							} catch (Exception e) {}
							
							if(f.length > 1 && f[1] != "" && !f[1].isEmpty()){
								try {
									if (f[1] != "" && !f[1].isEmpty())
										f2 = Integer.parseInt(f[1]) -1;									
								} catch (Exception e) {}
							}									
						}							
					
				}
				
				value = value.replaceAll("[^\\d.,^\\s+,^\\:,-]","");
				s = value.split(":");
				if(s.length > 0 ){	
					double from, to;
					
					try {
						from = Double.parseDouble(s[0]);
					} catch (Exception e) {
						from = Double.MIN_NORMAL + 100;
					}
					to = Double.MIN_NORMAL - 100;
					if(s.length > 1){							
						try {
							to = Double.parseDouble(s[1]);
						} catch (Exception e) {}
					}
					r = new Range(Math.min(from, to), Math.max(from, to));						
				}					
				return new IntegralPlotSettings(f1,f2,name,r);					
			}
			return null;				
		}
	}	
}
