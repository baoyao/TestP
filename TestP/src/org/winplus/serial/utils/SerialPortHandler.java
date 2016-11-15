package org.winplus.serial.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class SerialPortHandler {

    private String path = "/dev/ttyS3";
    private int baudrate = 9600;
    private ReadThread mReadThread;
    private Context mContext;
    private boolean isRun = true;
    private static final String ACTION_SERVICE = "org.winplus.serial.utils.SerialPortService";
    private ISerialPortService mISerialPortService;
    private OnDataCallbackListener mOnDataCallbackListener;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mISerialPortService.close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mISerialPortService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISerialPortService = ISerialPortService.Stub.asInterface(service);
            try {
                mISerialPortService.open();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    SerialPortHandler(Context context) {
        mContext = context;
    }

    void onCreate(Bundle savedInstanceState) {
        if (mContext.getApplicationInfo().targetSdkVersion >= 21) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(mContext.getPackageName(),ACTION_SERVICE);
            intent.setComponent(componentName);
            mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } else {
            mContext.bindService(new Intent(ACTION_SERVICE), connection, Context.BIND_AUTO_CREATE);
        }

        mReadThread = new ReadThread();
        mReadThread.start();
        Log.v("yzh", "thread start");
    }

    void write(String data) {
        Log.d("yzh", "send data is " + data);
        try {
            mISerialPortService.write(data.getBytes(), 0, data.getBytes().length);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("yzh", "write error " + e.toString());
        }
    }

    void onStop() {
        isRun = false;
    }

    void onDestroy() {
        mContext.unbindService(connection);
    }

    /** 将字节转化为16进制算法 */
    String toHex(byte b) {
        return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
    }

    /** 将字节转化为16进制算法 */
    String toHex(byte[] bytes) {
        StringBuffer strBuff = new StringBuffer();
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            str = toHex(bytes[i]);
            strBuff.append(str);
        }
        return strBuff.toString();
    }

    // 命令的长度
    private final int CMD_LENGTH = 5;

    private class ReadThread extends Thread {
        Message msg = new Message();
        byte[] buffer = new byte[CMD_LENGTH];
        String str;
        int size = 0;
        int position = 0;

        @Override
        public void run() {
            while (isRun) {
                try {
                    if (mISerialPortService == null) {
                        Thread.sleep(2000);
                    }
                    Log.d("yzh", "read data begin ");
                    size += mISerialPortService.read(buffer, position, CMD_LENGTH - position);
                    if (size == CMD_LENGTH) {
                        // Log.d("yzh","---read full size = " + size +
                        // "<--->read data = " + (int)buffer[0] + (int)buffer[1]
                        // + (int)buffer[2] + (int)buffer[3]
                        // + (int)buffer[4] + (int)buffer[5] + (int)buffer[6] +
                        // (int)buffer[7]
                        // + (int)buffer[8]);

                        Log.d("yzh", "<Hex> read full size = " + size + "<--->read data = " + toHex(buffer));
                        if (size > 0) {
                            mHandler.sendMessage(mHandler.obtainMessage(1111, buffer));
                        }
                        size = 0;
                        position = 0;
                        Thread.sleep(200);
                    } else if (size < CMD_LENGTH) {
                        Log.d("yzh", "not full size = " + size);
                        position = size;
                        continue;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            Log.v("yzh", "Thread stop");
        }
    }

    private Handler mHandler = new Handler() {
        private String str;

        public void handleMessage(android.os.Message msg) {
            byte[] buffer=(byte[]) msg.obj;
            switch (msg.what) {
            case 1111:
                if (mOnDataCallbackListener != null) {
                    mOnDataCallbackListener.onSerialPortCallback(toHex(buffer));
                    mOnDataCallbackListener.onSerialPortCallback(buffer);
                }
                break;
            default:
                break;
            }
        };
    };

    void setOnDataCallbackListener(OnDataCallbackListener onDataCallbackListener) {
        mOnDataCallbackListener = onDataCallbackListener;
    }

    interface OnDataCallbackListener {
        void onSerialPortCallback(String data);
        void onSerialPortCallback(byte[] data);
    }
}