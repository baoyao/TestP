package com.example.testp;

import android.os.Environment;

/**
 * @author houen.bao
 * @date Sep 30, 2016 10:44:23 AM
 */
public class PublicConfig {

    public static final String SONG_PATH=Environment.getExternalStorageDirectory().getAbsolutePath()+"/songs";
    
    public final static int MAX_SOUNDS = 88;

    public static final int KEY_WIDTH = 12;//60 必须能除以3
    
    
    public static int WHITE_KEY_WIDTH = 2 * KEY_WIDTH;//120
    public static int BLACK_KEY_WIDTH = 4 * (KEY_WIDTH/3);//80
    public static final int WHITE_KEY_LEFT_MARGIN = 1;

    public static final int RHYTHM_VIEW_HEIGTH=40;
    public static final int RHYTHM_VIEW_WIDTH=KEY_WIDTH;
    public static final int RHYTHM_VIEW_ANIM_SPEED=2;
    public static final int RHYTHM_VIEW_END_LINE=440;

}
