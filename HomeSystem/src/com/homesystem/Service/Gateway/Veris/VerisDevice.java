package com.homesystem.Service.Gateway.Veris;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.homesystem.Demo.Constant;
import com.homesystem.Service.Gateway.DataRetrieval;
import com.homesystem.Service.Gateway.SensorDevice;

public class VerisDevice extends SensorDevice implements Parcelable {
	// debugging
	private static final String TAG = "VerisDevice";
	
	/* Modbus function codes supported by E30 */
	private static final int MODBUS_FUNC_READ_REG = 0x03;
	private static final int MODBUS_FUNC_WRITE_REG = 0x06;
	private static final int MODBUS_FUNC_WRITE_MULTIREG = 0x10;
	private static final int MODBUS_FUNC_REPORT_SLAVEID = 0x11;
	
	/* Modbus parameter */
	private static final int MODBUS_REG_READ_QTY_DEFUALT = 1;
	private static final int MODBUS_REG_READ_QTY_MIN = 1;
	private static final int MODBUS_REF_READ_QTY_MAX = 125;
	private static final int MODBUS_PORT_NUM_DEFAULT = 4660;
	
//	private VerisChannel channels;
	private int modbus_addr;
	private int modbus_func;
	private int modbus_reg_addr;
	private int modbus_reg_qty;
	
	// Use Builder Pattern to Instantiate 
	public static class VerisBuilder {
		// Required Parameters 
		private String id;
		private String location;
		private String name;
		private String sensorType;
		private String ip_address;
		private int modbus_reg_addr;

		//Optional Parameters 
		private int interval = 10;
		private int port = 4660;
		private int modbus_addr = 1;
		private int modbus_reg_qty = 1;
		private int modbus_func = 0x03;

		public VerisBuilder(String loc, String name, String sensorType, String ip, int reg_addr) {
			this.location = loc;
			this.name = name;
			this.sensorType = sensorType;
			this.ip_address = ip;
			this.modbus_reg_addr = reg_addr;
		}

		public VerisBuilder setInterval(int val) {
			this.interval = val;
			return this;
		}

		public VerisBuilder setPort(int p) {
			this.port = p;
			return this;
		}
		
		public VerisBuilder setModbusAddr(int addr) {
			this.modbus_addr = addr;
			return this;
		}
		
		public VerisBuilder setModbusFunc(int func) {
			this.modbus_func = func;
			return this;
		}
		
		public VerisBuilder setRegQty(int reg_qty) {
			this.modbus_reg_qty = reg_qty;
			return this;
		}
		
		public VerisDevice build() {
			return new VerisDevice(this);
		}
	}

	private VerisDevice(VerisBuilder builder) {
		this.location = builder.location;
		this.ip_address = builder.ip_address;
		this.interval = builder.interval;
		this.name = builder.name;
		this.port = builder.port;
		this.sensorType = builder.sensorType;
		this.modbus_addr = builder.modbus_addr;
		this.modbus_func = builder.modbus_func;
		this.modbus_reg_addr = builder.modbus_reg_addr;
		this.modbus_reg_qty = builder.modbus_reg_qty;
	}
	
	public void setModbusAddr(int addr) {
		modbus_addr = addr;
	}

	public int getModbusAddr() {
		return modbus_addr;
	}

	public void setModbusFunc(int func) {
		modbus_func = func;
	}

	public int getModbusFunc() {
		return modbus_func;
	}

	public void setRegAddr(int reg_addr) {
		modbus_reg_addr = reg_addr;
	}

	public int getRegAddr() {
		return modbus_reg_addr;
	}

	public void setRegQty(int reg_qty) {
		modbus_reg_qty = reg_qty;
	}

	public int getRegQty() {
		return modbus_reg_qty;
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
		dest.writeInt(modbus_reg_addr);
		dest.writeInt(interval);
		dest.writeInt(port);
		dest.writeInt(modbus_addr);
		dest.writeInt(modbus_reg_qty);
		dest.writeInt(modbus_func);		
	}
	
	public static final Parcelable.Creator<VerisDevice> CREATOR
	= new Parcelable.Creator<VerisDevice>() {
		public VerisDevice createFromParcel(Parcel in) {
			return new VerisDevice(in);
		}

		public VerisDevice[] newArray(int size) {
			return new VerisDevice[size];
		}
	};

	private VerisDevice(Parcel in) {
		name = in.readString();
		location = in.readString();
		ip_address = in.readString();
		sensorType = in.readString();
		modbus_reg_addr = in.readInt();
		interval = in.readInt();
		port = in.readInt();
		modbus_addr = in.readInt();
		modbus_reg_qty = in.readInt();
		modbus_func = in.readInt();		
	}
	
}
