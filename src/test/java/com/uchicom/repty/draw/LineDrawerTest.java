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
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Resource;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Value;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class LineDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<Float> x1;
  @Captor ArgumentCaptor<Float> x2;
  @Captor ArgumentCaptor<Float> y1;
  @Captor ArgumentCaptor<Float> y2;
  @Captor ArgumentCaptor<Float> lineWidth;
  @Captor ArgumentCaptor<float[]> pattern;
  @Captor ArgumentCaptor<Float> phase;
  @Captor ArgumentCaptor<Color> stockingColor;

  @Test
  public void draw() throws Exception {
    // mock and data
    Draw draw = new Draw();

    draw.setKey("lineKey");
    Value value1 = new Value(1, 2, 3, 4);
    Value value2 = new Value(10, 20, 30, 40);
    draw.setValues(List.of(value1, value2));

    Template template = mock(Template.class);
    Resource resource = mock(Resource.class);
    doReturn(resource).when(template).getResource();
    Color color = Color.BLACK;
    doReturn(Map.of("colorKey", color)).when(resource).getColorMap();
    Line line = new Line("colorKey", 1.0f);
    doReturn(Map.of("lineKey", line)).when(resource).getLineMap();
    Repty repty = new Repty(mock(PDDocument.class), template);
    LineDrawer drawer = spy(new LineDrawer(repty, draw));

    PDPageContentStream stream = mock(PDPageContentStream.class);
    doNothing().when(stream).setLineWidth(lineWidth.capture());
    doNothing().when(stream).setLineDashPattern(pattern.capture(), phase.capture());
    doNothing().when(stream).setStrokingColor(stockingColor.capture());
    doNothing().when(stream).moveTo(x1.capture(), y1.capture());
    doNothing().when(stream).lineTo(x2.capture(), y2.capture());
    doNothing().when(stream).stroke();

    // test method
    drawer.draw(stream, null);

    // assert
    assertThat(lineWidth.getValue()).isEqualTo(1.0f);
    assertThat(stockingColor.getValue()).isEqualTo(color);
    assertThat(x1.getAllValues()).hasSize(2);
    assertThat(x1.getAllValues().get(0)).isEqualTo(1);
    assertThat(x1.getAllValues().get(1)).isEqualTo(10);
    assertThat(y1.getAllValues()).hasSize(2);
    assertThat(y1.getAllValues().get(0)).isEqualTo(2);
    assertThat(y1.getAllValues().get(1)).isEqualTo(20);
    assertThat(x2.getAllValues()).hasSize(2);
    assertThat(x2.getAllValues().get(0)).isEqualTo(3);
    assertThat(x2.getAllValues().get(1)).isEqualTo(30);
    assertThat(y2.getAllValues()).hasSize(2);
    assertThat(y2.getAllValues().get(0)).isEqualTo(4);
    assertThat(y2.getAllValues().get(1)).isEqualTo(40);
  }
}
