package com.uchicom.repty.dto;

public class Value {

	int x1;
	int y1;
	int x2;
	int y2;
	int nextX;
	int nextY;
	boolean fill;
	String memberName;
	String paramName;

	/**
	 * 座標2つ
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public Value(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * 座標２つと塗りつぶしフラグ
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param fill
	 */
	public Value(int x1, int y1, int x2, int y2, boolean fill) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.fill = fill;
	}
	String value;

	/**
	 * 座標と文字列
	 * @param x1
	 * @param y1
	 * @param value
	 */
	public Value(int x1, int y1, String value) {
		this.x1 = x1;
		this.y1 = y1;
		this.value = value;
	}

	/**
	 * 座標のみ
	 * @param x1
	 * @param y1
	 */
	public Value(int x1, int y1) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1;
		this.y2 = y1;
	}
	
	/**
	 * リストの値出力
	 * @param x1
	 * @param y1
	 * @param nextX2
	 * @param nextY2
	 */
	public Value(int x1, int y1, int nextX, int nextY, String memberName, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
	}

	public Value(int x1, int y1, int x2, int y2, int nextX, int nextY, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.paramName = paramName;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Value [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + ", value=" + value + "]";
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public int getNextX() {
		return nextX;
	}

	public void setNextX(int nextX) {
		this.nextX = nextX;
	}

	public int getNextY() {
		return nextY;
	}

	public void setNextY(int nextY) {
		this.nextY = nextY;
	}
}
