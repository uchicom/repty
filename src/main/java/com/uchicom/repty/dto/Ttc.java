package com.uchicom.repty.dto;

public class Ttc {

	private String ttc;
	private boolean resource;
	
	public Ttc(String ttc) {
		this.ttc = ttc;
	}
	public Ttc(String ttc, boolean resource) {
		this.ttc = ttc;
		this.resource = resource;
	}
	public String getTtc() {
		return ttc;
	}
	public void setTtc(String ttc) {
		this.ttc = ttc;
	}
	public boolean isResource() {
		return resource;
	}
	public void setResource(boolean resource) {
		this.resource = resource;
	}
}
