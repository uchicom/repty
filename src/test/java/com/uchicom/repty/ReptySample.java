package com.uchicom.repty;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.yaml.snakeyaml.Yaml;

import com.uchicom.repty.dto.CommentDto;
import com.uchicom.repty.dto.RecordDto;
import com.uchicom.repty.dto.TableDto;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Value;

public class ReptySample {

	/**
	 * ページは、自分で設定する。 PDPageを生成するメソッド Map<String, Object> param, cs,
	 * resourcesocument, Templateを渡す drawだけじゃじゃなくて、共通と各ページのキーとなる情報でまとめる。 可変表と線の対応
	 * 変数のみの設定を実施する。 文字レコード指定で、 レコードを指定して、変数を取得 type:record values: 1,2,0,10,a,リスト1
	 * offsetX,offsetY,nextX,nextY,dtoのメンバ変数名、list名（parameterMapで指定) 文字列出力
	 * offsetX1,offsetY1,offsetX2,offsetY2,nextX,nextY,list名
	 * 線出力繰り返しタイプ複数線、一直線nextの仕方で判別可能
	 * offsetX1,offsetY1,offsetX2,offsetY2,nextX,nextY,list名
	 * rectangle出力、サイズを可変にする。繰り返しのパターンはない。 いずれもmax値の制限がほしい。見た目を変えるのはtoStringで実装
	 * 右寄せ、左寄せ、中央寄せは、共通で実装 この処理は、commonとpageAを実施という風に変更
	 * 
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		if (args.length == 0)
			return;
		System.out.println(args[0]);
		long start = System.currentTimeMillis();
		// 1ページ テンプレートのみ
		// 2ページ 自動改行で文字列を表示する
		CommentDto commentDto = new CommentDto();
		commentDto.setComment1("あいうえお、かきくけこ、さしすせそ、たちつてと、なにぬねの、はひふへほ、まみむめも。");
		commentDto.setComment2("アイウエオ、カキクケコ、サシスセソ、タチツテト、ナニヌネノ、ハヒフヘホ、マミムメモ。");
		commentDto.setComment3("漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字。");
		commentDto.setComment3("abc def ghi jkl mno pqr, stu vwx yz0 123 456 789.");
		// 3,4ページ (10,5の15件を2ページで表示する,1レコード2行で表示する可変表)
		List<RecordDto> recordDtoList = new ArrayList<>();
		for (int i = 1; i < 16; i++) {
			RecordDto recordDto = new RecordDto();
			recordDto.setItem(i + "品盛り合わせ");
			recordDto.setPrice(i * 100);
			recordDto.setPer(0.1F * i);
			recordDto.setRate(1.25 * i);
			recordDto.setTotal(i * 1000L);
			recordDtoList.add(recordDto);
		}
		// 5ページ (1レコードの表を表示する固定表)
		int data = 1234567;

		// 6~8ページ (1,2,2の5件を3ページで表示する、1レコード1表で表示する)
		List<TableDto> tableDtoList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			TableDto tableDto = new TableDto();
			tableDto.setName(i + "○○水産");
			tableDto.setTel(i + "123456789");
			tableDto.setAddress1("神奈川県藤沢市");
			tableDto.setAddress2((i + 1) + "丁目");
			tableDto.setAddress3("××ビル" + (i + 1) + "F");
			tableDto.setContent("シラス丼がおいしい" + (i + 2) + "杯はいける");
			tableDtoList.add(tableDto);
		}
		System.out.println(tableDtoList.size());
		List<TableDto> tableDtoList2 = new ArrayList<>();
		for (int i = 0; i < 50; i++) {
			TableDto tableDto = new TableDto();
			tableDto.setName(i + "○○水産");
			tableDto.setTel(i + "123456789");
			tableDto.setAddress1("神奈川県藤沢市");
			tableDto.setAddress2((i + 1) + "丁目");
			tableDto.setAddress3("××ビル" + (i + 1) + "F");
			tableDto.setContent("シラス丼がおいしい" + (i + 2) + "杯はいける");
			tableDtoList2.add(tableDto);
		}

		Yaml yaml = new Yaml();
		System.out.println((System.currentTimeMillis() - start) + "[msec]yaml create");
		start = System.currentTimeMillis();
		Template template = null;
		try {
			template = yaml.loadAs(new String(Files.readAllBytes(new File(args[0]).toPath())), Template.class);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		for (int i = 1; i >= 0; i--) {
			try (PDDocument document = new PDDocument(MemoryUsageSetting.setupMainMemoryOnly());) {
				System.out.println((System.currentTimeMillis() - start) + "[msec]yaml create1");
				start = System.currentTimeMillis();
				try (Repty yamlPdf = new Repty(document, template);) {
					System.out.println((System.currentTimeMillis() - start) + "[msec]yaml create2");
					start = System.currentTimeMillis();
					// System.out.println(template);

					System.out.println((System.currentTimeMillis() - start) + "[msec]template create");
					start = System.currentTimeMillis();
					// PDFドキュメントを作成

					// DocumentへのObjectの登録はContentStream生成の前で実施。
					// サイズ指定
					// ページを追加(1ページ目)

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
					paramMap.put("commentDto.comment1", commentDto.getComment1());
					paramMap.put("commentDto.comment2", commentDto.getComment2());
					paramMap.put("commentDto.comment3", commentDto.getComment3());
					paramMap.put("commentDto.comment4", commentDto.getComment4());

					paramMap.put("name", "○○株式会社");
					paramMap.put("startDate", "2018/6/1");
					paramMap.put("endDate", "2018/11/30");
					BufferedImage bimage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics g = bimage.createGraphics();
					g.setColor(Color.YELLOW);
					g.fillRect(0, 0, 100, 100);
					g.setColor(Color.RED);
					g.fillOval(50, 50, 20, 20);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bimage, "PNG", baos);
					paramMap.put("imageKey", baos.toByteArray());

					long forstart = System.currentTimeMillis();
					int total = 1 + 1 + (recordDtoList.size() / 10 + 1) + 1 + (tableDtoList.size() / 2 + 1);
					// TODO 削除追加で切り替えるのは効率が悪い
					// TODO 設定をマップで保持して切り替えるのが良い
					// TODO paramMapも同じものは入れないで保持するのが早い。インスタンスは生成してないからまあいいか
				yamlPdf.init();//pdfboxの改修が必要
					yamlPdf.addKey("default"); // 共通の設定は繰り返しの外で作成

					PDPage d = yamlPdf.createPage(paramMap);
					List<PDStream> cs = new ArrayList<>();
					PDResources resources = null;
					resources = d.getResources();
					Iterator<PDStream> iterator = d.getContentStreams();

					while (iterator.hasNext()) {
						cs.add(iterator.next());
					}
					yamlPdf.changeKey("default", "append");
					System.out.println(resources.getFontNames());
					System.out.println(i);
					paramMap.put("total", total);
					// yamlPdf.init(); // initが毎回必要、固定ページはクローン出来る？

					System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf init");
					start = System.currentTimeMillis();

					int page = 1;
					// 1ページ目テンプレート
					yamlPdf.addKey("page1");
					paramMap.put("page", page++);// TODO リソースは解消するけど、ページ数をあとでかきこむをやるとだめ。
					document.addPage(yamlPdf.appendPage(paramMap, cs, resources));

					// デフォルトで配置したで固定のものは作成しておいて、そのリソースとコンテンツを保持しておき、必要なものだけを追記するシステム。
					// ページは追記で実施する。
					System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create 1page");
					start = System.currentTimeMillis();
					// 2ページ目文字列表示
					yamlPdf.changeKey("page1", "page2");
					paramMap.put("page", page++);

					document.addPage(yamlPdf.appendPage(paramMap, cs, resources));

					// 3,4ページ(リスト表示)
					paramMap.put("tableDtoList2", tableDtoList2);
					yamlPdf.changeKey("page2", "page3");

					long start2 = System.currentTimeMillis();
					int listSize = recordDtoList.size();
					for (int j = 0; j < 20; j++) {
						int recordMax = 10;
						for (int recordIndex = 0; recordIndex < listSize; recordIndex += 10) {
							paramMap.put("page", page++);
							int toIndex = recordIndex + recordMax;
							if (toIndex > listSize) {
								toIndex = listSize;
							}
							paramMap.put("recordDtoList", recordDtoList.subList(recordIndex, toIndex));

							document.addPage(yamlPdf.appendPage(paramMap, cs, resources));
						}
					}
					System.out.println((System.currentTimeMillis() - start2) + "[msec] list出力計測");
					// 5ページ（１データ表示）
					yamlPdf.changeKey("page3", "page4");
					paramMap.put("page", page++);
					paramMap.put("data", data);

					document.addPage(yamlPdf.appendPage(paramMap, cs, resources));

					// 6ページ
					yamlPdf.changeKey("page4", "page5");
					if (tableDtoList.size() > 0) {
						TableDto dto = tableDtoList.get(0);
						paramMap.put("name", dto.getName());
						paramMap.put("tel", dto.getTel());
						paramMap.put("address1", dto.getAddress1());
						paramMap.put("address2", dto.getAddress2());
						paramMap.put("address3", dto.getAddress3());

						document.addPage(yamlPdf.appendPage(paramMap, cs, resources));
					}
					// 7,8ページ
					yamlPdf.changeKey("page5", "page6");
					int tableMax = 2;
					for (int tableIndex = 1; tableIndex < tableDtoList.size(); tableIndex += tableMax) {
						paramMap.put("page", page++);

						int toIndex = tableIndex + tableMax;
						if (toIndex > tableDtoList.size()) {
							toIndex = tableDtoList.size();
						}
						paramMap.put("tableDtoList", tableDtoList.subList(tableIndex, toIndex));
						document.addPage(yamlPdf.appendPage(paramMap, cs, resources));
					}
					yamlPdf.removeKeys("page6", "append");

					System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create page 1 file");
					start = System.currentTimeMillis();

					// ファイル作成
					File outFile = new File("result", i + "sample.pdf");
					if (!outFile.exists()) {
						outFile.createNewFile();
					}
					try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(outFile))) {// bufferingすると300msec右40msecになる
						document.save(fos);

					}

					// ページ削除
					yamlPdf.removeAllPage();
					yamlPdf.clearKeys();

					System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create 1 file");
					start = System.currentTimeMillis();
					System.out.println((System.currentTimeMillis() - forstart) + "[msec]yamlPdf process 1 file");

					memory();

					// 作成したPDFを保存
				}
			} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		memory();
		memory();
	}

	public static void memory() {
		Runtime runtime = Runtime.getRuntime();
		System.out.println("used:" + (runtime.totalMemory() - runtime.freeMemory()));
		runtime.gc();
	}

}
