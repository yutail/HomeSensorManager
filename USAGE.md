# Usage

## Import Service Files
At first, you should import all `.aidl` files and `Device class` java files into your Android application project. `Device class` java files include `RaritanDevice.java`, `VerisDevice.java`, `VeraDevice.java` and java files under the `VeraSensor` directory. These class files have all implemented `Parcelable` interface which can be used for passing objects over Android IPC. **IMPORTANT** These imported files in your application project should have the same package path as they are in the HomeSystem service.

## ISensorReportService Interface
This interface will return a `HomeSystem` object to the caller. This object contains all sensor information at home. API function is listed below:
* HomeSystem reportHomeSensor();

## IVeraService Interface
This interface is used to retrieve data from Vera devices through HTTP request. API functions are listed below:
* void startDataRetrieval(in int id);
* void stopDataRetrieval(in int id);
*	void setInterval(in int i);
*	void registerVeraCallback(IVeraServiceCallback vera_cb);
*	void unregisterVeraCallback(IVeraServiceCallback vera_cb);

## IVerisService Interface
This interface is used to receive data from Veris devices thourgh transmitting Modubs RTU packets over TCP. API fucntiona are listed below:
*	void startDataRetrieval();
*	void stopDataRetrieval();
*	void setInterval(in int i);
*	void setRegAddr(in int reg_addr);
*	void setRegQty(in int reg_qty);
*	void registerVerisCallback(IVerisServiceCallback veris_cb);
*	void unregisterVerisCallback(IVerisServiceCallback veris_cb);

## IRaritanService Interface
This interface is used to retrieve data from Raritan devices through Simple Network Management Protocol. API functions are listed below:
*	void startDataRetrieval(in int id);
*	void stopDataRetrieval(in int id);
*	void setInterval(in int i);
*	void setChannel(in String channel);
*	void registerRaritanCallback(IRaritanServiceCallback raritan_cb);
*	void unregisterRaritanCallback(IRaritanServiceCallback raritan_cb);

## Call IPC Methods
Here are the steps a calling class must implement to call a remote interface defined in AIDL:
1. Declare an instance of the IBinder interface (generated based on the AIDL).

2. Implement ServiceConnection.

3. Call `Context.bindService()`, passing in your ServiceConnection implementation.

4. In your implementation of `onServiceConnected()`, you will receive an IBinder instance (called service). Call `YourInterfaceName.Stub.asInterface((IBinder)service)` to cast the returned parameter to YourInterface type.

5. Call the methods that you defined on your interface.

6. To disconnect, call `Context.unbindService()` with the instance of your interface.

Code example for using IVeraService interface:
```
	private VeraService mVeraService = null;
	private VeraConnection mVeraConnection = null;
	
	/* Vera Service Connection */
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
	
	...
	
	/* Bind to the remote service */
	bindService(intent, mVeraConnection, Context.BIND_AUTO_CREATE);
	
	...
	
	/* Call IPC method after onServiceConnected() returns successfully */
	mVeraService.startDataRetrieval();
```


