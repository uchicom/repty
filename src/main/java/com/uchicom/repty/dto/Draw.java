package com.uchicom.repty.dto;

import java.util.List;

public class Draw {

	String type;
	@Override
	public String toString() {
		return "Draw [type=" + type + ", key=" + key + ", values=" + values + "]";
	}
	String key;
	List<Value> values;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<Value> getValues() {
		return values;
	}
	public void setValues(List<Value> values) {
		this.values = values;
	}
}
