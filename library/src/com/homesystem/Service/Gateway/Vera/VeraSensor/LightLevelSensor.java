package com.homesystem.Service.Gateway.Vera.VeraSensor;

import android.os.Parcel;
import android.os.Parcelable;

public class LightLevelSensor extends MotherSensor implements Parcelable {
	
	public LightLevelSensor(int c, int s, int d, String t, int p) {
		super(c, s, d, t);
		this.parent = p;
	}

	private float lightLevel;
	private String unit = "Percent";
	private int parent;
	
	public String getUnit() {
		return this.unit;
	}
	
	public void setLightLevel(float l) {	
		this.lightLevel = l;
	}
	
	public float getLightLevel() {	
		return this.lightLevel;
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
		dest.writeFloat(lightLevel);
	}
	
	public static final Parcelable.Creator<LightLevelSensor> CREATOR
	= new Parcelable.Creator<LightLevelSensor>() {
		public LightLevelSensor createFromParcel(Parcel in) {
			return new LightLevelSensor(in);
		}

		public LightLevelSensor[] newArray(int size) {
			return new LightLevelSensor[size];
		}
	};
	
	private LightLevelSensor(Parcel in) {
		super(in);
		parent = in.readInt();
		lightLevel = in.readFloat();
	}
	
}
