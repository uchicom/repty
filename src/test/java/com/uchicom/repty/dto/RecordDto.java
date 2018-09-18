package com.uchicom.repty.dto;

import java.math.BigDecimal;

public class RecordDto {

	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Float getPer() {
		return per;
	}
	public void setPer(Float per) {
		this.per = per;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	private String item;
	private Integer price;
	private Double rate;
	private Float per;
	private Long total;
}
