package com.homesystem.Service.Gateway.Veris;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.SensorDevice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class VerisService extends Service {
	// debugging
	private static final String TAG = "VerisService";
	
	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private VerisDevice veris = null;
	private String devName;

	// This is the object that receives interactions from clients.
	//private VerisBinder mBinder = null;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "VerisService onBind");
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		veris = (VerisDevice) devByName.get(devName);
		return mBinder;
	}
	
	
	private final IVerisService.Stub mBinder = new IVerisService.Stub() {
		@Override
		public void startDataRetrieval(int id) throws RemoteException {
			veris.subscribeToSensor(id);
			
		}
	};
	
	public void startDataRetrieval(int id) {
		veris.subscribeToSensor(id);
	}
	
	@Override 
    public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "VerisService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();	
    } 
	
	@Override 
    public void onDestroy() { 
		Log.d(TAG, "VerisService OnDestroy");
		veris.unsubscribeFromSensor(0);
		super.onDestroy();
		
	}

}
