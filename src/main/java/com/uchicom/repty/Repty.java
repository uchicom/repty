// (C) 2018 uchicom
package com.uchicom.repty;

import com.uchicom.repty.draw.Drawer;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Meta;
import com.uchicom.repty.dto.ResourceFile;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Unit;
import com.uchicom.repty.dto.Value;
import com.uchicom.repty.factory.PDFactory;
import com.uchicom.repty.util.DrawUtil;
import java.awt.Color;
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

  /** テンプレート情報をもとにPDFページを出力します. */
  public void createPage(Map<String, Object> paramMap, PDPageContentStream stream)
      throws IOException,
          NoSuchFieldException,
          SecurityException,
          IllegalArgumentException,
          IllegalAccessException,
          NoSuchMethodException,
          InvocationTargetException {

    // 書き込む用のストリームを準備
    Map<String, Color> colorMap = template.getResource().getColorMap();
    Map<String, Line> lineMap = template.getResource().getLineMap();
    Map<String, Text> textMap = template.getResource().getTextMap();
    Map<String, Font> fontMap = template.getResource().getFontMap();
    for (Drawer drawer : drawers) {
      Draw draw = drawer.getDraw();
      switch (draw.getDrawKind()) {
        case LINE: // 線
          Line line = lineMap.get(draw.getKey());
          Color color = colorMap.get(line.getColorKey());
          float lineWidth = line.getWidth();
          stream.setLineWidth(lineWidth);
          if (line.getPattern() != null) {
            stream.setLineDashPattern(line.getPattern(), line.getPhase());
          }
          stream.setStrokingColor(color);
          if (draw.getList() != null) {
            List<?> list = (List<?>) paramMap.get(draw.getList());
            if (list == null || list.isEmpty()) return;
            int size = list.size();
            for (int i = 0; i < draw.getValues().size(); i++) {
              Value value = draw.getValues().get(i);
              DrawUtil.drawRecordLine(stream, value, size);
            }
          } else {
            for (Value value : draw.getValues()) {
              stream.moveTo(value.getX1(), value.getY1());
              stream.lineTo(value.getX2(), value.getY2());
              stream.stroke();
            }
          }
          break;
        case RECTANGLE: // 四角形
          line = lineMap.get(draw.getKey());
          color = colorMap.get(line.getColorKey());
          lineWidth = line.getWidth();
          stream.setLineWidth(lineWidth);
          stream.setStrokingColor(color);
          if (draw.getList() != null) {
            List<?> list = (List<?>) paramMap.get(draw.getList());
            if (list == null || list.isEmpty()) return;
            int size = list.size();
            for (int i = 0; i < draw.getValues().size(); i++) {
              Value value = draw.getValues().get(i);
              if (value.isFill()) {
                stream.setNonStrokingColor(color);
              }
              DrawUtil.drawRecordRectangle(stream, value, paramMap, size);
            }
          } else {
            for (Value value : draw.getValues()) {
              // 塗りつぶしかどうか
              stream.addRect(
                  value.getX1(),
                  value.getY1(),
                  value.getX2() - value.getX1(),
                  value.getY2() - value.getY1());
              if (value.isFill()) {
                stream.setNonStrokingColor(color);
                stream.fill(); // 塗りつぶし
              } else {
                stream.stroke();
              }
            }
          }
          break;
        case TEXT: // 文字列描画
        case OBJECT:
          Text text = textMap.get(draw.getKey());
          Color color2 = colorMap.get(text.getColorKey());

          Font font2 = fontMap.get(text.getFontKey());
          PDFont pdFont = pdFontMap.get(text.getFontKey());

          stream.setNonStrokingColor(color2);
          stream.setFont(pdFont, font2.getSize());

          for (Value value : draw.getValues()) {
            stream.beginText();
            String tempValue = null;
            if ("object".equals(draw.getType())) {
              tempValue = String.valueOf(paramMap.get(value.getValue()));
            } else {
              tempValue = value.getValue();
              // 文字列置換機能 TODO 効率が悪いので、変えたい
              for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                if (tempValue.contains("${")) {
                  String replace = null;
                  if (entry.getValue() == null) {
                    replace = "";
                  } else {
                    replace = entry.getValue().toString();
                  }
                  tempValue = tempValue.replaceAll("\\$\\{" + entry.getKey() + "\\}", replace);
                }
              }
            }
            // 自動改行機能
            if (value.getLimitX() > 0) {
              stringList.clear();
              // リスト作成
              float limitWidth = value.getLimitX() - value.getX1();
              int nextLineIndex = 0;
              int currentIndex = 0;
              int maxLength = tempValue.length();
              do {
                nextLineIndex =
                    DrawUtil.getNextLineIndex(
                        pdFont, font2.getSize(), tempValue.substring(currentIndex), limitWidth);
                if (currentIndex + nextLineIndex > maxLength) {
                  nextLineIndex = maxLength - currentIndex;
                }
                String lineValue = tempValue.substring(currentIndex, currentIndex + nextLineIndex);
                stringList.add(lineValue);
                currentIndex += nextLineIndex;
              } while (currentIndex < maxLength);
              // リスト出力
              boolean isFirst = true;
              float currentX = 0;
              // 縦寄せ
              float y =
                  DrawUtil.getAlignOffset(
                      value.getY1() + value.getNewLineY(),
                      value.getNewLineY() * stringList.size(),
                      value.getAlignY() == 0 ? 2 : value.getAlignY() == 2 ? 0 : value.getAlignY());

              for (String lineValue : stringList) {
                // 横寄せ
                float x =
                    DrawUtil.getAlignOffset(
                        value.getX1(),
                        DrawUtil.getPdfboxSize(font2.getSize(), pdFont.getStringWidth(lineValue)),
                        value.getAlignX());
                // 初回チェック
                if (isFirst) {
                  stream.newLineAtOffset(x, y);
                  isFirst = false;
                } else {
                  stream.newLineAtOffset(x - currentX, value.getNewLineY());
                }
                stream.showText(lineValue);
                currentX = x;
              }
            } else {
              // 横寄せ
              float x =
                  DrawUtil.getAlignOffset(
                      value.getX1(),
                      DrawUtil.getPdfboxSize(font2.getSize(), pdFont.getStringWidth(tempValue)),
                      value.getAlignX());
              // 縦寄せ
              float y =
                  DrawUtil.getAlignOffset(
                      value.getY1(),
                      DrawUtil.getPdfboxSize(
                          font2.getSize(), pdFont.getFontDescriptor().getCapHeight()),
                      value.getAlignY());
              stream.newLineAtOffset(x, y);
              stream.showText(tempValue);
            }
            stream.endText();
          }
          break;
        case IMAGE:
          // イメージ描画（今回は使い回し）
          PDImageXObject imagex = xImageMap.get(draw.getKey());
          for (Value value : draw.getValues()) {
            if (value.getX1() == value.getX2()) {
              stream.drawImage(imagex, value.getX1(), value.getY1());
            } else {
              stream.drawImage(
                  imagex,
                  value.getX1(),
                  value.getY1(),
                  value.getX2() - value.getX1(),
                  value.getY2() - value.getY1());
            }
          }
          break;
        case BYTE_IMAGE:
          byte[] bytes = (byte[]) paramMap.get(draw.getKey());
          PDImageXObject byteImagex =
              PDImageXObject.createFromByteArray(document, bytes, draw.getKey());

          for (Value value : draw.getValues()) {
            if (value.getX1() == value.getX2()) {
              stream.drawImage(byteImagex, value.getX1(), value.getY1());
            } else {
              stream.drawImage(
                  byteImagex,
                  value.getX1(),
                  value.getY1(),
                  value.getX2() - value.getX1(),
                  value.getY2() - value.getY1());
            }
          }
          break;
        // case "form": // TODO v2対応
        // PDAcroForm acroForm = new PDAcroForm(document);
        // document.getDocumentCatalog().setAcroForm(acroForm);
        // PDFont font = PDType1Font.HELVETICA;
        // PDResources resources = new PDResources();
        // resources.put(COSName.getPDFName("Helv"), font);
        // acroForm.setDefaultResources(resources);
        //
        // PDTextField field = new PDTextField(acroForm);
        // field.setPartialName("test");
        // field.setDefaultAppearance("/Helv 12 Tf 0 0 1 rg");// 12→0で自動
        //
        // acroForm.getFields().add(field);
        //
        // PDAnnotationWidget widget = field.getWidgets().get(0);
        // PDRectangle rectangle = new PDRectangle(10, 200, 50, 50);
        // widget.setRectangle(rectangle);
        // widget.setPage(page);
        // field.getWidgets().add(widget);
        //
        // widget.setPrinted(true);
        // widget.setReadOnly(true);
        //
        // page.getAnnotations().add(widget);
        // field.setValue("sample"); // /DA is a required entry
        //
        // break;
        case OFFSET_STRING: // TODO textに統合したい
          Text recordText1 = textMap.get(draw.getKey());
          Color recordColor1 = colorMap.get(recordText1.getColorKey());

          Font recordFont1 = fontMap.get(recordText1.getFontKey());
          PDFont recordPdFont1 = pdFontMap.get(recordText1.getFontKey());

          stream.setNonStrokingColor(recordColor1);
          stream.setFont(recordPdFont1, recordFont1.getSize());
          List<?> list = (List<?>) paramMap.get(draw.getList());
          if (list == null || list.isEmpty()) return;
          int size = list.size() - 1;
          for (int i = 0; i < draw.getValues().size(); i++) {
            Value value = draw.getValues().get(i);
            DrawUtil.drawOffsetString(stream, value, recordPdFont1, recordFont1.getSize(), size);
          }
          break;
        case RECORD_STRING: // TODO textに統合したいrepeatedフラグで
          Text recordText = textMap.get(draw.getKey());
          Color recordColor = colorMap.get(recordText.getColorKey());

          Font recordFont = fontMap.get(recordText.getFontKey());
          PDFont recordPdFont = pdFontMap.get(recordText.getFontKey());

          stream.setNonStrokingColor(recordColor);
          stream.setFont(recordPdFont, recordFont.getSize());
          if (draw.getList() != null) {
            DrawUtil.drawRecordString(
                stream, draw, paramMap, recordPdFont, recordFont.getSize(), stringList);
          }
          break;

        default:
          break;
      }
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
