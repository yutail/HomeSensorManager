package com.homesystem.Service.Gateway.Raritan;


import java.io.IOException;
import java.util.Arrays;
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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;

public class RaritanDevice extends SensorDevice implements DataRetrieval, Parcelable {
	// debugging
	private static final String TAG = "RaritanDevice";
	
	private String password;
	private String username;
	private int outletNum;
	private RaritanChannel channels;	
	private String channel;
	
	// Handling Threads
	private final int poolSize = 10;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private final int CHANNEL_NUM = 10;
	private boolean interruptFlag[] = new boolean[CHANNEL_NUM];
	private Object lock_interruptFlag = new Object();
	
	// Handling Message
	private Handler mHandler;
	
	// Use Builder Pattern to Instantiate 
	public static class RaritanBuilder {
		// Required Parameters 
		private String id;
		private String location;
		private String name;
		private String sensorType;
		private String ip_address;
		
		//Optional Parameters 
		private int interval = 1000;
		private String password = "abcd";
		private String username = "admin";
		private int port = 161;
		private int outletNum = 8;

		public RaritanBuilder(String loc, String name, String sensorType, String ip) {
			this.location = loc;
			this.name = name;
			this.sensorType = sensorType;
			this.ip_address = ip;			
		}

		public RaritanBuilder setInterval(int val) {
			this.interval = val;
			return this;
		}
		
		public RaritanBuilder setUsername(String s) {
			this.username = s;
			return this;
		}
		
		public RaritanBuilder setPassword(String p) {
			this.password = p;
			return this;
		}
		
		public RaritanBuilder setPort(int p) {	
			this.port = p;
			return this;
		}
		
		public RaritanBuilder setOutletNum(int n) {
			this.port = n;
			return this;
		}

		public RaritanDevice build() {
			return new RaritanDevice(this);
		}
	}

	private RaritanDevice(RaritanBuilder builder) {
		this.location = builder.location;
		this.ip_address = builder.ip_address;
		this.interval = builder.interval;
		this.name = builder.name;
		this.password = builder.password;
		this.port = builder.port;
		this.outletNum = builder.outletNum;	
		this.username = builder.username;
		this.sensorType = builder.sensorType;
	}
	
	public void setUsername(String u) {
		this.username = u;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setPassword(String p) {
		this.password = p;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public synchronized void setInterruptFlag(boolean flag, int id) {
		this.interruptFlag[id] = flag;
	}
	
	public synchronized boolean getInterruptFlag(int id) {
		return interruptFlag[id];
	}
	
	public synchronized void setChannel(String c) {
		this.channel = c;
	}
	
	public synchronized String getChannel() {
		return this.channel;
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	private class RaritanSNMPManager implements Runnable {
		
		private String client_address = null;
		private String client_password = null;
		private int client_outlet;
		private String client_channel;
		private Snmp snmp = null;
		private String targetIOD;
		
		public RaritanSNMPManager(String ip_addr, int portNum, int outlet, 
				String pwd, String chanl) {
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
			if (client_channel.equals(Constant.RARITAN_VOLTAGE))
				targetIOD = RaritanOID.voltageOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_ACTIVE_POWER))
				targetIOD = RaritanOID.activePowerOID[client_outlet-1];
			else if (client_channel.equals(Constant.RARITAN_CURRENT))
				targetIOD = RaritanOID.currentOID[client_outlet-1];
		}

		@Override
		public void run() {
			while (true) {
				if (Thread.interrupted()) {
					// Send the name of the connected device back to the UI Activity
			        Message msg = mHandler.obtainMessage(Constant.RARITAN_MESSAGE);
			        Bundle bundle = new Bundle();
			        bundle.putString(Constant.RARITAN_CHANNEL, client_channel);
			        bundle.putInt(Constant.RARITAN_OUTLET, client_outlet);
			        bundle.putString(Constant.RARITAN_VALUE, "");
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);
					return;
				}
				
				try {
					start();
					String result = getAsString(new OID(targetIOD));
					// Send the name of the connected device back to the UI Activity
			        Message msg = mHandler.obtainMessage(Constant.RARITAN_MESSAGE);
			        Bundle bundle = new Bundle();
			        bundle.putString(Constant.RARITAN_CHANNEL, client_channel);
			        bundle.putInt(Constant.RARITAN_OUTLET, client_outlet);
			        bundle.putString(Constant.RARITAN_VALUE, result);
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
		
		public String getAsString(OID oid) throws IOException {
			ResponseEvent event = get(new OID[] { oid });
			return event.getResponse().get(0).getVariable().toString();
		}

		public ResponseEvent get(OID oids[]) throws IOException {
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
	
	
	@Override
	public void subscribeToSensor(int... id) {
//		setInterruptFlag(false, id);
//		threadPool.execute(new RaritanSNMPManager(ip_address,
//				port, id, password, channel));	
		
	}

	@Override
	public void unsubscribeFromSensor(int... id) {
//		setInterruptFlag(true, id);
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(location);
		dest.writeString(ip_address);
		dest.writeString(sensorType);
		dest.writeString(username);
		dest.writeString(password);
		dest.writeInt(interval);
		dest.writeInt(port);
		dest.writeInt(outletNum);
	}
	
	public static final Parcelable.Creator<RaritanDevice> CREATOR
	= new Parcelable.Creator<RaritanDevice>() {
		public RaritanDevice createFromParcel(Parcel in) {
			return new RaritanDevice(in);
		}

		public RaritanDevice[] newArray(int size) {
			return new RaritanDevice[size];
		}
	};

	private RaritanDevice(Parcel in) {
		name = in.readString();
		location = in.readString();
		ip_address = in.readString();
		sensorType = in.readString();
		username = in.readString();
		password = in.readString();
		interval = in.readInt();
		port = in.readInt();
		outletNum = in.readInt();
	}	
	

}
