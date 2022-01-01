// (C) 2018 uchicom
package com.uchicom.repty.type;

import java.util.Arrays;
import java.util.function.BiFunction;

import com.uchicom.repty.Repty;
import com.uchicom.repty.draw.ByteImageDrawer;
import com.uchicom.repty.draw.Drawer;
import com.uchicom.repty.draw.ImageDrawer;
import com.uchicom.repty.draw.LineDrawer;
import com.uchicom.repty.draw.OffsetStringDrawer;
import com.uchicom.repty.draw.RecordStringDrawer;
import com.uchicom.repty.draw.RectangleDrawer;
import com.uchicom.repty.draw.TextDrawer;
import com.uchicom.repty.dto.Draw;

public enum DrawKind {

  /** 線. */
  LINE("line", (repty, draw) -> new LineDrawer(repty, draw)),
  /** 矩形. */
  RECTANGLE("rectangle", (repty, draw) -> new RectangleDrawer(repty, draw)),
  /** 文字列. */
  TEXT("text", (repty, draw) -> new TextDrawer(repty, draw)),
  /** 画像. */
  IMAGE("image", (repty, draw) -> new ImageDrawer(repty, draw)),
  BYTE_IMAGE("byteImage", (repty, draw) -> new ByteImageDrawer(repty, draw)),
  FORM("form", (repty, draw) -> new ByteImageDrawer(repty, draw)),
  RECORD_STRING("recordString", (repty, draw) -> new RecordStringDrawer(repty, draw)), 
  OFFSET_STRING("offsetString", (repty, draw) -> new OffsetStringDrawer(repty, draw)), OBJECT("object", (repty, draw) -> new TextDrawer(repty, draw));

  private final String type;
  private final BiFunction<Repty, Draw, Drawer> drawerCreator;

  private DrawKind(String type, BiFunction<Repty, Draw, Drawer> drawerCreator) {
    this.type = type;
    this.drawerCreator = drawerCreator;
  }

  public static DrawKind is(String drawType) {
    return Arrays.stream(values()).filter(value -> drawType.equals(value.type)).findFirst().get();
  }

  public Drawer createDrawer(Repty repty, Draw draw) {
    return drawerCreator.apply(repty, draw);
  }
}
