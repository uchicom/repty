package com.uchicom.repty;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Meta;
import com.uchicom.repty.dto.Path;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Unit;
import com.uchicom.repty.dto.Value;

/**
 * 
 * @author hex
 *
 */
public class Repty implements Closeable {

	Map<String, TrueTypeCollection> ttcMap = new HashMap<>();
	Map<String, PDImageXObject> xImageMap = new HashMap<>();
	Map<String, TrueTypeFont> ttFontMap = new HashMap<>();
	Map<String, PDFont> pdFontMap = new HashMap<>();
	PDDocument document;
	Template template;
	List<Draw> draws = new ArrayList<>(1024);
	List<Meta> metas = new ArrayList<>(8);

	/**
	 * Spec初期化.
	 * 
	 * @param document
	 * @param template
	 * @throws IOException
	 */
	public Repty(PDDocument document, Template template) throws IOException {
		// フォントマップ作成
		for (Entry<String, Font> entry : template.getSpec().getFontMap().entrySet()) {
			String key = entry.getKey();
			Font font = entry.getValue();
			if (font.getTtc() != null) {
				if (!ttcMap.containsKey(font.getTtc())) {
					if (!font.isResource()) {
						try (InputStream is = Files.newInputStream(Paths.get(font.getTtc()))) {
							ttcMap.put(font.getTtc(), new TrueTypeCollection(is));
						}
					} else {
						try (InputStream is = getClass().getClassLoader().getResourceAsStream(font.getTtc())) {
							ttcMap.put(font.getTtc(), new TrueTypeCollection(is));
						}
					}
				}
				ttFontMap.put(key, ttcMap.get(font.getTtc()).getFontByName(font.getName()));

			}
		}
		// デフォルトフォントマップ
		pdFontMap.put("PDType1Font.COURIER", PDType1Font.COURIER);
		pdFontMap.put("PDType1Font.COURIER_BOLD", PDType1Font.COURIER_BOLD);
		pdFontMap.put("PDType1Font.COURIER_BOLD_OBLIQUE", PDType1Font.COURIER_BOLD_OBLIQUE);
		pdFontMap.put("PDType1Font.COURIER_OBLIQUE", PDType1Font.COURIER_OBLIQUE);
		pdFontMap.put("PDType1Font.HELVETICA", PDType1Font.HELVETICA);
		pdFontMap.put("PDType1Font.HELVETICA_BOLD", PDType1Font.HELVETICA_BOLD);
		pdFontMap.put("PDType1Font.HELVETICA_BOLD_OBLIQUE", PDType1Font.HELVETICA_BOLD_OBLIQUE);
		pdFontMap.put("PDType1Font.HELVETICA_OBLIQUE", PDType1Font.HELVETICA_OBLIQUE);
		pdFontMap.put("PDType1Font.SYMBOL", PDType1Font.SYMBOL);
		pdFontMap.put("PDType1Font.TIMES_BOLD", PDType1Font.TIMES_BOLD);
		pdFontMap.put("PDType1Font.TIMES_BOLD_ITALIC", PDType1Font.TIMES_BOLD_ITALIC);
		pdFontMap.put("PDType1Font.TIMES_ITALIC", PDType1Font.TIMES_ITALIC);
		pdFontMap.put("PDType1Font.TIMES_ROMAN", PDType1Font.TIMES_ROMAN);
		pdFontMap.put("PDType1Font.ZAPF_DINGBATS", PDType1Font.ZAPF_DINGBATS);
		// イメージマップ作成
		for (Entry<String, Path> entry : template.getSpec().getImageMap().entrySet()) {
			String key = entry.getKey();
			Path value = entry.getValue();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
					InputStream is = getClass().getClassLoader().getResourceAsStream(value.getPath());) {
				byte[] bytes = new byte[1024 * 4 * 1024];
				int length = 0;
				while ((length = is.read(bytes)) > 0) {
					baos.write(bytes, 0, length);
				}
				xImageMap.put(key, PDImageXObject.createFromByteArray(document, baos.toByteArray(), value.getPath()));
			}
		}
		// addPageする前に初期状態を準備しておいて、出力内容を使いまわして保存する。
		this.document = document;
		this.template = template;
	}

	/**
	 * 初期化 保存時にクリアされてしまう 新バージョンで解決されるかも。
	 * 
	 * @throws IOException
	 *             入出力エラー
	 */
	public void init() throws IOException {
		// フォント作成
		for (Entry<String, TrueTypeFont> entry : ttFontMap.entrySet()) {
			pdFontMap.put(entry.getKey(), PDType0Font.load(document, entry.getValue(), true));
		}
	}

	/**
	 * テンプレートキー追加.
	 * 
	 * @param drawKey
	 * @return
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
	 * テンプレートキー切り替え.
	 * 
	 * @param removeKey
	 * @param addKey
	 * @return
	 */
	public Repty changeKey(String removeKey, String addKey) {
		removeKey(removeKey);
		addKey(addKey);
		return this;
	}

	/**
	 * 
	 * @param paramMap
	 * @return
	 * @throws IOException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public PDPage createPage(Map<String, Object> paramMap) throws IOException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		PDPage page = null;
		if (metas.isEmpty()) {
			page = new PDPage(PDRectangle.A4);
		} else {
			Field field = PDRectangle.class.getDeclaredField(metas.get(metas.size() - 1).getPdRectangle());
			page = new PDPage((PDRectangle) field.get(PDRectangle.A4));
		}

		Map<String, Color> colorMap = template.getSpec().getColorMap();
		Map<String, Line> lineMap = template.getSpec().getLineMap();
		Map<String, Text> textMap = template.getSpec().getTextMap();
		Map<String, Font> fontMap = template.getSpec().getFontMap();

		List<String> stringList = new ArrayList<>(100);
		// 書き込む用のストリームを準備
		try (PDPageContentStream stream = new PDPageContentStream(document, page);) {
			for (Draw draw : draws) {
				switch (draw.getType()) {
				case "line":// 線
					Line line = lineMap.get(draw.getKey());
					Color color = colorMap.get(line.getColorKey());
					float lineWidth = line.getWidth();
					stream.setLineWidth(lineWidth);
					stream.setStrokingColor(color);
					if (draw.isRepeated()) {
						for (int i = 0; i < draw.getValues().size(); i++) {
							Value value = draw.getValues().get(i);
							try {
								drawRecordLine(stream, value, paramMap);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						for (Value value : draw.getValues()) {
							stream.moveTo(value.getX1(), value.getY1());
							stream.lineTo(value.getX2(), value.getY2());
							stream.stroke();
						}
					}
					break;
				case "rectangle": // 四角形
					line = lineMap.get(draw.getKey());
					color = colorMap.get(line.getColorKey());
					lineWidth = line.getWidth();
					stream.setLineWidth(lineWidth);
					stream.setStrokingColor(color);
					if (draw.isRepeated()) {
						for (int i = 0; i < draw.getValues().size(); i++) {
							Value value = draw.getValues().get(i);
							if (value.isFill()) {
								stream.setNonStrokingColor(color);
							}
							try {
								drawRecordRectangle(stream, value, paramMap);
							} catch (Exception e) {
								e.printStackTrace();
							}
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
				case "text": // 文字列描画
				case "object":
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
								nextLineIndex = getNextLineIndex(pdFont, font2.getSize(),
										tempValue.substring(currentIndex), limitWidth);
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
									getPdfboxSize(font2.getSize(), pdFont.getStringWidth(tempValue)),
									value.getAlignX());
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
				case "image":
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
				case "form": // TODO v2対応
					PDAcroForm acroForm = new PDAcroForm(document);
					document.getDocumentCatalog().setAcroForm(acroForm);
					PDFont font = PDType1Font.HELVETICA;
					PDResources resources = new PDResources();
					resources.put(COSName.getPDFName("Helv"), font);
					acroForm.setDefaultResources(resources);

					PDTextField field = new PDTextField(acroForm);
					field.setPartialName("test");
					field.setDefaultAppearance("/Helv 12 Tf 0 0 1 rg");// 12→0で自動

					acroForm.getFields().add(field);

					PDAnnotationWidget widget = field.getWidgets().get(0);
					PDRectangle rectangle = new PDRectangle(10, 200, 50, 50);
					widget.setRectangle(rectangle);
					widget.setPage(page);
					field.getWidgets().add(widget);

					widget.setPrinted(true);
					widget.setReadOnly(true);

					page.getAnnotations().add(widget);
					field.setValue("sample"); // /DA is a required entry

					break;
				case "offsetString"://TODO textに統合したい
					Text recordText1 = textMap.get(draw.getKey());
					Color recordColor1 = colorMap.get(recordText1.getColorKey());

					Font recordFont1 = fontMap.get(recordText1.getFontKey());
					PDFont recordPdFont1 = pdFontMap.get(recordText1.getFontKey());

					stream.setNonStrokingColor(recordColor1);
					stream.setFont(recordPdFont1, recordFont1.getSize());
					for (int i = 0; i < draw.getValues().size(); i++) {
						Value value = draw.getValues().get(i);
						try {
							drawOffsetString(stream, value, paramMap, recordPdFont1, recordFont1.getSize());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case "recordString": //TODO textに統合したいrepeatedフラグで
					Text recordText = textMap.get(draw.getKey());
					Color recordColor = colorMap.get(recordText.getColorKey());

					Font recordFont = fontMap.get(recordText.getFontKey());
					PDFont recordPdFont = pdFontMap.get(recordText.getFontKey());

					stream.setNonStrokingColor(recordColor);
					stream.setFont(recordPdFont, recordFont.getSize());
					for (int i = 0; i < draw.getValues().size(); i++) {
						Value value = draw.getValues().get(i);
						try {
							drawRecordString(stream, value, paramMap, recordPdFont, recordFont.getSize(), stringList);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;

				default:
					break;
				}

			}
			// ストリームを閉じる
		}
		return page;
	}

	/**
	 * 文字列出力用寄せたoffset取得.
	 * 
	 * @param offset
	 * @param pdfboxSize
	 * @param align
	 * @return
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
	 * @param fontSize
	 * @param length
	 * @return
	 */
	private static float getPdfboxSize(float fontSize, float length) {
		return fontSize * length / 1000;
	}

	/**
	 * オフセット出力.
	 * 
	 * @param stream
	 * @param value
	 * @param paramMap
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public static void drawOffsetString(PDPageContentStream stream, Value value, Map<String, Object> paramMap,
			PDFont pdFont, float fontSize) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		List<?> list = (List<?>) paramMap.get(value.getParamName());
		if (list == null || list.isEmpty())
			return;
		int size = list.size() - 1;
		if (value.isRepeat()) {
			stream.beginText();
			float x = getAlignOffset(value.getX1(),
					getPdfboxSize(fontSize, pdFont.getStringWidth(value.getMemberName())), value.getAlignX());
			stream.newLineAtOffset(x, value.getY1());
			stream.showText(value.getMemberName());
			for (int i = 0; i < size; i++) {
				stream.newLineAtOffset(value.getNextX(), value.getNextY());
				stream.showText(value.getMemberName());
			}
			stream.endText();
		} else {
			stream.beginText();
			float x = getAlignOffset(value.getX1() + value.getNextX() * size,
					getPdfboxSize(fontSize, pdFont.getStringWidth(value.getMemberName())), value.getAlignX());
			stream.newLineAtOffset(x, value.getY1() + value.getNextY() * size);
			stream.showText(value.getMemberName());
			stream.endText();
		}
	}

	/**
	 * 繰り返し文字列出力
	 * 
	 * @param stream
	 * @param value
	 * @param paramMap
	 * @throws IOException
	 */
	public static void drawRecordRectangle(PDPageContentStream stream, Value value, Map<String, Object> paramMap)
			throws IOException {
		List<?> list = (List<?>) paramMap.get(value.getParamName());
		if (list == null || list.isEmpty())
			return;
		int size = list.size();
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

	public static void drawRecordLine(PDPageContentStream stream, Value value, Map<String, Object> paramMap)
			throws IOException {
		List<?> list = (List<?>) paramMap.get(value.getParamName());
		if (list == null || list.isEmpty())
			return;
		int size = list.size();
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

	public static void drawRecordString(PDPageContentStream stream, Value value, Map<String, Object> paramMap,
			PDFont pdFont, float fontSize, List<String> stringList) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
		List<?> list = (List<?>) paramMap.get(value.getParamName());
		if (list == null || list.isEmpty())
			return;

		Method method = list.get(0).getClass().getMethod(
				"get" + value.getMemberName().substring(0, 1).toUpperCase() + value.getMemberName().substring(1));

		for (int i = 0; i < list.size(); i++) {
			String string = String.valueOf(method.invoke(list.get(i)));
			stream.beginText();
			if (value.getLimitX() > 0) {
				stringList.clear();
				// リスト作成
				float limitWidth = value.getLimitX() - value.getX1();
				int nextLineIndex = 0;
				int currentIndex = 0;
				int maxLength = string.length();
				do {
					nextLineIndex = getNextLineIndex(pdFont, fontSize, string.substring(currentIndex), limitWidth);
					if (currentIndex + nextLineIndex > maxLength) {
						nextLineIndex = maxLength - currentIndex;
					}
					String lineValue = string.substring(currentIndex, currentIndex + nextLineIndex);
					stringList.add(lineValue);
					currentIndex += nextLineIndex;
				} while (currentIndex < maxLength);
				// リスト出力
				boolean isFirst = true;
				float currentX = 0;
				// 縦寄せ
				float y = getAlignOffset(value.getY1() + value.getNextY() * i + value.getNewLineY(),
						value.getNewLineY() * stringList.size(),
						value.getAlignY() == 0 ? 2 : value.getAlignY() == 2 ? 0 : value.getAlignY());

				for (String lineValue : stringList) {
					// 横寄せ
					float x = getAlignOffset(value.getX1() + value.getNextX() * i,
							getPdfboxSize(fontSize, pdFont.getStringWidth(lineValue)), value.getAlignX());
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
				float x = getAlignOffset(value.getX1() + value.getNextX() * i,
						getPdfboxSize(fontSize, pdFont.getStringWidth(string)), value.getAlignX());
				// 縦寄せ
				float y = getAlignOffset(value.getY1() + value.getNextY() * i,
						getPdfboxSize(fontSize, pdFont.getFontDescriptor().getCapHeight()), value.getAlignY());
				stream.newLineAtOffset(x, y);
				stream.showText(string);
			}
			stream.endText();

		}
	}

	/**
	 * 繰り返しで最適解を作成する
	 * 
	 * @throws IOException
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
