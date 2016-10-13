package com.example.testp;

import android.os.Environment;

/**
 * @author houen.bao
 * @date Sep 30, 2016 10:44:23 AM
 */
public class PublicConfig {

    public static final String SONG_PATH=Environment.getExternalStorageDirectory().getAbsolutePath()+"/songs";
    
    public final static int MAX_SOUNDS = 88;
    
    public static final int BLACK_KEY_WIDTH = 2 * 40;
    public static final int WHITE_KEY_WIDTH = 2 * 60;
    public static final int WHITE_KEY_LEFT_MARGIN = 1;

    public static final int RHYTHM_VIEW_HEIGTH=20;//音符控件高度
    public static final int RHYTHM_VIEW_WIDTH=WHITE_KEY_WIDTH-BLACK_KEY_WIDTH;//音符控件高度
    public static final int RHYTHM_VIEW_ANIM_SPEED=2;//音符控件掉下的速度
    public static final int RHYTHM_VIEW_END_LINE=440;//音符控件掉下的最低位置

}
