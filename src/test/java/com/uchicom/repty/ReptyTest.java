// (C) 2023 uchicom
package com.uchicom.repty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.uchicom.repty.dto.Template;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;

public class ReptyTest extends AbstractTest {
  @Mock PDDocument pdDocument;
  @Mock Template template;

  @Captor ArgumentCaptor<PDPage> pdPageCaptor;
  @Captor ArgumentCaptor<Map<String, Object>> paramMapCaptor;
  @Captor ArgumentCaptor<PDPageContentStream> pdPageContentStreamCaptor;

  @Spy Repty repty;

  @Test
  public void createPage() throws Exception {
    // mock
    PDPage pdPage = new PDPage();
    doReturn(pdPage).when(repty).getInstancePage();

    doNothing().when(repty).write(pdPageCaptor.capture(), paramMapCaptor.capture());

    Map<String, Object> paramMap = new HashMap<>();
    // test
    PDPage result = repty.createPage(paramMap);

    // assert
    assertThat(result).isEqualTo(pdPage);
    assertThat(pdPageCaptor.getValue()).isEqualTo(pdPage);
    assertThat(paramMapCaptor.getValue()).isEqualTo(paramMap);
  }

  @Test
  public void writePage() throws Exception {
    // mock
    PDPageContentStream pdPageContentStream = mock(PDPageContentStream.class);
    doReturn(pdPageContentStream).when(repty).createPDPageContentStream(pdPageCaptor.capture());
    doNothing().when(repty).write(pdPageContentStreamCaptor.capture(), paramMapCaptor.capture());

    PDPage pdPage = new PDPage();
    Map<String, Object> paramMap = new HashMap<>();
    // test
    repty.write(pdPage, paramMap);

    // assert
    assertThat(pdPageCaptor.getValue()).isEqualTo(pdPage);
    assertThat(pdPageContentStreamCaptor.getValue()).isEqualTo(pdPageContentStream);
    assertThat(paramMapCaptor.getValue()).isEqualTo(paramMap);
  }
}
