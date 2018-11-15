package com.uchicom.repty.dto;

public class Image {

	private String image;
	private boolean resource;
	
	public Image(String image) {
		this.image = image;
	}
	public Image(String image, boolean resource) {
		this.image = image;
		this.resource = resource;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public boolean isResource() {
		return resource;
	}
	public void setResource(boolean resource) {
		this.resource = resource;
	}
}
