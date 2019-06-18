package com.lhzw.searchlocmap.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 设备信息表  包含设备的北斗号和设备类型
 */
@DatabaseTable(tableName = "DeviceNum")
public class DeviceNum implements Parcelable{
	@DatabaseField(generatedId = true)
	private int _Id;
	@DatabaseField(columnName = "num")
	private String num;
	@DatabaseField(columnName = "tx_type")
	private int tx_type;

	public DeviceNum(String num, int tx_type) {
		super();
		this.num = num;
		this.tx_type = tx_type;
	}
	public DeviceNum() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public int getTx_type() {
		return tx_type;
	}
	public void setTx_type(int tx_type) {
		this.tx_type = tx_type;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeString(num);
		parcel.writeInt(tx_type);
	}
	
	public DeviceNum(Parcel parcel) {
		num = parcel.readString();
		tx_type = parcel.readInt();
	}
	
	public static final Creator<DeviceNum> CREATOR = new Creator<DeviceNum>() {
			
			@Override
			public DeviceNum[] newArray(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DeviceNum createFromParcel(Parcel parcel) {
				// TODO Auto-generated method stub
				return new DeviceNum(parcel);
			}
		};
	
}
