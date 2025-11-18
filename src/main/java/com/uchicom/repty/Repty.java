// (C) 2018 uchicom
package com.uchicom.repty;

import com.uchicom.repty.draw.Drawer;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Meta;
import com.uchicom.repty.dto.ResourceFile;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Unit;
import com.uchicom.repty.factory.PDFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Repty.
 *
 * @author hex
 */
public class Repty implements Closeable {

  /** ロガー */
  private static final Logger logger = Logger.getLogger(Repty.class.getCanonicalName());

  /** True Type Collectionのマップ */
  final Map<String, TrueTypeCollection> ttcMap = new HashMap<>();

  /** True Type Fontのマップ */
  final Map<String, TrueTypeFont> ttFontMap = new HashMap<>();

  /** Imageオブジェクトのマップ */
  public final Map<String, PDImageXObject> xImageMap = new HashMap<>();

  /** PDフォントのマップ */
  public final Map<String, PDFont> pdFontMap = new HashMap<>();

  /** PDフォント名のマップ */
  final Map<String, PDFont> pdFontNameMap = new HashMap<>();

  /** テンプレート */
  public Template template;

  /** PDドキュメント */
  public PDDocument document;

  /** 描画情報 */
  final List<Drawer> drawers = new ArrayList<>(1024);

  /** メタ情報 */
  final List<Meta> metas = new ArrayList<>(8);

  /** 改行計算用文字列リスト */
  final List<String> stringList = new ArrayList<>(100);

  final PDFactory pdfFactory = new PDFactory();

  private InputStream getStream(ResourceFile resourceFile) throws IOException {
    if (resourceFile.isResource()) {
      return getClass().getClassLoader().getResourceAsStream(resourceFile.getFile());
    } else {
      return Files.newInputStream(Paths.get(resourceFile.getFile()));
    }
  }

  private static InputStream createInputStream(boolean isResource, String path) throws IOException {
    return isResource
        ? Repty.class.getClassLoader().getResourceAsStream(path)
        : Files.newInputStream(Paths.get(path));
  }

  public Repty() {}

  /**
   * コンストラクタ.<br>
   * リソースの初期化も実施します.
   *
   * @param document PDドキュメント
   * @param template テンプレート
   * @throws IOException ファイル読み込みに失敗した場合
   */
  public Repty(PDDocument document, Template template) throws IOException {
    // addPageする前に初期状態を準備しておいて、出力内容を使いまわして保存する。
    this.document = document;
    this.template = template;
    initFontMap(template);
    initImageMap(template);
  }

  void initFontMap(Template template) throws IOException {

    // フォントマップ作成
    for (Entry<String, Font> entry : template.getResource().getFontMap().entrySet()) {
      Font font = entry.getValue();
      if (font.getFontFileKey() == null) {
        continue;
      }
      if (template.getResource().getTtcMap() != null
          && !ttcMap.containsKey(font.getFontFileKey())
          && template.getResource().getTtcMap().containsKey(font.getFontFileKey())) {
        // ttc
        ResourceFile ttc = template.getResource().getTtcMap().get(font.getFontFileKey());
        try (InputStream is = createInputStream(ttc.isResource(), ttc.getFile());
            TrueTypeCollection ttco = new TrueTypeCollection(is)) {
          ttcMap.put(font.getFontFileKey(), ttco);
        }
        ttFontMap.put(
            font.getName(), ttcMap.get(font.getFontFileKey()).getFontByName(font.getName()));
      } else if (template.getResource().getTtfMap() != null
          && !ttFontMap.containsKey(font.getFontFileKey())
          && template.getResource().getTtfMap().containsKey(font.getFontFileKey())) {
        // ttf
        ResourceFile ttf = template.getResource().getTtfMap().get(font.getFontFileKey());
        PDFont ttco = null;
        try (InputStream is = createInputStream(ttf.isResource(), ttf.getFile())) {
          ttco = PDType0Font.load(document, is);
        }
        for (Entry<String, Font> entry2 : template.getResource().getFontMap().entrySet()) {
          Font font2 = entry2.getValue();
          if (pdFontNameMap.containsKey(font2.getName())) {
            pdFontMap.put(entry2.getKey(), pdFontNameMap.get(font2.getName()));
          } else {
            pdFontNameMap.put(font2.getName(), ttco);
            pdFontMap.put(entry2.getKey(), ttco);
          }
        }
      }
    }
    //		// fileMapからpdFontMapを作成
    //		if (template.getResource().getFileMap() != null) {
    //			for (Entry<String, ResourceFile> entry : template.getResource().getFileMap().entrySet()) {
    //				ResourceFile resourceFile = entry.getValue();
    //				if (resourceFile.getFile().endsWith(".ttf")) {
    //					try (InputStream is = getStream(resourceFile)) {
    //						pdFontMap.put(entry.getKey(), PDType0Font.load(document, is));
    //					}
    //				} else if (resourceFile.getFile().endsWith(".ttc")) {
    //					try (InputStream is = getStream(resourceFile);
    //							TrueTypeCollection ttco = new TrueTypeCollection(is)) {
    //						ttcMap.put(entry.getKey(), ttco);
    //					}
    //				} else {
    //					try (InputStream is = getStream(resourceFile)) {
    //						xImageMap.put(entry.getKey(), PDImageXObject.createFromByteArray(document,
    // is.readAllBytes(),
    //								resourceFile.getFile()));
    //
    //					}
    //				}
    //			}
    //		}
    //		// ttfMapからpdFontMapを作成
    //		if (template.getResource().getTtfMap() != null) {
    //			for (Entry<String, ResourceFile> entry : template.getResource().getTtfMap().entrySet()) {
    //				ResourceFile resourceFile = entry.getValue();
    //				try (InputStream is = getStream(resourceFile)) {
    //					pdFontMap.put(entry.getKey(), PDType0Font.load(document, is));
    //				}
    //			}
    //		}
    //		// ttcMapからttcMapを作成
    //		if (template.getResource().getTtcMap() != null) {
    //			for (Entry<String, ResourceFile> entry : template.getResource().getTtcMap().entrySet()) {
    //				ResourceFile resourceFile = entry.getValue();
    //				try (InputStream is = getStream(resourceFile); TrueTypeCollection ttco = new
    // TrueTypeCollection(is)) {
    //					ttcMap.put(entry.getKey(), ttco);
    //				}
    //			}
    //		}
    //		// フォントマップ作成
    //		for (Entry<String, Font> entry : template.getResource().getFontMap().entrySet()) {
    //			Font font = entry.getValue();
    //			if (font.getTtc() != null && ttcMap.containsKey(font.getTtc())) {
    //				ttFontMap.put(font.getName(), ttcMap.get(font.getTtc()).getFontByName(font.getName()));
    //			}
    //		}

  }

  void initImageMap(Template template) throws IOException {
    // イメージマップ作成
    if (template.getResource().getImageMap() != null) {
      for (Entry<String, ResourceFile> entry : template.getResource().getImageMap().entrySet()) {
        ResourceFile resourceFile = entry.getValue();
        try (InputStream is = getStream(resourceFile)) {
          xImageMap.put(
              entry.getKey(),
              PDImageXObject.createFromByteArray(
                  document, is.readAllBytes(), resourceFile.getFile()));
        }
      }
    }
  }

  /**
   * 初期化 保存時にクリアされてしまう 新バージョンで解決されるかも。
   *
   * @throws IOException 入出力エラー
   */
  public void init() throws IOException {
    pdFontNameMap.clear();
    // フォント作成
    for (Entry<String, Font> entry : template.getResource().getFontMap().entrySet()) {
      Font font = entry.getValue();
      if (pdFontNameMap.containsKey(font.getName())) {
        pdFontMap.put(entry.getKey(), pdFontNameMap.get(font.getName()));
      } else if (ttFontMap.containsKey(font.getName())) {
        PDFont pdFont = PDType0Font.load(document, ttFontMap.get(font.getName()), true);
        if (font.getEncoding() != null) {
          pdFont.getCOSObject().setItem(COSName.ENCODING, COSName.getPDFName(font.getEncoding()));
        }
        pdFontNameMap.put(font.getName(), pdFont);
        pdFontMap.put(entry.getKey(), pdFont);
      }
    }
  }

  /**
   * テンプレートキーを追加します.
   *
   * @param drawKey テンプレートキー
   * @return このオブジェクトへの参照
   */
  public Repty addKey(String drawKey) {
    Unit unit = template.getDrawMap().get(drawKey);
    if (unit.hasDraw()) {
      drawers.addAll(unit.getDrawerList(this));
    }
    if (unit.getMeta() != null) {
      metas.add(unit.getMeta());
    }
    return this;
  }

  /**
   * テンプレートキーを追加します.
   *
   * @param drawKeys テンプレートキー配列
   * @return このオブジェクトへの参照
   */
  public Repty addKeys(String... drawKeys) {
    Arrays.stream(drawKeys).forEach(this::addKey);
    return this;
  }

  /**
   * テンプレートキーを削除します.
   *
   * @param removeKey テンプレートキー
   * @return このオブジェクトへの参照
   */
  public Repty removeKey(String removeKey) {
    Unit unit = template.getDrawMap().get(removeKey);
    if (unit.hasDraw()) {
      List<Draw> drawList = unit.getDrawList();
      drawers.removeIf(drawer -> drawList.contains(drawer.getDraw()));
    }
    if (unit.getMeta() != null) {
      metas.remove(unit.getMeta());
    }
    return this;
  }

  /**
   * テンプレートキーを削除します.
   *
   * @param drawKeys テンプレートキー配列
   * @return このオブジェクトへの参照
   */
  public Repty removeKeys(String... drawKeys) {
    Arrays.stream(drawKeys).forEach(this::removeKey);
    return this;
  }

  /**
   * テンプレートキーとメタ情報を全て削除します.
   *
   * @return このオブジェクトへの参照
   */
  public Repty clearKeys() {
    drawers.clear();
    metas.clear();
    return this;
  }

  /**
   * テンプレートキーを切り替えます.
   *
   * @param removeKey 削除するテンプレートキー
   * @param addKey 追加するテンプレートキー
   * @return このオブジェクトへの参照
   */
  public Repty changeKey(String removeKey, String addKey) {
    removeKey(removeKey);
    addKey(addKey);
    return this;
  }

  /**
   * メタ情報をもとにPDページを作成します.
   *
   * @return PDページ
   */
  public PDPage getInstancePage()
      throws NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException {
    if (metas.isEmpty()) {
      return new PDPage(PDRectangle.A4);
    }

    Meta meta = metas.get(metas.size() - 1);
    return pdfFactory.createPage(meta);
  }

  /** リソースを設定してPDページを作成します. */
  public PDPage getInstancePage(List<PDStream> cs, PDResources resources)
      throws NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException {
    PDPage page = getInstancePage();
    page.setContents(cs);
    page.setResources(resources);
    return page;
  }

  /** テンプレートをもとにPDFページを作成します. */
  public PDPage createPage(Map<String, Object> paramMap)
      throws IOException,
          NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException,
          NoSuchMethodException,
          InvocationTargetException {
    PDPage page = getInstancePage();
    write(page, paramMap);
    return page;
  }

  PDPageContentStream createPDPageContentStream(PDPage pdPage) throws IOException {
    return new PDPageContentStream(document, pdPage);
  }

  public void write(PDPage page, Map<String, Object> paramMap) throws IOException {
    try (PDPageContentStream stream = createPDPageContentStream(page)) {
      write(stream, paramMap);
    }
  }

  /** リソースを設定してPDFページを追加します. */
  public PDPage appendPage(Map<String, Object> paramMap, List<PDStream> cs, PDResources resources)
      throws IOException,
          NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException {
    PDPage page = getInstancePage(cs, resources);
    appendPage(paramMap, page);
    return page;
  }

  /** テンプレートをもとに既存のPDFページに出力します. */
  public PDPage appendPage(Map<String, Object> paramMap, PDPage page) throws IOException {
    try (PDPageContentStream stream =
        new PDPageContentStream(document, page, AppendMode.APPEND, true, false); ) {
      write(stream, paramMap);
      return page;
    }
  }

  public void write(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
    for (Drawer drawer : drawers) {
      drawer.draw(stream, paramMap);
    }
  }

  @Override
  public void close() throws IOException {
    ttcMap.forEach(
        (key, value) -> {
          try {
            value.close();
          } catch (IOException e) {
            logger.log(Level.SEVERE, "close", e);
          }
        });
  }

  /** 全てのページを削除します. */
  public void removeAllPage() {
    document
        .getPages()
        .forEach(
            pdPage -> {
              document.removePage(pdPage);
            });
  }
}
