package com.example.testp;
/**
 * @author houen.bao
 * @date Sep 28, 2016 10:58:52 AM
 */
public class TimeController {
    
    public OnTimeChangedListener mOnTimeChangedListener;
    
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener){
        mOnTimeChangedListener=onTimeChangedListener;
    }
    
    public OnTimeChangedListener getOnTimeChangedListener(){
        return mOnTimeChangedListener;
    }
    
    public interface OnTimeChangedListener{
        public void onChanged(EditItemView itemView);
    }

}
