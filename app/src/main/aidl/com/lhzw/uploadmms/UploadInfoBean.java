package com.lhzw.uploadmms;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadInfoBean implements Parcelable{

	/**
	 *
	 */
	private int tx_type;          //传输类型  基指、前指、终端
	private int data_type;        // 数据类型 sos mms common fireL fireP
	private long time;            //发送时间、定位时间
	private String body;          //消息体
	private String locTimes;      // 定位时间
	private String offsets;       //偏移集合
	private String local_latlon;  //手持定位数据
	private long local_time;      //手持机定位时间
	private int data_state;         //传输状态 开始  结束 sos 大火小火中火等
	private String num;        // 备注  可能为北斗号
	private int comFont;          //北斗传输类型
	private int sendID;           //有数据ID
	public UploadInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UploadInfoBean(int tx_type, int data_type, long time, String body, String locTimes,  String offsets,
						  String local_latlon, long local_time, int data_state, String num,
						  int comFont, int sendID) {
		super();
		this.tx_type = tx_type;
		this.data_type = data_type;
		this.time = time;
		this.locTimes = locTimes;
		this.body = body;
		this.offsets = offsets;
		this.local_latlon = local_latlon;
		this.local_time = local_time;
		this.data_state = data_state;
		this.num = num;
		this.comFont = comFont;
		this.sendID = sendID;
	}



	public UploadInfoBean(Parcel parcel) {
		// TODO Auto-generated constructor stub
		tx_type = parcel.readInt();
		data_type = parcel.readInt();
		time = parcel.readLong();
		locTimes = parcel.readString();
		body = parcel.readString();
		offsets = parcel.readString();
		local_latlon = parcel.readString();
		local_time = parcel.readLong();
		data_state = parcel.readInt();
		num = parcel.readString();
		comFont = parcel.readInt();
		sendID = parcel.readInt();
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeInt(tx_type);
		parcel.writeInt(data_type);
		parcel.writeLong(time);
		parcel.writeString(locTimes);
		parcel.writeString(body);
		parcel.writeString(offsets);
		parcel.writeString(local_latlon);
		parcel.writeLong(local_time);
		parcel.writeInt(data_state);
		parcel.writeString(num);
		parcel.writeInt(comFont);
		parcel.writeInt(sendID);
	}

	public static final android.os.Parcelable.Creator<UploadInfoBean> CREATOR = new Creator<UploadInfoBean>() {

		@Override
		public UploadInfoBean[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UploadInfoBean createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub
			return new UploadInfoBean(parcel);
		}
	};
	public int getTx_type() {
		return tx_type;
	}

	public void setTx_type(int tx_type) {
		this.tx_type = tx_type;
	}

	public int getData_type() {
		return data_type;
	}

	public void setData_type(int data_type) {
		this.data_type = data_type;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getLocTimes() {
		return locTimes;
	}

	public void setLocTimes(String locTimes) {
		this.locTimes = locTimes;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLocal_latlon() {
		return local_latlon;
	}

	public void setLocal_latlon(String local_latlon) {
		this.local_latlon = local_latlon;
	}

	public long getLocal_time() {
		return local_time;
	}

	public void setLocal_time(long local_time) {
		this.local_time = local_time;
	}

	public String getOffsets() {
		return offsets;
	}

	public void setOffsets(String offsets) {
		this.offsets = offsets;
	}

	public int getData_state() {
		return data_state;
	}

	public void setData_state(int data_state) {
		this.data_state = data_state;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getComFont() {
		return comFont;
	}

	public void setComFont(int comFont) {
		this.comFont = comFont;
	}

	public int getSendID() {
		return sendID;
	}

	public void setSendID(int sendID) {
		this.sendID = sendID;
	}

}
