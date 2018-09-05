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

			paramMap.put("name", "○○株式会社");
			paramMap.put("startDate", "2018/6/1");
			paramMap.put("endDate", "2018/11/30");
			// yaml 設定をキーで作成し、yamlPdf.addKey("page1", "default");
			// yaml 設定をキーで作成し、yamlPdf.addKey("page1", "page1);
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
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
