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
        drawRecordRectangle(stream, value, paramMap, size);
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

  /** 矩形を繰り返し追加します. */
  void drawRecordRectangle(
      PDPageContentStream stream, Value value, Map<String, Object> paramMap, int size)
      throws IOException {
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
    float width = x2 - x1;
    float height = y2 - y1;
    if (nextX == 0 && !value.isRepeat()) {
      y2 += nextY * size;
      stream.addRect(x1, y1, width, y2 - y1);
    } else if (nextY == 0 && !value.isRepeat()) {
      x2 += nextX * size;
      stream.addRect(x1, y1, x2 - x1, height);
    } else {
      for (int i = 0; i < size; i++) {
        // x1,x2大きい方で判断、差分の+-を比較する。
        // listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
        // テンプレートはそこまでやらない。リストサイズを調整する
        stream.addRect(x1 + nextX * i, y1 + nextY * i, width, height);
      }
    }
    if (value.isFill()) {
      stream.fill(); // 塗りつぶし
    } else {
      stream.stroke();
    }
  }
}
