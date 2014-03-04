package com.homesystem.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Vera.VeraDevice;
import com.homesystem.Service.Gateway.Vera.VeraSensorQueue;
import com.homesystem.Service.Gateway.Vera.VeraSensor.LightLevelSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotherSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.MotionSensor;
import com.homesystem.Service.Gateway.Vera.VeraSensor.TemperatureSensor;
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
			
			VeraDevice vera = new VeraDevice.VeraBuilder("nesl", "vera1", "Vera", "172.17.5.117").
					setInterval(10).setPort(3480).build();
			
			myHomeSystem.addDevicesByName("vera1", vera);
			
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
	
public void getVeraDeviceInfo() {		
		
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// HTTP connection
					URL targetURL = new URL(generateURL(request[2], format));
					HttpURLConnection http_connection = (HttpURLConnection) targetURL.openConnection();
					http_connection.setRequestMethod("GET");
					http_connection.setDoOutput(false);
					http_connection.setDoInput(true);
					// Retrieve data
					InputStream tempIn = http_connection.getInputStream();
					BufferedReader buf = new BufferedReader(new InputStreamReader(tempIn));
					StringBuffer sb = new StringBuffer();
					String line = buf.readLine();
					sb.append(line);
					// Parse JSON Data
					JSONObject deviceSummary = new JSONObject(sb.toString());			
					JSONArray devices = deviceSummary.getJSONArray("devices");
					int len = devices.length();
					VeraSensorQueue.mSensorSize.add(len);
					Log.d(TAG, "Num of Devices: " + len);
					for (int i=0; i<len; i++) {
						JSONObject dev = (JSONObject) devices.get(i);
						String name = dev.getString("name");
						int category = Integer.parseInt(dev.getString("category"));
						int subcategory = Integer.parseInt(dev.getString("subcategory"));
						int parent = dev.getInt("parent");
						int deviceNum = dev.getInt("id");
						Log.d(TAG, "Name: " + name + ", Category: " + category + 
								", Subcategory: " + subcategory + ", DeviceNum: " + deviceNum);

						if (dev.has("batterylevel")) {

							MotherSensor mController = new MotherSensor(category, subcategory, 
									deviceNum, "3-in-1 Sensor");
							VeraDevice.this.setMotherSensor(deviceNum, mController);	
							VeraSensorQueue.mSensorQueue.add(mController);
							
						} else if (dev.has("light")) {

							LightLevelSensor mLight = new LightLevelSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mLight.setLightLevel(Float.parseFloat(dev.getString("light")));
							VeraDevice.this.setLightSensor(deviceNum, mLight);
							VeraSensorQueue.mSensorQueue.add(mLight);

						} else if (dev.has("temperature")) {
							
							TemperatureSensor mTemperature = new TemperatureSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mTemperature.setTemperature(Float.parseFloat(dev.getString("temperature")));
							VeraDevice.this.setTemperatureSensor(deviceNum, mTemperature);
							VeraSensorQueue.mSensorQueue.add(mTemperature);

						} else {
							
							MotionSensor mMotion = new MotionSensor(category, subcategory, 
									deviceNum, "Motion Sensor", parent);
							VeraDevice.this.setMotionSensor(deviceNum, mMotion);	
							VeraSensorQueue.mSensorQueue.add(mMotion);
						}
					}
//					Log.d(TAG, "Inner Light Map key: " + VeraDevice.this.mLightMap.keySet());
//					Log.d(TAG, "Inner Temperature Map key: " + VeraDevice.this.mTemperatureMap.keySet());

				}  catch (MalformedURLException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
