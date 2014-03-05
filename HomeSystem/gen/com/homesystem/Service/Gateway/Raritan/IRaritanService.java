/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/HomeSystem/src/com/homesystem/Service/Gateway/Raritan/IRaritanService.aidl
 */
package com.homesystem.Service.Gateway.Raritan;
public interface IRaritanService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.Gateway.Raritan.IRaritanService
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.Gateway.Raritan.IRaritanService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.Gateway.Raritan.IRaritanService interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.Gateway.Raritan.IRaritanService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.Gateway.Raritan.IRaritanService))) {
return ((com.homesystem.Service.Gateway.Raritan.IRaritanService)iin);
}
return new com.homesystem.Service.Gateway.Raritan.IRaritanService.Stub.Proxy(obj);
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
int _arg0;
_arg0 = data.readInt();
this.startDataRetrieval(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopDataRetrieval:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.stopDataRetrieval(_arg0);
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
case TRANSACTION_registerRaritanCallback:
{
data.enforceInterface(DESCRIPTOR);
com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback _arg0;
_arg0 = com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerRaritanCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterRaritanCallback:
{
data.enforceInterface(DESCRIPTOR);
com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback _arg0;
_arg0 = com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterRaritanCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.Gateway.Raritan.IRaritanService
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
@Override public void startDataRetrieval(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_startDataRetrieval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopDataRetrieval(int id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(id);
mRemote.transact(Stub.TRANSACTION_stopDataRetrieval, _data, _reply, 0);
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
@Override public void registerRaritanCallback(com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback raritan_cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((raritan_cb!=null))?(raritan_cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerRaritanCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterRaritanCallback(com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback raritan_cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((raritan_cb!=null))?(raritan_cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterRaritanCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startDataRetrieval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopDataRetrieval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_registerRaritanCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_unregisterRaritanCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void startDataRetrieval(int id) throws android.os.RemoteException;
public void stopDataRetrieval(int id) throws android.os.RemoteException;
public void setInterval(int i) throws android.os.RemoteException;
public void registerRaritanCallback(com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback raritan_cb) throws android.os.RemoteException;
public void unregisterRaritanCallback(com.homesystem.Service.Gateway.Raritan.IRaritanServiceCallback raritan_cb) throws android.os.RemoteException;
}
