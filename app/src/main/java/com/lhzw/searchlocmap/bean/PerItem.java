package com.lhzw.searchlocmap.bean;

public class PerItem {
	private String perName_1;
	private String perName_2;
	private int perIcon_1;
	private int perIcon_2;
	public PerItem(String perName_1, String perName_2, int perIcon_1,
			int perIcon_2) {
		super();
		this.perName_1 = perName_1;
		this.perName_2 = perName_2;
		this.perIcon_1 = perIcon_1;
		this.perIcon_2 = perIcon_2;
	}
	public PerItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getPerName_1() {
		return perName_1;
	}
	public void setPerName_1(String perName_1) {
		this.perName_1 = perName_1;
	}
	public String getPerName_2() {
		return perName_2;
	}
	public void setPerName_2(String perName_2) {
		this.perName_2 = perName_2;
	}
	public int getPerIcon_1() {
		return perIcon_1;
	}
	public void setPerIcon_1(int perIcon_1) {
		this.perIcon_1 = perIcon_1;
	}
	public int getPerIcon_2() {
		return perIcon_2;
	}
	public void setPerIcon_2(int perIcon_2) {
		this.perIcon_2 = perIcon_2;
	}
	
}
