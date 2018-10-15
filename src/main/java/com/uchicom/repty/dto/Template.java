package com.uchicom.repty.dto;

import java.util.Map;

/**
 * テンプレート.
 * @author hex
 *
 */
public class Template {

	private Resource resource;

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

	@Override
	public String toString() {
		return "Template [spec=" + resource + ", drawMap=" + drawMap + "]";
	}
}
