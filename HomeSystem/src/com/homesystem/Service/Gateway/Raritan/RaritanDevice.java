package com.homesystem.Service.Gateway.Raritan;


import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;

public class RaritanDevice extends SensorDevice implements Parcelable {
	// debugging
	private static final String TAG = "RaritanDevice";
	
	private String password;
	private String username;
	private int outletNum;
	private RaritanChannel channels;	
	private String channel;
	
	// Use Builder Pattern to Instantiate 
	public static class RaritanBuilder {
		// Required Parameters 
		private String id;
		private String location;
		private String name;
		private String sensorType;
		private String ip_address;
		
		//Optional Parameters 
		private int interval = 10;
		private String password = "abcd";
		private String username = "admin";
		private int port = 161;
		private int outletNum = 8;

		public RaritanBuilder(String loc, String name, String sensorType, String ip) {
			this.location = loc;
			this.name = name;
			this.sensorType = sensorType;
			this.ip_address = ip;			
		}
		
		public RaritanBuilder setUsername(String s) {
			this.username = s;
			return this;
		}
		
		public RaritanBuilder setPassword(String p) {
			this.password = p;
			return this;
		}
		
		public RaritanBuilder setPort(int p) {	
			this.port = p;
			return this;
		}
		
		public RaritanBuilder setOutletNum(int n) {
			this.port = n;
			return this;
		}

		public RaritanDevice build() {
			return new RaritanDevice(this);
		}
	}

	private RaritanDevice(RaritanBuilder builder) {
		this.location = builder.location;
		this.ip_address = builder.ip_address;
		this.name = builder.name;
		this.password = builder.password;
		this.port = builder.port;
		this.outletNum = builder.outletNum;	
		this.username = builder.username;
		this.sensorType = builder.sensorType;
	}
	
	public void setUsername(String u) {
		this.username = u;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setPassword(String p) {
		this.password = p;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public synchronized void setChannel(String c) {
		this.channel = c;
	}
	
	public synchronized String getChannel() {
		return this.channel;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(location);
		dest.writeString(ip_address);
		dest.writeString(sensorType);
		dest.writeString(username);
		dest.writeString(password);
		dest.writeInt(port);
		dest.writeInt(outletNum);
	}
	
	public static final Parcelable.Creator<RaritanDevice> CREATOR
	= new Parcelable.Creator<RaritanDevice>() {
		public RaritanDevice createFromParcel(Parcel in) {
			return new RaritanDevice(in);
		}

		public RaritanDevice[] newArray(int size) {
			return new RaritanDevice[size];
		}
	};

	private RaritanDevice(Parcel in) {
		name = in.readString();
		location = in.readString();
		ip_address = in.readString();
		sensorType = in.readString();
		username = in.readString();
		password = in.readString();
		port = in.readInt();
		outletNum = in.readInt();
	}	

}
