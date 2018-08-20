package com.uchicom.repty.dto;

public class Font {

	private String ttc;
	private String name;
	private int type;
	private int size;
	
	private Font(String ttc, String name, int type, int size) {
		this.ttc = ttc;
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
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
