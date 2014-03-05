package com.homesystem.Demo;

import java.util.HashMap;

import com.homesystem.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.homesystem.Gateway.*;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;
import com.homesystem.Service.Gateway.Veris.VerisDevice;



public class AddDeviceActivity extends Activity {
	// debugging
	private static final String TAG = "AddDeviceActivity";
	
	
	// Result Code
	public static final int RESULT_VERA = 11;
	public static final int RESULT_VERIS = 12;
	public static final int RESULT_RARITAN = 13;
	
	// Layout View
	private Spinner sensorTypeSpinner;
	private ArrayAdapter mAdapter; 
	private Button saveButton;
	private EditText nameText;
	private EditText locationText;
	private EditText ipText;
	private EditText portText;
	private EditText intervalText;
	private EditText regAddrText;
	private EditText regQtyText;
	private EditText userText;
	private EditText passwordText;
	
	// Sensor Device Parameter
	private String sensor_type = "Nothing Selected";
	private String name;
	private String location;
	private String ip_addr;
	private int interval;
	private int portNum;
	private int mod_reg_addr;
	private int mod_reg_qty;
	private String password;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_add);
		
		// Set up EditText
		nameText = (EditText) findViewById(R.id.edit_name);
		locationText = (EditText) findViewById(R.id.edit_location);
		ipText = (EditText) findViewById(R.id.edit_ip);
		portText = (EditText) findViewById(R.id.edit_port);
		intervalText = (EditText) findViewById(R.id.edit_interval);
		regAddrText = (EditText) findViewById(R.id.edit_reg_addr);
		regQtyText = (EditText) findViewById(R.id.edit_reg_qty);
		userText = (EditText) findViewById(R.id.edit_usr);
		passwordText = (EditText) findViewById(R.id.edit_password);
		regAddrText.setEnabled(false);
		regQtyText.setEnabled(false);
		userText.setEnabled(false);
		passwordText.setEnabled(false);
		
		// Set up Spinner
		sensorTypeSpinner = (Spinner) findViewById(R.id.choose_sensor);
		mAdapter = ArrayAdapter.createFromResource(this,
		        R.array.sensor_type, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sensorTypeSpinner.setAdapter(mAdapter);
		
		sensorTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {
				
				if (mAdapter.getItem(pos).equals("Vera Device")) {
					sensor_type = "Vera Device";
					regAddrText.setEnabled(false);
					regQtyText.setEnabled(false);
					userText.setEnabled(false);
					passwordText.setEnabled(false);
//					nameText.setText("vera1");
//					locationText.setText("nesl");
//					ipText.setText("172.17.5.117");
//					portText.setText("3480");
//					intervalText.setText("10");
				}
				else if (mAdapter.getItem(pos).equals("Veris E30")) {
					sensor_type = "Veris E30";
					regAddrText.setEnabled(true);
					regQtyText.setEnabled(true);
					userText.setEnabled(false);
					passwordText.setEnabled(false);
//					nameText.setText("veris1");
//					locationText.setText("nesl");
//					ipText.setText("128.97.93.90");
//					portText.setText("4660");
//					intervalText.setText("10");
//					regAddrText.setText("2251");
//					regQtyText.setText("40");
				}
				else {
					sensor_type = "Raritan Dominion";
					regAddrText.setEnabled(false);
					regQtyText.setEnabled(false);
					userText.setEnabled(true);
					passwordText.setEnabled(true);
//					nameText.setText("raritan1");
//					locationText.setText("backroom");
//					ipText.setText("172.17.5.174");
//					portText.setText("161");
//					intervalText.setText("10");
//					userText.setText("admin");
//					passwordText.setText("abcd");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}	
		});
		
		// Set up button
		saveButton = (Button) findViewById(R.id.save_info);
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Sensor Type: " + sensor_type);
//				name = nameText.getText().toString();
//				location = locationText.getText().toString();
//				ip_addr = ipText.getText().toString();
//				interval = Integer.parseInt(intervalText.getText().toString());
//				portNum = Integer.parseInt(portText.getText().toString());
//				if (regAddrText.isEnabled())
//					mod_reg_addr = Integer.parseInt(regAddrText.getText().toString());
//				if (regQtyText.isEnabled())
//					mod_reg_qty = Integer.parseInt(regQtyText.getText().toString());
//				if (userText.isEnabled())
//					username = userText.getText().toString();
//				if (passwordText.isEnabled())
//					password = passwordText.getText().toString();				
				
				VeraDevice vera = null;
				VerisDevice veris = null;
				RaritanDevice raritan = null;
				
				if (sensor_type.equals("Vera Device")) {
//					vera = new VeraDevice.VeraBuilder(location, name, sensor_type, ip_addr).
//						setInterval(interval).setPort(portNum).build(); 
					
					vera = new VeraDevice.VeraBuilder("nesl", "vera1", sensor_type, "172.17.5.117").
							setPort(3480).build();
					
					MotherSensor mother = new MotherSensor(1,2,3,"mother");
					vera.setMotherSensor(3, mother);
					LightLevelSensor light = new LightLevelSensor(4,5,6,"light",7);
					vera.setLightSensor(6, light);
					
					Intent veraIntent = new Intent();
					veraIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, (Parcelable) vera);
					// Set result and finish this Activity
		            setResult(RESULT_VERA, veraIntent);
		            
				} else if (sensor_type.equals("Veris E30")) {
//					veris = new VerisDevice.VerisBuilder(location, name, sensor_type, 
//							ip_addr, mod_reg_addr).setInterval(interval).setPort(portNum).
//							setRegQty(mod_reg_qty).build();
					
					veris = new VerisDevice.VerisBuilder("nesl", "veris1", sensor_type, 
							"128.97.93.90", 2251).setPort(4660).
							setRegQty(40).build();
					
					Intent verisIntent = new Intent();
					verisIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, (Parcelable) veris);
					setResult(RESULT_VERIS, verisIntent);
					
				} else if (sensor_type.equals("Raritan Dominion")) {	
//					raritan = new RaritanDevice.RaritanBuilder(location, name, sensor_type, 
//							ip_addr).setInterval(interval).setPort(portNum).
//							setUsername(username).setPassword(password).build();
					
					raritan = new RaritanDevice.RaritanBuilder("nesl", "raritan1", sensor_type, 
							"172.17.5.174").setPort(161).setUsername("admin").
							setPassword("abcd").build();
					
					Intent raritanIntent = new Intent();
					raritanIntent.putExtra(Constant.EXTRA_DEVICE_TYPE, (Parcelable) raritan);
					setResult(RESULT_RARITAN, raritanIntent);	
				}
				finish();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "---On Destroy---");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
}
