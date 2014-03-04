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

import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotionSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.TemperatureSensor;
import com.homesystem.Service.Gateway.DataRetrieval;
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


public class VeraDevice extends SensorDevice implements DataRetrieval, Parcelable {
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
	
	
	private Handler mHandler = null;
	
	// Used for generating URL
	private String request[] = {"user_data", "status", "sdata"};
	private String format = "output_format=json";	
	
	// Handling Threads
	//private final int poolSize = 10;
	//private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
	private ExecutorService threadPool = Executors.newCachedThreadPool();	
	private final int SENSOR_QTY = 20;
	private boolean interruptFlag[] = new boolean[SENSOR_QTY];
	private Object lock_interruptFlag = new Object();
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
//		this.mControllerMap = new Bundle(MotherSensor.class.getClassLoader());
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
	
	public void setInterruptFlag(boolean flag, int index) {
		synchronized(lock_interruptFlag) {
			this.interruptFlag[index] = flag;
		}
	}
	
	public boolean getInterruptFlag(int index) {
		synchronized(lock_interruptFlag) {
			return this.interruptFlag[index];
		}	
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public String generateURL(String r, String f) {
		// Format of Requested URL: http://ip_address:3480/data_request?id=sdata&output_format=json
		StringBuilder result = new StringBuilder();
		result.append("http://");
		result.append(ip_address);
		result.append(":");
		result.append(port);
		result.append("/data_request?");
		result.append("id=");
		result.append(r);
		result.append("&");
		result.append(f);
		return result.toString();
	}
	
	public String retrieveData(JSONObject deviceData, int index, String tag) {
		try {
			JSONArray devices = deviceData.getJSONArray("devices");
			int len = devices.length();
			for (int i=0; i<len; i++) {
				JSONObject dev = (JSONObject) devices.get(i);
				int deviceNum = dev.getInt("id");
				if (index == deviceNum) {
					String status = dev.getString(tag);
					return status;
				}				
			}
			return null;			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	private class HttpClient implements Runnable {
		
		private URL vera_url = null;
		private HttpURLConnection vera_connection = null;
		private int tid;
		private MotherSensor mSensor = null;
		private String tag;
				
		public HttpClient(int id, String url) throws IOException {
			this.vera_url = new URL(url);
			this.tid = id;

			if(mLightMap.containsKey(id)) {
				mSensor = mLightMap.get(id);
				tag = "light";
			}
			else if (mTemperatureMap.containsKey(id)) {
				mSensor = mTemperatureMap.get(id);
				tag = "temperature";
			}
		}

		@Override
		public void run() {
			while (true) {
				if (Thread.interrupted()) {
					Message msg = mHandler.obtainMessage(Constant.VERA_MESSAGE);
			        Bundle bundle = new Bundle();
			        bundle.putString(Constant.VERA_VALUE, "");
			        bundle.putInt(Constant.VERA_ID, tid);
			        bundle.putString(Constant.VERA_SUBTYPE, tag);
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);
					return;
				}
				try {	
					Log.d(TAG, "New Thread: " + tag+ Thread.currentThread().getId());
					// Http URL connection setting
					vera_connection = (HttpURLConnection) vera_url.openConnection();
					vera_connection.setRequestMethod("GET");
					vera_connection.setDoOutput(false);
					vera_connection.setDoInput(true);
					// Retrieving data
					InputStream tempIn = vera_connection.getInputStream();
					BufferedReader buf = new BufferedReader(new InputStreamReader(tempIn));
					StringBuffer sb = new StringBuffer();
					String line = buf.readLine();
					sb.append(line);
					JSONObject devInfo = new JSONObject(sb.toString());
					String result = retrieveData(devInfo, tid, tag);
					Log.d(TAG, "Received Data: " + tag + ": " + result);
					
					// Send the name of the connected device back to the UI Activity
			        Message msg = mHandler.obtainMessage(Constant.VERA_MESSAGE);
			        Bundle bundle = new Bundle();
			        bundle.putString(Constant.VERA_VALUE, result);
			        bundle.putInt(Constant.VERA_ID, tid);
			        bundle.putString(Constant.VERA_SUBTYPE, tag);
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);
					
					if(tag.equals("light")) {
						((LightLevelSensor) mSensor).setLightLevel(Float.parseFloat(result));
						setLightSensor(tid, (LightLevelSensor) mSensor);	
					}
					else if (tag.equals("temperature")) {
						((TemperatureSensor) mSensor).setTemperature(Float.parseFloat(result));
						setTemperatureSensor(tid, (TemperatureSensor) mSensor);
					}
					Thread.sleep(interval*1000);
					
					if (getInterruptFlag(tid)) {
						Log.d(TAG, "Thread Interruptted " + 
								Thread.currentThread().getId() + " " + tag);
						Thread.currentThread().interrupt();
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
					
				} catch (IOException e) {
					e.printStackTrace();

				} catch (Exception e) {
					e.printStackTrace();	
				} 			
			}
		}				
	}	
	
	public void getDeviceInfo() {		
		
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// HTTP connection
					URL targetURL = new URL(generateURL(request[2], format));
					HttpURLConnection http_connection = (HttpURLConnection) targetURL.openConnection();
					http_connection.setRequestMethod("GET");
					http_connection.setDoOutput(false);
					http_connection.setDoInput(true);
					// Retrieve data
					InputStream tempIn = http_connection.getInputStream();
					BufferedReader buf = new BufferedReader(new InputStreamReader(tempIn));
					StringBuffer sb = new StringBuffer();
					String line = buf.readLine();
					sb.append(line);
					// Parse JSON Data
					JSONObject deviceSummary = new JSONObject(sb.toString());			
					JSONArray devices = deviceSummary.getJSONArray("devices");
					int len = devices.length();
					VeraSensorQueue.mSensorSize.add(len);
					Log.d(TAG, "Num of Devices: " + len);
					for (int i=0; i<len; i++) {
						JSONObject dev = (JSONObject) devices.get(i);
						String name = dev.getString("name");
						int category = Integer.parseInt(dev.getString("category"));
						int subcategory = Integer.parseInt(dev.getString("subcategory"));
						int parent = dev.getInt("parent");
						int deviceNum = dev.getInt("id");
						Log.d(TAG, "Name: " + name + ", Category: " + category + 
								", Subcategory: " + subcategory + ", DeviceNum: " + deviceNum);

						if (dev.has("batterylevel")) {

							MotherSensor mController = new MotherSensor(category, subcategory, 
									deviceNum, "3-in-1 Sensor");
							VeraDevice.this.setMotherSensor(deviceNum, mController);	
							VeraSensorQueue.mSensorQueue.add(mController);
							
						} else if (dev.has("light")) {

							LightLevelSensor mLight = new LightLevelSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mLight.setLightLevel(Float.parseFloat(dev.getString("light")));
							VeraDevice.this.setLightSensor(deviceNum, mLight);
							VeraSensorQueue.mSensorQueue.add(mLight);

						} else if (dev.has("temperature")) {
							
							TemperatureSensor mTemperature = new TemperatureSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mTemperature.setTemperature(Float.parseFloat(dev.getString("temperature")));
							VeraDevice.this.setTemperatureSensor(deviceNum, mTemperature);
							VeraSensorQueue.mSensorQueue.add(mTemperature);

						} else {
							
							MotionSensor mMotion = new MotionSensor(category, subcategory, 
									deviceNum, "Motion Sensor", parent);
							VeraDevice.this.setMotionSensor(deviceNum, mMotion);	
							VeraSensorQueue.mSensorQueue.add(mMotion);
						}
					}
//					Log.d(TAG, "Inner Light Map key: " + VeraDevice.this.mLightMap.keySet());
//					Log.d(TAG, "Inner Temperature Map key: " + VeraDevice.this.mTemperatureMap.keySet());

				}  catch (MalformedURLException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void subscribeToSensor(int id) {
		String targetURL = generateURL(request[2], format);
		try {
			setInterruptFlag(false, id);
			threadPool.execute(new HttpClient(id, targetURL));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void unsubscribeFromSensor(int id) {
		Log.d(TAG, "Unsubscribe from Sensor");
		setInterruptFlag(true, id);
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
		//in.readBundle();
		in.readMap(mControllerMap, MotherSensor.class.getClassLoader());
		in.readMap(mLightMap, LightLevelSensor.class.getClassLoader());
		in.readMap(mMotionMap, MotionSensor.class.getClassLoader());
		in.readMap(mTemperatureMap, TemperatureSensor.class.getClassLoader());
	}
}

	
