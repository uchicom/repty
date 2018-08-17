package com.uchicom.repty.dto;

public class Text {

	String colorKey;
	String fontKey;
	public Text(String colorKey, String fontKey) {
		this.colorKey = colorKey;
		this.fontKey = fontKey;
	}
	public String getColorKey() {
		return colorKey;
	}
	@Override
	public String toString() {
		return "Text [colorKey=" + colorKey + ", fontKey=" + fontKey + "]";
	}
	public void setColorKey(String colorKey) {
		this.colorKey = colorKey;
	}
	public String getFontKey() {
		return fontKey;
	}
	public void setFontKey(String fontKey) {
		this.fontKey = fontKey;
	}
}
