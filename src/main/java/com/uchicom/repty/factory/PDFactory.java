// (C) 2022 uchicom
package com.uchicom.repty.factory;

import com.uchicom.repty.dto.Meta;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDFactory {

  public PDPage createPage(Meta meta)
      throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
          SecurityException {
    if (meta.getPdRectangle() != null) {
      PDRectangle pdRectangle =
          (PDRectangle) PDRectangle.class.getDeclaredField(meta.getPdRectangle()).get(null);

      return meta.isLandscape()
          ? new PDPage(new PDRectangle(pdRectangle.getHeight(), pdRectangle.getWidth()))
          : new PDPage(pdRectangle);
    }
    return new PDPage(new PDRectangle(meta.getX(), meta.getY(), meta.getWidth(), meta.getHeight()));
  }
}
