package com.lhzw.searchlocmap.bean;

public class PlotItem {
	private String name;
	private int picId;
	
	public PlotItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PlotItem(String name, int picId) {
		super();
		this.name = name;
		this.picId = picId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPicId() {
		return picId;
	}
	public void setPicId(int picId) {
		this.picId = picId;
	}
	
}
