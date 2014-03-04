package com.homesystem.Service.Gateway.Raritan;

import java.util.HashMap;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RaritanService extends Service {
	// debugging 
	private static final String TAG = "RaritanService";

	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private RaritanDevice raritan = null;
	private String devName;
	private int outletNum;

	// This is the object that receives interactions from clients.
	private RaritanBinder mBinder = null;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class RaritanBinder extends Binder {
		public RaritanService getService() {
			return RaritanService.this;
		}
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
		Log.d(TAG, "OnStartCommand");
		outletNum = intent.getIntExtra(Constant.EXTRA_DEVICE_ID, -1);
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		raritan = (RaritanDevice) devByName.get(devName);
		raritan.subscribeToSensor(outletNum);
    	return START_STICKY;
    }

	@Override
	public IBinder onBind(Intent intent) {
		//return null;
		Log.d(TAG, "RaritanService onBind");
		outletNum = intent.getIntExtra(Constant.EXTRA_DEVICE_ID, -1);
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		raritan = (RaritanDevice) devByName.get(devName);
		raritan.subscribeToSensor(outletNum);
		return mBinder;
	}
	
	public void startDataRetrieval(int id) {
		raritan.subscribeToSensor(id);
	}

	@Override 
	public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "RaritanService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();	
		mBinder = new RaritanBinder();
	}
	
	@Override 
	public void onDestroy() {  
        super.onDestroy(); 
        Log.d(TAG, "VeraService onDestroy");
        raritan.unsubscribeFromSensor(outletNum);
        
	}

}
