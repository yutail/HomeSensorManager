package com.homesystem.Service.Gateway;

import android.os.Parcelable;

public abstract class SensorDevice implements Parcelable {
	
	// Sensor attributes
	protected String id;
	protected String location;
	protected String name;
	protected String sensorType;
	protected String ip_address;
	protected int port;
	
	public String getId() {

		return this.id;
	}

	public void setLocation(String loc) {

		this.location = loc;
	}

	public String getLocation() {

		return this.location;
	}

	public void setName(String n) {

		this.name = n;
	}

	public String getName() {

		return this.name;
	}

	public void setSensorType(String type) {

		this.sensorType = type;
	}

	public String getSensorType() {

		return this.sensorType;
	}
	
	public void setIp(String ip) {
		
		this.ip_address = ip;
	}
	
	public String getIp() {
		
		return this.ip_address;
	}
	
	public void setPort(int p) {
		
		this.port = p;
	}
	
	public int getPort() {
		
		return this.port;
	}
	
	
	
}
