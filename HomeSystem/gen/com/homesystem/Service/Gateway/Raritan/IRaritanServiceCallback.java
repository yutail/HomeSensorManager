/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/HomeSystem/src/com/homesystem/Service/Gateway/Raritan/IRaritanServiceCallback.aidl
 */
package com.homesystem.Service.Gateway.Raritan;
public interface IRaritanServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback))) {
return ((com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback)iin);
}
return new com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_updateRaritanValue:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.updateRaritanValue(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void updateRaritanValue(java.lang.String value, java.lang.String channel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
_data.writeString(channel);
mRemote.transact(Stub.TRANSACTION_updateRaritanValue, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateRaritanValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void updateRaritanValue(java.lang.String value, java.lang.String channel) throws android.os.RemoteException;
}
