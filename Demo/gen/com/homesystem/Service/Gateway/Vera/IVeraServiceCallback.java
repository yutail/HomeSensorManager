/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/Demo/src/com/homesystem/Service/Gateway/Vera/IVeraServiceCallback.aidl
 */
package com.homesystem.Service.Gateway.Vera;
public interface IVeraServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.Gateway.Vera.IVeraServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.Gateway.Vera.IVeraServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.Gateway.Vera.IVeraServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.Gateway.Vera.IVeraServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.Gateway.Vera.IVeraServiceCallback))) {
return ((com.homesystem.Service.Gateway.Vera.IVeraServiceCallback)iin);
}
return new com.homesystem.Service.Gateway.Vera.IVeraServiceCallback.Stub.Proxy(obj);
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
case TRANSACTION_updateVeraValue:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.updateVeraValue(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.Gateway.Vera.IVeraServiceCallback
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
@Override public void updateVeraValue(java.lang.String value, java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_updateVeraValue, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateVeraValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void updateVeraValue(java.lang.String value, java.lang.String tag) throws android.os.RemoteException;
}
