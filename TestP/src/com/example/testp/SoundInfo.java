package com.example.testp;


/**
 * @author houen.bao
 * @date Sep 23, 2016 5:49:00 PM
 */
public class SoundInfo {

    private int index;
    private int sound;
    private int[] time;
    private String soundName;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int[] getTime() {
        return time;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }
}
