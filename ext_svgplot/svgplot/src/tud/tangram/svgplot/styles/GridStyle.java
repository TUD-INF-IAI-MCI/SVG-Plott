package tud.tangram.svgplot.styles;

public enum GridStyle {
	NONE, HORIZONTAL, VERTICAL, FULL;

	public boolean showHorizontal() {
		return this.equals(FULL) || this.equals(HORIZONTAL);
	}
	
	public boolean showVertical() {
		return this.equals(FULL) || this.equals(VERTICAL);
	}

	public static GridStyle fromStrings(String horizontal, String vertical) {
		if(horizontal == null && vertical == null)
			return null;
		if(horizontal == null || "off".equals(horizontal))
			return "on".equals(vertical) ? VERTICAL : NONE;
		if(vertical == null || "off".equals(vertical))
			return "on".equals(horizontal) ? HORIZONTAL : NONE;
		return FULL;
	}
}