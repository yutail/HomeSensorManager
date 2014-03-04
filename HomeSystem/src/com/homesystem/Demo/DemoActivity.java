package com.homesystem.Demo;

import java.util.HashMap;

import com.homesystem.R;
import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Veris.VerisDevice;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class DemoActivity extends Activity {
	// debugging
	private static final String TAG = "DemoActivity";
	
	// Intent request codes
	private static final int REQUEST_ADD_DEVICE = 1;
	private static final int REQUEST_LIST_DEVICE = 2;
	
	// Layout Views
	private Button mAddButton;
	private Button mReportButton;
	
	// Contains info about all devices
	private HomeSystem myHomeSystem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "DemoActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
		
		// Create a Singleton of HomeSystem class
		myHomeSystem = HomeSystem.getInstance();
		
		// Set up Button
		mAddButton = (Button) findViewById(R.id.add_button);
		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent addIntent = new Intent(DemoActivity.this, AddDeviceActivity.class);
				startActivityForResult(addIntent, REQUEST_ADD_DEVICE);
				
			}
		});
		
		mReportButton = (Button) findViewById(R.id.report_button);
		mReportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reportIntent = new Intent(DemoActivity.this, DeviceListActivity.class);
				startActivity(reportIntent);					
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + resultCode);
		switch(requestCode) {
		case REQUEST_ADD_DEVICE:
			addDevices(data, resultCode);
		}     
	}
	
	public void addDevices(Intent data, int resultCode) {
		VeraDevice vera = null;
		VerisDevice veris = null;
		RaritanDevice raritan = null;
		
		if (resultCode == AddDeviceActivity.RESULT_VERA) {
			vera = (VeraDevice) data.getParcelableExtra(Constant.EXTRA_DEVICE_TYPE);
			myHomeSystem.addDevicesByName(vera.getName(), vera);
			Log.d(TAG, "MotherSensor KeySet: " + vera.getMotherSensor().isEmpty());
			Log.d(TAG, "LightLevelSensor KeySet: " + vera.getLightSensor().keySet());
			Log.d(TAG, "Vera Name: " + vera.getName());
			
		} else if (resultCode == AddDeviceActivity.RESULT_VERIS) {
			veris = (VerisDevice) data.getParcelableExtra(Constant.EXTRA_DEVICE_TYPE);
			myHomeSystem.addDevicesByName(veris.getName(), veris);
			Log.d(TAG, "Veris Name: " + veris.getName());
			
		} else if (resultCode == AddDeviceActivity.RESULT_RARITAN) {
			raritan = (RaritanDevice) data.getParcelableExtra(Constant.EXTRA_DEVICE_TYPE);
			myHomeSystem.addDevicesByName(raritan.getName(), raritan);
			Log.d(TAG, "Raritan Name: " + raritan.getName());	
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
