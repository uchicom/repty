package com.uchicom.repty.dto;

public class Line {

	@Override
	public String toString() {
		return "Line [colorKey=" + colorKey + ", width=" + width + ", type=" + type + "]";
	}
	public Line(String colorKey, float width, int type) {
		super();
		this.colorKey = colorKey;
		this.width = width;
		this.type = type;
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
	int type;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
