// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Value;
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
  }

  @Override
  void initLine(Map<String, Color> colorMap, Map<String, Line> lineMap) {
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
        drawRecordLine(stream, value, size);
      }
    } else {
      for (Value value : draw.getValues()) {
        stream.moveTo(value.getX1(), value.getY1());
        stream.lineTo(value.getX2(), value.getY2());
        stream.stroke();
      }
    }
  }

  /** 線を繰り返し追加します. */
  void drawRecordLine(PDPageContentStream stream, Value value, int size) throws IOException {

    float nextX = value.getNextX();
    float nextY = value.getNextY();
    float x1 = value.getX1();
    float y1 = value.getY1();
    float x2 = value.getX2();
    float y2 = value.getY2();
    if (nextX < 0 && value.getX1() < value.getX2() || nextX > 0 && value.getX1() > value.getX2()) {
      x2 = x1;
      x1 = value.getX2();
    }
    if (nextY < 0 && value.getY1() < value.getY2() || nextY > 0 && value.getY1() > value.getY2()) {
      y2 = y1;
      y1 = value.getY2();
    }

    // 延長か繰り返しかを判断する
    if (x1 == x2 && nextX == 0 && !value.isRepeat()) {
      y2 += nextY * (size - 1);
      stream.moveTo(x1, y1);
      stream.lineTo(x2, y2);
      stream.stroke();
    } else if (y1 == y2 && nextY == 0 && !value.isRepeat()) {
      x2 += nextX * (size - 1);
      stream.moveTo(x1, y1);
      stream.lineTo(x2, y2);
      stream.stroke();
    } else {
      for (int i = 0; i < size; i++) {
        // x1,x2大きい方で判断、差分の+-を比較する。
        // listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
        stream.moveTo(x1 + nextX * i, y1 + nextY * i);
        stream.lineTo(x2 + nextX * i, y2 + nextY * i);
        stream.stroke();
      }
    }
  }
}
