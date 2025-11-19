// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class OffsetStringDrawer extends AbstractDrawer {
  Color recordColor;
  float fontSize;
  PDFont recordPdFont;

  public OffsetStringDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  void initText(Map<String, Color> colorMap, Map<String, Text> textMap, Map<String, Font> fontMap) {
    Text text = textMap.get(draw.getKey());
    recordColor = colorMap.get(text.getColorKey());
    fontSize = fontMap.get(text.getFontKey()).getSize();
    recordPdFont = repty.pdFontMap.get(text.getFontKey());
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
    List<?> list = (List<?>) paramMap.get(draw.getList());
    if (list == null || list.isEmpty()) return;
    stream.setNonStrokingColor(recordColor);
    stream.setFont(recordPdFont, fontSize);
    int listSize = list.size() - 1;
    for (Value value : draw.getValues()) {
      drawOffsetString(stream, value, recordPdFont, fontSize, listSize);
    }
  }

  /**
   * オフセット出力.
   *
   * @throws IOException
   */
  void drawOffsetString(
      PDPageContentStream stream, Value value, PDFont pdFont, float fontSize, int size)
      throws IOException {

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
}
