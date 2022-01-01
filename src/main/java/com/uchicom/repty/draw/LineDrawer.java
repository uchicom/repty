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

public class LineDrawer extends AbstractDrawer {

  Line line;
  Color color;

  public LineDrawer(Repty repty, Draw draw) {
    super(repty, draw);
    line = lineMap.get(draw.getKey());
    color = colorMap.get(line.getColorKey());
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {

    float lineWidth = line.getWidth();
    stream.setLineWidth(lineWidth);
    if (line.getPattern() != null) {
      stream.setLineDashPattern(line.getPattern(), line.getPhase());
    }
    stream.setStrokingColor(color);
    if (draw.getList() != null) {
      List<?> list = (List<?>) paramMap.get(draw.getList());
      if (list == null || list.isEmpty()) return;
      int size = list.size();
      for (int i = 0; i < draw.getValues().size(); i++) {
        Value value = draw.getValues().get(i);
        DrawUtil.drawRecordLine(stream, value, size);
      }
    } else {
      for (Value value : draw.getValues()) {
        stream.moveTo(value.getX1(), value.getY1());
        stream.lineTo(value.getX2(), value.getY2());
        stream.stroke();
      }
    }
  }
}
