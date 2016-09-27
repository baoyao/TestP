package com.example.testp;

import java.util.List;

import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Sep 27, 2016 5:51:11 PM
 */
public class PreloadPlay {

    private Gson mGson;
    private SoundPool mSoundPool;
    private int[] msgId;

    public PreloadPlay(SoundPool soundPool) {
        mGson = new Gson();
        mSoundPool=soundPool;
    }

    public void playDelayFromJson(String json) {
        List<SoundInfo> resultList = mGson.fromJson(json, new TypeToken<List<SoundInfo>>() {
        }.getType());
        
        msgId=new int[resultList.size()];
        for(int i=0;i<resultList.size();i++){
            SoundInfo info=resultList.get(i);
            msgId[i]=info.getIndex();
            
            Message msg=mHandler.obtainMessage();
            msg.obj=info;
            mHandler.sendMessageDelayed(msg, calcMillis(info.getTime()));
        }
    }
    
    private long calcMillis(int[] time){
        
        return 0;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SoundInfo info=(SoundInfo) msg.obj;
            int sId=info.getSound();
            mSoundPool.play(sId, 1, 1, 0, 0, 1);
        }
    };
    
    public void stopPlay(){
        
    }

}
