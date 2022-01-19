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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class RecordStringDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<PDPageContentStream> streamCaptor;
  @Captor ArgumentCaptor<Color> colorCaptor;
  @Captor ArgumentCaptor<PDFont> pdFontCaptor;
  @Captor ArgumentCaptor<Float> fontSizeCaptor;
  @Captor ArgumentCaptor<List<?>> listCaptor;

  @Test
  public void draw() throws Exception {
    // mock and data
    Draw draw = new Draw();
    draw.setKey("text");
    draw.setList("list");
    Value value1 = new Value(10, 15, "method1", 20, 30);
    Value value2 = new Value(1, 2, 3, 4, "method2", 10, 20);
    draw.setValues(List.of(value1, value2));

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

    RecordStringDrawer drawer = spy(new RecordStringDrawer(repty, draw));
    doNothing().when(drawer).drawRecordString(streamCaptor.capture(), listCaptor.capture());

    PDPageContentStream stream = mock(PDPageContentStream.class);
    doNothing().when(stream).setNonStrokingColor(colorCaptor.capture());
    doNothing().when(stream).setFont(pdFontCaptor.capture(), fontSizeCaptor.capture());

    List<String> list = List.of("list1");

    // test method
    drawer.draw(stream, Map.of("list", list));

    // assert
    assertThat(streamCaptor.getValue()).isEqualTo(stream);
    assertThat(colorCaptor.getValue()).isEqualTo(Color.BLACK);
    assertThat(pdFontCaptor.getValue()).isEqualTo(pdFont);
    assertThat(fontSizeCaptor.getValue()).isEqualTo(2);
    assertThat(listCaptor.getValue()).isEqualTo(list);
  }
}
