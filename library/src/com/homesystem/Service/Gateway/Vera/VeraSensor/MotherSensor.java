package com.homesystem.Service.Gateway.Vera.VeraSensor;

import java.util.HashMap;
import android.os.Parcel;
import android.os.Parcelable;

public class MotherSensor implements Parcelable {

	protected int category;
	protected int subcategory;
	protected int deviceNum;
	protected String tag;
	
	public MotherSensor(int c, int s, int d, String t) {
		
		category = c;
		subcategory = s;
		deviceNum = d;
		tag = t;		
	}
	
	public void setCategory(int c) {
		
		this.category = c;
	}
	
	public int getCategory() {
		
		return this.category;
	}
	
	public void setSubcategory(int s) {
		
		this.subcategory = s;
	}
	
	public int getSubcategory() {
		
		return this.subcategory;
	}
	
	public void setDeviceNum(int d) {
		
		this.deviceNum = d;
	}
	
	public int getDeviceNum() {
		
		return this.deviceNum;
	}
	
	public void setTag(String t) {
		
		this.tag = t;
	}
	
	public String getTag() {
		
		return this.tag;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(category);
		dest.writeInt(subcategory);
		dest.writeInt(deviceNum);
		dest.writeString(tag);
		
	}
	
	public static final Parcelable.Creator<MotherSensor> CREATOR
	= new Parcelable.Creator<MotherSensor>() {
		public MotherSensor createFromParcel(Parcel in) {
			return new MotherSensor(in);
		}

		public MotherSensor[] newArray(int size) {
			return new MotherSensor[size];
		}
	};

	protected MotherSensor(Parcel in) {
		category = in.readInt();
		subcategory = in.readInt();
		deviceNum = in.readInt();
		tag = in.readString();
	}

}
