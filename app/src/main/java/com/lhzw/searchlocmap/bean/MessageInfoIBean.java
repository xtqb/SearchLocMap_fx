package com.lhzw.searchlocmap.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "MessageInfoIBean")
public class MessageInfoIBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true)
	private int _Id;
	@DatabaseField(columnName = "time")
	private long time;
	@DatabaseField(columnName = "body")
	private String body;
	@DatabaseField(columnName = "type")
	private int type;
	@DatabaseField(columnName = "num")
	private String num;
	@DatabaseField(columnName = "state")
	private int state;
	@DatabaseField(columnName = "ID")
	private int ID;

	/**
	 * 
	 */
	public MessageInfoIBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param time
	 * @param body
	 * @param type
	 * @param num
     * @param state
     */
	public MessageInfoIBean(String num, long time, String body, int type, int state, int ID) {
		this.time = time;
		this.body = body;
		this.type = type;
		this.num = num;
		this.state = state;
		this.ID = ID;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String bogy) {
		this.body = bogy;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public String toString() {
		return "MessageInfoIBean{" +
				"_Id=" + _Id +
				", time=" + time +
				", body='" + body + '\'' +
				", type=" + type +
				", num='" + num + '\'' +
				", state=" + state +
				", ID=" + ID +
				'}';
	}
}
