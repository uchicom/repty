// (C) 2022 uchicom
package com.uchicom.repty.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.repty.AbstractTest;
import com.uchicom.repty.dto.Meta;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

public class PDFactoryTest extends AbstractTest {

  @Spy @InjectMocks PDFactory factory;

  @Test
  public void createPage() throws Exception {

    // mock and data
    Meta meta = new Meta();
    meta.setX(1.1f);
    meta.setY(2.2f);
    meta.setWidth(3.3f);
    meta.setHeight(4.4f);

    // test method
    PDPage result = factory.createPage(meta);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getMediaBox()).isNotNull();
    assertThat(result.getMediaBox().getLowerLeftX()).isEqualTo(meta.getX());
    assertThat(result.getMediaBox().getLowerLeftY()).isEqualTo(meta.getY());
    assertThat(result.getMediaBox().getUpperRightX()).isEqualTo(meta.getX() + meta.getWidth());
    assertThat(result.getMediaBox().getUpperRightY()).isEqualTo(meta.getY() + meta.getHeight());
  }

  @Test
  public void createPage_PDRectangle() throws Exception {

    // mock and data
    Meta meta = new Meta();
    meta.setPdRectangle("A4");

    // test method
    PDPage result = factory.createPage(meta);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getMediaBox()).isNotNull();
    assertThat(result.getMediaBox().getLowerLeftX()).isEqualTo(0);
    assertThat(result.getMediaBox().getLowerLeftY()).isEqualTo(0);
    assertThat(result.getMediaBox().getUpperRightX()).isEqualTo(PDRectangle.A4.getWidth());
    assertThat(result.getMediaBox().getUpperRightY()).isEqualTo(PDRectangle.A4.getHeight());
  }

  @Test
  public void createPage_PDRectangle_Landscape() throws Exception {

    // mock and data
    Meta meta = new Meta();
    meta.setPdRectangle("A4");
    meta.setLandscape(true);

    // test method
    PDPage result = factory.createPage(meta);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getMediaBox()).isNotNull();
    assertThat(result.getMediaBox().getLowerLeftX()).isEqualTo(0);
    assertThat(result.getMediaBox().getLowerLeftY()).isEqualTo(0);
    assertThat(result.getMediaBox().getUpperRightX()).isEqualTo(PDRectangle.A4.getHeight());
    assertThat(result.getMediaBox().getUpperRightY()).isEqualTo(PDRectangle.A4.getWidth());
  }
}
