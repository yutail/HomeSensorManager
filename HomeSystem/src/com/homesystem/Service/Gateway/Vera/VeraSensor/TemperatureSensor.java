package com.homesystem.Service.Gateway.Vera.VeraSensor;

import android.os.Parcel;
import android.os.Parcelable;

public class TemperatureSensor extends MotherSensor implements Parcelable {
	
	public TemperatureSensor(int c, int s, int d, String t, int p) {
		super(c, s, d, t);
		this.parent = p;		
	}

	private float temperature;
	private String unit = "Fahrenheit";
	private int parent;
	
	public void setUnit(String u) {
		unit = u;
	}
	
	public String getUnit() {		
		return this.unit;
	}
	
	public void setTemperature(float t) {	
		this.temperature = t;
	}
		
	public float getTemperature() {	
		return this.temperature;
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
		dest.writeFloat(temperature);
	}
	
	public static final Parcelable.Creator<TemperatureSensor> CREATOR
	= new Parcelable.Creator<TemperatureSensor>() {
		public TemperatureSensor createFromParcel(Parcel in) {
			return new TemperatureSensor(in);
		}

		public TemperatureSensor[] newArray(int size) {
			return new TemperatureSensor[size];
		}
	};
	
	private TemperatureSensor(Parcel in) {
		super(in);
		parent = in.readInt();
		temperature = in.readFloat();
	}

}
