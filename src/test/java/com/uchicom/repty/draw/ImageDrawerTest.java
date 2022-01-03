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
import com.uchicom.repty.dto.Resource;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Value;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class ImageDrawerTest extends AbstractTest {

  @Captor ArgumentCaptor<PDImageXObject> pdfImageXObject;
  @Captor ArgumentCaptor<Float> x1;
  @Captor ArgumentCaptor<Float> lengthX;
  @Captor ArgumentCaptor<Float> y1;
  @Captor ArgumentCaptor<Float> lengthY;

  @Test
  public void draw() throws Exception {
    // mock and data
    PDImageXObject image = mock(PDImageXObject.class);
    Draw draw = new Draw();
    draw.setKey("key");
    Value value1 = new Value(10, 15);
    Value value2 = new Value(1, 2, 3, 4);
    draw.setValues(List.of(value1, value2));

    Template template = mock(Template.class);
    doReturn(mock(Resource.class)).when(template).getResource();
    Repty repty = new Repty(mock(PDDocument.class), template);
    repty.xImageMap.put("key", image);
    ImageDrawer drawer = spy(new ImageDrawer(repty, draw));

    PDPageContentStream stream = mock(PDPageContentStream.class);
    doNothing().when(stream).drawImage(pdfImageXObject.capture(), x1.capture(), y1.capture());
    doNothing()
        .when(stream)
        .drawImage(
            pdfImageXObject.capture(),
            x1.capture(),
            y1.capture(),
            lengthX.capture(),
            lengthY.capture());

    // test method
    drawer.draw(stream, null);

    // assert
    assertThat(pdfImageXObject.getValue()).isEqualTo(image);
    assertThat(x1.getAllValues()).hasSize(2);
    assertThat(x1.getAllValues().get(0)).isEqualTo(10);
    assertThat(x1.getAllValues().get(1)).isEqualTo(1);
    assertThat(y1.getAllValues()).hasSize(2);
    assertThat(y1.getAllValues().get(0)).isEqualTo(15);
    assertThat(y1.getAllValues().get(1)).isEqualTo(2);
    assertThat(lengthX.getAllValues()).hasSize(1);
    assertThat(lengthX.getValue()).isEqualTo(2);
    assertThat(lengthY.getAllValues()).hasSize(1);
    assertThat(lengthY.getValue()).isEqualTo(2);
  }
}
