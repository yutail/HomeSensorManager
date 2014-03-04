/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/tiger/Documents/workspace/HomeSensorManager/HomeSystem/src/com/homesystem/Service/ISensorReportService.aidl
 */
package com.homesystem.Service;
public interface ISensorReportService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.homesystem.Service.ISensorReportService
{
private static final java.lang.String DESCRIPTOR = "com.homesystem.Service.ISensorReportService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.homesystem.Service.ISensorReportService interface,
 * generating a proxy if needed.
 */
public static com.homesystem.Service.ISensorReportService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.homesystem.Service.ISensorReportService))) {
return ((com.homesystem.Service.ISensorReportService)iin);
}
return new com.homesystem.Service.ISensorReportService.Stub.Proxy(obj);
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
case TRANSACTION_reportHomeSensor:
{
data.enforceInterface(DESCRIPTOR);
com.homesystem.Service.HomeSystem _result = this.reportHomeSensor();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.homesystem.Service.ISensorReportService
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
@Override public com.homesystem.Service.HomeSystem reportHomeSensor() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.homesystem.Service.HomeSystem _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reportHomeSensor, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.homesystem.Service.HomeSystem.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_reportHomeSensor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public com.homesystem.Service.HomeSystem reportHomeSensor() throws android.os.RemoteException;
}
