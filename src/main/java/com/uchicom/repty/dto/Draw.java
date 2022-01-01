// (C) 2018 uchicom
package com.uchicom.repty.dto;

import com.uchicom.repty.type.DrawKind;
import java.util.List;

/**
 * 描画情報.
 *
 * @author shigeki.uchiyama
 */
public class Draw {

  String type;
  String key;
  String list;
  boolean repeated;
  List<Value> values;

  DrawKind drawKind;

  @Override
  public String toString() {
    return "Draw [type=" + type + ", key=" + key + ", values=" + values + "]";
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
    drawKind = DrawKind.is(type);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public boolean isRepeated() {
    return repeated;
  }

  public void setRepeated(boolean repeated) {
    this.repeated = repeated;
  }

  public List<Value> getValues() {
    return values;
  }

  public void setValues(List<Value> values) {
    this.values = values;
  }

  public String getList() {
    return list;
  }

  public void setList(String list) {
    this.list = list;
  }

  public DrawKind getDrawKind() {
    return drawKind;
  }
}
