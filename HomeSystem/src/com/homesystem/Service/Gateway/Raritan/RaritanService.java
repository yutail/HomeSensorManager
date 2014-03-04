package com.homesystem.Service.Gateway.Raritan;

import java.util.HashMap;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.IVeraService;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
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
	
	private final IRaritanService.Stub mBinder = new IRaritanService.Stub() {
		@Override
		public void startDataRetrieval(int id) throws RemoteException {
			raritan.subscribeToSensor(id);
			
		}
	};

	@Override 
	public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "RaritanService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();	
	}
	
	@Override 
	public void onDestroy() {  
        super.onDestroy(); 
        Log.d(TAG, "VeraService onDestroy");
        raritan.unsubscribeFromSensor(outletNum);
        
	}

}
