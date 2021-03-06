/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: frameworks/base/packages/PrintSpooler/src/com/android/printspooler/renderer/IPdfRenderer.aidl
 */
package com.android.printspooler.renderer;
/**
 * Interface for communication with a remote pdf renderer.
 */
public interface IPdfRenderer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.printspooler.renderer.IPdfRenderer
{
private static final java.lang.String DESCRIPTOR = "com.android.printspooler.renderer.IPdfRenderer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.printspooler.renderer.IPdfRenderer interface,
 * generating a proxy if needed.
 */
public static com.android.printspooler.renderer.IPdfRenderer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.printspooler.renderer.IPdfRenderer))) {
return ((com.android.printspooler.renderer.IPdfRenderer)iin);
}
return new com.android.printspooler.renderer.IPdfRenderer.Stub.Proxy(obj);
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
case TRANSACTION_openDocument:
{
data.enforceInterface(DESCRIPTOR);
android.os.ParcelFileDescriptor _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.openDocument(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_renderPage:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
android.print.PrintAttributes _arg3;
if ((0!=data.readInt())) {
_arg3 = android.print.PrintAttributes.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
android.os.ParcelFileDescriptor _arg4;
if ((0!=data.readInt())) {
_arg4 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg4 = null;
}
this.renderPage(_arg0, _arg1, _arg2, _arg3, _arg4);
return true;
}
case TRANSACTION_closeDocument:
{
data.enforceInterface(DESCRIPTOR);
this.closeDocument();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.printspooler.renderer.IPdfRenderer
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
@Override public int openDocument(android.os.ParcelFileDescriptor source) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((source!=null)) {
_data.writeInt(1);
source.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_openDocument, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void renderPage(int pageIndex, int bitmapWidth, int bitmapHeight, android.print.PrintAttributes attributes, android.os.ParcelFileDescriptor destination) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(pageIndex);
_data.writeInt(bitmapWidth);
_data.writeInt(bitmapHeight);
if ((attributes!=null)) {
_data.writeInt(1);
attributes.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((destination!=null)) {
_data.writeInt(1);
destination.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_renderPage, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void closeDocument() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_closeDocument, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_openDocument = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_renderPage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_closeDocument = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public int openDocument(android.os.ParcelFileDescriptor source) throws android.os.RemoteException;
public void renderPage(int pageIndex, int bitmapWidth, int bitmapHeight, android.print.PrintAttributes attributes, android.os.ParcelFileDescriptor destination) throws android.os.RemoteException;
public void closeDocument() throws android.os.RemoteException;
}
