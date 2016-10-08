package com.example.testp;

import java.util.List;

import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Sep 27, 2016 5:51:11 PM
 */
public class SoundPlayer {

    private Gson mGson;
    private SoundPool mSoundPool;
    private PlayThread mPlayThread;
    
    private Handler mHandler;

    public SoundPlayer(Handler handler) {
        mHandler=handler;
        mGson = new Gson();
        mSoundPool = PublicCache.SoundPool;
    }

    public void play(String json) {
        if(json == null||"".equals(json)){
            return;
        }
        if (mPlayThread == null) {
            isRun = true;
            currentTime = -1;
            playIndex = 0;
            isPause = false;
            sleepTime = 10;
            mPlayThread = new PlayThread(json);
            mPlayThread.start();
        } else {
            stop();
            play(json);
        }
    }

    private boolean isRun = true;
    private long currentTime = -1;
    private int playIndex = -1;
    private boolean isPause = false;
    private int sleepTime = 10;

    private class PlayThread extends Thread {
        List<SoundInfo> resultList;

        public PlayThread(String json) {
            resultList = mGson.fromJson(json, new TypeToken<List<SoundInfo>>() {
            }.getType());
        }

        @Override
        public void run() {
            while (isRun) {
                if (!isPause) {
                    if (playIndex > resultList.size() - 1) {
                        SoundPlayer.this.stop();
                        return;
                    }
                    currentTime++;
                    boolean timeToPlay = currentTime >= calcMillis(resultList.get(playIndex).getTime());
                    if (timeToPlay) {
                        int sId = resultList.get(playIndex).getSound();
//                        mSoundPool.play(sId, 1, 1, 0, 0, 1);
                        mHandler.sendEmptyMessage(sId);
                        playIndex++;
                        Log.v("tt", "play...");
                    }
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private long calcMillis(int[] time) {
        return (time[0] * 60 * 60) + (time[1] * 60) + time[2];
    }

    public void pause() {
        isPause = true;
        sleepTime = 500;
    }

    public void resume() {
        isPause = false;
        sleepTime = 10;
    }

    public void stop() {
        if (mPlayThread != null) {
            try {
                isRun = false;
                mPlayThread.interrupt();
                mPlayThread = null;
            } catch (Exception e) {
                mPlayThread = null;
                e.printStackTrace();
            }
        }
    }

}
