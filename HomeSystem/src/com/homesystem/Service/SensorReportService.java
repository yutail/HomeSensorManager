package com.homesystem.Service;

import java.util.HashMap;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Veris.VerisDevice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SensorReportService extends Service {
	// debugging
	private static final String TAG = "SensorReportService";
	
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> sensorByName;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "OnBind");
		return mBinder;
	}
	
	
	private final ISensorReportService.Stub mBinder = new ISensorReportService.Stub() {
		@Override
		public HomeSystem reportHomeSensor() throws RemoteException {
			
			VerisDevice veris = new VerisDevice.VerisBuilder("nesl", "veris1", "Veris E30", 
					"128.97.93.90", 2251).setInterval(10).setPort(4660).
					setRegQty(40).build();
			
			myHomeSystem.addDevicesByName("veris1", veris);
			
			return myHomeSystem;		
		}
	};
	
	@Override 
    public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "SensorReportService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		sensorByName = myHomeSystem.getHomeSensors();	
    } 
	
	@Override 
    public void onDestroy() { 
		super.onDestroy();
		Log.d(TAG, "SensorReportService OnDestroy");
	
	}

}
