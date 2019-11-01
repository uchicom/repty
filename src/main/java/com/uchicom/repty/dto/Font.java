// (c) 2018 uchicom
package com.uchicom.repty.dto;

public class Font {

	private String fontFileKey;
	private String name;
	private int type;
	private float size;
	private String encoding;

	public Font(String fontFileKey, String name, int type, int size) {
		this.fontFileKey = fontFileKey;
		this.name = name;
		this.type = type;
		this.size = size;
	}
	public Font(String fontFileKey, String name, int type, float size) {
		this.fontFileKey = fontFileKey;
		this.name = name;
		this.type = type;
		this.size = size;
	}
	public Font(String name, int type, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}
	public Font(String name, int type, float size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}

	public Font(String fontFileKey, String name, int type, int size, String encoding) {
		this.fontFileKey = fontFileKey;
		this.name = name;
		this.type = type;
		this.size = size;
		this.encoding = encoding;
	}
	public Font(String fontFileKey, String name, int type, float size, String encoding) {
		this.fontFileKey = fontFileKey;
		this.name = name;
		this.type = type;
		this.size = size;
		this.encoding = encoding;
	}
	public Font(String name, int type, int size, String encoding) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.encoding = encoding;
	}
	public Font(String name, int type, float size, String encoding) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.encoding = encoding;
	}
	public String getFontFileKey() {
		return fontFileKey;
	}
	public void setFontFileKey(String fontFileKey) {
		this.fontFileKey = fontFileKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
