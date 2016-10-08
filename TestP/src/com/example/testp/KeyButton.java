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
    
    private int keyId;
    
    private int soundId;
    

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
    }
    
    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }
    
    private int rhythmViewLeftMargin;
    
    public int getRhythmViewLeftMargin() {
        return rhythmViewLeftMargin;
    }

    public void setRhythmViewLeftMargin(int rhythmViewLeftMargin) {
        this.rhythmViewLeftMargin = rhythmViewLeftMargin;
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
