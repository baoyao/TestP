package org.winplus.serial.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SerialPortService extends Service{
	public static String TAG = "SerialPortService";
	private SerialPort mSerialPort;
	private String mPathDefault = "/dev/ttyS3"; //ttyGS3 ttyS3
	private int mBaudrateDefault = 9600;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"[SerialPortService] ----onCreate----");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG,"[SerialPortService] ----onBind----");
		return new SerialPortServiceBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		if(mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		Log.d(TAG,"[SerialPortService] ----onUnbind----");
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG,"[SerialPortService] ----onDestroy----");
		super.onDestroy();
	}

	public class SerialPortServiceBinder extends ISerialPortService.Stub {

		@Override
		public void open() throws RemoteException {
			try {
				if(null == mSerialPort) {
					mSerialPort = new SerialPort(new File(mPathDefault), mBaudrateDefault, 0);
					mOutputStream = mSerialPort.getOutputStream();
					mInputStream = mSerialPort.getInputStream();
				}
			} catch (SecurityException e1) {
				Log.e(TAG, e1.toString());
				e1.printStackTrace();
			}catch(IOException e2) {
				Log.e(TAG, e2.toString());
				e2.printStackTrace();
			}
			Log.d(TAG,"[SerialPortService] open");
		}

		@Override
		public int read(byte[] buffer, int byteOffset, int byteCount) throws RemoteException {
			int ret = -1;
			try {
				if(mInputStream != null) {
					ret = mInputStream.read(buffer, byteOffset, byteCount);
				}else {
					Log.e(TAG,"mInputStream = null Do you invoke ISerialPortService.open() ?");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG,"[SerialPortService] read");
			return ret;
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws RemoteException {
			try {
				if(mOutputStream != null) {
					mOutputStream.write(buffer, offset, count);
				}else {
					Log.e(TAG,"mOutputStream = null Do you invoke ISerialPortService.open() ?");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG,"[SerialPortService] write");
		}

		@Override
		public void close() throws RemoteException {
			if(mSerialPort != null) {
				mSerialPort.close();
				mSerialPort = null;
			}
			Log.d(TAG,"[SerialPortService] close");
		}
	}

}
