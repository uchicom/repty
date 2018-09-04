package com.uchicom.repty.dto;

import java.util.List;
import java.util.Map;

public class Template {

	@Override
	public String toString() {
		return "Template [name=" + name + ", spec=" + spec + ", draws=" + drawMap + "]";
	}
	String name;
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
	Spec spec;
	
	Map<String, Unit> drawMap;
	public Map<String, Unit> getDrawMap() {
		return drawMap;
	}
	public void setDrawMap(Map<String, Unit> drawMap) {
		this.drawMap = drawMap;
	}

}
