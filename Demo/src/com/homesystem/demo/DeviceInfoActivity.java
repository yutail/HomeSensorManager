package com.homesystem.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.homesystem.Service.Constant;
import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.ISensorReportService;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.IRaritanService;
import com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.IVeraService;
import com.homesystem.Service.Gateway.Vera.IVeraServiceCallback;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotionSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.TemperatureSensor;
import com.homesystem.Service.Gateway.Veris.IVerisService;
import com.homesystem.Service.Gateway.Veris.IVerisServiceCallback;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeviceInfoActivity extends Activity {
	// debugging
	private static final String TAG = "DeviceInfoActivity";

	// Vera Layout View
	private EditText veraNameText;
	private EditText veraLocText;
	private EditText veraIpText;
	private EditText veraIntervalText;	
	private CheckBox veraSensorCheckBox1;
	private CheckBox veraSensorCheckBox2;
	private CheckBox veraSensorCheckBox3;
	private TextView veraSensorValue1;
	private TextView veraSensorValue2;
	private TextView veraSensorValue3;

	// Veris Layout View
	private EditText verisNameText;
	private EditText verisLocText;
	private EditText verisIpText;
	private EditText verisIntervalText;	
	private EditText verisRegAddrText;
	private EditText verisRegQtyText;
	private EditText verisDataEditText;
	private CheckBox verisCheckBox;

	// Raritan Layout View
	private EditText raritanNameText;
	private EditText raritanLocText;
	private EditText raritanIntervalText;
	private EditText raritanIpText;
	private EditText raritanUserText;
	private EditText raritanPasswordText;
	private CheckBox raritanVoltageCheckBox;
	private CheckBox raritanActivePowerCheckBox;
	private CheckBox raritanCurrentCheckBox;
	private EditText raritanVoltageEdit;
	private EditText raritanActivePowerEdit;
	private EditText raritanCurrentEdit;
	private TextView raritanVoltageValue;
	private TextView raritanActivePowerValue;
	private TextView raritanCurrentValue;
	
	// Sensor Info
	private HomeSystem myHomeSystem = null;
	private HashMap<String, SensorDevice> devByName;
	private String devName;
	private String deviceType = null;
	private VeraDevice vera = null;
	private VerisDevice veris = null;
	private RaritanDevice raritan = null;
	private int idTem;
	private int idLight;
	private int idMotion;
	private int veris_value[];
	private int interval;

	// Service 
	private IVeraService mVeraService = null;
	private VeraConnection mVeraConnection;
	private boolean mIsVeraBind = false;
	private IVerisService mVerisService = null;
	private VerisConnection mVerisConnection;
	private boolean mIsVerisBind = false;
	private IRaritanService mRaritanService = null;
	private RaritanConnection mRaritanConnection;
	private boolean mIsRaritanBind = false;
	
	// Handling Messages
	private static final int VERA_MESSAGE = 1;
	private static final int VERIS_MESSAGE = 2;
	private static final int RARITAN_MESSAGE = 3;
	private static final String VERA_VALUE = "vera_value";
	private static final String VERA_SUBTYPE = "vera_subtype";
	private static final String VERIS_VALUE = "veris_value";
	private static final String RARITAN_VALUE = "raritan_value";
	private static final String RARITAN_CHANNEL = "raritan_channel";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();

		// Intent send from deviceListActivity
		Intent receivedIntent = getIntent();
		deviceType = receivedIntent.getStringExtra(Constant.EXTRA_DEVICE_TYPE);
		devName = receivedIntent.getStringExtra(Constant.EXTRA_DEVICE_NAME);

		// Vera Device
		if (deviceType.equals(Constant.VERA_NAME)) {
			vera = (VeraDevice) devByName.get(devName);
			setVeraLayout();
			Log.d(TAG, "Vera Sensor Type: " + vera.getSensorType());
			
			if (mIsVeraBind == false)
				doBindVeraService();

			HashMap<Integer, TemperatureSensor> mTemperatureMap = vera.getTemperatureSensor();
			HashMap<Integer, LightLevelSensor> mLightMap = vera.getLightSensor();
			HashMap<Integer, MotionSensor> mMotionMap = vera.getMotionSensor();

			Iterator<Integer> mTemIt = mTemperatureMap.keySet().iterator();
			Log.d(TAG, "Temperature Map key: " + mTemperatureMap.keySet());
			Iterator<Integer> mLightIt = mLightMap.keySet().iterator();
			Log.d(TAG, "Light Map key: " + mLightMap.keySet());
			Iterator<Integer> mMotionIt = mMotionMap.keySet().iterator();
			Log.d(TAG, "Light Map key: " + mMotionMap.keySet());

			while (mTemIt.hasNext()) {
				idTem = mTemIt.next();
				TemperatureSensor mTemSensor = mTemperatureMap.get(idTem);
				veraSensorCheckBox1.setText("Temperature"+String.valueOf(idTem));	
				Log.d(TAG, "Temperature id: " + idTem);
			}

			while (mLightIt.hasNext()) {
				idLight = mLightIt.next();
				LightLevelSensor mLightSensor = mLightMap.get(idLight);
				veraSensorCheckBox2.setText("LightLevel"+String.valueOf(idLight));
				Log.d(TAG, "Light id: " + idLight);
			}

			while (mMotionIt.hasNext()) {
				idMotion = mMotionIt.next();
				MotionSensor mMotionSensor = mMotionMap.get(idMotion);
				veraSensorCheckBox3.setText("Motion"+String.valueOf(idMotion));
				Log.d(TAG, "Motion id: " + idMotion);
			}			

			veraSensorCheckBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						Log.d(TAG, "Temp Service Started: " + idTem);
						try {
							mVeraService.setInterval(10);
							mVeraService.startDataRetrieval(idTem);
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}

					} else {
						
						veraSensorValue1.setText("");
						Log.d(TAG, "Temp Service Stopped");
					}	
				}
			});

			veraSensorCheckBox2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						Log.d(TAG, "Light Service Started: " + idLight);
						try {
							mVeraService.startDataRetrieval(idLight);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						
					} else {
						
						Log.d(TAG, "Light Service Stopped");
						veraSensorValue2.setText("");
					}
				}
			});	

			veraSensorCheckBox3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						//veraSensorValue3.setText("No Motion");
					} else {
						veraSensorValue3.setText("");
					}					
				}				
			});		
		}
		// Veris Device
		else if (deviceType.equals(Constant.VERIS_NAME)) {
			veris = (VerisDevice) devByName.get(devName);
			setVerisLayout();
			
			// Bind to Veris Service
			if (mIsVerisBind == false)
				doBindVerisService();

			verisCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						int len = veris.getRegQty();
						veris_value = new int[len];
						interval = Integer.parseInt(verisIntervalText.getText().toString());
						
						try {
							mVerisService.setInterval(interval);
							mVerisService.startDataRetrieval();
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					} else {
						doUnbindVerisService();
						try {
							mVerisService.unregisterVerisCallback(mVerisCallback);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						verisDataEditText.setText("");
					}					
				}				
			});

			Log.d(TAG, "Veris Sensor Type: " + veris.getSensorType());

		}
		// Raritan Device 
		else if (deviceType.equals(Constant.RARITAN_NAME)) {
			raritan = (RaritanDevice) devByName.get(devName);
			setRaritanLayout();
			
			// Bind to Raritan Service
			if (mIsRaritanBind == false)
				doBindRaritanService();
			
			Log.d(TAG, "Raritan Sensor Type: " + raritan.getSensorType());

			raritanVoltageCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						raritan.setChannel(Constant.RARITAN_VOLTAGE);
						int id = Integer.parseInt(raritanVoltageEdit.getText().toString());
						try {
							mRaritanService.startDataRetrieval(id);
						} catch (RemoteException e) {
							e.printStackTrace();
						}						
						
					} else {
						doUnbindRaritanService();
						raritanVoltageValue.setText("");	
					}	
				}	
			});			

			raritanActivePowerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {

						raritan.setChannel(Constant.RARITAN_ACTIVE_POWER);
						int id = Integer.parseInt(raritanActivePowerEdit.getText().toString());
						try {
							mRaritanService.startDataRetrieval(id);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					} else {
						doUnbindRaritanService();
						raritanActivePowerValue.setText("");
					}	
				}	
			});

			raritanCurrentCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {

						raritan.setChannel(Constant.RARITAN_CURRENT);
						int id = Integer.parseInt(raritanCurrentEdit.getText().toString());
						try {
							mRaritanService.startDataRetrieval(id);
						} catch (RemoteException e) {
							e.printStackTrace();
						}				

					} else {
						doUnbindRaritanService();
						raritanCurrentValue.setText("");
					}	
				}	
			});
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "++ ON START ++");
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Log.d(TAG, "+ ON RESUME +");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		Log.d(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	public void setVeraLayout() {
		// Set up Vera Layout
		setContentView(R.layout.vera_info);
		veraNameText = (EditText) findViewById(R.id.edit_vera_name);
		veraLocText = (EditText) findViewById(R.id.edit_vera_location);
		veraIpText = (EditText) findViewById(R.id.edit_vera_ip);
		veraIntervalText = (EditText) findViewById(R.id.edit_vera_interval);
		veraSensorCheckBox1 = (CheckBox) findViewById(R.id.check_sensor1);
		veraSensorCheckBox2 = (CheckBox) findViewById(R.id.check_sensor2);
		veraSensorCheckBox3 = (CheckBox) findViewById(R.id.check_sensor3);
		veraSensorValue1 = (TextView) findViewById(R.id.sensor_value1);
		veraSensorValue2 = (TextView) findViewById(R.id.sensor_value2);
		veraSensorValue3 = (TextView) findViewById(R.id.sensor_value3);	
		veraNameText.setText(vera.getName());
		veraLocText.setText(vera.getLocation());
		veraIpText.setText(vera.getIp());
		//veraIntervalText.setText(String.valueOf(vera.getInterval()));
	}

	public void setVerisLayout() {
		setContentView(R.layout.veris_info);
		verisNameText = (EditText) findViewById(R.id.edit_veris_name);
		verisLocText = (EditText) findViewById(R.id.edit_veris_location);
		verisIpText = (EditText) findViewById(R.id.edit_veris_ip);
		verisIntervalText = (EditText) findViewById(R.id.edit_veris_interval);
		verisRegAddrText = (EditText) findViewById(R.id.edit_veris_reg_addr);
		verisRegQtyText = (EditText) findViewById(R.id.edit_veris_reg_qty);
		verisNameText.setText(veris.getName());
		verisLocText.setText(veris.getLocation());
		verisIpText.setText(veris.getIp());
		verisRegAddrText.setText(String.valueOf(veris.getRegAddr()));
		verisRegQtyText.setText(String.valueOf(veris.getRegQty()));
		verisDataEditText = (EditText) findViewById(R.id.edit_veris_data);
		verisCheckBox = (CheckBox) findViewById(R.id.check_veris_get_data);
	}

	public void setRaritanLayout() {
		setContentView(R.layout.raritan_info);
		raritanNameText = (EditText) findViewById(R.id.edit_raritan_name);
		raritanLocText = (EditText) findViewById(R.id.edit_raritan_location);
		raritanIpText = (EditText) findViewById(R.id.edit_raritan_ip);
		raritanIntervalText = (EditText) findViewById(R.id.edit_raritan_interval);
		raritanUserText = (EditText) findViewById(R.id.edit_raritan_usr);
		raritanPasswordText = (EditText) findViewById(R.id.edit_raritan_password);
		raritanNameText.setText(raritan.getName());
		raritanLocText.setText(raritan.getLocation());
		raritanIpText.setText(raritan.getIp());
		//raritanIntervalText.setText(String.valueOf(raritan.getInterval()));
		raritanUserText.setText(raritan.getUsername());
		raritanPasswordText.setText(raritan.getPassword());

		raritanVoltageCheckBox = (CheckBox) findViewById(R.id.check_raritan_voltage);;
		raritanActivePowerCheckBox = (CheckBox) findViewById(R.id.check_raritan_active_power);
		raritanCurrentCheckBox = (CheckBox) findViewById(R.id.check_raritan_current);
		raritanVoltageEdit = (EditText) findViewById(R.id.edit_voltage_outlet);
		raritanActivePowerEdit = (EditText) findViewById(R.id.edit_active_power_outlet);
		raritanCurrentEdit = (EditText) findViewById(R.id.edit_current_outlet);
		raritanVoltageValue = (TextView) findViewById(R.id.raritan_voltage_text);
		raritanActivePowerValue = (TextView) findViewById(R.id.raritan_active_power_text);
		raritanCurrentValue = (TextView) findViewById(R.id.raritan_current_text);	
	}

	// Vera Service Connection
	private class VeraConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mVeraService = IVeraService.Stub.asInterface(service);
			try {
				mVeraService.registerVeraCallback(mVeraCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "Remote Vera Service Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mVeraService = null;
			Log.d(TAG, "Remote Vera Serivce Disconnected");
		}	
	}

	public void doBindVeraService() {
		Log.d(TAG, "Bind Vera Service");
		mVeraConnection = new VeraConnection();
		Intent veraRetrievalIntent = new Intent("com.homesystem.Service.Gateway.Vera.IVeraService");
		veraRetrievalIntent.putExtra(Constant.EXTRA_DEVICE_NAME, devName);
		bindService(veraRetrievalIntent, mVeraConnection, Context.BIND_AUTO_CREATE);	
		mIsVeraBind = true;
	}

	public void doUnbindVeraService() {
		if (mIsVeraBind) {
			Log.d(TAG, "Unbind Vera Service");
			unbindService(mVeraConnection);
			mIsVeraBind = false;
		}
	}
	
	// VeraService Callback
	private IVeraServiceCallback mVeraCallback = new IVeraServiceCallback.Stub() {
		
		@Override
		public void updateVeraValue(String value, String tag) throws RemoteException {
	        Message msg = mHandler.obtainMessage(VERA_MESSAGE);
	        Bundle bundle = new Bundle();
	        bundle.putString(VERA_VALUE, value);	
	        bundle.putString(VERA_SUBTYPE, tag);
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);		
		}
	};
	
	// Veris Service Connection
	private class VerisConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mVerisService = IVerisService.Stub.asInterface(service);
			try {
				mVerisService.registerVerisCallback(mVerisCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "Remote Veris Service Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mVerisService = null;
			Log.d(TAG, "Remote Veris Serivce Disconnected");
		}	
	}

	public void doBindVerisService() {
		Log.d(TAG, "Bind Veris Service");
		mVerisConnection = new VerisConnection();
		Intent verisRetrievalIntent = new Intent("com.homesystem.Service.Gateway.Veris.IVerisService");
		verisRetrievalIntent.putExtra(Constant.EXTRA_DEVICE_NAME, devName);
		bindService(verisRetrievalIntent, mVerisConnection, Context.BIND_AUTO_CREATE);	
		mIsVerisBind = true;	
	}

	public void doUnbindVerisService() {
		if (mIsVerisBind) {
			Log.d(TAG, "Unbind Veris Service");
			unbindService(mVerisConnection);
			mIsVerisBind = false;
		}
	}
	
	// VerisService Callback
	private IVerisServiceCallback mVerisCallback = new IVerisServiceCallback.Stub() {
		@Override
		public void updateVerisValue(int[] value) throws RemoteException {
	        Message msg = mHandler.obtainMessage(VERIS_MESSAGE);
	        Bundle bundle = new Bundle();
	        bundle.putIntArray(VERIS_VALUE, value);			       
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);		
		}
	};

	// Raritan Service Connection
	private class RaritanConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mRaritanService = IRaritanService.Stub.asInterface(service);
			Log.d(TAG, "Remote Raritan Service Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mRaritanService = null;
			Log.d(TAG, "Remote Raritan Serivce Disconnected");
		}	
	}

	public void doBindRaritanService() {
		Log.d(TAG, "Bind Raritan Service");
		mRaritanConnection = new RaritanConnection();
		Intent raritanRetrievalIntent = new Intent("com.homesystem.Service.Gateway.Raritan.IRaritanService");
		raritanRetrievalIntent.putExtra(Constant.EXTRA_DEVICE_NAME, devName);
		bindService(raritanRetrievalIntent, mRaritanConnection, Context.BIND_AUTO_CREATE);	
		mIsRaritanBind = true;
	}

	public void doUnbindRaritanService() {
		if (mIsRaritanBind) {
			Log.d(TAG, "Unbind Raritan Service");
			unbindService(mRaritanConnection);
			mIsRaritanBind = false;
		}
	}
	
	// RaritanService Callback
	private IRaritanServiceCallback mRaritanCallback = new IRaritanServiceCallback.Stub() {
		
		@Override
		public void updateRaritanValue(String value, String channel)
				throws RemoteException {
			Message msg = mHandler.obtainMessage(RARITAN_MESSAGE);
			Bundle bundle = new Bundle();
			bundle.putString(RARITAN_VALUE, value);
			bundle.putString(RARITAN_CHANNEL, channel);
			msg.setData(bundle);			
		}
	};

	// The Handler that gets information back from the Worker Thread
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case VERA_MESSAGE:
				String vera_value = msg.getData().getString(VERA_VALUE);
				String vera_type = msg.getData().getString(VERA_SUBTYPE);
				if (vera_type.equals("temperature"))
					veraSensorValue1.setText(vera_value);
				else if (vera_type.equals("light"))
					veraSensorValue2.setText(vera_value);
				else if (vera_type.equals("motion"))
					veraSensorValue3.setText(vera_value);
				break;

			case VERIS_MESSAGE:
				veris_value = msg.getData().getIntArray(VERIS_VALUE);				
				verisDataEditText.setText(Arrays.toString(veris_value));
				break;

			case RARITAN_MESSAGE:
				String raritan_value = msg.getData().getString(RARITAN_VALUE);
				String channel = msg.getData().getString(RARITAN_CHANNEL);
				Log.d(TAG, "Received Channel Value: " + channel);
				if (channel.equals(Constant.RARITAN_VOLTAGE))
					raritanVoltageValue.setText(raritan_value);	
				else if (channel.equals(Constant.RARITAN_ACTIVE_POWER))
					raritanActivePowerValue.setText(raritan_value);
				else if (channel.equals(Constant.RARITAN_CURRENT))
					raritanCurrentValue.setText(raritan_value);
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.demo, menu);
		return true;
	}

}
