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

public class OffsetStringDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<Color> color;
  @Captor ArgumentCaptor<PDFont> pdFontCaptor;
  @Captor ArgumentCaptor<Float> fontSizeCaptor;
  @Captor ArgumentCaptor<PDPageContentStream> streamCaptor;
  @Captor ArgumentCaptor<Value> valueCaptor;
  @Captor ArgumentCaptor<Integer> sizeCaptor;

  @Test
  public void draw() throws Exception {
    try {
      // mock and data
      Draw draw = new Draw();
      draw.setKey("text");
      draw.setList("list");
      Value value1 = new Value(1, 2, "offsetString", 3, 4);
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

      OffsetStringDrawer drawer = spy(new OffsetStringDrawer(repty, draw));
      doNothing()
          .when(drawer)
          .drawOffsetString(
              streamCaptor.capture(),
              valueCaptor.capture(),
              pdFontCaptor.capture(),
              fontSizeCaptor.capture(),
              sizeCaptor.capture());

      PDPageContentStream stream = mock(PDPageContentStream.class);
      doNothing().when(stream).setNonStrokingColor(color.capture());
      doNothing().when(stream).setFont(pdFontCaptor.capture(), fontSizeCaptor.capture());

      // test method
      drawer.draw(stream, Map.of("list", List.of("a", "b", "c")));

      // assert
      assertThat(color.getValue()).isEqualTo(Color.BLACK);
      assertThat(pdFontCaptor.getValue()).isEqualTo(pdFont);
      assertThat(fontSizeCaptor.getAllValues()).hasSize(2);
      assertThat(fontSizeCaptor.getAllValues().get(0)).isEqualTo(2);
      assertThat(fontSizeCaptor.getAllValues().get(1)).isEqualTo(2);
      assertThat(streamCaptor.getValue()).isEqualTo(stream);
      assertThat(valueCaptor.getValue()).isEqualTo(value1);
      assertThat(sizeCaptor.getValue()).isEqualTo(2);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
