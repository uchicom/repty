package com.uchicom.repty.dto;

/**
 * 改行のnextY
 * 繰り返しのnextYを区別する
 * @author hex
 *
 */
public class Value {

	float x1;
	float y1;
	float x2;
	float y2;
	/** 繰り返しで使用 */
	float nextX;
	/** 繰り返しで使用 */
	float nextY;

	/** 改行で使用 */
	float limitX;

	/** 改行で使用 */
	float newLineY;
	boolean fill;
	String memberName;
	String paramName;
	String value;
	/** 縦横寄せ 00(デフォルト下段左),1:中央,2:右,10:中段,20:上段 */
	int align;
	boolean repeat;

	/**
	 * 
	 * @param x1 開始x
	 * @param y1 開始y
	 * @param x2 終了x
	 * @param y2 終了y
	 */
	public Value(float x1,float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	public Value(int x1,int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * 
	 * @param x1 開始x
	 * @param y1 開始y
	 * @param x2 終了x
	 * @param y2 終了y
	 * @param fill 塗りつぶしフラグ
	 */
	public Value(float x1, float y1, float x2, float y2, boolean fill) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.fill = fill;
	}
	public Value(int x1, int y1, int x2, int y2, boolean fill) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.fill = fill;
	}


	/**
	 * 
	 * @param x1 開始x
	 * @param y1 開始y
	 * @param value 文字列
	 */
	public Value(float x1, float y1, String value) {
		this.x1 = x1;
		this.y1 = y1;
		this.value = value;
	}
	public Value(int x1, int y1, String value) {
		this.x1 = x1;
		this.y1 = y1;
		this.value = value;
	}
	/**
	 * 
	 * @param x1 開始x
	 * @param y1 開始y
	 * @param value 文字列
	 * @param align 1:左,2:中央,3:右
	 */
	public Value(float x1, float y1, String value, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.value = value;
		this.align = align;
	}
	public Value(int x1, int y1, String value, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.value = value;
		this.align = align;
	}
	/**
	 * 改行用
	 * @param x1
	 * @param y1
	 * @param limitX
	 * @param newLineY
	 * @param value
	 * @param align
	 */
	public Value(int x1, int y1, int limitX, int newLineY, String value, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.value = value;
		this.align = align;
	}
	/**
	 * 
	 * @param x1 開始x
	 * @param y1 開始y
	 * @param limitX 終了x
	 * @param newLineY 次行へのオフセット高さ（複数行の場合に使用）
	 * @param value 文字列
	 */
	public Value(float x1, float y1, float limitX, float newLineY, String value) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.value = value;
	}
	public Value(int x1, int y1, int limitX, int newLineY, String value) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.value = value;
	}

	public Value(float x1, float y1) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1;
		this.y2 = y1;
	}
	public Value(int x1, int y1) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1;
		this.y2 = y1;
	}

	public Value(float x1, float y1, float nextX, float nextY, String memberName, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
	}
	public Value(int x1, int y1, int nextX, int nextY, String memberName, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
	}
	public Value(float x1, float y1, float nextX, float nextY, String memberName, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//offsetString用
	}
	public Value(int x1, int y1, int nextX, int nextY, String memberName, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//offsetString用
	}
	public Value(float x1, float y1, float nextX, float nextY, String memberName, String paramName, boolean repeat, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//offsetString用;
		this.align = align;//offsetString用
	}
	public Value(int x1, int y1, int nextX, int nextY, String memberName, String paramName, boolean repeat, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//offsetString用;
		this.align = align;//offsetString用
	}
	public Value(float x1, float y1, float nextX, float nextY, String memberName, String paramName, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.align = align;//offsetString用
	}
	public Value(int x1, int y1, int nextX, int nextY, String memberName, String paramName, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.align = align;//offsetString用
	}

	public Value(float x1, float y1, float limitX, float newLineY, float nextX, float nextY, String memberName, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
	}
	public Value(int x1, int y1, int limitX, int newLineY, int nextX, int nextY, String memberName, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
	}
	public Value(float x1, float y1, float limitX, float newLineY, float nextX, float nextY, String memberName, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//lastFlgのがいいかも
	}
	public Value(int x1, int y1, int limitX, int newLineY, int nextX, int nextY, String memberName, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//lastFlgのがいいかも
	}
	public Value(float x1, float y1, float limitX, float newLineY, float nextX, float nextY, String memberName, String paramName, boolean repeat, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;//lastFlgのがいいかも
		this.align = align;
	}
	public Value(int x1, int y1, int limitX, int newLineY, int nextX, int nextY, String memberName, String paramName, boolean repeat, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.repeat = repeat;
		this.align = align;
	}
	public Value(float x1, float y1, float limitX, float newLineY, float nextX, float nextY, String memberName, String paramName, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.align = align;
	}
	public Value(int x1, int y1, int limitX, int newLineY, int nextX, int nextY, String memberName, String paramName, int align) {
		this.x1 = x1;
		this.y1 = y1;
		this.limitX = limitX;
		this.newLineY = newLineY;
		this.nextX = nextX;
		this.nextY = nextY;
		this.memberName = memberName;
		this.paramName = paramName;
		this.align = align;
	}
	public Value(float x1, float y1, float x2, float y2, float nextX, float nextY, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
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
	public Value(float x1, float y1, float x2, float y2, float nextX, float nextY, boolean fill, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.fill = fill;
		this.paramName = paramName;
	}
	public Value(int x1, int y1, int x2, int y2, int nextX, int nextY, boolean fill, String paramName) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.fill = fill;
		this.paramName = paramName;
	}

	public Value(float x1, float y1,float x2, float y2, float nextX, float nextY, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.paramName = paramName;
		this.repeat = repeat;
	}
	public Value(int x1, int y1,int x2, int y2, int nextX, int nextY, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.paramName = paramName;
		this.repeat = repeat;
	}

	public Value(float x1, float y1,float x2, float y2, float nextX, float nextY, boolean fill, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.fill = fill;
		this.paramName = paramName;
		this.repeat = repeat;
	}
	public Value(int x1, int y1,int x2, int y2, int nextX, int nextY, boolean fill, String paramName, boolean repeat) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.nextX = nextX;
		this.nextY = nextY;
		this.fill = fill;
		this.paramName = paramName;
		this.repeat = repeat;
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

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
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

	public float getNextX() {
		return nextX;
	}

	public void setNextX(float nextX) {
		this.nextX = nextX;
	}

	public float getNextY() {
		return nextY;
	}

	public void setNextY(float nextY) {
		this.nextY = nextY;
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	//機能
	public int getAlignX() {
		return align % 10;
	}
	public int getAlignY() {
		return align / 10;
	}
	public float getLimitX() {
		return limitX;
	}
	public void setLimitX(float limitX) {
		this.limitX = limitX;
	}
	public float getNewLineY() {
		return newLineY;
	}
	public void setNewLineY(float newLineY) {
		this.newLineY = newLineY;
	}
}
