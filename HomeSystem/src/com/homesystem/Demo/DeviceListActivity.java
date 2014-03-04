package com.homesystem.Demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.homesystem.Service.HomeSystem;
import com.homesystem.R;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Gateway.Vera.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DeviceListActivity extends Activity {
	// debugging
	private static final String TAG = "DeviceListActivity";	
	
	// Layout View
	private ListView deviceList;
	
	// Home System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> sensorByName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		
		myHomeSystem = HomeSystem.getInstance();
		sensorByName = myHomeSystem.getHomeSensors();
		ArrayList<String> sensorList = new ArrayList<String>();
		for (String sensor: sensorByName.keySet())
			sensorList.add(sensor);
		
		String[] sensorArray = sensorList.toArray(new String[sensorList.size()]);
		Log.d(TAG, "List size: " + sensorList.size());
		Log.d(TAG, "Sensor Name: " + sensorArray[0]);
		
		// Set up ListView
		deviceList = (ListView) findViewById(R.id.list_device);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, sensorArray);
		deviceList.setAdapter(mAdapter);
		deviceList.setOnItemClickListener(new OnSensorClickListener());
		
	}
	
	private class OnSensorClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos,
				long id) {
			String sensorName = (String) deviceList.getItemAtPosition(pos);	
			SensorDevice device = sensorByName.get(sensorName);
			Intent deviceInfoIntent = new Intent(DeviceListActivity.this, DeviceInfoActivity.class);
			if (device instanceof VeraDevice) {
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, Constant.VERA_NAME);
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_NAME, sensorName);	
				//((VeraDevice) device).getDeviceInfo();
				//myHomeSystem.addDevicesByName(sensorName, device);
			}				
			else if (device instanceof VeraDevice) {
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, Constant.VERIS_NAME);
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_NAME, sensorName);
			}
			else if (device instanceof RaritanDevice) {
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, Constant.RARITAN_NAME);
				deviceInfoIntent.putExtra(Constant.EXTRA_DEVICE_NAME, sensorName);
			}
			startActivity(deviceInfoIntent);			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
