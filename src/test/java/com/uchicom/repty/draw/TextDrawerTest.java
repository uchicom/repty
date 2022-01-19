// (C) 2022 uchicom
package com.uchicom.repty.draw;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.uchicom.repty.AbstractTest;
import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Resource;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

public class TextDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<Color> color;
  @Captor ArgumentCaptor<PDFont> pdFontCaptor;
  @Captor ArgumentCaptor<Float> fontSize;
  @Captor ArgumentCaptor<Float> x;
  @Captor ArgumentCaptor<Float> y;
  @Captor ArgumentCaptor<String> text;

  @Test
  public void draw() throws Exception {

    // mock and data
    Draw draw = new Draw();
    draw.setKey("text");
    Value value1 = new Value(3, 4, "test");
    draw.setValues(List.of(value1));

    Resource resource = new Resource();
    resource.setTextMap(Map.of("text", new Text("color", "font")));
    resource.setColorMap(Map.of("color", Color.BLACK));
    Font font1 = new Font(null, 0, 2);
    resource.setFontMap(Map.of("font", font1));
    Template template = mock(Template.class);
    doReturn(resource).when(template).getResource();
    Repty repty = new Repty(mock(PDDocument.class), template);
    PDFont pdFont = mock(PDFont.class);
    repty.pdFontMap.put("font", pdFont);
    doReturn(mock(PDFontDescriptor.class)).when(pdFont).getFontDescriptor();
    TextDrawer drawer = spy(new TextDrawer(repty, draw));
    doReturn(0.1F).when(drawer).getPdfboxHeightSize(Mockito.anyFloat(), Mockito.any());

    PDPageContentStream stream = mock(PDPageContentStream.class);
    doNothing().when(stream).setNonStrokingColor(color.capture());
    doNothing().when(stream).setFont(pdFontCaptor.capture(), fontSize.capture());
    // TODO doAnswer startText
    // TODO doAnswer endText
    doNothing().when(stream).newLineAtOffset(x.capture(), y.capture());
    doNothing().when(stream).showText(text.capture());
    // test method
    drawer.draw(stream, Map.of());

    // assert
    assertThat(color.getValue()).isEqualTo(Color.BLACK);
    assertThat(pdFontCaptor.getValue()).isEqualTo(pdFont);
    assertThat(fontSize.getValue()).isEqualTo(2);
    assertThat(x.getValue()).isEqualTo(3);
    assertThat(y.getValue()).isEqualTo(4);
    assertThat(text.getValue()).isEqualTo("test");
  }
}
