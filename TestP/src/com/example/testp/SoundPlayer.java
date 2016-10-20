package com.example.testp;

import java.util.List;

import com.example.testp.RhythmController.TimeRecord;

import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author houen.bao
 * @date Sep 27, 2016 5:51:11 PM
 */
public class SoundPlayer {

    private PlayThread mPlayThread;

    private RhythmController mRhythmController;

    private static final int MSG_PLAY_BY_SOUND_ID = 1;
    private static final int MSG_REFRESH_RHYTHVIEW = 2;
    private static final int MSG_START_PLAY = 3;
    private static final int MSG_STOP_PLAY = 4;

    public SoundPlayer(MainActivity mainActivity, SoundPool soundPool, List<KeyButton> keyButtonList) {
        mRhythmController = new RhythmController(mainActivity, soundPool, keyButtonList);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_PLAY_BY_SOUND_ID:
                mRhythmController.addRhythViewToLayout(msg.arg1);
                break;
            case MSG_REFRESH_RHYTHVIEW:
                mRhythmController.refreshRhythView();
                break;
            case MSG_START_PLAY:
                mRhythmController.startPlay();
                break;
            case MSG_STOP_PLAY:
                mRhythmController.stopPlay();
                break;
            default:
                break;
            }
        }
    };

    public void play(String json) {
        if (json == null || "".equals(json)) {
            return;
        }
        if (mPlayThread == null) {
            startTime = -1;
            playIndex = 0;
            isPause = false;
            sleepTime = 10;
            mPlayThread = new PlayThread(json);
            mPlayThread.isRun = true;
            mPlayThread.start();
        } else {
            stop();
            play(json);
        }
    }

    private long startTime = -1;
    private int playIndex = -1;
    private boolean isPause = false;
    private int sleepTime = 10;
    private long playSpeed=0;
    private boolean isFirstPause=true;
    
    public void setSpeed(long playSpeed){
        this.playSpeed=playSpeed;
    }

    private class PlayThread extends Thread {
        private List<SoundInfo> resultList;
        private boolean isRun = false;
        private long speedCount=0;
        private long parseTime=0;

        public PlayThread(String json) {
            resultList = Utils.jsonParseToSoundObject(json);
        }

        @Override
        public void run() {
            while (isRun) {
                if (!isPause) {
                    if (playIndex < resultList.size()) {
                        if (startTime == -1) {
                            startTime = System.currentTimeMillis();
                            speedCount = 0;
                            mHandler.sendEmptyMessage(MSG_START_PLAY);
                        }
                        if(!isFirstPause){
                            isFirstPause = true;
                            startTime=System.currentTimeMillis()-parseTime;
                        }
                        speedCount+=playSpeed;
                        long changeTime = ((System.currentTimeMillis() - startTime) / 10)+(speedCount/10);
                        boolean timeToPlay = changeTime >= Utils.parseTime(resultList.get(playIndex).getTime());
                        if (timeToPlay) {
                            int sId = resultList.get(playIndex).getSound();
                            // mSoundPool.play(sId, 1, 1, 0, 0, 1);
                            if(sId != 0){
                                Message mess = mHandler.obtainMessage();
                                mess.arg1 = sId;
                                mess.what = MSG_PLAY_BY_SOUND_ID;
                                mHandler.sendMessage(mess);
                            }
                            playIndex++;
                        }
                    } else {
                        if (mRhythmController.getRhythmNum() == 0) {
                            SoundPlayer.this.stop();
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_REFRESH_RHYTHVIEW);
                }else{
                    if(isFirstPause){
                        isFirstPause=false;
                        parseTime=System.currentTimeMillis()-startTime;
                    }
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!isRun){
                try {
                    this.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean isPlaying(){
        return mPlayThread==null ? false : mPlayThread.isRun;
    }
    
    public void pause() {
        isFirstPause=true;
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
                mPlayThread.isRun = false;
                mPlayThread.interrupt();
                mPlayThread = null;
            } catch (Exception e) {
                mPlayThread = null;
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessage(MSG_STOP_PLAY);
    }
    
    public List<TimeRecord> getRecord(){
        return mRhythmController.getRecord();
    }

}
