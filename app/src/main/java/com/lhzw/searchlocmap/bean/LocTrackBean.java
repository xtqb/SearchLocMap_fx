package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "LocTrackBean")
public class LocTrackBean implements Serializable{
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true)
	private int _Id;
	@DatabaseField(columnName = "trackName")
	private String trackName;
	@DatabaseField(columnName = "time")
	private long time;
	@DatabaseField(columnName = "paths")
	private String paths;
	public LocTrackBean(String trackName, long time, String paths) {
		super();
		this.trackName = trackName;
		this.time = time;
		this.paths = paths;
	}
	public LocTrackBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getPaths() {
		return paths;
	}
	public void setPaths(String paths) {
		this.paths = paths;
	}
}
