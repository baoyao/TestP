package com.example.testp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.testp.EditItemView.OnAddListener;
import com.example.testp.EditItemView.OnDeleteListener;
import com.example.testp.EditItemView.OnTimeChangedListener;
import com.example.testp.GeneralPopupKeypad.OnKeypadItemClickListener;

/**
 * @author houen.bao
 * @date Sep 23, 2016 5:52:01 PM
 */
public class EditTimeActivity extends Activity {

    private LinearLayout mItemsLayout;
    private EditText mSongNameEditText;
    private int mRequestCode = -1;
    private Button mGrowButton1,mGrowButton2;
    private HorizontalScrollView mItemsScrollview;
    private CheckBox mAutoChangeTimeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_main);
        mItemsLayout = (LinearLayout) this.findViewById(R.id.items_layout);
        mSongNameEditText = (EditText) this.findViewById(R.id.song_name);
        mAutoChangeTimeCheckBox=(CheckBox) this.findViewById(R.id.auto_change_time);
        mItemsScrollview=(HorizontalScrollView) this.findViewById(R.id.items_scrollview);
        
        Intent intent = this.getIntent();
        mRequestCode = intent.getIntExtra(Constants.EXT_REQUEST_CODE, -1);
        String soundData = intent.getStringExtra(Constants.EXT_SOUND_DATA);
        if (soundData != null) {
            String soundName = intent.getStringExtra(Constants.EXT_SONG_NAME);
            mSongNameEditText.setText(soundName);
            try {
                List<SoundInfo> soundList = Utils.jsonParseToSoundObject(soundData);
                for (int i = 0; i < soundList.size(); i++) {
                    SoundInfo info = soundList.get(i);
                    EditItemView item = createEditView(info.getIndex());
                    item.setTime(info.getTime());
                    item.setSound(info.getSound());
                    mItemsLayout.addView(item, mItemsLayout.getChildCount() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        mSongNameEditText.setSelection(mSongNameEditText.getText().toString().length());
        
        initGrowButton();
        initAutoChangeTime();
    }
    
    private void initAutoChangeTime(){
        mAutoChangeTimeCheckBox.setChecked(Utils.getConfiguration(this, Constants.KEY_AUTO_CHANGE_TIME, true));
        mAutoChangeTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.saveConfiguration(EditTimeActivity.this, Constants.KEY_AUTO_CHANGE_TIME, isChecked);
            }
        });
    }
    
    private void initGrowButton(){
        mGrowButton1=(Button) this.findViewById(R.id.grow_num1);
        mGrowButton2=(Button) this.findViewById(R.id.grow_num2);
//        mGrowButton1.setText("01");
//        mGrowButton1.setTag(""+1);

        mGrowButton2.setText("50");
        mGrowButton2.setTag(""+50);
    }
    
    private int[] getGrowNum(){
        int g1=mGrowButton1.getTag() == null ? 0 : Integer.parseInt(mGrowButton1.getTag().toString());
        int g2=mGrowButton2.getTag() == null ? 0 : Integer.parseInt(mGrowButton2.getTag().toString());
        return new int[]{g1,g2};
    }
    
    public void onGrowButtonClick(final View view) {
        String[] tempData =null;
        switch (view.getId()) {
        case R.id.grow_num1: 
            tempData = Utils.getTimeData();
            break;
        case R.id.grow_num2: 
            tempData = Utils.getMillTimeData();
            break;
        default:
            break;
        }
        
        final String[] timeData=tempData;
        
        new GeneralPopupKeypad(this).showKeypad(view, timeData, new OnKeypadItemClickListener() {
            @Override
            public void onItemClick(int position, View clickItemView, View tagView) {
                ((Button) view).setText(timeData[position]);
                view.setTag("" + position);
            }
        });
    }

    private OnTimeChangedListener mOnTimeChangedListener = new OnTimeChangedListener() {
        @Override
        public void onChanged(EditItemView itemView,long changedTime) {
            List<EditItemView> list = new ArrayList<EditItemView>();
            for (int i = 0; i < mItemsLayout.getChildCount(); i++) {
                if (mItemsLayout.getChildAt(i) instanceof EditItemView) {
                    EditItemView tempItemView = (EditItemView) mItemsLayout.getChildAt(i);
                    if (tempItemView.getIndex() > itemView.getIndex()) {
                        long time1 = Utils.parseTime(tempItemView.getTime());
                        long time2 = Utils.parseTime(itemView.getTime());
                        
                        if (Utils.getConfiguration(EditTimeActivity.this, Constants.KEY_AUTO_CHANGE_TIME, true)) {
                            long parseTime = Utils.parseTime(tempItemView.getTime()) + changedTime;
                            tempItemView.setTime(Utils.parseTime(parseTime));
                        }
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
                Toast.makeText(EditTimeActivity.this, "第" + str + "个的时间小于前面的！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onAddButtonClick(View view) {
        EditItemView editItemView = createEditView(mItemsLayout.getChildCount());
        mItemsLayout.addView(editItemView, mItemsLayout.getChildCount() - 1);
        updateItemsIndex();
        updateItemsTime(editItemView);
        scrollToEnd();
    }
    
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mItemsScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
    };
    
    private void scrollToEnd(){
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 100);
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
            int[] t1= getGrowNum();
            int[] t2=((EditItemView) mItemsLayout
                    .getChildAt(index - 1 - 1)).getTime();
            int[] time=new int[]{t2[0],(t2[1]+t1[0]),(t2[2]+t1[1])};
            time=Utils.parseTime(Utils.parseTime(time));
            ((EditItemView) mItemsLayout.getChildAt(index - 1)).setTime(time);
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
                scrollToEnd();
            }
        });
        editItemView.setOnTimeChangedListener(mOnTimeChangedListener);
        return editItemView;
    }

    public void onSaveButtonClick(View view) {
        try {
            String songName = mSongNameEditText.getText().toString();
            if ("".equals(songName.trim())) {
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
            File file = new File(SONG_PATH + "/" + songName + ".txt");
            boolean write = writeTxtFile(soundJson, file);

            if (!write) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(Constants.EXT_SOUND_DATA, soundJson);
            if (mRequestCode == Constants.SONG_LIST_ACTIVITY_REQUEST_CODE) {
                intent.setClass(this, SongListActivity.class);
            } else {
                intent.setClass(this, MainActivity.class);
            }
            this.setResult(RESULT_OK, intent);
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private final String SONG_PATH = PublicConfig.SONG_PATH;

    private File makeNewFile(String fileName) {
        File dir = new File(SONG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(SONG_PATH + "/" + fileName + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private boolean writeTxtFile(String content, File fileName) {
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
