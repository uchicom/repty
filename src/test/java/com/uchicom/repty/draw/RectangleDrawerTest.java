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

public class RectangleDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<Float> x1;
  @Captor ArgumentCaptor<Float> lengthX;
  @Captor ArgumentCaptor<Float> y1;
  @Captor ArgumentCaptor<Float> lengthY;
  @Captor ArgumentCaptor<Float> lineWidthCaptor;
  @Captor ArgumentCaptor<Color> colorCaptor;

  @Test
  public void draw() throws Exception {
    // mock and data
    Draw draw = new Draw();

    draw.setKey("lineKey");
    Value value1 = new Value(1, 2, 30, 40);
    draw.setValues(List.of(value1));

    Template template = mock(Template.class);
    Resource resource = mock(Resource.class);
    doReturn(resource).when(template).getResource();
    Color color = Color.BLACK;
    doReturn(Map.of("colorKey", color)).when(resource).getColorMap();
    Line line = new Line("colorKey", 1.0f);
    doReturn(Map.of("lineKey", line)).when(resource).getLineMap();
    Repty repty = new Repty(mock(PDDocument.class), template);
    RectangleDrawer drawer = spy(new RectangleDrawer(repty, draw));

    PDPageContentStream stream = mock(PDPageContentStream.class);
    doNothing().when(stream).setLineWidth(lineWidthCaptor.capture());
    doNothing().when(stream).setStrokingColor(colorCaptor.capture());
    doNothing()
        .when(stream)
        .addRect(x1.capture(), y1.capture(), lengthX.capture(), lengthY.capture());
    doNothing().when(stream).stroke();

    // test method
    drawer.draw(stream, null);

    // assert
    assertThat(lineWidthCaptor.getValue()).isEqualTo(1.0f);
    assertThat(colorCaptor.getValue()).isEqualTo(color);
    assertThat(x1.getValue()).isEqualTo(1);
    assertThat(y1.getValue()).isEqualTo(2);
    assertThat(lengthX.getValue()).isEqualTo(29);
    assertThat(lengthY.getValue()).isEqualTo(38);
  }
}
