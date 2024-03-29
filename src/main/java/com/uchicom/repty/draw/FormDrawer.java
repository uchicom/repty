// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class FormDrawer extends AbstractDrawer {

  public FormDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {}
}
