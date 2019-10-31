// (c) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.List;

public class Unit {

	public Meta meta;
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<Draw> drawList;

	public List<Draw> getDrawList() {
		return drawList;
	}

	public void setDrawList(List<Draw> drawList) {
		this.drawList = drawList;
	}
}
