package com.example.testp;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Oct 10, 2016 10:23:43 AM
 */
public class Utils {

    private static Gson mGson;

    private static void initGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
    }

    public static List<SoundInfo> jsonParseToSoundObject(String json) {
        initGson();
        return mGson.fromJson(json, new TypeToken<List<SoundInfo>>() {
        }.getType());
    }

    public static String soundObjectParseToJson(List<SoundInfo> soundList) {
        initGson();
        return mGson.toJson(soundList, new TypeToken<List<SoundInfo>>() {
        }.getType());
    }
    
    public static long parseTime(int[] time){
        return (time[0] * 60 * 100) + (time[1] * 100) + time[2];
    }

    public static int[] parseTime(long time){
        return new int[]{(int) (time/(100*60)),(int) ((time%(100*60))/(100)),(int) ((time%(100*60))%100)};
    }

    public static String[] getTimeData(){
        String[] times = new String[60];
        for(int i=0;i<60;i++){
            if(i<10){
                times[i]="0"+i;
            }else{
                times[i]=""+i;
            }
        }
        return times;
    }

    public static String[] getMillTimeData(){
        String[] times = new String[100];
        for(int i=0;i<100;i++){
            if(i<10){
                times[i]="0"+i;
            }else{
                times[i]=""+i;
            }
        }
        return times;
    }
    

    private static final String SHARED_PREFERENCES_NAME = "shared_preferences";

    public static boolean saveConfiguration(Context context, String key, boolean value) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(
                    SHARED_PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
            Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean getConfiguration(Context context, String key,boolean defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
        boolean value = preferences.getBoolean(key, defaultValue);
        return value;
    }

}
