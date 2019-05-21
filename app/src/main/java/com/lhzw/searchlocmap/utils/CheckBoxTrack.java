package com.lhzw.searchlocmap.utils;

public class CheckBoxTrack {
	private int ID;
	private long time;
	private boolean isCheck;
	public CheckBoxTrack() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CheckBoxTrack(int ID, long time, boolean isCheck) {
		super();
		this.ID = ID;
		this.time = time;
		this.isCheck = isCheck;
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
	
}	
