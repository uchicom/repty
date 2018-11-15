package com.uchicom.repty.dto;

import java.awt.Color;
import java.util.Map;

public class Resource {

	@Override
	public String toString() {
		return "Spec [colorMap=" + colorMap + ", lineMap=" + lineMap + ", fontMap=" + fontMap + ", ttcMap=" + ttcMap + "]";
	}

	Map<String, Color> colorMap;
	Map<String, Line> lineMap;
	Map<String, Image> imageMap;
	Map<String, Font> fontMap;
	Map<String, Text> textMap;
	Map<String, Ttc> ttcMap;
	Map<String, Ttf> ttfMap;

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

	public Map<String, Image> getImageMap() {
		return imageMap;
	}

	public void setImageMap(Map<String, Image> imageMap) {
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

	public Map<String, Ttc> getTtcMap() {
		return ttcMap;
	}

	public void setTtcMap(Map<String, Ttc> ttcMap) {
		this.ttcMap = ttcMap;
	}

	public Map<String, Ttf> getTtfMap() {
		return ttfMap;
	}

	public void setTtfMap(Map<String, Ttf> ttfMap) {
		this.ttfMap = ttfMap;
	}

}
