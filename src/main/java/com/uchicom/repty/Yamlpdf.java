package com.uchicom.repty;

import java.awt.Color;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;

public class Yamlpdf implements Closeable {

	/**
	 * ページは、自分で設定する。 PDPageを生成するメソッド Map<String, Object> param, document,
	 * Templateを渡す
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) return;
		long start = System.currentTimeMillis();
		try (PDDocument document = new PDDocument()) {
			Yaml yaml = new Yaml();
			System.out.println((System.currentTimeMillis() - start) + "[msec]yaml create");
			start = System.currentTimeMillis();
			Template template = yaml.loadAs(
					new String(Files.readAllBytes(new File(args[0]).toPath())),
					Template.class);
//			System.out.println(template);

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

			for (int i = 0; i < 10; i++) {
				System.out.println(i);
				paramMap.put("name", i + "株式会社");
				yamlPdf.init();
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf init");
				start = System.currentTimeMillis();
				PDPage page1 = yamlPdf.addPage(paramMap);
				PDPage page2 = yamlPdf.addPage(paramMap);
				PDPage page3 = yamlPdf.addPage(paramMap);
				PDPage page4 = yamlPdf.addPage(paramMap);
				PDPage page5 = yamlPdf.addPage(paramMap);
				PDPage page6 = yamlPdf.addPage(paramMap);
				PDPage page7 = yamlPdf.addPage(paramMap);
				PDPage page8 = yamlPdf.addPage(paramMap);
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
		} catch (IOException e) {
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

	public Yamlpdf(PDDocument document, Template template) throws IOException {
		long start = System.currentTimeMillis();
		System.out.println((System.currentTimeMillis() - start) + "[msec]ttf create");
		start = System.currentTimeMillis();
		
		//フォントマップ作成
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
		//イメージマップ作成
		Map<String, URL> imageMap = template.getSpec().getImageMap();
		imageMap.forEach((key, value) -> {
			try {
				xImageMap.put(key, PDImageXObject.createFromFile(value.getFile(), document));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		//addPageする前に初期状態を準備しておいて、出力内容を使いまわして保存する。
		System.out.println((System.currentTimeMillis() - start) + "[msec]imageMap create");
		start = System.currentTimeMillis();
		this.document = document;
		this.template = template;
	}

	/**
	 * 初期化
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
	/**
	 *
	 * @param paramMap
	 * @return
	 * @throws IOException
	 */
	public PDPage addPage(Map<String, Object> paramMap) throws IOException {
		PDPage page = new PDPage(PDRectangle.A4);// TODO ページサイズの指定
		document.addPage(page);
		Map<String, Color> colorMap = template.getSpec().getColorMap();
		Map<String, Line> lineMap = template.getSpec().getLineMap();
		Map<String, Text> textMap = template.getSpec().getTextMap();
		Map<String, Font> fontMap = template.getSpec().getFontMap();

		// 書き込む用のストリームを準備
		PDPageContentStream stream = new PDPageContentStream(document, page);
		for (Draw draw : template.getDraws()) {
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
			case "string": //文字列描画
				Text text = textMap.get(draw.getKey());
				Color color2 = colorMap.get(text.getColorKey());

				Font font2 = fontMap.get(text.getFontKey());
				PDFont pdFont = pdFontMap.get(text.getFontKey());
				stream.setStrokingColor(color2);
				stream.setFont(pdFont, font2.getSize());

				for (Value value : draw.getValues()) {
					stream.beginText();
					stream.newLineAtOffset(value.getX1(), value.getY1());
					String tempValue = value.getValue();
					for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
						if (tempValue.contains("${")) {
							tempValue = tempValue.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue().toString());
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
			case "form":
				PDAcroForm acroForm = new PDAcroForm(document);
				document.getDocumentCatalog().setAcroForm(acroForm);
				PDFont font = PDType1Font.HELVETICA;
				PDResources resources = new PDResources();
				resources.put(COSName.getPDFName("Helv"), font);
				acroForm.setDefaultResources(resources);
				

				PDTextField field = new PDTextField(acroForm);
				field.setPartialName("test");
				field.setDefaultAppearance("/Helv 12 Tf 0 0 1 rg");//12→0で自動
				
				acroForm.getFields().add(field);
				
				PDAnnotationWidget widget = field.getWidgets().get(0);
				PDRectangle rectangle = new PDRectangle(10,200,50,50);
				widget.setRectangle(rectangle);
				widget.setPage(page);
				field.getWidgets().add(widget);
				
				widget.setPrinted(true);
				widget.setReadOnly(true);
				
				page.getAnnotations().add(widget);
				field.setValue("testtesttesttest"); // /DA is a required entry
				
				break;
			default:
				break;
			}

		}
		// ストリームを閉じる
		stream.close();
		return page;
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
