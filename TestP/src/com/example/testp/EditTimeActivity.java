package com.example.testp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.testp.EditItemView.OnAddListener;
import com.example.testp.EditItemView.OnDeleteListener;
import com.example.testp.TimeController.OnTimeChangedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Sep 23, 2016 5:52:01 PM
 */
public class EditTimeActivity extends Activity {

    private LinearLayout mItemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_main);

        mItemsLayout = (LinearLayout) this.findViewById(R.id.items_layout);

        PublicCache.TimeController = new TimeController();
        PublicCache.TimeController.setOnTimeChangedListener(new OnTimeChangedListener() {
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
        });
    }

    private long calcMillis(int[] time) {
        return (time[0] * 60 * 60) + (time[1] * 60) + time[2];
    }

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
        return editItemView;
    }

    public void onSaveButtonClick(View view) {
        Gson gson = new Gson();
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

        String soundJson = gson.toJson(list, new TypeToken<List<SoundInfo>>() {
        }.getType());

        Log.v("tt", "soundJson: " + soundJson);
        PublicCache.SongJson=soundJson;
        if (PublicCache.SoundPlayer != null) {
            PublicCache.SoundPlayer.play(PublicCache.SongJson);
            this.finish();
        }
    }

}
