package com.homesystem.Service.Gateway.Veris;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.homesystem.Service.HomeSystem;
import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class VerisService extends Service implements DataRetrieval {
	// debugging
	private static final String TAG = "VerisService";
	
	// Home Sensor System
	private HomeSystem myHomeSystem;
	private HashMap<String, SensorDevice> devByName;
	private VerisDevice veris = null;
	private String devName;

	// Handling Threads
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private boolean interruptFlag = false;
	
	private RemoteCallbackList<IVerisServiceCallback> mVerisCallbackList =
			new RemoteCallbackList<IVerisServiceCallback>();
	
	// Sampling Interval 
	private int interval = 10;
	// Handling Message
	private static final int REPORT_MSG = 1;
	private static final String VERIS_VALUE = "veris_value";
	
	public void setInterval(int i) {	
		this.interval = i;
	}
	
	public int getInterval() {	
		return this.interval;
	}
	
	public synchronized void setFlag(boolean flag) {
		this.interruptFlag = flag;
	}
	
	public synchronized boolean getFlag() {
		return interruptFlag;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "VerisService onBind");
		devName = intent.getStringExtra(Constant.EXTRA_DEVICE_NAME);
		veris = (VerisDevice) devByName.get(devName);
		return mBinder;
	}
	
	
	private final IVerisService.Stub mBinder = new IVerisService.Stub() {
		@Override
		public void startDataRetrieval() throws RemoteException {
			subscribeToSensor();	
		}
		
		public void stopDataRetrieval() throws RemoteException {
			unsubscribeFromSensor();
		}
		
		public void setInterval(int i) throws RemoteException {
			interval = i;
		}

		@Override
		public void registerVerisCallback(IVerisServiceCallback veris_cb)
				throws RemoteException {
			if (veris_cb != null)
				mVerisCallbackList.register(veris_cb);	
		}

		@Override
		public void unregisterVerisCallback(IVerisServiceCallback veris_cb)
				throws RemoteException {
			if (veris_cb != null)
				mVerisCallbackList.unregister(veris_cb);			
		}
	};
	
	// Handler
	private final Handler mHandler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch(msg.what) {
			case REPORT_MSG: {
				int[] veris_value = new int[veris.getRegQty()];
				veris_value = msg.getData().getIntArray(VERIS_VALUE);
				
				// Broadcast to client the new value.
                final int num = mVerisCallbackList.beginBroadcast();
                Log.d(TAG, "Number of Clients: " + num);
                try {
                	mVerisCallbackList.getBroadcastItem(num-1).updateVerisValue(veris_value);
                } catch (RemoteException e) {
                	e.printStackTrace();
                }
			
                mVerisCallbackList.finishBroadcast();
			} break;
			
			default:
				super.handleMessage(msg);		
			
			}
		}		
	};
	
	@Override 
    public void onCreate() { 
		super.onCreate();  
		Log.d(TAG, "VerisService onCreate");
		myHomeSystem = HomeSystem.getInstance();
		devByName = myHomeSystem.getHomeSensors();	
    } 
	
	@Override 
    public void onDestroy() { 
		Log.d(TAG, "VerisService OnDestroy");
		unsubscribeFromSensor();
		super.onDestroy();	
	}

	@Override
	public void subscribeToSensor(int... id) {
		Log.d(TAG, "Subscribe to VerisDevice");
		setFlag(false);	
		String ip_address = veris.getIp();
		int port = veris.getPort();
		int modbus_addr = veris.getModbusAddr();
		int modbus_reg_addr = veris.getRegAddr();
		int modbus_reg_qty = veris.getRegQty();
		int func = veris.getModbusFunc();
		
		try {
			threadPool.execute(new ModbusRTUClient(ip_address, 
					port, modbus_addr, modbus_reg_addr, 
					modbus_reg_qty, func, interval));
				
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public void unsubscribeFromSensor(int... id) {
		Log.d(TAG, "Unsubscribe from VerisDevice");
		setFlag(true);		
	}
	
	private class ModbusRTUClient implements Runnable {
		
		private Socket socket = null;
		private InetAddress server_addr;
		private int server_port;
		private int server_mod_addr;
		private int server_reg_addr;
		private int server_reg_qty;
		private int server_func;
		private int interval;
		private int[] tempBuf;
		
		private byte[] txBuf;
		private byte[] rxBuf;
		private int[] result;
		private static final int SNDBUFSIZE = 8;
		private static final int RCVBUFSIZE = 1024;
		private static final int BYTEMASK = 0xFF;
		// 1 byte for modbus address, 1 byte for modbus func code
		// 1 byte for register quantity, 2 byte for crc16
		private int RCVBYTESNUM = 5;
		
		public ModbusRTUClient(String ip, int p, int m, int r, 
				int q, int func, int interval) throws UnknownHostException {	
			server_addr = InetAddress.getByName(ip);
			server_port = p;
			server_mod_addr = m;
			server_reg_addr = r;
			server_reg_qty = q;		
			server_func = func;
			this.interval = interval;
			
			tempBuf = new int[SNDBUFSIZE];
			tempBuf[0] = server_mod_addr;
			tempBuf[1] = server_func;
			tempBuf[2] = higherByte(server_reg_addr);
			tempBuf[3] = lowerByte(server_reg_addr);
			tempBuf[4] = higherByte(server_reg_qty);
			tempBuf[5] = lowerByte(server_reg_qty);
			// Calculate CRC16
			int crc = cala_crc16(tempBuf, SNDBUFSIZE-2);	
			tempBuf[SNDBUFSIZE-2] = lowerByte(crc);
			tempBuf[SNDBUFSIZE-1] = higherByte(crc);	
			txBuf = new byte[SNDBUFSIZE];	
			encodeIntArray(txBuf, tempBuf);	
			result = new int[server_reg_qty];	
			RCVBYTESNUM += 2*server_reg_qty;
		}

		@Override
		public void run() {
			
			while(true) {
				if (Thread.interrupted()) {
					return;
				}
				
				try {
					socket = new Socket(server_addr, server_port);
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();
					
					Log.d(TAG, "Transmitted Bytes: " + Arrays.toString(tempBuf));
					out.write(txBuf);
					int bytesRcvd = 0;
					int totalBytesRcvd = 0;
					rxBuf = new byte[RCVBUFSIZE];
					
					while (totalBytesRcvd < RCVBYTESNUM) {
						if ((bytesRcvd=in.read(rxBuf, totalBytesRcvd, rxBuf.length-totalBytesRcvd)) == -1)
							throw new SocketException("Socket Closed");
						totalBytesRcvd += bytesRcvd;				
					}
					
					Log.d(TAG, "Total Bytes Received: " + totalBytesRcvd);
					decodeByteArray(result, rxBuf);
					Log.d(TAG, "Received Bytes: " + Arrays.toString(result));
					
			        Message msg = mHandler.obtainMessage(REPORT_MSG);
			        Bundle bundle = new Bundle();
			        bundle.putIntArray(VERIS_VALUE, result);			       
			        msg.setData(bundle);
			        mHandler.sendMessage(msg);	
					
					socket.close();
					Thread.sleep(interval*1000);
					if (VerisService.this.getFlag())
						Thread.currentThread().interrupt();
					
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}
		
		// Change int array to byte array
		private int encodeIntArray(byte[] dst, int[] src) {
			for (int i=0; i<src.length; i++) {
				dst[i] = (byte) src[i];
			}
			return 0;			
		}
		
		// Change byte array to int array
		private int decodeByteArray(int[] dst, byte[] src) {
			int len = RCVBYTESNUM;
			int temp[] = new int[len];
			for (int i=0; i<len; i++) {
				temp[i] = src[i] & BYTEMASK;
			}
			int index = 0;
			for (int i=3; i<temp.length-2; i=i+2) {
				String hi = Integer.toHexString(temp[i]);
				String lo = Integer.toHexString(temp[i+1]);
				dst[index++] = Integer.parseInt(hi+lo, 16);
			}
			return 0;
		}
		
		private int higherByte(int n) {
			
			return ((n & 0xff00) >> 8);
		}
		
		private int lowerByte(int n) {
			
			return (n & 0x00ff);
		}
		
		private int cala_crc16(int[] message, int size) {		

			int crcHi = 0xff;
			int crcLo = 0xff;
			int index;

			for (int i=0; i<size; i++) {
				index = crcLo ^ (int) message[i];
				crcLo = crcHi ^ aCRCHi[index];
				crcHi = aCRCLo[index];
			}
			return (crcHi<<8 | crcLo);
		}	
	}

	/* Table of CRC values for highorder byte */
	private final static int aCRCHi[] = {
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 
		0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 
		0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 
		0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 
		0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 
		0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 
		0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 
		0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 
		0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 
		0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 
		0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01,
		0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 
		0x40
	};
	
	
	/* Table of CRC values for loworder byte */
	private static final int aCRCLo[] = {
		0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4,
		0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
		0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD,
		0x1D, 0x1C, 0xDC, 0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
		0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7,
		0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
		0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE,
		0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
		0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2,
		0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
		0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB,
		0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
		0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50, 0x90, 0x91,
		0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
		0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88,
		0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
		0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80,
		0x40
	};	

}
