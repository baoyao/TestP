package com.example.testp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.SoundPool;
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
    private List<KeyButton> mKeyButtonList;
    private SoundPool mSoundPool;

    public RhythmController(Context context, SoundPool soundPool, List<KeyButton> keyButtonList) {
        mContext = context;
        mKeyButtonList=keyButtonList;
        mSoundPool=soundPool;
        mRhythmLayout = (FrameLayout) ((Activity) context).findViewById(R.id.rhythm_anim_layout);
        crrentLeftMargin = 0;
        mRhythmLayout.removeAllViews();
        buildRhythmItem();
    }

    // 120-80=40
    private int crrentLeftMargin = 0;

    private void buildRhythmItem() {
        for (int i = 0; i < mKeyButtonList.size(); i++) {
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
            mKeyButtonList.get(i).setRhythmViewLeftMargin(crrentLeftMargin + leftMargin);
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
        int leftMargin=mKeyButtonList.get(soundId-1).getRhythmViewLeftMargin();
        subParams.setMargins(leftMargin, 0, 0, 0);
        rhythmView.setLayoutParams(subParams);
        rhythmView.setTag(soundId+"");
        return rhythmView;
    }

    public void addRhythViewToLayout(int soundId){
        mRhythmLayout.addView(buildRhythmView(soundId));
        TimeRecord record=new TimeRecord();
        record.soundIndex=soundId;
        record.startTime=System.currentTimeMillis();
        mTimeRecord.add(record);
        Log.v("tt", "SoundId: "+record.soundIndex+" start time: "+record.startTime);
    }
    
    public void refreshRhythView(){
        for (int i = 0; i < mRhythmLayout.getChildCount(); i++) {
            ImageView rhythmView = (ImageView) mRhythmLayout.getChildAt(i);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) rhythmView
                    .getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin + DOWN_SPEED, params.rightMargin, params.bottomMargin);
            rhythmView.setLayoutParams(params);
            if (params.topMargin >= PublicConfig.RHYTHM_VIEW_END_LINE) {
                mRhythmLayout.removeViewAt(i);
                int soundId=Integer.parseInt(rhythmView.getTag().toString());
                mSoundPool.play(soundId, 1, 1, 0, 0, 1);
                printLog(soundId);
            }
        }
    }

    private final int DOWN_SPEED=PublicConfig.RHYTHM_VIEW_ANIM_SPEED;
    private List<TimeRecord> mTimeRecord=new ArrayList<TimeRecord>();
    
    public int getRhythmNum(){
        return mRhythmLayout.getChildCount();
    }
    
    private void printLog(int soundId){
        //print log
        for(int j=0;j<mTimeRecord.size();j++){
            if(mTimeRecord.get(j).soundIndex==soundId){
                mTimeRecord.get(j).endTime=System.currentTimeMillis();
                Log.v("tt","index: "+j);
                Log.v("tt", "SoundId: "+mTimeRecord.get(j).soundIndex+" change time: "+(mTimeRecord.get(j).endTime-mTimeRecord.get(j).startTime));
                
                if(j>0){
                    Log.v("tt", "start time change: "+(mTimeRecord.get(j).startTime-mTimeRecord.get(j-1).startTime));
                    Log.v("tt", "end time change: "+(mTimeRecord.get(j).endTime-mTimeRecord.get(j-1).endTime));
                }
                break;
            }
        }
    }
    
    class TimeRecord{
        int soundIndex;
        long startTime;
        long endTime;
        
    }

}
