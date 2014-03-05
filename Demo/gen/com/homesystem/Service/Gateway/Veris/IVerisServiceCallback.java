/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/Demo/src/com/homesystem/Service/Gateway/Veris/IVerisServiceCallback.aidl
 */
package com.homesystem.Service.Gateway.Veris;
public interface IVerisServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.Gateway.Veris.IVerisServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.Gateway.Veris.IVerisServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.Gateway.Veris.IVerisServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.Gateway.Veris.IVerisServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.Gateway.Veris.IVerisServiceCallback))) {
return ((com.homesystem.Service.Gateway.Veris.IVerisServiceCallback)iin);
}
return new com.homesystem.Service.Gateway.Veris.IVerisServiceCallback.Stub.Proxy(obj);
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
case TRANSACTION_updateVerisValue:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
this.updateVerisValue(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.Gateway.Veris.IVerisServiceCallback
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
@Override public void updateVerisValue(int[] value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(value);
mRemote.transact(Stub.TRANSACTION_updateVerisValue, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateVerisValue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void updateVerisValue(int[] value) throws android.os.RemoteException;
}
