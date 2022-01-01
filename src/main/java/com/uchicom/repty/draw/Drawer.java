// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.dto.Draw;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public interface Drawer {

  void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception;

  Draw getDraw();
}
