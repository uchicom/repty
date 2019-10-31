// (c) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.List;

import com.uchicom.repty.DrawType;

public class Draw {

	String type;
	String key;
	String list;
	boolean repeated;
	List<Value> values;

	DrawType drawType;

	@Override
	public String toString() {
		return "Draw [type=" + type + ", key=" + key + ", values=" + values + "]";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		drawType = DrawType.is(type);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isRepeated() {
		return repeated;
	}

	public void setRepeated(boolean repeated) {
		this.repeated = repeated;
	}

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public DrawType getDrawType() {
		return drawType;
	}
}
