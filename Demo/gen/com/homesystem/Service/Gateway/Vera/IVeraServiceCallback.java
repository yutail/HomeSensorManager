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
}
}
}
