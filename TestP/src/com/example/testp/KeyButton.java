package com.example.testp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * @author houen.bao
 * @date Sep 29, 2016 1:59:09 PM
 */
public class KeyButton extends Button {

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
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
