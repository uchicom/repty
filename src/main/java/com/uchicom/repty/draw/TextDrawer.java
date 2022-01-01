// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import com.uchicom.repty.util.DrawUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextDrawer extends AbstractDrawer {

  public TextDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception {
    Text text = textMap.get(draw.getKey());
    Color color2 = colorMap.get(text.getColorKey());

    Font font = fontMap.get(text.getFontKey());
    float fontSize = font.getSize();
    PDFont pdFont = font.getPdFont();
    stream.setNonStrokingColor(color2);

    stream.setFont(font.getPdFont(), fontSize);

    List<String> stringList = new ArrayList<>(10);
    for (Value value : draw.getValues()) {
      stream.beginText();
      String tempValue = null;
      if ("object".equals(draw.getType())) {
        tempValue = String.valueOf(paramMap.get(value.getValue()));
      } else {
        tempValue = value.getValue();
        // 文字列置換機能 TODO 効率が悪いので、変えたい
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
          if (tempValue.contains("${")) {
            String replace = null;
            if (entry.getValue() == null) {
              replace = "";
            } else {
              replace = entry.getValue().toString();
            }
            tempValue = tempValue.replaceAll("\\$\\{" + entry.getKey() + "\\}", replace);
          }
        }
      }
      // 自動改行機能
      if (value.getLimitX() > 0) {
        stringList.clear();
        // リスト作成
        float limitWidth = value.getLimitX() - value.getX1();
        int nextLineIndex = 0;
        int currentIndex = 0;
        int maxLength = tempValue.length();
        do {
          nextLineIndex =
              DrawUtil.getNextLineIndex(
                  pdFont, fontSize, tempValue.substring(currentIndex), limitWidth);
          if (currentIndex + nextLineIndex > maxLength) {
            nextLineIndex = maxLength - currentIndex;
          }
          String lineValue = tempValue.substring(currentIndex, currentIndex + nextLineIndex);
          stringList.add(lineValue);
          currentIndex += nextLineIndex;
        } while (currentIndex < maxLength);
        // リスト出力
        boolean isFirst = true;
        float currentX = 0;
        // 縦寄せ
        float y =
            DrawUtil.getAlignOffset(
                value.getY1() + value.getNewLineY(),
                value.getNewLineY() * stringList.size(),
                value.getAlignY() == 0 ? 2 : value.getAlignY() == 2 ? 0 : value.getAlignY());

        for (String lineValue : stringList) {
          // 横寄せ
          float x =
              DrawUtil.getAlignOffset(
                  value.getX1(),
                  DrawUtil.getPdfboxSize(fontSize, pdFont.getStringWidth(lineValue)),
                  value.getAlignX());
          // 初回チェック
          if (isFirst) {
            stream.newLineAtOffset(x, y);
            isFirst = false;
          } else {
            stream.newLineAtOffset(x - currentX, value.getNewLineY());
          }
          stream.showText(lineValue);
          currentX = x;
        }
      } else {
        // 横寄せ
        float x =
            DrawUtil.getAlignOffset(
                value.getX1(),
                DrawUtil.getPdfboxSize(fontSize, pdFont.getStringWidth(tempValue)),
                value.getAlignX());
        // 縦寄せ
        float y =
            DrawUtil.getAlignOffset(
                value.getY1(),
                DrawUtil.getPdfboxSize(fontSize, pdFont.getFontDescriptor().getCapHeight()),
                value.getAlignY());
        stream.newLineAtOffset(x, y);
        stream.showText(tempValue);
      }
      stream.endText();
    }
  }
}
