package com.uchicom.repty.dto;

import java.util.Map;

/**
 * テンプレート.
 * @author hex
 *
 */
public class Template {

	private Spec spec;

	private Map<String, Unit> drawMap;

	public Spec getSpec() {
		return spec;
	}

	public void setSpec(Spec spec) {
		this.spec = spec;
	}

	public Map<String, Unit> getDrawMap() {
		return drawMap;
	}

	public void setDrawMap(Map<String, Unit> drawMap) {
		this.drawMap = drawMap;
	}

	@Override
	public String toString() {
		return "Template [spec=" + spec + ", drawMap=" + drawMap + "]";
	}
}
