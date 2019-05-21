package com.lhzw.searchlocmap.dipper;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "UploadInfo")
public class UploadInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private int _Id;
	@DatabaseField(columnName = "name")
	private String name; // 求救人员姓名
	@DatabaseField(columnName = "tel")
	private String tel; // 求救人员电话
	@DatabaseField(columnName = "lat")
	private String lat; // 纬度
	@DatabaseField(columnName = "lon")
	private String lon; // 经度
	@DatabaseField(columnName = "server_tel")
	private String server_tel; // 搜救服务人员
	@DatabaseField(columnName = "counter")
	private int counter; // 上传次数

	/**
	 * 
	 */
	public UploadInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param tel
	 * @param lat
	 * @param lon
	 * @param server_tel
	 * @param counter
	 */
	public UploadInfo(String name, String tel, String lat, String lon,
			String server_tel, int counter) {
		super();
		this.name = name;
		this.tel = tel;
		this.lat = lat;
		this.lon = lon;
		this.server_tel = server_tel;
		this.counter = counter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getServer_tel() {
		return server_tel;
	}

	public void setServer_tel(String server_tel) {
		this.server_tel = server_tel;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}
