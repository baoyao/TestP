package com.example.testp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author houen.bao
 * @date Sep 30, 2016 10:31:11 AM
 */
public class RhythmController {

    private Context mContext;
    private FrameLayout mRhythmLayout;
    private List<KeyButton> mKeyButtonList;
    private SoundPool mSoundPool;
    private HorizontalScrollView mScrollview;
    private int mRhythmViewEndLine=PublicConfig.RHYTHM_VIEW_END_LINE;
    private String[] mSoundStr;

    public RhythmController(Context context, SoundPool soundPool, List<KeyButton> keyButtonList) {
        mContext = context;
        mKeyButtonList=keyButtonList;
        mSoundPool=soundPool;
        mRhythmLayout = (FrameLayout) ((Activity) context).findViewById(R.id.rhythm_anim_layout);
        mScrollview=(HorizontalScrollView) ((Activity) context).findViewById(R.id.scrollview);
        mRhythmLayout.removeAllViews();
        buildRhythmItem();
        mRhythmViewEndLine = mRhythmLayout.getHeight();
        mSoundStr=mContext.getResources().getStringArray(R.array.sound_list);
    }

    // 120-80=40
    private void buildRhythmItem() {
        int whiteKeyWidth=PublicConfig.WHITE_KEY_WIDTH;
        int rhythmWidth=PublicConfig.RHYTHM_VIEW_WIDTH;
        int whiteKeyLeftMargin=PublicConfig.WHITE_KEY_LEFT_MARGIN;
        int whiteKeyWidthAndMargin=whiteKeyWidth+whiteKeyLeftMargin;
        
        int inWhiteLeft=(whiteKeyWidth+whiteKeyLeftMargin-rhythmWidth)/2;
        int inBlackLeft=whiteKeyWidth+whiteKeyLeftMargin-(rhythmWidth/2);
        
        for (int i = 0; i < mKeyButtonList.size(); i++) {
            int cellNum=(i-3)/12;
            int leftMargin = 0;
            if (i < 3) {
                if (i == 0) {
                    leftMargin = inWhiteLeft;
                } else if (i == 1) {
                    leftMargin = inBlackLeft;
                } else if (i == 2) {
                    leftMargin = whiteKeyWidth+inWhiteLeft;
                }
            } else {
                int lastMargin=((cellNum*7)*whiteKeyWidthAndMargin)+(whiteKeyWidthAndMargin*2);
                int index = getEndKeyNum(i - 3 + 1);
                switch (index) {
                case 1:
                    leftMargin = lastMargin+inWhiteLeft;
                    break;
                case 2:// b
                    leftMargin = lastMargin+inBlackLeft;
                    break;
                case 3:
                    leftMargin = lastMargin+whiteKeyWidthAndMargin+inWhiteLeft;
                    break;
                case 4:// b
                    leftMargin = lastMargin+whiteKeyWidthAndMargin+inBlackLeft;
                    break;
                case 5:
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*2)+inWhiteLeft;
                    break;
                case 6:
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*3)+inWhiteLeft;
                    break;
                case 7:// b
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*3)+inBlackLeft;
                    break;
                case 8:
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*4)+inWhiteLeft;
                    break;
                case 9:// b
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*4)+inBlackLeft;
                    break;
                case 10:
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*5)+inWhiteLeft;
                    break;
                case 11:// b
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*5)+inBlackLeft;
                    break;
                case 12:
                    leftMargin = lastMargin+(whiteKeyWidthAndMargin*6)+inWhiteLeft;
                    break;
                default:
                    break;
                }
            }
            mKeyButtonList.get(i).setRhythmViewLeftMargin(leftMargin);
        }
    }

    private int getEndKeyNum(int count) {
        if (count <= 12) {
            return count;
        }
        return getEndKeyNum(count - 12);
    }

    private View buildRhythmView(int soundId) {
        View rhythmView=LayoutInflater.from(mContext).inflate(R.layout.rhythm_item, null);
        FrameLayout.LayoutParams subParams = new FrameLayout.LayoutParams(PublicConfig.RHYTHM_VIEW_WIDTH,
                PublicConfig.RHYTHM_VIEW_HEIGTH);
        int leftMargin=mKeyButtonList.get(soundId-1).getRhythmViewLeftMargin();
        subParams.setMargins(leftMargin, 0, 0, 0);
        rhythmView.setLayoutParams(subParams);
        rhythmView.setTag(soundId+"");
        if(PublicConfig.RHYTHM_VIEW_WIDTH >= 40){
            ((TextView)rhythmView.findViewById(R.id.sound)).setText(mSoundStr[soundId]);
        }
        return rhythmView;
    }

    public void addRhythViewToLayout(int soundId){
        mRhythmLayout.addView(buildRhythmView(soundId));
        
        if(mRhythmLayout.getChildCount()==1){
            smoothToDownView(soundId);
        }
        
        TimeRecord record=new TimeRecord();
        record.soundIndex=soundId;
        record.startTime=System.currentTimeMillis();
        mTimeRecord.add(record);
    }
    
    public static boolean isKeyButtonTouchDown = false;
    
    private void smoothToDownView(int soundId){
        if(!isKeyButtonTouchDown){
            int leftMargin=mKeyButtonList.get(soundId-1).getRhythmViewLeftMargin();
            Message msg=mHandler.obtainMessage(MSG_SMOOTH_TO_DOWN_VIEW, leftMargin, 0);
            mHandler.removeMessages(MSG_SMOOTH_TO_DOWN_VIEW);
            mHandler.sendMessageAtTime(msg, 0);
        }
    }

    private final int MSG_SMOOTH_TO_DOWN_VIEW =1;
    
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
            case MSG_SMOOTH_TO_DOWN_VIEW:
                mScrollview.smoothScrollTo(msg.arg1-400, 0);
                break;
                default:
                    break;
            }
        }
    };
    
    public void refreshRhythView(){
        for (int i = 0; i < mRhythmLayout.getChildCount(); i++) {
            View rhythmView = mRhythmLayout.getChildAt(i);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) rhythmView
                    .getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin + DOWN_SPEED, params.rightMargin, params.bottomMargin);
            rhythmView.setLayoutParams(params);
            if (params.topMargin >= mRhythmViewEndLine) {
                int soundId=Integer.parseInt(rhythmView.getTag().toString());
                if(!Utils.getConfiguration(mContext, Constants.KEY_MUTE, false)){
                    mSoundPool.play(soundId, 1, 1, 0, 0, 1);
                }
                mRhythmLayout.removeViewAt(i);
                recordEndLog(soundId);
            }
            if(params.topMargin == (mRhythmViewEndLine-(DOWN_SPEED*50))){
                int soundId=Integer.parseInt(rhythmView.getTag().toString());
                smoothToDownView(soundId);
                recordPreTime(soundId);
            }
        }
    }

    private final int DOWN_SPEED=PublicConfig.RHYTHM_VIEW_ANIM_SPEED;
    private List<TimeRecord> mTimeRecord=new ArrayList<TimeRecord>();
    
    public int getRhythmNum(){
        return mRhythmLayout.getChildCount();
    }
    
    public List<TimeRecord> getRecord(){
        return mTimeRecord;
    }
    
    public void startPlay(){
        mTimeRecord.clear();
    }
    
    public void stopPlay(){
        mTimeRecord.clear();
        mRhythmLayout.removeAllViews();
    }
    
    private void recordPreTime(int soundId){
        for(int i=0;i<mTimeRecord.size();i++){
            if(mTimeRecord.get(i).soundIndex==soundId){
                mTimeRecord.get(i).preTime=System.currentTimeMillis();
                break;
            }
        }
    }
    
    private void recordEndLog(int soundId){
        for(int i=0;i<mTimeRecord.size();i++){
            if(mTimeRecord.get(i).soundIndex==soundId){
                mTimeRecord.get(i).endTime=System.currentTimeMillis();
                /*Log.v("tt", "SoundId: "+mTimeRecord.get(i).soundIndex+" change time: "+(mTimeRecord.get(i).endTime-mTimeRecord.get(i).startTime));
                
                if(i>0){
                    Log.v("tt", "start time change: "+(mTimeRecord.get(i).startTime-mTimeRecord.get(i-1).startTime));

                    Log.v("tt", "pre time change: "+(mTimeRecord.get(i).preTime-mTimeRecord.get(i-1).preTime));
                    
                    Log.v("tt", "end time change: "+(mTimeRecord.get(i).endTime-mTimeRecord.get(i-1).endTime));
                }*/
                break;
            }
        }
    }
    
    class TimeRecord{
        int soundIndex;
        long startTime;
        long preTime;
        long endTime;
    }

}
