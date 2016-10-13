package com.example.testp;

import java.util.List;

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

}
