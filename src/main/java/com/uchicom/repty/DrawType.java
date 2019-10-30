package com.uchicom.repty;

import java.util.Arrays;

public enum DrawType {

	LINE("line"), RECTANGLE("rectangle"), TEXT("text"), IMAGE("image"), BYTE_IMAGE("byteImage"), FORM("form"), RECORD_STRING("recordString"),
	RECORD_LINE("recordLine"), RECORD_RECTANGLE("recordRectangle"), OFFSET_STRING("offsetString"), OBJECT("object");

	private final String type;

	private DrawType(String type) {
		this.type = type;
	}

	public static DrawType is(String drawType) {
		return Arrays.stream(values()).filter(value -> drawType.equals(value.type)).findFirst().get();
	}
}
