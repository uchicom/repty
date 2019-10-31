// (c) 2018 uchicom
package com.uchicom.repty.dto;

import java.awt.Color;
import java.util.Map;

/**
 * リソース情報
 * 
 * @author shige
 *
 */
public class Resource {

	@Override
	public String toString() {
		return "Spec [colorMap=" + colorMap + ", lineMap=" + lineMap + ", fontMap=" + fontMap + ", ttcMap=" + ttcMap
				+ "]";
	}

	/** 拡張子判別用ファイルマップ,ttc,ttf,それ以外は画像と認識させる */
	Map<String, ResourceFile> fileMap;
	Map<String, Color> colorMap;
	Map<String, Line> lineMap;
	Map<String, ResourceFile> imageMap;
	Map<String, Font> fontMap;
	Map<String, Text> textMap;
	/** TTCファイルマップ */
	Map<String, ResourceFile> ttcMap;
	/** TTFファイルマップ */
	Map<String, ResourceFile> ttfMap;

	public Map<String, ResourceFile> getFileMap() {
		return fileMap;
	}

	public void setFileMap(Map<String, ResourceFile> fileMap) {
		this.fileMap = fileMap;
	}

	public Map<String, Color> getColorMap() {
		return colorMap;
	}

	public void setColorMap(Map<String, Color> colorMap) {
		this.colorMap = colorMap;
	}

	public Map<String, Line> getLineMap() {
		return lineMap;
	}

	public void setLineMap(Map<String, Line> lineMap) {
		this.lineMap = lineMap;
	}

	public Map<String, ResourceFile> getImageMap() {
		return imageMap;
	}

	public void setImageMap(Map<String, ResourceFile> imageMap) {
		this.imageMap = imageMap;
	}

	public Map<String, Font> getFontMap() {
		return fontMap;
	}

	public void setFontMap(Map<String, Font> fontMap) {
		this.fontMap = fontMap;
	}

	public Map<String, Text> getTextMap() {
		return textMap;
	}

	public void setTextMap(Map<String, Text> textMap) {
		this.textMap = textMap;
	}

	public Map<String, ResourceFile> getTtcMap() {
		return ttcMap;
	}

	public void setTtcMap(Map<String, ResourceFile> ttcMap) {
		this.ttcMap = ttcMap;
	}

	public Map<String, ResourceFile> getTtfMap() {
		return ttfMap;
	}

	public void setTtfMap(Map<String, ResourceFile> ttfMap) {
		this.ttfMap = ttfMap;
	}

}
