package com.uchicom.repty.dto;

public class Ttf {

	private String ttf;
	private boolean resource;
	
	public Ttf(String ttf) {
		this.ttf = ttf;
	}
	public Ttf(String ttf, boolean resource) {
		this.ttf = ttf;
		this.resource = resource;
	}
	public String getTtf() {
		return ttf;
	}
	public void setTtf(String ttf) {
		this.ttf = ttf;
	}
	public boolean isResource() {
		return resource;
	}
	public void setResource(boolean resource) {
		this.resource = resource;
	}
}
