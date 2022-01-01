// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Resource;
import com.uchicom.repty.dto.Text;
import java.awt.Color;
import java.util.Map;

public abstract class AbstractDrawer implements Drawer {
  Repty repty;
  Draw draw;
  Map<String, Color> colorMap;
  Map<String, Line> lineMap;
  Map<String, Text> textMap;
  Map<String, Font> fontMap;

  public AbstractDrawer(Repty repty, Draw draw) {
    this.repty = repty;
    this.draw = draw;
    Resource resource = repty.template.getResource();
    colorMap = resource.getColorMap();
    lineMap = resource.getLineMap();
    textMap = resource.getTextMap();
    fontMap = resource.getFontMap();
  }

  public Draw getDraw() {
    return draw;
  }
}
