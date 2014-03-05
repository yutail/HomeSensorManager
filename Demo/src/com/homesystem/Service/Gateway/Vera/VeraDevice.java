package com.homesystem.Service.Gateway.Vera;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.homesystem.Service.Constant;
import com.homesystem.Service.DataRetrieval;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotionSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.TemperatureSensor;

import com.homesystem.Service.Gateway.SensorDevice;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class VeraDevice extends SensorDevice implements Parcelable {
	// debugging
	private static final String TAG = "VeraDevice";
	
	// Store Sensor Info
	private HashMap<Integer, MotherSensor> mControllerMap =
			new HashMap<Integer, MotherSensor>();
//	private Bundle mControllerMap;
	private HashMap<Integer, LightLevelSensor> mLightMap = 
			new HashMap<Integer, LightLevelSensor>();
	private HashMap<Integer, MotionSensor> mMotionMap =  
			new HashMap<Integer, MotionSensor>();
	private HashMap<Integer, TemperatureSensor> mTemperatureMap = 
			new HashMap<Integer, TemperatureSensor>();
	
	// Thread Locks
	private Object lock_motherSensor = new Object();
	private Object lock_tempSensor = new Object();
	private Object lock_motionSensor = new Object();
	private Object lock_lightSensor = new Object();
	
	// Use Builder Pattern to Instantiate 
	public static class VeraBuilder {
		// Required Parameters
		private String id;
		private String location;
		private String name;
		private String sensorType;
		private String ip_address;
		
		// Optional Parameters
		private int interval = 10;
		private int port = 3480;
		
		public VeraBuilder(String loc, String name, String sensorType, String ip) {	
			this.location = loc;
			this.name = name;
			this.sensorType = sensorType;
			this.ip_address = ip;			
		}
		
		public VeraBuilder setInterval(int val) {	
			this.interval = val;
			return this;
		}
		
		public VeraBuilder setPort(int p) {	
			this.port = p;
			return this;
		}
		
		public VeraDevice build() {
			return new VeraDevice(this);
		}				
	}
	
	private VeraDevice(VeraBuilder builder) {
		this.location = builder.location;
		this.ip_address = builder.ip_address;
		this.name = builder.name;
		this.interval = builder.interval;
		this.sensorType = builder.sensorType;
		this.port = builder.port;	
//		mControllerMap = new Bundle(MotherSensor.class.getClassLoader());
//		mLightMap = new HashMap<Integer, LightLevelSensor>();
//		mMotionMap = new HashMap<Integer, MotionSensor>();
//		mTemperatureMap = new HashMap<Integer, TemperatureSensor>();		
	}
	
	public void setMotherSensor(int devNum, MotherSensor m) {
		synchronized(lock_motherSensor) {
			this.mControllerMap.put(devNum, m);
			//this.mControllerMap.putParcelable(String.valueOf(devNum), (Parcelable) m);
		}
	}
	
	public HashMap<Integer, MotherSensor> getMotherSensor() {
	//public Bundle getMotherSensor() {
		synchronized(lock_motherSensor) {
			Log.d(TAG, "Is Bundle Empty: " + this.mControllerMap.isEmpty());
			return this.mControllerMap;
		}
	}
	
	public void setLightSensor(int devNum, LightLevelSensor l) {
		synchronized(lock_lightSensor) {
			this.mLightMap.put(devNum, l);
		}
	}
	
	public HashMap<Integer, LightLevelSensor> getLightSensor() {
		synchronized(lock_lightSensor) {
			return this.mLightMap;
		}
	}
	
	public void setTemperatureSensor(int devNum, TemperatureSensor t) {
		synchronized(lock_tempSensor) {
			this.mTemperatureMap.put(devNum, t);
		}
	}
	
	public HashMap<Integer, TemperatureSensor> getTemperatureSensor() {
		//Log.d(TAG, "Temperature Map key: " + VeraDevice.this.mTemperatureMap.keySet());
		synchronized(lock_tempSensor) {
			return this.mTemperatureMap;
		}
	}
	
	public void setMotionSensor(int devNum, MotionSensor m) {
		synchronized(lock_motionSensor) {
			this.mMotionMap.put(devNum, m);
		}
	}
	
	public HashMap<Integer, MotionSensor> getMotionSensor() {
		synchronized(lock_motionSensor) {
			return this.mMotionMap;
		}
	}
	

	
	
		
	// Implement Parcelable
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
		dest.writeInt(port);
		dest.writeInt(interval);
		dest.writeMap(mControllerMap);
		dest.writeMap(mLightMap);
		dest.writeMap(mMotionMap);
		dest.writeMap(mTemperatureMap);
	}

	public static final Parcelable.Creator<VeraDevice> CREATOR
	= new Parcelable.Creator<VeraDevice>() {
		public VeraDevice createFromParcel(Parcel in) {
			return new VeraDevice(in);
		}

		public VeraDevice[] newArray(int size) {
			return new VeraDevice[size];
		}
	};

	private VeraDevice(Parcel in) {
		name = in.readString();
		location = in.readString();
		ip_address = in.readString();
		sensorType = in.readString();
		port = in.readInt();
		interval = in.readInt();
		in.readMap(mControllerMap, MotherSensor.class.getClassLoader());
		in.readMap(mLightMap, LightLevelSensor.class.getClassLoader());
		in.readMap(mMotionMap, MotionSensor.class.getClassLoader());
		in.readMap(mTemperatureMap, TemperatureSensor.class.getClassLoader());
	}
}

	