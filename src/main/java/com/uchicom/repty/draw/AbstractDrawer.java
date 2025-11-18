// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Resource;
import com.uchicom.repty.dto.Text;
import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.pdmodel.font.PDFont;

public abstract class AbstractDrawer implements Drawer {
  Repty repty;
  Draw draw;

  public AbstractDrawer(Repty repty, Draw draw) {
    this.repty = repty;
    this.draw = draw;
    Resource resource = repty.template.getResource();
    init(
        resource.getColorMap(),
        resource.getLineMap(),
        resource.getTextMap(),
        resource.getFontMap());
  }

  void init(
      Map<String, Color> colorMap,
      Map<String, Line> lineMap,
      Map<String, Text> textMap,
      Map<String, Font> fontMap) {
    initLine(colorMap, lineMap);
    initText(colorMap, textMap, fontMap);
  }

  void initLine(Map<String, Color> colorMap, Map<String, Line> lineMap) {}

  void initText(
      Map<String, Color> colorMap, Map<String, Text> textMap, Map<String, Font> fontMap) {}

  public Draw getDraw() {
    return draw;
  }

  /**
   * 文字列出力用pdfbox文字列長さ計算
   *
   * @param fontSize フォントサイズ
   * @param length 文字列長さ
   * @return 長さ
   */
  float getPdfboxSize(float fontSize, float length) {
    return fontSize * length / 1000;
  }

  /** 文字列出力用寄せたoffset取得. */
  float getAlignOffset(float offset, float pdfboxSize, int align) {
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

  float getPdfboxHeightSize(float fontSize, PDFont pdFont) {
    return getPdfboxSize(fontSize, pdFont.getFontDescriptor().getCapHeight());
  }

  /**
   * 繰り返しで最適解を作成する
   *
   * @throws IOException IOエラー
   */
  int getNextLineIndex(PDFont pdFont, float fontSize, String value, float limitWidth)
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
