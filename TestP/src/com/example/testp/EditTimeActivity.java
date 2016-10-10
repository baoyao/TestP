package com.example.testp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.testp.EditItemView.OnAddListener;
import com.example.testp.EditItemView.OnDeleteListener;
import com.example.testp.EditItemView.OnTimeChangedListener;

/**
 * @author houen.bao
 * @date Sep 23, 2016 5:52:01 PM
 */
public class EditTimeActivity extends Activity {

    private LinearLayout mItemsLayout;
    private EditText songNameEditText;
    private int mRequestCode=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_main);
        mItemsLayout = (LinearLayout) this.findViewById(R.id.items_layout);
        songNameEditText=(EditText) this.findViewById(R.id.song_name);
        
        Intent intent=this.getIntent();
        mRequestCode=intent.getIntExtra(Constants.EXT_REQUEST_CODE, -1);
        String soundData=intent.getStringExtra(Constants.EXT_SOUND_DATA);
        if(soundData!=null){
            String soundName=intent.getStringExtra(Constants.EXT_SONG_NAME);
            songNameEditText.setText(soundName);
            List<SoundInfo> soundList=Utils.jsonParseToSoundObject(soundData);
            for(int i=0;i<soundList.size();i++){
                SoundInfo info=soundList.get(i);
                EditItemView item=createEditView(info.getIndex());
                item.setTime(info.getTime());
                item.setSound(info.getSound());
                mItemsLayout.addView(item,mItemsLayout.getChildCount()-1);
            }
        }
    }

    private long calcMillis(int[] time) {
        return (time[0] * 60 * 60) + (time[1] * 60) + time[2];
    }
    
    private OnTimeChangedListener mOnTimeChangedListener=new OnTimeChangedListener() {
        @Override
        public void onChanged(EditItemView itemView) {
            List<EditItemView> list = new ArrayList<EditItemView>();
            for (int i = 0; i < mItemsLayout.getChildCount(); i++) {
                if (mItemsLayout.getChildAt(i) instanceof EditItemView) {
                    EditItemView tempItemView = (EditItemView) mItemsLayout.getChildAt(i);
                    if (tempItemView.getIndex() > itemView.getIndex()) {
                        long time1 = calcMillis(tempItemView.getTime());
                        long time2 = calcMillis(itemView.getTime());
                        if (time1 < time2) {
                            list.add(tempItemView);
                        }
                    }
                }
            }
            if (list.size() > 0) {
                String str = "";
                for (int i = 0; i < list.size(); i++) {
                    str += "" + list.get(i).getIndex();
                    if (i != list.size() - 1) {
                        str += "/";
                    }
                }
                Toast.makeText(EditTimeActivity.this, "第" + str+"个的时间小于前面的！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onAddButtonClick(View view) {
        EditItemView editItemView = createEditView(mItemsLayout.getChildCount());
        mItemsLayout.addView(editItemView, mItemsLayout.getChildCount() - 1);
        updateItemsIndex();
        updateItemsTime(editItemView);
    }

    private void updateItemsIndex() {
        for (int i = 0; i < mItemsLayout.getChildCount(); i++) {
            if (mItemsLayout.getChildAt(i) instanceof EditItemView) {
                ((EditItemView) mItemsLayout.getChildAt(i)).setIndex(i + 1);
            }
        }
    }

    private void updateItemsTime(EditItemView itemView) {
        int index = itemView.getIndex();
        if (index > 1) {
            ((EditItemView) mItemsLayout.getChildAt(index - 1)).setTime(((EditItemView) mItemsLayout
                    .getChildAt(index - 1 - 1)).getTime());
        }
    }

    private EditItemView createEditView(int index) {
        EditItemView editItemView = (EditItemView) LayoutInflater.from(this).inflate(R.layout.edit_item, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 450);
        editItemView.setLayoutParams(params);
        editItemView.setPadding(5, 5, 5, 5);
        editItemView.setIndex(index);
        editItemView.setGravity(Gravity.CENTER);
        editItemView.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onClick(View v) {
                mItemsLayout.removeView(v);
                updateItemsIndex();
            }
        });

        editItemView.setOnAddListener(new OnAddListener() {
            @Override
            public void onClick(int index, View v) {
                EditItemView subEditItemView = createEditView(index);
                mItemsLayout.addView(subEditItemView, index);
                updateItemsIndex();
                updateItemsTime(subEditItemView);
            }
        });
        editItemView.setOnTimeChangedListener(mOnTimeChangedListener);
        return editItemView;
    }

    public void onSaveButtonClick(View view) {
        String songName=songNameEditText.getText().toString();
        if("".equals(songName.trim())){
            Toast.makeText(this, "Song name is null", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<SoundInfo> list = new ArrayList<SoundInfo>();
        for (int i = 0; i < mItemsLayout.getChildCount(); i++) {
            if (mItemsLayout.getChildAt(i) instanceof EditItemView) {
                EditItemView item = (EditItemView) mItemsLayout.getChildAt(i);
                int index = item.getIndex();
                int sound = item.getSoundIndex();
                int[] time = item.getTime();
                SoundInfo info = new SoundInfo();
                info.setIndex(index);
                info.setSound(sound);
                info.setTime(time);
                list.add(info);
            }
        }

        String soundJson = Utils.soundObjectParseToJson(list);

        makeNewFile(songName);
        File file=new File(SONG_PATH+"/"+songName+".txt");
        boolean write=writeTxtFile(soundJson,file);
        Log.v("tt","write: "+write);
        
        Intent intent=new Intent();
        intent.putExtra(Constants.EXT_SOUND_DATA, soundJson);
        if(mRequestCode==Constants.SONG_LIST_ACTIVITY_REQUEST_CODE){
            intent.setClass(this, SongListActivity.class);
        }else{
            intent.setClass(this, MainActivity.class);
        }
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    private final String SONG_PATH=PublicConfig.SONG_PATH;
    
    private File makeNewFile(String fileName){
        File dir=new File(SONG_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file=new File(SONG_PATH+"/"+fileName+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    
    private boolean writeTxtFile(String content, File fileName){
        boolean flag = false;
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(fileName);
            o.write(content.getBytes("UTF-8"));
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    


}
