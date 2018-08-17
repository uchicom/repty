package com.uchicom.repty.dto;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.Map;

public class Spec {

	@Override
	public String toString() {
		return "Spec [colorMap=" + colorMap + ", lineMap=" + lineMap + ", fontMap=" + fontMap + "]";
	}

	Map<String, Color> colorMap;
	public Map<String, Color> getColorMap() {
		return colorMap;
	}
	public void setColorMap(Map<String, Color> colorMap) {
		this.colorMap = colorMap;
	}
	
	public Map<String, Line> getLineMap() {
		return lineMap;
	}
	public void setLineMap(Map<String, Line> lineMap) {
		this.lineMap = lineMap;
	}

	Map<String, Line> lineMap;
	Map<String, Font> fontMap;
	Map<String, Text> textMap;
	Map<String, URL> imageMap;
	public Map<String, URL> getImageMap() {
		return imageMap;
	}
	public void setImageMap(Map<String, URL> imageMap) {
		this.imageMap = imageMap;
	}
	public Map<String, Font> getFontMap() {
		return fontMap;
	}
	public void setFontMap(Map<String, Font> fontMap) {
		this.fontMap = fontMap;
	}
	public Map<String, Text> getTextMap() {
		return textMap;
	}
	public void setTextMap(Map<String, Text> textMap) {
		this.textMap = textMap;
	}
	

}
