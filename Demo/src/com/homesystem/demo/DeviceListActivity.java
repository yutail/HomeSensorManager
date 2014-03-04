package com.homesystem.demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.homesystem.Service.Constant;
import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.ISensorReportService;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Veris.VerisDevice;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListActivity extends Activity {
	// debugging 
	private static final String TAG = "DeviceListActivity";
	
	// Layout view
	ListView deviceList;
	
	// HomeSystem
	HomeSystem myHomeSystem;
	HashMap<String, SensorDevice> sensorByName;
	
	// Service instance
	private ISensorReportService mSensorReportService = null;
	private boolean isSensorReportServiceBind = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		Log.d(TAG, "OnCreate");
		// Bind to SensorReportService
		initSensorReportService();		
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "---OnDestroy---");
		super.onDestroy();
		releaseSensorReportService();
	}
	
	private void initSensorReportService() {
		Intent sensorReportIntent = new Intent("com.homesystem.Service.ISensorReportService");
		bindService(sensorReportIntent, mSensorReportConnection, Context.BIND_AUTO_CREATE);
		isSensorReportServiceBind = true;
		Log.d(TAG, "Bind to Sensor Report Service");		
	}
	
	private void releaseSensorReportService() {
		if (isSensorReportServiceBind) {
			unbindService(mSensorReportConnection);
			mSensorReportConnection = null;
			isSensorReportServiceBind = false;
			Log.d(TAG, "Unbind to Sensor Report Service");			
		}	
	}
	
    private ServiceConnection mSensorReportConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
        	mSensorReportService = ISensorReportService.Stub.asInterface(service);
            Log.d(TAG, "Remote SensorReportService Connected");

        	try {
    			myHomeSystem = mSensorReportService.reportHomeSensor();
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}
        	/* Need to be Modified */
        	HomeSystem.setInstance(myHomeSystem);
        	
    		sensorByName = myHomeSystem.getHomeSensors();
    		ArrayList<String> sensorList = new ArrayList<String>();
    		for (String sensor: sensorByName.keySet())
    			sensorList.add(sensor);
    		
    		String[] sensorArray = sensorList.toArray(new String[sensorList.size()]);
    		Log.d(TAG, "List size: " + sensorList.size());
    		Log.d(TAG, "Sensor Name: " + sensorArray[0]);
    		
    		// Set up ListView
    		deviceList = (ListView) findViewById(R.id.list_device);
    		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(DeviceListActivity.this, 
    				android.R.layout.simple_list_item_1, sensorArray);
    		deviceList.setAdapter(mAdapter);
    		deviceList.setOnItemClickListener(new OnSensorClickListener());
    	}

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mSensorReportService = null;
            Log.d(TAG, "Remote SensorReportService Disconnected");
        }
    };
    
    private class OnSensorClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos,
				long id) {
			String sensorName = (String) deviceList.getItemAtPosition(pos);	
			SensorDevice device = sensorByName.get(sensorName);
			Intent deviceInfoIntent = new Intent(DeviceListActivity.this, DeviceInfoActivity.class);
			
			if (device instanceof VerisDevice) {
				Log.d(TAG, "Veris Device");
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, Constant.VERIS_NAME);
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_NAME, sensorName);
			} else if (device instanceof VeraDevice) {
				Log.d(TAG, "Vera Device");
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, Constant.VERA_NAME);
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_NAME, sensorName);
				
			}
			
			startActivity(deviceInfoIntent);
		}	
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.demo, menu);
		return true;
	}

}
