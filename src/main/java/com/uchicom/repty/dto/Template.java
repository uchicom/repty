package com.uchicom.repty.dto;

import java.util.List;

public class Template {

	@Override
	public String toString() {
		return "Template [name=" + name + ", spec=" + spec + ", draws=" + draws + "]";
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
	
	List<Draw> draws;
	public List<Draw> getDraws() {
		return draws;
	}
	public void setDraws(List<Draw> draws) {
		this.draws = draws;
	}
	
	
}
