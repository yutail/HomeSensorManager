package com.homesystem.Service;

import java.util.ArrayList;
import java.util.HashMap;

import com.homesystem.Service.Gateway.SensorDevice;

import android.os.Parcel;
import android.os.Parcelable;


public class HomeSystem implements Parcelable {
	
	private HashMap<String, SensorDevice> SensorByName = 
			new HashMap<String, SensorDevice>();
	private HashMap<String, ArrayList<SensorDevice>> SensorByLocation = 
			new HashMap<String, ArrayList<SensorDevice>>();
	private static HomeSystem personalizedHome = null;
	
	private HomeSystem() {
		
	}
	
	// Make it as singleton class
	public synchronized static HomeSystem getInstance() {
		
		if (personalizedHome == null) {
			personalizedHome = new HomeSystem();
		}
		return personalizedHome;
	}
	
	public synchronized static void setInstance(HomeSystem mHomeSystem) {
		personalizedHome = mHomeSystem;
	}
	
	public synchronized HashMap<String, SensorDevice> getHomeSensors() {
		
		return SensorByName;
	}
	
	public synchronized void addDevicesByName(String name, SensorDevice dev) {
		SensorByName.put(name, dev);
	}
	
	public synchronized void removeDevices(SensorDevice target) {
		SensorByName.remove(target.getName());	
	}
	
	public SensorDevice searchByName(String name) {
		SensorDevice sensor;
		sensor = SensorByName.get(name);
		return sensor;		
	}
	
	public ArrayList<SensorDevice> searchByLocation(String location) {		
		ArrayList<SensorDevice> sensorList;
		sensorList = SensorByLocation.get(location);
		return sensorList;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(SensorByName);
		
	}
	
	public static final Parcelable.Creator<HomeSystem> CREATOR
	= new Parcelable.Creator<HomeSystem>() {
		public HomeSystem createFromParcel(Parcel in) {
			return new HomeSystem(in);
		}

		public HomeSystem[] newArray(int size) {
			return new HomeSystem[size];
		}
	};

	private HomeSystem(Parcel in) {
		in.readMap(SensorByName, SensorDevice.class.getClassLoader());
		
	}

}
