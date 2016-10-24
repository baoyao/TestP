package com.example.testp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * @author houen.bao
 * @date Oct 24, 2016 10:09:22 AM
 */
public class ItemHorizontalScrollView extends HorizontalScrollView {

    public ItemHorizontalScrollView(Context context) {
        this(context, null, 0);
    }

    public ItemHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mOnScrollChanged.onScrollChanged(l, t, oldl, oldt);
    }
    
    private OnScrollChanged mOnScrollChanged;
    
    public void setOnScrollChanged(OnScrollChanged onScrollChanged){
        mOnScrollChanged = onScrollChanged;
    }
    
    interface OnScrollChanged{
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
