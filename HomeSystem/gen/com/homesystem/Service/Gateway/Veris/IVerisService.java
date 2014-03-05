/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/HomeSystem/src/com/homesystem/Service/Gateway/Veris/IVerisService.aidl
 */
package com.homesystem.Service.Gateway.Veris;
public interface IVerisService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.Gateway.Veris.IVerisService
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.Gateway.Veris.IVerisService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.Gateway.Veris.IVerisService interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.Gateway.Veris.IVerisService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.Gateway.Veris.IVerisService))) {
return ((com.homesystem.Service.Gateway.Veris.IVerisService)iin);
}
return new com.homesystem.Service.Gateway.Veris.IVerisService.Stub.Proxy(obj);
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
case TRANSACTION_startDataRetrieval:
{
data.enforceInterface(DESCRIPTOR);
this.startDataRetrieval();
reply.writeNoException();
return true;
}
case TRANSACTION_setInterval:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setInterval(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerVerisCallback:
{
data.enforceInterface(DESCRIPTOR);
com.homesystem.Service.Gateway.Veris.IVerisServiceCallback _arg0;
_arg0 = com.homesystem.Service.Gateway.Veris.IVerisServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerVerisCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterVerisCallback:
{
data.enforceInterface(DESCRIPTOR);
com.homesystem.Service.Gateway.Veris.IVerisServiceCallback _arg0;
_arg0 = com.homesystem.Service.Gateway.Veris.IVerisServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterVerisCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.Gateway.Veris.IVerisService
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
@Override public void startDataRetrieval() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startDataRetrieval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setInterval(int i) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(i);
mRemote.transact(Stub.TRANSACTION_setInterval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerVerisCallback(com.homesystem.Service.Gateway.Veris.IVerisServiceCallback veris_cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((veris_cb!=null))?(veris_cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerVerisCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterVerisCallback(com.homesystem.Service.Gateway.Veris.IVerisServiceCallback veris_cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((veris_cb!=null))?(veris_cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterVerisCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startDataRetrieval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerVerisCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterVerisCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void startDataRetrieval() throws android.os.RemoteException;
public void setInterval(int i) throws android.os.RemoteException;
public void registerVerisCallback(com.homesystem.Service.Gateway.Veris.IVerisServiceCallback veris_cb) throws android.os.RemoteException;
public void unregisterVerisCallback(com.homesystem.Service.Gateway.Veris.IVerisServiceCallback veris_cb) throws android.os.RemoteException;
}
