// (C) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.uchicom.repty.Repty;
import com.uchicom.repty.draw.Drawer;

/**
 * 処理単位.
 *
 * @author shigeki.uchiyama
 */
public class Unit {

  public Meta meta;

  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }

  public List<Draw> drawList;

  public List<Draw> getDrawList() {
    return drawList;
  }

  public void setDrawList(List<Draw> drawList) {
    this.drawList = drawList;
  }

  public List<Drawer> getDrawerList(Repty repty) {
    return drawList.stream()
      .map(draw -> draw.getDrawKind().createDrawer(repty, draw))
      .collect(Collectors.toList());
  }

  public boolean hasDraw() {
    return !drawList.isEmpty();
  }
}
