package com.example.testp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author houen.bao
 * @date Sep 29, 2016 1:59:09 PM
 */
public class KeyButton extends Button {
    
    private int soundId;
    
    private ImageView rhythmView;

    public KeyButton(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    public KeyButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public KeyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        buildRhythmView(context);
    }
    
    private void buildRhythmView(Context context){
        rhythmView=new ImageView(context);
        rhythmView.setBackgroundResource(R.drawable.white_note_hd);
    }

    @Override
    public void setLayoutParams(LayoutParams params) {
        super.setLayoutParams(params);
        LinearLayout.LayoutParams parentParams=((LinearLayout.LayoutParams)params);

        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(parentParams.width,
                PublicConfig.RHYTHM_VIEW_HEIGTH);
        subParams.setMargins(parentParams.leftMargin, 0, 0, 0);
        rhythmView.setLayoutParams(subParams);
    }
    
    public void setRhythmViewTopMargins(int topMargin){
        LinearLayout.LayoutParams params=(android.widget.LinearLayout.LayoutParams) rhythmView.getLayoutParams();
        params.setMargins(topMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        rhythmView.setLayoutParams(params);
    }

    public ImageView getRhythmView() {
        return rhythmView;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case KeyEvent.ACTION_DOWN:
            if(mOnTouchDownListener!=null){
                mOnTouchDownListener.onTouchDown(this);
            }
            break;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnTouchDownListener(OnTouchDownListener l) {
        mOnTouchDownListener = l;
        this.setClickable(true);
    }

    private OnTouchDownListener mOnTouchDownListener;

    public interface OnTouchDownListener {
        void onTouchDown(View v);
    }

}
