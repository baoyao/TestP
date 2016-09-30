package com.example.testp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author houen.bao
 * @date Sep 30, 2016 10:31:11 AM
 */
public class RhythmController {

    private Context mContext;
    private LinearLayout mRhythmLayout;

    public RhythmController(Context context) {
        mContext = context;
        mRhythmLayout = (LinearLayout) ((Activity) context).findViewById(R.id.rhythm_anim_layout);
        
        
    }

    private View buildRhythmItem() {
        ImageView item = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        item.setLayoutParams(params);
        item.setBackgroundResource(R.drawable.white_note_hd);
        return item;
    }
    
    public void startPlayAnim(){
        
    }

}
