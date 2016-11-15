package org.winplus.serial.utils;

import org.winplus.serial.utils.SerialPortHandler.OnDataCallbackListener;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author houen.bao
 * @date Nov 4, 2016 1:54:24 PM
 */
public class SerialPortActivity extends Activity implements OnDataCallbackListener {

    private SerialPortHandler mSerialPortHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mSerialPortHandler=new SerialPortHandler(this);
        mSerialPortHandler.onCreate(savedInstanceState);
        mSerialPortHandler.setOnDataCallbackListener(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mSerialPortHandler.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mSerialPortHandler.onDestroy();
    }
    
    /**
     * 往串口发数据
     * @param data
     */
    public void writeDataToSerialPort(String data){
        mSerialPortHandler.write(data);
    }

    /**
     * 此方法是串口返回数据时回调
     * 往串口发数据请用 {@link #writeDataToSerialPort}
     */
    @Override
    public void onSerialPortCallback(String data) {
        
    }

    /**
     * 此方法是串口返回数据时回调
     * 往串口发数据请用 {@link #writeDataToSerialPort}
     */
    @Override
    public void onSerialPortCallback(byte[] data) {
        
    }
    

}
