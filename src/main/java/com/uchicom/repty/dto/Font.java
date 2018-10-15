package com.uchicom.repty.dto;

public class Font {

	private String ttc;
	private String name;
	private int type;
	private float size;

	public Font(String ttc, String name, int type, int size) {
		this.ttc = ttc;
		this.name = name;
		this.type = type;
		this.size = size;
	}
	public Font(String ttc, String name, int type, float size) {
		this.ttc = ttc;
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

	public String getTtc() {
		return ttc;
	}
	public void setTtc(String ttc) {
		this.ttc = ttc;
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
}
