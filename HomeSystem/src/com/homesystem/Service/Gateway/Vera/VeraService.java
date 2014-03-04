package com.homesystem.Service.Gateway.Vera;

import java.util.HashMap;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class VeraService extends Service {
	// debugging
	private static final String TAG = "VeraService";
	
	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private VeraDevice vera = null;
	private String devName;
	private int devId;
	private int idLight;
	
	// This is the object that receives interactions from clients.
    private VeraBinder mBinder = null;
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class VeraBinder extends Binder {
        public VeraService getService() {
            return VeraService.this;
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
		devId = intent.getIntExtra(Constant.EXTRA_DEVICE_ID, -1);
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		vera = (VeraDevice) devByName.get(devName);
		vera.subscribeToSensor(devId);
    	return START_STICKY;
    }

	@Override
	public IBinder onBind(Intent intent) {
		// A client is binding to the service with bindService()
		//return null;
		Log.d(TAG, "VeraService onBind");
		devId = intent.getIntExtra(Constant.EXTRA_DEVICE_ID, -1);
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		vera = (VeraDevice) devByName.get(devName);
		vera.subscribeToSensor(devId);
		return mBinder;
	}
	
	public void startDataRetrieval(int id) {
		vera.subscribeToSensor(id);
		id = idLight;	
	}
	
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
		mBinder = new VeraBinder();
    } 
	
	@Override 
	public void onDestroy() {  
        super.onDestroy(); 
        Log.d(TAG, "VeraService onDestroy");
        vera.unsubscribeFromSensor(devId);
        vera.unsubscribeFromSensor(idLight);
        
	}

}
