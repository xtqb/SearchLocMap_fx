package com.lhzw.uploadmms;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadInfoBean implements Parcelable{

	/**
	 *
	 */
	private int tx_type;          //浼犺緭绫诲瀷  鍩烘寚銆佸墠鎸囥�佺粓绔�
	private int data_type;        // 鏁版嵁绫诲瀷 sos mms common fireL fireP
	private long time;            //鍙戦�佹椂闂淬�佸畾浣嶆椂闂�
	private String body;          //娑堟伅浣�
	private String locTimes;      // 瀹氫綅鏃堕棿
	private String offsets;       //鍋忕Щ闆嗗悎
	private String local_latlon;  //鎵嬫寔瀹氫綅鏁版嵁
	private long local_time;      //鎵嬫寔鏈哄畾浣嶆椂闂�
	private String data_state;         //浼犺緭鐘舵�� 寮�濮�  缁撴潫 sos 澶х伀灏忕伀涓伀绛�
	private String num;        // 澶囨敞  鍙兘涓哄寳鏂楀彿
	private int comFont;          //鍖楁枟浼犺緭绫诲瀷
	private int sendID;           //鏈夋暟鎹甀D
	private String bdNum;          //鍖楁枟鍙峰垪琛�
	public UploadInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UploadInfoBean(int tx_type, int data_type, long time, String body, String locTimes,  String offsets,
						  String local_latlon, long local_time, String data_state, String num,
						  int comFont, int sendID, String bdNum) {
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
		this.bdNum = bdNum;
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
		data_state = parcel.readString();
		num = parcel.readString();
		comFont = parcel.readInt();
		sendID = parcel.readInt();
		bdNum = parcel.readString();
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
		parcel.writeString(data_state);
		parcel.writeString(num);
		parcel.writeInt(comFont);
		parcel.writeInt(sendID);
		parcel.writeString(bdNum);
	}

	public static final Creator<UploadInfoBean> CREATOR = new Creator<UploadInfoBean>() {

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

	public String getBdNum() {
		return bdNum;
	}

	public void setBdNum(String bdNum) {
		this.bdNum = bdNum;
	}

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

	public String getData_state() {
		return data_state;
	}

	public void setData_state(String data_state) {
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
