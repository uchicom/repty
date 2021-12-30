// (c) 2018 uchicom
package com.uchicom.repty.dto;

/**
 * メタ情報.
 * 
 * @author shigeki.uchiyama
 *
 */
public class Meta {

	private float x;
	private float y;
	private float width;
	private float height;
	private String pdRectangle;
	private boolean landscape;

	public String getPdRectangle() {
		return pdRectangle;
	}

	public void setPdRectangle(String pdRectangle) {
		this.pdRectangle = pdRectangle;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public void setLandscape(boolean landscape) {
		this.landscape = landscape;
	}
}
