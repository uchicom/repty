package com.uchicom.repty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.yaml.snakeyaml.Yaml;

import com.uchicom.repty.dto.CommentDto;
import com.uchicom.repty.dto.RecordDto;
import com.uchicom.repty.dto.TableDto;
import com.uchicom.repty.dto.Template;
import com.uchicom.repty.dto.Value;

public class ReptySample {


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
		System.out.println(args[0]);
		long start = System.currentTimeMillis();
		// 1ページ　テンプレートのみ
		// 2ページ　自動改行で文字列を表示する
		CommentDto commentDto = new CommentDto();
		commentDto.setComment1("あいうえお、かきくけこ、さしすせそ、たちつてと、なにぬねの、はひふへほ、まみむめも。");
		commentDto.setComment2("アイウエオ、カキクケコ、サシスセソ、タチツテト、ナニヌネノ、ハヒフヘホ、マミムメモ。");
		commentDto.setComment3("漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字、漢字漢字。");
		commentDto.setComment3("abc def ghi jkl mno pqr, stu vwx yz0 123 456 789.");
		// 3,4ページ　(10,5の15件を2ページで表示する,1レコード2行で表示する可変表)
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
		// 5ページ　(1レコードの表を表示する固定表)
		int data = 1234567;
		
		// 6~8ページ　(1,2,2の5件を3ページで表示する、1レコード1表で表示する)
		List<TableDto> tableDtoList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			TableDto tableDto = new TableDto();
			tableDto.setName(i + "○○水産");
			tableDto.setTel(i + "123456789");
			tableDto.setAddress1("神奈川県藤沢市");
			tableDto.setAddress2( (i + 1) + "丁目");
			tableDto.setAddress3("××ビル" + (i + 1) + "F");
			tableDto.setContent("シラス丼がおいしい" + (i + 2) + "杯はいける");
			tableDtoList.add(tableDto);
		}
		
		
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
			Repty yamlPdf = new Repty(document, template);

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
			// yaml 設定をキーで作成し、yamlPdf.addKey("page1", "default");
			// yaml 設定をキーで作成し、yamlPdf.addKey("page1", "page1);
			List<PDPage> pdpageList = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				int total = 1 + 1 + (recordDtoList.size() / 10 + 1) + 1 + (tableDtoList.size() / 2 + 1) ;
				// TODO 削除追加で切り替えるのは効率が悪い
				// TODO 設定をマップで保持して切り替えるのが良い
				// TODO paramMapも同じものは入れないで保持するのが早い。インスタンスは生成してないからまあいいか
				// TODO 右寄せ、左寄せ、中央寄せ
				// TODO 改行機能

				System.out.println(i);
				paramMap.put("total", total);
				yamlPdf.init();
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf init");
				start = System.currentTimeMillis();
				int page = 1;
				// 1ページ目テンプレート
				yamlPdf.addKey("default");
				yamlPdf.addKey("page1");
				paramMap.put("page", page++);
				PDPage page1 = yamlPdf.addPage(paramMap);
				document.addPage(page1);
				pdpageList.add(page1);
				
				// 2ページ目文字列表示
				yamlPdf.changeKey("page1", "page2");
				paramMap.put("page", page++);
				PDPage page2 = yamlPdf.addPage(paramMap);
				document.addPage(page2);
				pdpageList.add(page2);
				

				// 3,4ページ(リスト表示)
				yamlPdf.changeKey("page2", "page3");
				int recordMax = 10;
				for (int recordIndex = 0; recordIndex < recordDtoList.size(); recordIndex+= 10) {
					paramMap.put("page", page++);
					int toIndex = recordIndex + recordMax;
					if (toIndex > recordDtoList.size()) {
						toIndex = recordDtoList.size();
					}
					paramMap.put("recordDtoList",  recordDtoList.subList(recordIndex, toIndex));
					
					PDPage page3 = yamlPdf.addPage(paramMap);
					document.addPage(page3);
					pdpageList.add(page3);
				}
				
				// 5ページ（１データ表示）
				yamlPdf.changeKey("page3", "page4");
				paramMap.put("page", page++);
				paramMap.put("data", data);
				PDPage page4 = yamlPdf.addPage(paramMap);
				document.addPage(page4);
				pdpageList.add(page4);
				
				// 6ページ
				yamlPdf.changeKey("page4", "page5");
				if (tableDtoList.size() > 0) {
					paramMap.put("tableDto",  tableDtoList.get(0));
					PDPage page3 = yamlPdf.addPage(paramMap);
					document.addPage(page3);
					pdpageList.add(page3);
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
					paramMap.put("tableDtoList",  tableDtoList.subList(tableIndex, toIndex));
					
					PDPage page3 = yamlPdf.addPage(paramMap);
					document.addPage(page3);
					pdpageList.add(page3);
				}
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create page 1 file");
				start = System.currentTimeMillis();
				File outFile = new File("result/" + i + "test.pdf");
				outFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outFile);
				document.save(fos);
				fos.close();

				pdpageList.forEach(pdpage->document.removePage(pdpage));
				pdpageList.clear();
				System.out.println((System.currentTimeMillis() - start) + "[msec]yamlPdf create 1 file");
				start = System.currentTimeMillis();

			}

			yamlPdf.close();
			// 作成したPDFを保存
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
