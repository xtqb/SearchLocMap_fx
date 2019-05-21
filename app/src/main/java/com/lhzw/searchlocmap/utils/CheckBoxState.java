package com.lhzw.searchlocmap.utils;

public class CheckBoxState {
	private boolean isCheck;
	private int ID;
	private String num;

	/**
	 * 
	 */
	public CheckBoxState() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isCheck
	 * @param iD
	 * @param num
	 */
	public CheckBoxState(boolean isCheck, int iD, String num) {
		super();
		this.isCheck = isCheck;
		ID = iD;
		this.num = num;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
