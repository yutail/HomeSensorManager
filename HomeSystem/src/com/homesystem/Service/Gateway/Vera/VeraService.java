package com.homesystem.Service.Gateway.Vera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotionSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.TemperatureSensor;


import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class VeraService extends Service implements DataRetrieval {
	// debugging
	private static final String TAG = "VeraService";
	
	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private VeraDevice vera = null;
	private HashMap<Integer, MotherSensor> mControllerMap = null;
	private HashMap<Integer, LightLevelSensor> mLightMap = null;
	private HashMap<Integer, MotionSensor> mMotionMap = null;
	private HashMap<Integer, TemperatureSensor> mTemperatureMap = null;
	private String devName;
	
	// Sampling Interval
	private int interval = 10;
	
	private RemoteCallbackList<IVeraServiceCallback> mVeraCallbackList = 
			new RemoteCallbackList<IVeraServiceCallback>();
	
	// Handling Messages
	private static final int REPORT_MSG = 1;
	private static final String VERA_VALUE = "vera_value";
	private static final String VERA_SUBTYPE = "vera_subtype";
	private static final String VERA_ID = "vera_id";
	
	// Handling Threads
	private ExecutorService threadPool = Executors.newCachedThreadPool();	
	private final int SENSOR_QTY = 20;
	private boolean interruptFlag[] = new boolean[SENSOR_QTY];
	private Object lock_interruptFlag = new Object();
	
	// Used for generating URL
	private String request[] = {"user_data", "status", "sdata"};
	private String format = "output_format=json";	
	
	public void setInterruptFlag(boolean flag, int id) {
		synchronized(lock_interruptFlag) {
			this.interruptFlag[id] = flag;
		}
	}
	
	public boolean getInterruptFlag(int index) {
		synchronized(lock_interruptFlag) {
			return this.interruptFlag[index];
		}	
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "VeraService onBind");
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		vera = (VeraDevice) devByName.get(devName);
		mControllerMap = vera.getMotherSensor();
		mLightMap = vera.getLightSensor();
		mTemperatureMap = vera.getTemperatureSensor();
		mMotionMap = vera.getMotionSensor();
		return mBinder;
	}
	
	private final IVeraService.Stub mBinder = new IVeraService.Stub() {
		@Override
		public void startDataRetrieval(int id) throws RemoteException {
			subscribeToSensor(id);
		}
		
		public void stopDataRetrieval(int id) throws RemoteException {
			unsubscribeFromSensor(id);
		}
		
		public void setInterval(int i) throws RemoteException {
			interval = i;
		}

		@Override
		public void registerVeraCallback(IVeraServiceCallback vera_cb)
				throws RemoteException {
			if (vera_cb != null)
				mVeraCallbackList.register(vera_cb);
		}

		@Override
		public void unregisterVeraCallback(IVeraServiceCallback vera_cb)
				throws RemoteException {
			if (vera_cb != null)
				mVeraCallbackList.unregister(vera_cb);
		}
	};
	
	// Handler
	private final Handler mHandler = new Handler() {
		@Override 
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case REPORT_MSG: {
				String vera_value = msg.getData().getString(VERA_VALUE);
				String tag = msg.getData().getString(VERA_SUBTYPE);
				
				// Broadcast to client the new value.
                final int num = mVeraCallbackList.beginBroadcast();
                Log.d(TAG, "Number of Clients: " + num);
                try {
                	mVeraCallbackList.getBroadcastItem(num-1).updateVeraValue(vera_value, tag);
                } catch (RemoteException e) {
                	e.printStackTrace();
                }
                mVeraCallbackList.finishBroadcast();
				
			} break;
			
			default:
				super.handleMessage(msg);
			
			}	
		}	
	};
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "VeraService unbind");
		return super.onUnbind(intent);
		
	}
	
	@Override 
    public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "VeraService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();	
    } 
	
	@Override 
	public void onDestroy() {  
        super.onDestroy(); 
        Log.d(TAG, "VeraService onDestroy");
        
	}

	@Override
	public void subscribeToSensor(int... id) {
		Log.d(TAG, "Subscribe to Vera Sensor");
		String targetURL = generateURL(request[2], format, vera);
		try {
			setInterruptFlag(false, id[0]);
			threadPool.execute(new HttpClient(id[0], targetURL, interval));
			
		} catch (MalformedURLException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void unsubscribeFromSensor(int... id) {
		Log.d(TAG, "Unsubscribe from Vera Sensor");
		setInterruptFlag(true, id[0]);	
	}
	
	public String generateURL(String r, String f, VeraDevice vera) {
		// Format of Requested URL: http://ip_address:3480/data_request?id=sdata&output_format=json
		StringBuilder result = new StringBuilder();
		result.append("http://");
		result.append(vera.getIp());
		result.append(":");
		result.append(vera.getPort());
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
		private int interval;
				
		public HttpClient(int id, String url, int interval) throws IOException {
			this.vera_url = new URL(url);
			this.tid = id;
			this.interval = interval;

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
//					Message msg = mHandler.obtainMessage(Constant.VERA_MESSAGE);
//			        Bundle bundle = new Bundle();
//			        bundle.putString(Constant.VERA_VALUE, "");
//			        bundle.putInt(Constant.VERA_ID, tid);
//			        bundle.putString(Constant.VERA_SUBTYPE, tag);
//			        msg.setData(bundle);
//			        mHandler.sendMessage(msg);
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
					
					// Sending message to update UI
			        Message msg = mHandler.obtainMessage(REPORT_MSG);
			        Bundle bundle = new Bundle();
			        bundle.putString(VERA_VALUE, result);
			        bundle.putString(VERA_SUBTYPE, tag);
			        bundle.putInt(VERA_ID, tid);
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);
					
					if(tag.equals("light")) {
						((LightLevelSensor) mSensor).setLightLevel(Float.parseFloat(result));
						//setLightSensor(tid, (LightLevelSensor) mSensor);	
					}
					else if (tag.equals("temperature")) {
						((TemperatureSensor) mSensor).setTemperature(Float.parseFloat(result));
						//setTemperatureSensor(tid, (TemperatureSensor) mSensor);
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
	
}
