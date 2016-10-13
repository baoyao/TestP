package com.example.testp;

import java.util.List;

import android.os.Handler;
import android.os.Message;

/**
 * @author houen.bao
 * @date Sep 27, 2016 5:51:11 PM
 */
public class SoundPlayer {

    private PlayThread mPlayThread;
    
    private Handler mHandler;

    public SoundPlayer(Handler handler) {
        mHandler=handler;
    }

    public void play(String json) {
        if(json == null||"".equals(json)){
            return;
        }
        if (mPlayThread == null) {
            mHandler.sendEmptyMessage(Constants.MSG_PREPARE_PLAY);
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
            resultList = Utils.jsonParseToSoundObject(json);
        }

        @Override
        public void run() {
            while (isRun) {
                if (!isPause) {
                    if (playIndex > resultList.size() - 1) {
                        SoundPlayer.this.stop();
                        mHandler.sendEmptyMessage(Constants.MSG_STOP_PLAY);
                        return;
                    }
                    currentTime++;
                    boolean timeToPlay = currentTime >= calcMillis(resultList.get(playIndex).getTime());
                    if (timeToPlay) {
                        int sId = resultList.get(playIndex).getSound();
//                        mSoundPool.play(sId, 1, 1, 0, 0, 1);
                        Message mess=mHandler.obtainMessage();
                        mess.arg1=sId;
                        mess.what=Constants.MSG_PLAY_BY_SOUND_ID;
                        mHandler.sendMessage(mess);
                        playIndex++;
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
        return (time[0] * 60 * 100) + (time[1] * 100) + time[2];
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
