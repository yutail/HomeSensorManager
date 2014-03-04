package com.homesystem.Service.Gateway.Vera.VeraSensor;

import android.os.Parcel;
import android.os.Parcelable;

public class MotionSensor extends MotherSensor implements Parcelable {
	
	public MotionSensor(int c, int s, int d, String t, int p) {
		super(c, s, d, t);
		// TODO Auto-generated constructor stub
		this.parent = p;
	}

	private float value;
	private String unit = "Motion/No Motion";
	private int parent;
	
	public String getUnit() {
		
		return this.unit;
	}
	
	public void setValue(float v) {
		
		this.value = v;
	}
	
	public float getValue() {
		
		return this.value;
	}	
	
	public void setParent(int p) {
		
		this.parent = p;
	}
	
	public int getParent() {
		
		return this.parent;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(parent);
		dest.writeFloat(value);
	}
	
	public static final Parcelable.Creator<MotionSensor> CREATOR
	= new Parcelable.Creator<MotionSensor>() {
		public MotionSensor createFromParcel(Parcel in) {
			return new MotionSensor(in);
		}

		public MotionSensor[] newArray(int size) {
			return new MotionSensor[size];
		}
	};
	
	private MotionSensor(Parcel in) {
		super(in);
		parent = in.readInt();
		value = in.readFloat();
	}
	
}
