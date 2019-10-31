// (c) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.Map;

/**
 * テンプレート.
 * 
 * @author hex
 *
 */
public class Template {

	/** リソース. */
	private Resource resource;

	/** 描画マップ. */
	private Map<String, Unit> drawMap;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Map<String, Unit> getDrawMap() {
		return drawMap;
	}

	public void setDrawMap(Map<String, Unit> drawMap) {
		this.drawMap = drawMap;
	}

	/**
	 * このオブジェクトを表す文字列を返します.
	 * 
	 * @return このオブジェクトを表す文字列
	 */
	@Override
	public String toString() {
		return "Template [spec=" + resource + ", drawMap=" + drawMap + "]";
	}
}
