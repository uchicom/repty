// (c) 2018 uchicom
package com.uchicom.repty;

import java.awt.Color;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Meta;
import com.uchicom.repty.dto.ResourceFile;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Unit;
import com.uchicom.repty.dto.Value;

/**
 * Repty.
 * 
 * @author hex
 *
 */
public class Repty implements Closeable {

	/** ロガー */
	private static final Logger logger = Logger.getLogger(Repty.class.getCanonicalName());

	/** True Type Collectionのマップ */
	private final Map<String, TrueTypeCollection> ttcMap = new HashMap<>();

	/** True Type Fontのマップ */
	private final Map<String, TrueTypeFont> ttFontMap = new HashMap<>();

	/** Imageオブジェクトのマップ */
	private final Map<String, PDImageXObject> xImageMap = new HashMap<>();

	/** PDフォントのマップ */
	private final Map<String, PDFont> pdFontMap = new HashMap<>();

	/** PDフォント名のマップ */
	private final Map<String, PDFont> pdFontNameMap = new HashMap<>();

	/** テンプレート */
	private final Template template;

	/** PDドキュメント */
	private final PDDocument document;

	/** 描画情報 */
	private final List<Draw> draws = new ArrayList<>(1024);

	/** メタ情報 */
	private final List<Meta> metas = new ArrayList<>(8);

	/** 改行計算用文字列リスト */
	private final List<String> stringList = new ArrayList<>(100);

	private InputStream getStream(ResourceFile resourceFile) throws IOException {
		if (resourceFile.isResource()) {
			return getClass().getClassLoader().getResourceAsStream(resourceFile.getFile());
		} else {
			return Files.newInputStream(Paths.get(resourceFile.getFile()));
		}
	}

	private void setDefaultPdFontMap(PDType1Font... fonts) {

		Arrays.stream(fonts).forEach(font -> pdFontMap.put(font.getName(), font));
	}

	/**
	 * コンストラクタ.<br>
	 * リソースの初期化も実施します.
	 * 
	 * @param document PDドキュメント
	 * @param template テンプレート
	 * @throws IOException ファイル読み込みに失敗した場合
	 */
	public Repty(PDDocument document, Template template) throws IOException {
		// フォントマップ作成
		for (Entry<String, Font> entry : template.getResource().getFontMap().entrySet()) {
			Font font = entry.getValue();
			if (font.getFontFileKey() != null) {
				if (template.getResource().getTtcMap() != null && !ttcMap.containsKey(font.getFontFileKey())) {
					ResourceFile ttc = template.getResource().getTtcMap().get(font.getFontFileKey());
					if (!ttc.isResource()) {
						try (InputStream is = Files.newInputStream(Paths.get(ttc.getFile()));
								TrueTypeCollection ttco = new TrueTypeCollection(is)) {
							ttcMap.put(font.getFontFileKey(), ttco);
						}
					} else {
						try (InputStream is = getClass().getClassLoader().getResourceAsStream(ttc.getFile());
								TrueTypeCollection ttco = new TrueTypeCollection(is)) {
							ttcMap.put(font.getFontFileKey(), ttco);
						}
					}
					ttFontMap.put(font.getName(), ttcMap.get(font.getFontFileKey()).getFontByName(font.getName()));
				} else if (template.getResource().getTtfMap() != null
						&& !ttFontMap.containsKey(font.getFontFileKey())) {
					ResourceFile ttf = template.getResource().getTtfMap().get(font.getFontFileKey());
					PDFont ttco = null;
					if (!ttf.isResource()) {
						try (InputStream is = Files.newInputStream(Paths.get(ttf.getFile()));) {
							ttco = PDType0Font.load(document, is);
						}
					} else {
						try (InputStream is = getClass().getClassLoader().getResourceAsStream(ttf.getFile());) {
							ttco = PDType0Font.load(document, is);
						}
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
//						xImageMap.put(entry.getKey(), PDImageXObject.createFromByteArray(document, is.readAllBytes(),
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
//				try (InputStream is = getStream(resourceFile); TrueTypeCollection ttco = new TrueTypeCollection(is)) {
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

		// デフォルトフォントマップ
		setDefaultPdFontMap(PDType1Font.COURIER, PDType1Font.COURIER_BOLD, PDType1Font.COURIER_BOLD_OBLIQUE,
				PDType1Font.COURIER_OBLIQUE, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD,
				PDType1Font.HELVETICA_BOLD_OBLIQUE, PDType1Font.HELVETICA_OBLIQUE, PDType1Font.SYMBOL,
				PDType1Font.TIMES_BOLD, PDType1Font.TIMES_BOLD_ITALIC, PDType1Font.TIMES_ITALIC,
				PDType1Font.TIMES_ROMAN, PDType1Font.ZAPF_DINGBATS);
		// イメージマップ作成
		if (template.getResource().getImageMap() != null) {
			for (Entry<String, ResourceFile> entry : template.getResource().getImageMap().entrySet()) {
				ResourceFile resourceFile = entry.getValue();
				try (InputStream is = getStream(resourceFile)) {
					xImageMap.put(entry.getKey(),
							PDImageXObject.createFromByteArray(document, is.readAllBytes(), resourceFile.getFile()));

				}
			}
		}
		// addPageする前に初期状態を準備しておいて、出力内容を使いまわして保存する。
		this.document = document;
		this.template = template;
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
			} else {
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
		List<Draw> drawList = unit.getDrawList();
		if (!drawList.isEmpty()) {
			draws.addAll(drawList);
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
		for (String drawKey : drawKeys) {
			addKey(drawKey);
		}
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
		List<Draw> drawList = unit.getDrawList();
		if (!drawList.isEmpty()) {
			draws.removeAll(drawList);
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
		for (String drawKey : drawKeys) {
			removeKey(drawKey);
		}
		return this;
	}

	/**
	 * テンプレートキーとメタ情報を全て削除します.
	 * 
	 * @return このオブジェクトへの参照
	 */
	public Repty clearKeys() {
		draws.clear();
		metas.clear();
		return this;
	}

	/**
	 * テンプレートキーを切り替えます.
	 * 
	 * @param removeKey 削除するテンプレートキー
	 * @param addKey    追加するテンプレートキー
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
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (metas.isEmpty()) {
			return new PDPage(PDRectangle.A4);
		} else {
			Meta meta = metas.get(metas.size() - 1);
			PDRectangle pdRectangle = null;
			if (meta.getPdRectangle() != null) {
				pdRectangle = (PDRectangle) PDRectangle.class.getDeclaredField(meta.getPdRectangle()).get(null);
				pdRectangle = meta.isLandscape() ? new PDRectangle(pdRectangle.getHeight(), pdRectangle.getWidth())
						: pdRectangle;
			} else {
				pdRectangle = new PDRectangle(meta.getX(), meta.getY(), meta.getWidth(), meta.getHeight());
			}
			return new PDPage(pdRectangle);
		}
	}

	/**
	 * リソースを設定してPDページを作成します.
	 * 
	 */
	public PDPage getInstancePage(List<PDStream> cs, PDResources resources)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		PDPage page = getInstancePage();
		page.setContents(cs);
		page.setResources(resources);
		return page;
	}

	/**
	 * テンプレートをもとにPDFページを作成します.
	 */
	public PDPage createPage(Map<String, Object> paramMap) throws IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PDPage page = getInstancePage();
		try (PDPageContentStream stream = new PDPageContentStream(document, page);) {
			createPage(paramMap, stream);
			return page;
		}
	}

	/**
	 * リソースを設定してPDFページを追加します.
	 */
	public PDPage appendPage(Map<String, Object> paramMap, List<PDStream> cs, PDResources resources)
			throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		PDPage page = getInstancePage(cs, resources);
		appendPage(paramMap, page);
		return page;
	}

	/**
	 * テンプレートをもとに既存のPDFページに出力します.
	 */
	public PDPage appendPage(Map<String, Object> paramMap, PDPage page)
			throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		try (PDPageContentStream stream = new PDPageContentStream(document, page, AppendMode.APPEND, true, false);) {
			createPage(paramMap, stream);
			return page;
		}
	}

	/**
	 * テンプレート情報をもとにPDFページを出力します.
	 */
	public void createPage(Map<String, Object> paramMap, PDPageContentStream stream)
			throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		// 書き込む用のストリームを準備
		Map<String, Color> colorMap = template.getResource().getColorMap();
		Map<String, Line> lineMap = template.getResource().getLineMap();
		Map<String, Text> textMap = template.getResource().getTextMap();
		Map<String, Font> fontMap = template.getResource().getFontMap();
		for (Draw draw : draws) {
			switch (draw.getDrawType()) {
			case LINE:// 線
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
					if (list == null || list.isEmpty())
						return;
					int size = list.size();
					for (int i = 0; i < draw.getValues().size(); i++) {
						Value value = draw.getValues().get(i);
						drawRecordLine(stream, value, paramMap, size);
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
					if (list == null || list.isEmpty())
						return;
					int size = list.size();
					for (int i = 0; i < draw.getValues().size(); i++) {
						Value value = draw.getValues().get(i);
						if (value.isFill()) {
							stream.setNonStrokingColor(color);
						}
						drawRecordRectangle(stream, value, paramMap, size);
					}
				} else {
					for (Value value : draw.getValues()) {
						// 塗りつぶしかどうか
						stream.addRect(value.getX1(), value.getY1(), value.getX2() - value.getX1(),
								value.getY2() - value.getY1());
						if (value.isFill()) {
							stream.setNonStrokingColor(color);
							stream.fill();// 塗りつぶし
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
							nextLineIndex = getNextLineIndex(pdFont, font2.getSize(), tempValue.substring(currentIndex),
									limitWidth);
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
						float y = getAlignOffset(value.getY1() + value.getNewLineY(),
								value.getNewLineY() * stringList.size(),
								value.getAlignY() == 0 ? 2 : value.getAlignY() == 2 ? 0 : value.getAlignY());

						for (String lineValue : stringList) {
							// 横寄せ
							float x = getAlignOffset(value.getX1(),
									getPdfboxSize(font2.getSize(), pdFont.getStringWidth(lineValue)),
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
						float x = getAlignOffset(value.getX1(),
								getPdfboxSize(font2.getSize(), pdFont.getStringWidth(tempValue)), value.getAlignX());
						// 縦寄せ
						float y = getAlignOffset(value.getY1(),
								getPdfboxSize(font2.getSize(), pdFont.getFontDescriptor().getCapHeight()),
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
						stream.drawImage(imagex, value.getX1(), value.getY1(), value.getX2() - value.getX1(),
								value.getY2() - value.getY1());
					}
				}
				break;
			case BYTE_IMAGE:
				byte[] bytes = (byte[]) paramMap.get(draw.getKey());
				PDImageXObject byteImagex = PDImageXObject.createFromByteArray(document, bytes, draw.getKey());

				for (Value value : draw.getValues()) {
					if (value.getX1() == value.getX2()) {
						stream.drawImage(byteImagex, value.getX1(), value.getY1());
					} else {
						stream.drawImage(byteImagex, value.getX1(), value.getY1(), value.getX2() - value.getX1(),
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
			case OFFSET_STRING:// TODO textに統合したい
				Text recordText1 = textMap.get(draw.getKey());
				Color recordColor1 = colorMap.get(recordText1.getColorKey());

				Font recordFont1 = fontMap.get(recordText1.getFontKey());
				PDFont recordPdFont1 = pdFontMap.get(recordText1.getFontKey());

				stream.setNonStrokingColor(recordColor1);
				stream.setFont(recordPdFont1, recordFont1.getSize());
				List<?> list = (List<?>) paramMap.get(draw.getList());
				if (list == null || list.isEmpty())
					return;
				int size = list.size() - 1;
				for (int i = 0; i < draw.getValues().size(); i++) {
					Value value = draw.getValues().get(i);
					drawOffsetString(stream, value, recordPdFont1, recordFont1.getSize(), size);
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
					drawRecordString(stream, draw, paramMap, recordPdFont, recordFont.getSize(), stringList);
				}
				break;

			default:
				break;
			}

		}
	}

	/**
	 * 文字列出力用寄せたoffset取得.
	 * 
	 */
	private static float getAlignOffset(float offset, float pdfboxSize, int align) {
		switch (align) {
		case 1:
			offset -= pdfboxSize / 2;
			break;
		case 2:
			offset -= pdfboxSize;
			break;
		default:
		}
		return offset;
	}

	/**
	 * 文字列出力用pdfbox文字列長さ計算
	 * 
	 * @param fontSize フォントサイズ
	 * @param length   文字列長さ
	 * @return 長さ
	 */
	private static float getPdfboxSize(float fontSize, float length) {
		return fontSize * length / 1000;
	}

	/**
	 * オフセット出力.
	 * 
	 */
	public static void drawOffsetString(PDPageContentStream stream, Value value, PDFont pdFont, float fontSize,
			int size) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		if (value.isRepeat()) {
			stream.beginText();
			float x = getAlignOffset(value.getX1(), getPdfboxSize(fontSize, pdFont.getStringWidth(value.getValue())),
					value.getAlignX());
			stream.newLineAtOffset(x, value.getY1());
			stream.showText(value.getValue());
			for (int i = 0; i < size; i++) {
				stream.newLineAtOffset(value.getNextX(), value.getNextY());
				stream.showText(value.getValue());
			}
			stream.endText();
		} else {
			stream.beginText();
			float x = getAlignOffset(value.getX1() + value.getNextX() * size,
					getPdfboxSize(fontSize, pdFont.getStringWidth(value.getValue())), value.getAlignX());
			stream.newLineAtOffset(x, value.getY1() + value.getNextY() * size);
			stream.showText(value.getValue());
			stream.endText();
		}
	}

	/**
	 * 矩形を繰り返し追加します.
	 * 
	 */
	public static void drawRecordRectangle(PDPageContentStream stream, Value value, Map<String, Object> paramMap,
			int size) throws IOException {
		float nextX = value.getNextX();
		float nextY = value.getNextY();
		float x1 = value.getX1();
		float y1 = value.getY1();
		float x2 = value.getX2();
		float y2 = value.getY2();
		if (nextX < 0 && value.getX1() < value.getX2() || nextX > 0 && value.getX1() > value.getX2()) {
			x2 = x1;
			x1 = value.getX2();
		}
		if (nextY < 0 && value.getY1() < value.getY2() || nextY > 0 && value.getY1() > value.getY2()) {
			y2 = y1;
			y1 = value.getY2();
		}
		float width = x2 - x1;
		float height = y2 - y1;
		if (nextX == 0 && !value.isRepeat()) {
			y2 += nextY * size;
			stream.addRect(x1, y1, width, y2 - y1);
		} else if (nextY == 0 && !value.isRepeat()) {
			x2 += nextX * size;
			stream.addRect(x1, y1, x2 - x1, height);
		} else {
			for (int i = 0; i < size; i++) {
				// x1,x2大きい方で判断、差分の+-を比較する。
				// listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
				// テンプレートはそこまでやらない。リストサイズを調整する
				stream.addRect(x1 + nextX * i, y1 + nextY * i, width, height);
			}
		}
		if (value.isFill()) {
			stream.fill();// 塗りつぶし
		} else {
			stream.stroke();
		}
	}

	/**
	 * 線を繰り返し追加します.
	 * 
	 */
	public static void drawRecordLine(PDPageContentStream stream, Value value, Map<String, Object> paramMap, int size)
			throws IOException {

		float nextX = value.getNextX();
		float nextY = value.getNextY();
		float x1 = value.getX1();
		float y1 = value.getY1();
		float x2 = value.getX2();
		float y2 = value.getY2();
		if (nextX < 0 && value.getX1() < value.getX2() || nextX > 0 && value.getX1() > value.getX2()) {
			x2 = x1;
			x1 = value.getX2();
		}
		if (nextY < 0 && value.getY1() < value.getY2() || nextY > 0 && value.getY1() > value.getY2()) {
			y2 = y1;
			y1 = value.getY2();
		}

		// 延長か繰り返しかを判断する
		if (x1 == x2 && nextX == 0 && !value.isRepeat()) {
			y2 += nextY * (size - 1);
			stream.moveTo(x1, y1);
			stream.lineTo(x2, y2);
			stream.stroke();
		} else if (y1 == y2 && nextY == 0 && !value.isRepeat()) {
			x2 += nextX * (size - 1);
			stream.moveTo(x1, y1);
			stream.lineTo(x2, y2);
			stream.stroke();
		} else {
			for (int i = 0; i < size; i++) {
				// x1,x2大きい方で判断、差分の+-を比較する。
				// listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
				stream.moveTo(x1 + nextX * i, y1 + nextY * i);
				stream.lineTo(x2 + nextX * i, y2 + nextY * i);
				stream.stroke();
			}
		}
	}

	/**
	 * 文字列を繰り返し追加します.
	 * 
	 */
	public static void drawRecordString(PDPageContentStream stream, Draw draw, Map<String, Object> paramMap,
			PDFont pdFont, float fontSize, List<String> stringList)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, NoSuchFieldException {
		List<?> list = (List<?>) paramMap.get(draw.getList());
		if (list == null || list.isEmpty())
			return;
		Class<?> clazz = list.get(0).getClass();
		List<Value> valueList = draw.getValues();
		int valueSize = valueList.size();
		Method[] methods = new Method[valueList.size()];
		StringBuilder sb = new StringBuilder(64);
		sb.append("get");
		for (int i = 0; i < valueSize; i++) {
			Value value = valueList.get(i);
			String memberName = value.getValue();
			char prefix = memberName.charAt(0);
			if (prefix >= 'a' && prefix <= 'z') {
				sb.append((char) (prefix + ('A' - 'a')));
			} else {
				sb.append(prefix);
			}
			sb.append(memberName, 1, memberName.length());
			methods[i] = clazz.getMethod(sb.toString());
			sb.setLength(3);
		}
		int listSize = list.size();
		stream.beginText();
		float currentX = 0;
		float currentY = 0;
		float x = 0;
		float y = 0;
		float fontHeight = pdFont.getFontDescriptor().getCapHeight();

		for (int i = 0; i < listSize; i++) {
			for (int iValue = 0; iValue < valueSize; iValue++) {
				try {
					Value value = valueList.get(iValue);
					String string = String.valueOf(methods[iValue].invoke(list.get(i)));
					if (value.getLimitX() > 0) {
						stringList.clear();
						// リスト作成
						float limitWidth = value.getLimitX() - value.getX1();
						int nextLineIndex = 0;
						int currentIndex = 0;
						int maxLength = string.length();
						do {
							nextLineIndex = getNextLineIndex(pdFont, fontSize, string.substring(currentIndex),
									limitWidth);
							if (currentIndex + nextLineIndex > maxLength) {
								nextLineIndex = maxLength - currentIndex;
							}
							String lineValue = string.substring(currentIndex, currentIndex + nextLineIndex);
							stringList.add(lineValue);
							currentIndex += nextLineIndex;
						} while (currentIndex < maxLength);
						// リスト出力
						boolean isFirst = true;

						// 縦寄せ
						if (value.getAlignY() == 2) {
							y = value.getY1() + value.getNextY() * i + value.getNewLineY();
						} else {
							y = getAlignOffset(value.getY1() + value.getNextY() * i + value.getNewLineY(),
									value.getNewLineY() * stringList.size(), value.getAlignY() == 0 ? 2 : 1);
						}
						for (String lineValue : stringList) {
							// 横寄せ
							if (value.getAlignX() == 0) {
								x = value.getX1() + value.getNextX() * i;
							} else {
								x = getAlignOffset(value.getX1() + value.getNextX() * i,
										getPdfboxSize(fontSize, pdFont.getStringWidth(lineValue)), value.getAlignX());
							}
							// 初回チェック
							if (isFirst) {
								stream.newLineAtOffset(x - currentX, y - currentY);
								currentY = y;
								isFirst = false;
							} else {
								stream.newLineAtOffset(x - currentX, value.getNewLineY());
								currentY += value.getNewLineY();
							}
							stream.showText(lineValue);
							currentX = x;
						}
					} else {
						// 横寄せ
						if (value.getAlignX() == 0) {
							x = value.getX1() + value.getNextX() * i;
						} else {
							x = getAlignOffset(value.getX1() + value.getNextX() * i,
									getPdfboxSize(fontSize, pdFont.getStringWidth(string)), value.getAlignX());
						}
						// 縦寄せ
						if (value.getAlignY() == 0) {
							y = value.getY1() + value.getNextY() * i;
						} else {
							y = getAlignOffset(value.getY1() + value.getNextY() * i,
									getPdfboxSize(fontSize, fontHeight), value.getAlignY());
						}
						stream.newLineAtOffset(x - currentX, y - currentY);
						stream.showText(string);

						currentX = x;
						currentY = y;
					}
				} catch (InvocationTargetException e) {
					logger.log(Level.SEVERE, valueList.get(iValue).getValue(), e);
					throw e;
				}

			}
		}
		stream.endText();

	}

	/**
	 * 繰り返しで最適解を作成する
	 * 
	 * @throws IOException IOエラー
	 */
	private static int getNextLineIndex(PDFont pdFont, float fontSize, String value, float limitWidth)
			throws IOException {
		float width = pdFont.getStringWidth(value) / 1000 * fontSize;

		if (width < limitWidth) {
			return value.length();
		}
		int nextIndex = (int) (value.length() * (limitWidth / width));
		if (nextIndex > value.length()) {
			nextIndex = value.length();
		}
		float nextWidth = pdFont.getStringWidth(value.substring(0, nextIndex)) / 1000 * fontSize;
		if (nextWidth < limitWidth) {
			for (int i = nextIndex + 1; i < value.length(); i++) {
				nextWidth = pdFont.getStringWidth(value.substring(0, i)) / 1000 * fontSize;
				if (nextWidth > limitWidth) {
					return i - 1;
				}
			}
			return nextIndex;
		} else {
			for (int i = nextIndex - 1; i > 0; i--) {
				nextWidth = pdFont.getStringWidth(value.substring(0, i)) / 1000 * fontSize;
				if (nextWidth < limitWidth) {
					return i;
				}
			}
			return nextIndex;
		}
	}

	@Override
	public void close() throws IOException {
		ttcMap.forEach((key, value) -> {
			try {
				value.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "close", e);
			}
		});
	}

	/**
	 * 全てのページを削除します.
	 */
	public void removeAllPage() {
		document.getPages().forEach(pdPage -> {
			document.removePage(pdPage);
		});
	}
}