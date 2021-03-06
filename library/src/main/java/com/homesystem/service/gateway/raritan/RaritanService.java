package com.homesystem.Service.Gateway.Raritan;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.homesystem.Service.Constant;
import com.homesystem.Service.HomeSystem;

import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;
import com.homesystem.Service.Gateway.Raritan.RaritanDevice;
import com.homesystem.Service.Gateway.Vera.IVeraService;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class RaritanService extends Service implements DataRetrieval {
	// debugging 
	private static final String TAG = "RaritanService";

	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private RaritanDevice raritan = null;
	private String devName;
	private int outletNum;
	
	// Handling Threads
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private final int CHANNEL_NUM = 10;
	private boolean interruptFlag[] = new boolean[CHANNEL_NUM];
	private Object lock_interruptFlag = new Object();
	
	// Sampling Interval
	private int interval = 10;
	private String channel;
	
	private RemoteCallbackList<IRaritanServiceCallback> mRaritanCallbackList = 
			new RemoteCallbackList<IRaritanServiceCallback>();
	
	// Hanlding Messages
	private static final int REPORT_MSG = 1;
	private static final String RARITAN_VALUE = "raritan_value";
	private static final String RARITAN_CHANNEL = "raritan_channel";
	private static final String RARITAN_OUTLET = "raritan_outlet";
	
	public synchronized void setInterruptFlag(boolean flag, int id) {
		this.interruptFlag[id] = flag;
	}
	
	public synchronized boolean getInterruptFlag(int id) {
		return interruptFlag[id];
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "RaritanService onBind");
		outletNum = intent.getIntExtra(Constant.EXTRA_DEVICE_ID, -1);
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		raritan = (RaritanDevice) devByName.get(devName);
		return mBinder;
	}
	
	private final IRaritanService.Stub mBinder = new IRaritanService.Stub() {
		@Override
		public void startDataRetrieval(int id) throws RemoteException {
			subscribeToSensor(id);	
		}
		
		@Override
		public void stopDataRetrieval(int id) throws RemoteException {
			unsubscribeFromSensor(id);
		}
		
		@Override
		public void setInterval(int i) throws RemoteException {
			interval = i;
		}
		
		@Override
		public void setChannel(String channel) throws RemoteException {
			RaritanService.this.channel = channel;
		}

		@Override
		public void registerRaritanCallback(IRaritanServiceCallback raritan_cb)
				throws RemoteException {
			mRaritanCallbackList.register(raritan_cb);
			
		}

		@Override
		public void unregisterRaritanCallback(IRaritanServiceCallback raritan_cb)
				throws RemoteException {
			mRaritanCallbackList.unregister(raritan_cb);
			
		}
	};
	
	// Handler
	private final Handler mHandler = new Handler() {
		@Override 
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case REPORT_MSG: {
				String raritan_value = msg.getData().getString(RARITAN_VALUE);
				String channel = msg.getData().getString(RARITAN_CHANNEL);
				
				final int num = mRaritanCallbackList.beginBroadcast();
                Log.d(TAG, "Number of Clients: " + num);
                try {
                	mRaritanCallbackList.getBroadcastItem(num-1).updateRaritanValue(raritan_value, channel);
                } catch (RemoteException e) {
                	e.printStackTrace();
                }
                mRaritanCallbackList.finishBroadcast();
				
			} break;
			
			default:
				super.handleMessage(msg);
			}
			
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
        Log.d(TAG, "RaritanService onDestroy"); 
	}

	@Override
	public void subscribeToSensor(int... id) {
		setInterruptFlag(false, id[0]);
		threadPool.execute(new RaritanSNMPManager(raritan.getIp(),
				raritan.getPort(), id[0], 
				raritan.getPassword(), channel,
				interval));		 	
	}

	@Override
	public void unsubscribeFromSensor(int... id) {
		setInterruptFlag(true, id[0]);		
	}
	
	private class RaritanSNMPManager implements Runnable {
		
		private String client_address = null;
		private String client_password = null;
		private int client_outlet;
		private String client_channel;
		private Snmp snmp = null;
		private String targetIOD;
		private int interval;
		
		public RaritanSNMPManager(String ip_addr, int portNum, int outlet, 
				String pwd, String chanl, int interval) {
			// Address format: udp:172.17.5.174/161
			StringBuffer sb = new StringBuffer();
			sb.append("udp:");
			sb.append(ip_addr);
			sb.append("/");
			sb.append(portNum);
			client_address = sb.toString();
			client_outlet = outlet;
			client_password = pwd;
			client_channel = chanl;
			this.interval = interval;
			if (client_channel.equals(Constant.RARITAN_VOLTAGE))
				targetIOD = RaritanOID.voltageOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_ACTIVE_POWER))
				targetIOD = RaritanOID.activePowerOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_CURRENT))
				targetIOD = RaritanOID.currentOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_APPARENT_POWER))
				targetIOD = RaritanOID.apparentPowerOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_POWER_FACTOR))
				targetIOD = RaritanOID.powerFactorIOD[client_outlet-1];
		}

		@Override
		public void run() {
			while (true) {
				if (Thread.interrupted()) {
					return;
				}
				try {
					start();
					String result = getAsString(new OID(targetIOD));
					// Send the name of the connected device back to the UI Activity
			        Message msg = mHandler.obtainMessage(REPORT_MSG);
			        Bundle bundle = new Bundle();
			        bundle.putString(RARITAN_CHANNEL, client_channel);
			        bundle.putInt(RARITAN_OUTLET, client_outlet);
			        bundle.putString(RARITAN_VALUE, result);
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);
					Log.d(TAG, result);
					
					Thread.sleep(interval*1000);
					if(getInterruptFlag(client_outlet)) 
						Thread.currentThread().interrupt();
				
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void start() throws IOException {
			TransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();
		}
		
		private String getAsString(OID oid) throws IOException {
			ResponseEvent event = get(new OID[] { oid });
			return event.getResponse().get(0).getVariable().toString();
		}

		private ResponseEvent get(OID oids[]) throws IOException {
		    PDU pdu = new PDU();
		    for (OID oid : oids) {
		    	pdu.add(new VariableBinding(oid));
		    }
		    
		    pdu.setType(PDU.GET);
		    ResponseEvent event = snmp.send(pdu, getTarget(), null);
		    
		    if(event != null) {
		    	return event;
		    } throw new RuntimeException("GET timed out");
		}
		
		private CommunityTarget getTarget() {
		    Address targetAddress = GenericAddress.parse(client_address);
		    CommunityTarget target = new CommunityTarget();
		    target.setCommunity(new OctetString(client_password));
		    target.setAddress(targetAddress);
		    target.setRetries(2);
		    target.setTimeout(1500);
		    target.setVersion(SnmpConstants.version2c);
		    return target;
		}	
	}

}
