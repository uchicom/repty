// (C) 2022 uchicom
package com.uchicom.repty.util;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Value;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class DrawUtil {

  /** ロガー */
  private static final Logger logger = Logger.getLogger(Repty.class.getCanonicalName());
  /** 文字列出力用寄せたoffset取得. */
  public static float getAlignOffset(float offset, float pdfboxSize, int align) {
    switch (align) {
      case 1:
        offset -= pdfboxSize / 2;
        break;
      case 2:
        offset -= pdfboxSize;
        break;
      default:
    }
    return offset;
  }

  /**
   * 文字列出力用pdfbox文字列長さ計算
   *
   * @param fontSize フォントサイズ
   * @param length 文字列長さ
   * @return 長さ
   */
  public static float getPdfboxSize(float fontSize, float length) {
    return fontSize * length / 1000;
  }

  /** オフセット出力. */
  public static void drawOffsetString(
      PDPageContentStream stream, Value value, PDFont pdFont, float fontSize, int size)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, IOException {

    if (value.isRepeat()) {
      stream.beginText();
      float x =
          getAlignOffset(
              value.getX1(),
              getPdfboxSize(fontSize, pdFont.getStringWidth(value.getValue())),
              value.getAlignX());
      stream.newLineAtOffset(x, value.getY1());
      stream.showText(value.getValue());
      for (int i = 0; i < size; i++) {
        stream.newLineAtOffset(value.getNextX(), value.getNextY());
        stream.showText(value.getValue());
      }
      stream.endText();
    } else {
      stream.beginText();
      float x =
          getAlignOffset(
              value.getX1() + value.getNextX() * size,
              getPdfboxSize(fontSize, pdFont.getStringWidth(value.getValue())),
              value.getAlignX());
      stream.newLineAtOffset(x, value.getY1() + value.getNextY() * size);
      stream.showText(value.getValue());
      stream.endText();
    }
  }

  /** 矩形を繰り返し追加します. */
  public static void drawRecordRectangle(
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

  /** 線を繰り返し追加します. */
  public static void drawRecordLine(PDPageContentStream stream, Value value, int size)
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

  /** 文字列を繰り返し追加します. */
  public static void drawRecordString(
      PDPageContentStream stream,
      Draw draw,
      Map<String, Object> paramMap,
      PDFont pdFont,
      float fontSize,
      List<String> stringList)
      throws NoSuchMethodException, SecurityException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
    List<?> list = (List<?>) paramMap.get(draw.getList());
    if (list == null || list.isEmpty()) return;
    Class<?> clazz = list.get(0).getClass();
    List<Value> valueList = draw.getValues();
    int valueSize = valueList.size();
    Method[] methods = new Method[valueList.size()];
    StringBuilder sb = new StringBuilder(64);
    sb.append("get");
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
    int listSize = list.size();
    stream.beginText();
    float currentX = 0;
    float currentY = 0;
    float x = 0;
    float y = 0;
    float fontHeight = pdFont.getFontDescriptor().getCapHeight();

    for (int i = 0; i < listSize; i++) {
      for (int iValue = 0; iValue < valueSize; iValue++) {
        try {
          Value value = valueList.get(iValue);
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
                  getNextLineIndex(pdFont, fontSize, string.substring(currentIndex), limitWidth);
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
                  getAlignOffset(
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
                    getAlignOffset(
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
                  getAlignOffset(
                      value.getX1() + value.getNextX() * i,
                      getPdfboxSize(fontSize, pdFont.getStringWidth(string)),
                      value.getAlignX());
            }
            // 縦寄せ
            if (value.getAlignY() == 0) {
              y = value.getY1() + value.getNextY() * i;
            } else {
              y =
                  getAlignOffset(
                      value.getY1() + value.getNextY() * i,
                      getPdfboxSize(fontSize, fontHeight),
                      value.getAlignY());
            }
            stream.newLineAtOffset(x - currentX, y - currentY);
            stream.showText(string);

            currentX = x;
            currentY = y;
          }
        } catch (InvocationTargetException e) {
          logger.log(Level.SEVERE, valueList.get(iValue).getValue(), e);
          throw e;
        }
      }
    }
    stream.endText();
  }

  /**
   * 繰り返しで最適解を作成する
   *
   * @throws IOException IOエラー
   */
  public static int getNextLineIndex(PDFont pdFont, float fontSize, String value, float limitWidth)
      throws IOException {
    float width = pdFont.getStringWidth(value) / 1000 * fontSize;

    if (width < limitWidth) {
      return value.length();
    }
    int nextIndex = (int) (value.length() * (limitWidth / width));
    if (nextIndex > value.length()) {
      nextIndex = value.length();
    }
    float nextWidth = pdFont.getStringWidth(value.substring(0, nextIndex)) / 1000 * fontSize;
    if (nextWidth < limitWidth) {
      for (int i = nextIndex + 1; i < value.length(); i++) {
        nextWidth = pdFont.getStringWidth(value.substring(0, i)) / 1000 * fontSize;
        if (nextWidth > limitWidth) {
          return i - 1;
        }
      }
      return nextIndex;
    } else {
      for (int i = nextIndex - 1; i > 0; i--) {
        nextWidth = pdFont.getStringWidth(value.substring(0, i)) / 1000 * fontSize;
        if (nextWidth < limitWidth) {
          return i;
        }
      }
      return nextIndex;
    }
  }
}
