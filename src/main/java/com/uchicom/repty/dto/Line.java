// (c) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.Arrays;

public class Line {

	public Line(String colorKey, float width) {
		super();
		this.colorKey = colorKey;
		this.width = width;
	}

	public Line(String colorKey, float width, float[] pattern, float phase) {
		super();
		this.colorKey = colorKey;
		this.width = width;
		this.pattern = pattern;
		this.phase = phase;
	}
	public String getColorKey() {
		return colorKey;
	}
	public void setColorKey(String colorKey) {
		this.colorKey = colorKey;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	String colorKey;
	float width;
	float[] pattern;
	float phase;
	
	public float[] getPattern() {
		return pattern;
	}
	public void setPattern(float[] pattern) {
		this.pattern = pattern;
	}
	public float getPhase() {
		return phase;
	}
	public void setPhase(float phase) {
		this.phase = phase;
	}
	@Override
	public String toString() {
		return "Line [colorKey=" + colorKey + ", width=" + width + ", pattern=" + Arrays.toString(pattern) + ", phase="
				+ phase + "]";
	}
	
}
