// (C) 2018 uchicom
package com.uchicom.repty.type;

import java.util.Arrays;

public enum DrawKind {

  /** 線. */
  LINE("line"),
  /** 矩形. */
  RECTANGLE("rectangle"),
  /** 文字列. */
  TEXT("text"),
  /** 画像. */
  IMAGE("image"),
  BYTE_IMAGE("byteImage"),
  FORM("form"),
  RECORD_STRING("recordString"),
  RECORD_LINE("recordLine"),
  RECORD_RECTANGLE("recordRectangle"),
  OFFSET_STRING("offsetString"),
  OBJECT("object");

  private final String type;

  private DrawKind(String type) {
    this.type = type;
  }

  public static DrawKind is(String drawType) {
    return Arrays.stream(values()).filter(value -> drawType.equals(value.type)).findFirst().get();
  }

  public void draw() {}
}
