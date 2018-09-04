package com.uchicom.repty;

import java.awt.Color;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.yaml.snakeyaml.Yaml;

import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Line;
import com.uchicom.repty.dto.Meta;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Unit;
import com.uchicom.repty.dto.Value;

public class Yamlpdf implements Closeable {

	/**
	 * ページは、自分で設定する。 PDPageを生成するメソッド Map<String, Object> param, document,
	 * Templateを渡す drawだけじゃじゃなくて、共通と各ページのキーとなる情報でまとめる。 可変表と線の対応 変数のみの設定を実施する。
	 * 文字レコード指定で、 レコードを指定して、変数を取得 type:record values: 1,2,0,10,a,リスト1
	 * offsetX,offsetY,nextX,nextY,dtoのメンバ変数名、list名（parameterMapで指定) 文字列出力
	 * offsetX1,offsetY1,offsetX2,offsetY2,nextX,nextY,list名
	 * 線出力繰り返しタイプ複数線、一直線nextの仕方で判別可能
	 * offsetX1,offsetY1,offsetX2,offsetY2,nextX,nextY,list名
	 * rectangle出力、サイズを可変にする。繰り返しのパターンはない。 いずれもmax値の制限がほしい。見た目を変えるのはtoStringで実装
	 * 右寄せ、左寄せ、中央寄せは、共通で実装 この処理は、commonとpageAを実施という風に変更
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0)
			return;
		long start = System.currentTimeMillis();
		try (PDDocument document = new PDDocument()) {
			Yaml yaml = new Yaml();
			System.out.println((System.currentTimeMillis() - start) + "[msec]yaml create");
			start = System.currentTimeMillis();
			Template template = yaml.loadAs(new String(Files.readAllBytes(new File(args[0]).toPath())), Template.class);
			// System.out.println(template);

			System.out.println((System.currentTimeMillis() - start) + "[msec]template create");
			start = System.currentTimeMillis();
			// PDFドキュメントを作成

			// DocumentへのObjectの登録はContentStream生成の前で実施。
			// サイズ指定
			// ページを追加(1ページ目)
			Yamlpdf yamlPdf = new Yamlpdf(document, template);

			System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create");
			start = System.currentTimeMillis();
			Map<String, Object> paramMap = new HashMap<>();
			List<Value> list = new ArrayList<>();
			Value val = new Value(1, 10, "こんにちは");
			list.add(val);
			list.add(val);
			list.add(val);
			list.add(val);
			List<Value> list2 = new ArrayList<>();
			Value val2 = new Value(1, 10, "Hello World");
			list2.add(val2);
			list2.add(val2);
			list2.add(val2);
			list2.add(val2);
			// 検索結果を保持する
			paramMap.put("list", list);
			paramMap.put("list2", list2);

			paramMap.put("name", "○○株式会社");
			paramMap.put("startDate", "2018/6/1");
			paramMap.put("endDate", "2018/11/30");
			//yaml 設定をキーで作成し、yamlPdf.addKey("page1", "default");
			//yaml 設定をキーで作成し、yamlPdf.addKey("page1", "page1);
			for (int i = 0; i < 10; i++) {
				// TODO 削除追加で切り替えるのは効率が悪い
				// TODO 設定をマップで保持して切り替えるのが良い
				// TODO paramMapも同じものは入れないで保持するのが早い。インスタンスは生成してないからまあいいか
				// TODO 右寄せ、左寄せ、中央寄せ
				// TODO 改行機能
				
				System.out.println(i);
				paramMap.put("total", "8");
				yamlPdf.init();
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf init");
				start = System.currentTimeMillis();
				paramMap.put("page", "1");
				yamlPdf.addKey("default");
				yamlPdf.addKey("page1");
				PDPage page1 = yamlPdf.addPage(paramMap);
				document.addPage(page1);
				paramMap.put("page", "2");
				yamlPdf.changeKey("page1", "page2");
				PDPage page2 = yamlPdf.addPage(paramMap);
				document.addPage(page2);
				yamlPdf.changeKey("page2", "page3");
				paramMap.put("page", "3");
				PDPage page3 = yamlPdf.addPage(paramMap);
				document.addPage(page3);
				paramMap.put("page", "4");
				PDPage page4 = yamlPdf.addPage(paramMap);
				document.addPage(page4);
				paramMap.put("page", "5");
				PDPage page5 = yamlPdf.addPage(paramMap);
				document.addPage(page5);
				paramMap.put("page", "6");
				PDPage page6 = yamlPdf.addPage(paramMap);
				document.addPage(page6);
				paramMap.put("page", "7");
				PDPage page7 = yamlPdf.addPage(paramMap);
				document.addPage(page7);
				paramMap.put("page", "8");
				PDPage page8 = yamlPdf.addPage(paramMap);
				document.addPage(page8);
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create page 1 file");
				start = System.currentTimeMillis();
				File outFile = new File("result/" + i + "test.pdf");
				outFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outFile);
				document.save(fos);
				fos.close();

				document.removePage(page1);

				document.removePage(page2);
				document.removePage(page3);
				document.removePage(page4);
				document.removePage(page5);
				document.removePage(page6);
				document.removePage(page7);
				document.removePage(page8);
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create 1 file");
				start = System.currentTimeMillis();

			}

			yamlPdf.close();
			// 作成したPDFを保存
			System.out.println((System.currentTimeMillis() - start) + "[msec]pdf save");
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Map<String, TrueTypeCollection> ttcMap = new HashMap<>();
	Map<String, PDImageXObject> xImageMap = new HashMap<>();
	Map<String, TrueTypeFont> ttFontMap = new HashMap<>();
	Map<String, PDFont> pdFontMap = new HashMap<>();
	PDDocument document;
	Template template;
	List<Draw> draws = new ArrayList<>(1024);
	List<Meta> metas = new ArrayList<>(8);
	public Yamlpdf(PDDocument document, Template template) throws IOException {
		long start = System.currentTimeMillis();
		System.out.println((System.currentTimeMillis() - start) + "[msec]ttf create");
		start = System.currentTimeMillis();

		// フォントマップ作成
		Map<String, Font> fontMap = template.getSpec().getFontMap();
		fontMap.forEach((key, value) -> {
			if (!ttcMap.containsKey(value.getTtc())) {
				try {
					ttcMap.put(value.getTtc(), new TrueTypeCollection(Files.newInputStream(Paths.get(value.getTtc()))));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				ttFontMap.put(key, ttcMap.get(value.getTtc()).getFontByName(value.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		System.out.println((System.currentTimeMillis() - start) + "[msec]document ttc font load");
		start = System.currentTimeMillis();
		// イメージマップ作成
		Map<String, URL> imageMap = template.getSpec().getImageMap();
		imageMap.forEach((key, value) -> {
			try {
				xImageMap.put(key, PDImageXObject.createFromFile(value.getFile(), document)); // TODO 画像指定検討
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		// addPageする前に初期状態を準備しておいて、出力内容を使いまわして保存する。
		System.out.println((System.currentTimeMillis() - start) + "[msec]imageMap create");
		start = System.currentTimeMillis();
		this.document = document;
		this.template = template;
	}

	/**
	 * 初期化 保存時にクリアされてしまう
	 * 新バージョンで解決されるかも。
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {

		ttFontMap.forEach((key, value) -> {
			try {
				pdFontMap.put(key, PDType0Font.load(document, value, true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public Yamlpdf addKey(String drawKey) {
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
	public Yamlpdf changeKey(String removeKey, String addKey) {
		Unit unit = template.getDrawMap().get(removeKey);
		List<Draw> drawList = unit.getDrawList();
		if (!drawList.isEmpty()) {
			draws.removeAll(drawList);
		}
		if (unit.getMeta() != null) {
			metas.remove(unit.getMeta());
		}
		addKey(addKey);
		return this;
	}
	/**
	 * 
	 *
	 * @param paramMap
	 * @return
	 * @throws IOException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private PDPage addPage(Map<String, Object> paramMap) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		PDPage page = null;
		if (metas.isEmpty()) {
			page = new PDPage();// TODO ページサイズの指定
		} else {
			Field field = PDRectangle.class.getDeclaredField(metas.get(metas.size() - 1).getPdRectangle());
			page = new PDPage((PDRectangle)field.get(PDRectangle.A4));
		}
		
		Map<String, Color> colorMap = template.getSpec().getColorMap();
		Map<String, Line> lineMap = template.getSpec().getLineMap();
		Map<String, Text> textMap = template.getSpec().getTextMap();
		Map<String, Font> fontMap = template.getSpec().getFontMap();

		// 書き込む用のストリームを準備
		PDPageContentStream stream = new PDPageContentStream(document, page);
		for (Draw draw : draws) {
			switch (draw.getType()) {
			case "line":// 線
				Line line = lineMap.get(draw.getKey());
				Color color = colorMap.get(line.getColorKey());
				int lineWidth = line.getWidth();
				for (Value value : draw.getValues()) {

					stream.setLineWidth(lineWidth);
					stream.setStrokingColor(color);
					stream.moveTo(value.getX1(), value.getY1());
					stream.lineTo(value.getX2(), value.getY2());
					stream.stroke();
				}
				break;
			case "rectangle": // 四角形
				Line line1 = lineMap.get(draw.getKey());
				Color color1 = colorMap.get(line1.getColorKey());
				int lineWidth1 = line1.getWidth();
				for (Value value : draw.getValues()) {
					// 塗りつぶしかどうか
					stream.setLineWidth(lineWidth1);
					stream.setStrokingColor(color1);
					stream.addRect(value.getX1(), value.getY1(), value.getX2() - value.getX1(),
							value.getY2() - value.getY1());
					if (value.isFill()) {
						stream.fill();// 塗りつぶし
					} else {
						stream.stroke();
					}
				}
				break;
			case "string": // 文字列描画 // TODO 文字列左右中央寄せ、全体の幅指定、改行機能検討
				Text text = textMap.get(draw.getKey());
				Color color2 = colorMap.get(text.getColorKey());

				Font font2 = fontMap.get(text.getFontKey());
				PDFont pdFont = pdFontMap.get(text.getFontKey());

				stream.setNonStrokingColor(color2);
				stream.setFont(pdFont, font2.getSize());

				for (Value value : draw.getValues()) {
					stream.beginText();
					stream.newLineAtOffset(value.getX1(), value.getY1());
					String tempValue = value.getValue();
					for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
						if (tempValue.contains("${")) {
							tempValue = tempValue.replaceAll("\\$\\{" + entry.getKey() + "\\}",
									entry.getValue().toString());
						}
					}
					stream.showText(tempValue);
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
			case "form": // TODO 今後の課題
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
				field.setValue("testtesttesttest"); // /DA is a required entry

				break;
			case "recordString":
				Text recordText = textMap.get(draw.getKey());
				Color recordColor = colorMap.get(recordText.getColorKey());

				Font recordFont = fontMap.get(recordText.getFontKey());
				PDFont recordPdFont = pdFontMap.get(recordText.getFontKey());

				stream.setNonStrokingColor(recordColor);
				stream.setFont(recordPdFont, recordFont.getSize());
				for (int i = 0; i < draw.getValues().size(); i++) {
					Value value = draw.getValues().get(i);
					try {
						drawRecordString(stream, value, paramMap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case "recordLine":

				line = lineMap.get(draw.getKey());
				color = colorMap.get(line.getColorKey());
				lineWidth = line.getWidth();
				stream.setLineWidth(lineWidth);
				stream.setStrokingColor(color);
				for (int i = 0; i < draw.getValues().size(); i++) {
					Value value = draw.getValues().get(i);
					try {
						drawRecordLine(stream, value, paramMap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case "recordRectangle":
				line = lineMap.get(draw.getKey());
				color = colorMap.get(line.getColorKey());
				lineWidth = line.getWidth();
				stream.setLineWidth(lineWidth);
				stream.setStrokingColor(color);
				for (int i = 0; i < draw.getValues().size(); i++) {
					Value value = draw.getValues().get(i);
					try {
						drawRecordRectangle(stream, value, paramMap);
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
		stream.close();
		return page;
	}

	public void drawRecordRectangle(PDPageContentStream stream, Value value, Map<String, Object> paramMap) throws IOException {
		int size = ((List) paramMap.get(value.getParamName())).size();
		if (size == 0) return;
		int nextX = value.getNextX();
		int nextY = value.getNextY();
		int x1 = value.getX1();
		int y1 = value.getY1();
		int x2 = value.getX2();
		int y2 = value.getY2();
		if (nextX < 0 && value.getX1() < value.getX2() ||
				nextX > 0 && value.getX1() > value.getX2()) {
			x2 = x1;
			x1 = value.getX2();
		}
		if (nextY < 0 && value.getY1() < value.getY2() ||
				nextY > 0 && value.getY1() > value.getY2()) {
			y2 = y1;
			y1 = value.getY2();
		}

		if (nextX == 0) {
			y2 += nextY * size;
			stream.addRect(x1,y1, x2 - x1, y2 - y1);
		} else if (nextY == 0) {
			x2 += nextX * size;
			stream.addRect(x1,y1, x2 - x1, y2 - y1);
		}
		if (value.isFill()) {
			stream.fill();// 塗りつぶし
		} else {
			stream.stroke();
		}
	}
	public void drawRecordLine(PDPageContentStream stream, Value value, Map<String, Object> paramMap) throws IOException {
		int size = ((List) paramMap.get(value.getParamName())).size();
		if (size == 0) return;
		int nextX = value.getNextX();
		int nextY = value.getNextY();
		int x1 = value.getX1();
		int y1 = value.getY1();
		int x2 = value.getX2();
		int y2 = value.getY2();
		if (nextX < 0 && value.getX1() < value.getX2() ||
				nextX > 0 && value.getX1() > value.getX2()) {
			x2 = x1;
			x1 = value.getX2();
		}
		if (nextY < 0 && value.getY1() < value.getY2() ||
				nextY > 0 && value.getY1() > value.getY2()) {
			y2 = y1;
			y1 = value.getY2();
		}

		//延長か繰り返しかを判断する
		if (x1 == x2 && nextX == 0) {
			y2 += nextY * (size - 1);
			stream.moveTo(x1, y1);
			stream.lineTo(x2, y2);
			stream.stroke();
		} else if (y1 == y2 && nextY == 0) {
			x2 += nextX * (size - 1);
			stream.moveTo(x1, y1);
			stream.lineTo(x2, y2);
			stream.stroke();
		} else {
			for (int i = 0; i < size; i++) {
				//x1,x2大きい方で判断、差分の+-を比較する。
				// listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
				stream.moveTo(x1 + nextX * i, y1 + nextY * i);
				stream.lineTo(x2 + nextX * i, y2 + nextY * i);
				stream.stroke();
			}
		}
	}
	/**
	 * 
	 * @param name
	 * @param val
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public <T> void drawRecordString(PDPageContentStream stream, Value value, Map<String, Object> paramMap)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {
		List<T> list = (List<T>) paramMap.get(value.getParamName());

		for (int i = 0; i < list.size(); i++) {
			T recordString = list.get(i);
			Method method = recordString.getClass().getMethod(
					"get" + value.getMemberName().substring(0, 1).toUpperCase() + value.getMemberName().substring(1),
					null);
			String string = method.invoke(recordString, null).toString();
			// listを取得、スタートindexを取得2ページにまたがる場合の処理が難しい。list.size()
			stream.beginText();
			stream.newLineAtOffset(value.getX1() + value.getNextX() * i, value.getY1() + value.getNextY() * i);
			String tempValue = string;
			// TODO 置換文字列かフラグを付けて変換するかどうかを指定する。
//			if (false) {
//				for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
//					if (tempValue.contains("${")) {
//						tempValue = tempValue.replaceAll("\\$\\{" + entry.getKey() + "\\}",
//								entry.getValue().toString());
//					}
//				}
//			}
			stream.showText(tempValue);
			stream.endText();

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
