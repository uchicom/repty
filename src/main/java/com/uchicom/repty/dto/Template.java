package com.uchicom.repty.dto;

import java.util.Map;

/**
 * テンプレート.
 * @author hex
 *
 */
public class Template {

	private String name;
	private Spec spec;

	private Map<String, Unit> drawMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
		return "Template [name=" + name + ", spec=" + spec + ", draws=" + drawMap + "]";
	}
}
