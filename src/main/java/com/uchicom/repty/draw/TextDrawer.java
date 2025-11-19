// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextDrawer extends AbstractDrawer {

  Color color;
  float fontSize;
  PDFont pdFont;

  public TextDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  void initText(Map<String, Color> colorMap, Map<String, Text> textMap, Map<String, Font> fontMap) {
    Text text = textMap.get(draw.getKey());
    color = colorMap.get(text.getColorKey());
    fontSize = fontMap.get(text.getFontKey()).getSize();
    pdFont = repty.pdFontMap.get(text.getFontKey());
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {

    stream.setNonStrokingColor(color);

    stream.setFont(pdFont, fontSize);

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
        String[] lines = tempValue.split("\r?\n", 0);
        for (String line : lines) {
          int nextLineIndex = 0;
          int currentIndex = 0;
          int maxLength = line.length();
          do {
            nextLineIndex =
                getNextLineIndex(pdFont, fontSize, line.substring(currentIndex), limitWidth);
            if (currentIndex + nextLineIndex > maxLength) {
              nextLineIndex = maxLength - currentIndex;
            }
            String lineValue = line.substring(currentIndex, currentIndex + nextLineIndex);
            stringList.add(lineValue);
            currentIndex += nextLineIndex;
          } while (currentIndex < maxLength);
        }
        // リスト出力
        boolean isFirst = true;
        float currentX = 0;
        // 縦寄せ
        float y =
            getAlignOffset(
                value.getY1() + value.getNewLineY(),
                value.getNewLineY() * stringList.size(),
                value.getAlignY() == 0 ? 2 : value.getAlignY() == 2 ? 0 : value.getAlignY());

        for (String lineValue : stringList) {
          // 横寄せ
          float x =
              getAlignOffset(
                  value.getX1(),
                  getPdfboxSize(fontSize, pdFont.getStringWidth(lineValue)),
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
        // 横寄せ TODO 寄せが不要な場合も計算されている。不要な場合は除外する
        float x =
            getAlignOffset(
                value.getX1(),
                getPdfboxSize(fontSize, pdFont.getStringWidth(tempValue)),
                value.getAlignX());
        // 縦寄せ TODO 寄せが不要な場合も計算されている。不要な場合は除外する
        float y =
            getAlignOffset(value.getY1(), getPdfboxHeightSize(fontSize, pdFont), value.getAlignY());
        stream.newLineAtOffset(x, y);
        stream.showText(tempValue);
      }
      stream.endText();
    }
  }
}
