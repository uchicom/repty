// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import com.uchicom.repty.exception.ReptyException;
import com.uchicom.repty.util.DrawUtil;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class RecordStringDrawer extends AbstractDrawer {
  private static final Logger logger = Logger.getLogger(Repty.class.getCanonicalName());
  Color recordColor;
  float fontSize;
  PDFont pdFont;

  public RecordStringDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  void initText(Map<String, Color> colorMap, Map<String, Text> textMap, Map<String, Font> fontMap) {
    Text recordText = textMap.get(draw.getKey());
    recordColor = colorMap.get(recordText.getColorKey());
    fontSize = fontMap.get(recordText.getFontKey()).getSize();
    pdFont = repty.pdFontMap.get(recordText.getFontKey());
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
    if (draw.getList() == null || draw.getList().isEmpty()) return;
    List<?> list = (List<?>) paramMap.get(draw.getList());
    if (list == null || list.isEmpty()) return;
    stream.setNonStrokingColor(recordColor);
    stream.setFont(pdFont, fontSize);
    drawRecordString(stream, list);
  }

  public void drawRecordString(PDPageContentStream stream, List<?> list) throws IOException {
    List<String> stringList = new ArrayList<>(16);
    Class<?> clazz = list.get(0).getClass();
    List<Value> valueList = draw.getValues();
    int valueSize = valueList.size();
    Method[] methods = new Method[valueList.size()];
    StringBuilder sb = new StringBuilder(64);
    sb.append("get");
    try {
      for (int i = 0; i < valueSize; i++) {
        Value value = valueList.get(i);
        String memberName = value.getValue();
        char prefix = memberName.charAt(0);
        if (prefix >= 'a' && prefix <= 'z') {
          sb.append((char) (prefix + ('A' - 'a')));
        } else {
          sb.append(prefix);
        }
        sb.append(memberName, 1, memberName.length());
        methods[i] = clazz.getMethod(sb.toString());
        sb.setLength(3);
      }
    } catch (NoSuchMethodException e) {
      throw new ReptyException(e);
    }
    int listSize = list.size();
    stream.beginText();
    float currentX = 0;
    float currentY = 0;
    float x = 0;
    float y = 0;
    float fontHeight = pdFont.getFontDescriptor().getCapHeight();

    for (int i = 0; i < listSize; i++) {
      for (int iValue = 0; iValue < valueSize; iValue++) {
        Value value = valueList.get(iValue);
        try {
          String string = String.valueOf(methods[iValue].invoke(list.get(i)));
          if (value.getLimitX() > 0) {
            stringList.clear();
            // リスト作成
            float limitWidth = value.getLimitX() - value.getX1();
            int nextLineIndex = 0;
            int currentIndex = 0;
            int maxLength = string.length();
            do {
              nextLineIndex =
                  DrawUtil.getNextLineIndex(
                      pdFont, fontSize, string.substring(currentIndex), limitWidth);
              if (currentIndex + nextLineIndex > maxLength) {
                nextLineIndex = maxLength - currentIndex;
              }
              String lineValue = string.substring(currentIndex, currentIndex + nextLineIndex);
              stringList.add(lineValue);
              currentIndex += nextLineIndex;
            } while (currentIndex < maxLength);
            // リスト出力
            boolean isFirst = true;

            // 縦寄せ
            if (value.getAlignY() == 2) {
              y = value.getY1() + value.getNextY() * i + value.getNewLineY();
            } else {
              y =
                  DrawUtil.getAlignOffset(
                      value.getY1() + value.getNextY() * i + value.getNewLineY(),
                      value.getNewLineY() * stringList.size(),
                      value.getAlignY() == 0 ? 2 : 1);
            }
            for (String lineValue : stringList) {
              // 横寄せ
              if (value.getAlignX() == 0) {
                x = value.getX1() + value.getNextX() * i;
              } else {
                x =
                    DrawUtil.getAlignOffset(
                        value.getX1() + value.getNextX() * i,
                        getPdfboxSize(fontSize, pdFont.getStringWidth(lineValue)),
                        value.getAlignX());
              }
              // 初回チェック
              if (isFirst) {
                stream.newLineAtOffset(x - currentX, y - currentY);
                currentY = y;
                isFirst = false;
              } else {
                stream.newLineAtOffset(x - currentX, value.getNewLineY());
                currentY += value.getNewLineY();
              }
              stream.showText(lineValue);
              currentX = x;
            }
          } else {
            // 横寄せ
            if (value.getAlignX() == 0) {
              x = value.getX1() + value.getNextX() * i;
            } else {
              x =
                  DrawUtil.getAlignOffset(
                      value.getX1() + value.getNextX() * i,
                      getPdfboxSize(fontSize, pdFont.getStringWidth(string)),
                      value.getAlignX());
            }
            // 縦寄せ
            if (value.getAlignY() == 0) {
              y = value.getY1() + value.getNextY() * i;
            } else {
              y =
                  DrawUtil.getAlignOffset(
                      value.getY1() + value.getNextY() * i,
                      getPdfboxSize(fontSize, fontHeight),
                      value.getAlignY());
            }
            stream.newLineAtOffset(x - currentX, y - currentY);
            stream.showText(string);

            currentX = x;
            currentY = y;
          }
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
          throw new ReptyException(value.getValue(), e);
        }
      }
    }
    stream.endText();
  }
}
