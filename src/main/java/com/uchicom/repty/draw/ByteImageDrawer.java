// (C) 2022 uchicom
package com.uchicom.repty.draw;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Value;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ByteImageDrawer extends AbstractDrawer {

  public ByteImageDrawer(Repty repty, Draw draw) {
    super(repty, draw);
  }

  @Override
  public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception {
    byte[] bytes = (byte[]) paramMap.get(draw.getKey());
    PDImageXObject byteImagex = createPDImage(bytes);

    for (Value value : draw.getValues()) {
      if (value.isDefaultSize()) {
        stream.drawImage(byteImagex, value.getX1(), value.getY1());
      } else {
        stream.drawImage(
            byteImagex, value.getX1(), value.getY1(), value.getLengthX(), value.getLengthY());
      }
    }
  }

  PDImageXObject createPDImage(byte[] bytes) throws IOException {
    return PDImageXObject.createFromByteArray(repty.document, bytes, draw.getKey());
  }
}
