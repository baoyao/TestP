package com.example.testp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author houen.bao
 * @date Sep 30, 2016 10:31:11 AM
 */
public class RhythmController {

    private Context mContext;
    private FrameLayout mRhythmLayout;

    public RhythmController(Context context) {
        mContext = context;
        mRhythmLayout = (FrameLayout) ((Activity) context).findViewById(R.id.rhythm_anim_layout);
        crrentLeftMargin = 0;
        mRhythmLayout.removeAllViews();
        buildRhythmItem();
    }

    // 120-80=40
    private int crrentLeftMargin = 0;

    private void buildRhythmItem() {
        for (int i = 0; i < PublicCache.keyButtonList.size(); i++) {
            int leftMargin = 0;
            if (i < 3) {
                if (i == 0) {
                    leftMargin = 40 + 1;
                } else if (i == 1) {
                    leftMargin = 20;
                } else if (i == 2) {
                    leftMargin = 20 + 1;
                }
            } else {
                int index = getEndKeyNum(i - 3 + 1);
                switch (index) {
                case 1:
                    leftMargin = 40 + 40 + 1;
                    break;
                case 2:// b
                    leftMargin = 20;
                    break;
                case 3:
                    leftMargin = 20 + 1;
                    break;
                case 4:// b
                    leftMargin = 20;
                    break;
                case 5:
                    leftMargin = 20 + 1;
                    break;
                case 6:
                    leftMargin = 40 + 40 + 1;
                    break;
                case 7:// b
                    leftMargin = 20;
                    break;
                case 8:
                    leftMargin = 20 + 1;
                    break;
                case 9:// b
                    leftMargin = 20;
                    break;
                case 10:
                    leftMargin = 20 + 1;
                    break;
                case 11:// b
                    leftMargin = 20;
                    break;
                case 12:
                    leftMargin = 20 + 1;
                    break;
                default:
                    break;
                }
            }
            PublicCache.keyButtonList.get(i).setRhythmViewLeftMargin(crrentLeftMargin + leftMargin);
//            mRhythmLayout.addView(buildRhythmView(PublicCache.keyButtonList.get(i).getSoundId()));
            crrentLeftMargin += leftMargin + 40;
        }
    }

    private int getEndKeyNum(int count) {
        if (count <= 12) {
            return count;
        }
        return getEndKeyNum(count - 12);
    }

    private ImageView buildRhythmView(int soundId) {
        ImageView rhythmView = new ImageView(mContext);
        rhythmView.setBackgroundResource(R.drawable.white_note_hd);
        FrameLayout.LayoutParams subParams = new FrameLayout.LayoutParams(PublicConfig.RHYTHM_VIEW_WIDTH,
                PublicConfig.RHYTHM_VIEW_HEIGTH);
        int leftMargin=PublicCache.keyButtonList.get(soundId-1).getRhythmViewLeftMargin();
        subParams.setMargins(leftMargin, 0, 0, 0);
        rhythmView.setLayoutParams(subParams);
        rhythmView.setTag(soundId+"");
        return rhythmView;
    }

    public void addRhythViewToLayout(int soundId){
        mRhythmLayout.addView(buildRhythmView(soundId));
        if(mRhythmLayout.getChildCount()==1){
            startPlayAnim();
        }
    }

    private boolean isRun = true;
    private RefreshThread mRefreshThread;

    private void startPlayAnim() {
        if (mRefreshThread == null) {
            isRun = true;
            isResume = true;
            sleepTime = 10;
            mRefreshThread = new RefreshThread();
            mRefreshThread.start();
        }else{
            stopPlayAnim();
            startPlayAnim();
        }
    }

    private void stopPlayAnim() {
        if (mRefreshThread != null) {
            isRun = false;
            try {
                mRefreshThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRefreshThread = null;
        }
    }

    private boolean isResume = true;
    private int sleepTime = 10;
    private final int REFRESH_MSG = 1;
    
    private final int DOWN_SPEED=PublicConfig.RHYTHM_VIEW_ANIM_SPEED;

    class RefreshThread extends Thread {
        @Override
        public void run() {
            while (isRun) {
                if (isResume) {
                    mHandler.sendEmptyMessage(REFRESH_MSG);
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            for (int i = 0; i < mRhythmLayout.getChildCount(); i++) {
                ImageView rhythmView = (ImageView) mRhythmLayout.getChildAt(i);
                FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) rhythmView
                        .getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin + DOWN_SPEED, params.rightMargin, params.bottomMargin);
                rhythmView.setLayoutParams(params);
                if (params.topMargin >= PublicConfig.RHYTHM_VIEW_END_LINE) {
                    mRhythmLayout.removeViewAt(i);
                    int soundId=Integer.parseInt(rhythmView.getTag().toString());
                    PublicCache.SoundPool.play(soundId, 1, 1, 0, 0, 1);
                }
            }
            if(mRhythmLayout.getChildCount()==0){
                stopPlayAnim();
            }
        }
    };

    public void pause() {
        isResume = false;
        sleepTime = 1000;
    }

    public void resume() {
        isResume = true;
        sleepTime = 10;
    }

}
