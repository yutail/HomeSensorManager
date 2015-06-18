package com.homesystem.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
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
	private HashMap<String, SensorDevice> sensorByName = null;
	
	private String vera_url = null;
	private String vera_ip;
	// Used for generating URL
	private String request[] = {"user_data", "status", "sdata"};
	private String format = "output_format=json";
	
	// Handling Threads
	private ExecutorService threadPool = Executors.newCachedThreadPool();	

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "OnBind");
		return mBinder;
	}
	
	private final ISensorReportService.Stub mBinder = new ISensorReportService.Stub() {
		@Override
		public HomeSystem reportHomeSensor() throws RemoteException {
			
			InputStream vera_in = null;
			try {
				vera_in = getAssets().open("DeviceToRegister/Vera.json");
			} catch (IOException e) {
				e.printStackTrace();
			}
			VeraDevice vera = registerVeraDevice(vera_in);		
			myHomeSystem.addDevicesByName("vera1", vera);
			
			InputStream veris_in = null;
			try {
				veris_in = getAssets().open("DeviceToRegister/Veris.json");
			} catch (IOException e) {
				e.printStackTrace();
			}
			VerisDevice veris = registerVerisDevice(veris_in);
			myHomeSystem.addDevicesByName("veris1", veris);
			
			InputStream raritan_in = null;
			try {
				raritan_in = getAssets().open("DeviceToRegister/Raritan.json");
			} catch (IOException e) {
				e.printStackTrace();
			}
			RaritanDevice raritan = registerRaritanDevice(raritan_in);
			myHomeSystem.addDevicesByName("raritan1", raritan);
			
			return myHomeSystem;		
		}
	};
	
	VeraDevice registerVeraDevice(InputStream in) {
		String name = null;
		String loc = null;
		String type = null;
		String ip = null;
		int port = 0;
		
		try {
			int size = in.available();
			byte[] read_buf = new byte[size];
	        in.read(read_buf);
	        in.close();
	        String vera_string = new String(read_buf);
			JSONObject vera_json = new JSONObject(vera_string);
			name = vera_json.getString("name");
			loc = vera_json.getString("location");
			type = vera_json.getString("type");
			ip = vera_json.getString("ip");
			vera_ip = ip;
			port = Integer.parseInt(vera_json.getString("port"));	

		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		VeraDevice v = new VeraDevice.VeraBuilder(loc, name, type, ip).
				setPort(port).build();	
		
		vera_url = generateURL(request[2], format, v);
		getVeraDeviceInfo(v);	
		
		return v;	
	}
	
	VerisDevice registerVerisDevice(InputStream in) {
		String name = null;
		String loc = null;
		String type = null;
		String ip = null;
		int port = 0;
		int reg_addr = 0;
		int reg_qty = 0;
		
		try {
			int size = in.available();
			byte[] read_buf = new byte[size];
	        in.read(read_buf);
	        in.close();
	        String veris_string = new String(read_buf);
			JSONObject veris_json = new JSONObject(veris_string);
			name = veris_json.getString("name");
			loc = veris_json.getString("location");
			type = veris_json.getString("type");
			ip = veris_json.getString("ip");
			port = Integer.parseInt(veris_json.getString("port"));	
			reg_addr = Integer.parseInt(veris_json.getString("register address"));
			reg_qty = Integer.parseInt(veris_json.getString("register quantity"));
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "Port Num: " + port);
		
		VerisDevice v = new VerisDevice.VerisBuilder(loc, name, type, 
				ip, reg_addr).setPort(port).setRegQty(reg_qty).build();
		
		return v;		
	}
	
	RaritanDevice registerRaritanDevice(InputStream in) {
		String name = null;
		String loc = null;
		String type = null;
		String ip = null;
		String username = null;
		String password = null;
		int port = 0;
		
		try {
			int size = in.available();
			byte[] read_buf = new byte[size];
	        in.read(read_buf);
	        in.close();
	        String raritan_string = new String(read_buf);
			JSONObject raritan_json = new JSONObject(raritan_string);
			name = raritan_json.getString("name");
			loc = raritan_json.getString("location");
			type = raritan_json.getString("type");
			ip = raritan_json.getString("ip");
			port = Integer.parseInt(raritan_json.getString("port"));	
			username = raritan_json.getString("username");
			password = raritan_json.getString("password");
		} catch (IOException e) {

			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		RaritanDevice r = new RaritanDevice.RaritanBuilder(loc, name, type, ip).
				setPort(port).setUsername(username).
				setPassword(password).build();
		
		return r;		
	}
	
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
	
	public void getVeraDeviceInfo(VeraDevice vera) {	
		
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// HTTP connection
					URL targetURL = new URL(vera_url);
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
						float lasttrip = Float.parseFloat(dev.getString("lasttrip"));
						Log.d(TAG, "Name: " + name + ", Category: " + category + 
								", Subcategory: " + subcategory + ", DeviceNum: " + deviceNum);

						if (dev.has("batterylevel")) {

							MotherSensor mController = new MotherSensor(category, subcategory, 
									deviceNum, "3-in-1 Sensor");
							//vera.setMotherSensor(deviceNum, mController);	
							VeraSensorQueue.mSensorQueue.add(mController);
							
						} else if (dev.has("light")) {

							LightLevelSensor mLight = new LightLevelSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mLight.setLightLevel(Float.parseFloat(dev.getString("light")));
							//vera.setLightSensor(deviceNum, mLight);
							VeraSensorQueue.mSensorQueue.add(mLight);

						} else if (dev.has("temperature")) {
							
							TemperatureSensor mTemperature = new TemperatureSensor(category, subcategory, 
									deviceNum, "Temperature Sensor", parent);
							mTemperature.setTemperature(Float.parseFloat(dev.getString("temperature")));
							//vera.setTemperatureSensor(deviceNum, mTemperature);
							VeraSensorQueue.mSensorQueue.add(mTemperature);

						} else {
							
							MotionSensor mMotion = new MotionSensor(category, subcategory, 
									deviceNum, "Motion Sensor", parent);
							mMotion.setLasttrip(lasttrip);
							DecimalFormat df = new DecimalFormat("##########");
							Log.d(TAG, "Lasttrip: " + df.format(lasttrip));
							//vera.setMotionSensor(deviceNum, mMotion);	
							VeraSensorQueue.mSensorQueue.add(mMotion);
						}
					}

				}  catch (MalformedURLException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			int size = VeraSensorQueue.mSensorSize.take();
			Log.d(TAG, "Number of Sensors: " + size);
			for (int i=0; i<size; i++) {
				MotherSensor mSensor = VeraSensorQueue.mSensorQueue.take();
				if (mSensor instanceof TemperatureSensor)
					vera.setTemperatureSensor(mSensor.getDeviceNum(), (TemperatureSensor) mSensor);
				else if (mSensor instanceof LightLevelSensor)
					vera.setLightSensor(mSensor.getDeviceNum(), (LightLevelSensor) mSensor);
				else if (mSensor instanceof MotionSensor)
					vera.setMotionSensor(mSensor.getDeviceNum(), (MotionSensor) mSensor);	
				else
					vera.setMotherSensor(mSensor.getDeviceNum(), mSensor);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String generateURL(String r, String f, VeraDevice vera) {
		// Format of Requested URL: http://ip_address:3480/data_request?id=sdata&output_format=json
		StringBuilder result = new StringBuilder();
		result.append("http://");
		result.append(vera.getIp());
		result.append(":");
		result.append(vera.getPort());
		result.append("/data_request?");
		result.append("id=");
		result.append(r);
		result.append("&");
		result.append(f);
		return result.toString();
	}

}
