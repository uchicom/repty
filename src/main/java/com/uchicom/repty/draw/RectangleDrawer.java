// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Value;
import com.uchicom.repty.util.DrawUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class RectangleDrawer extends AbstractDrawer {

  Line line;
  Color color;

  public RectangleDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  void initLine(Map<String, Color> colorMap, Map<String, Line> lineMap) {
    line = lineMap.get(draw.getKey());
    color = colorMap.get(line.getColorKey());
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
    stream.setLineWidth(line.getWidth());
    stream.setStrokingColor(color);
    if (draw.getList() != null) {
      List<?> list = (List<?>) paramMap.get(draw.getList());
      if (list == null || list.isEmpty()) return;
      int size = list.size();
      for (int i = 0; i < draw.getValues().size(); i++) {
        Value value = draw.getValues().get(i);
        if (value.isFill()) {
          stream.setNonStrokingColor(color);
        }
        DrawUtil.drawRecordRectangle(stream, value, paramMap, size);
      }
    } else {
      for (Value value : draw.getValues()) {
        // 塗りつぶしかどうか
        stream.addRect(value.getX1(), value.getY1(), value.getLengthX(), value.getLengthY());
        if (value.isFill()) {
          stream.setNonStrokingColor(color);
          stream.fill(); // 塗りつぶし
        } else {
          stream.stroke();
        }
      }
    }
  }
}
