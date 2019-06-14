package com.lhzw.uploadmms;

import android.os.Parcel;
import android.os.Parcelable;

public class BDNum implements Parcelable{
	private String num;
	private int tx_type;
	
	
	
	public BDNum(String num, int tx_type) {
		super();
		this.num = num;
		this.tx_type = tx_type;
	}
	public BDNum() {
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
	
	public BDNum(Parcel parcel) {
		num = parcel.readString();
		tx_type = parcel.readInt();
	}
	
	public static final Creator<BDNum> CREATOR = new Creator<BDNum>() {
			
			@Override
			public BDNum[] newArray(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public BDNum createFromParcel(Parcel parcel) {
				// TODO Auto-generated method stub
				return new BDNum(parcel);
			}
		};
	
}
