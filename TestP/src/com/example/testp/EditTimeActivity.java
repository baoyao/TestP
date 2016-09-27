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

import com.example.testp.EditItemView.OnAddListener;
import com.example.testp.EditItemView.OnDeleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Sep 23, 2016 5:52:01 PM
 */
public class EditTimeActivity extends Activity {

    private LinearLayout itemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_main);

        itemsLayout = (LinearLayout) this.findViewById(R.id.items_layout);
    }

    public void onAddButtonClick(View view) {
        EditItemView editItemView = createEditView(itemsLayout.getChildCount());
        itemsLayout.addView(editItemView, itemsLayout.getChildCount() - 1);
        updateItemsIndex();
    }

    private void updateItemsIndex() {
        for (int i = 0; i < itemsLayout.getChildCount(); i++) {
            if (itemsLayout.getChildAt(i) instanceof EditItemView) {
                ((EditItemView) itemsLayout.getChildAt(i)).setIndex(i + 1);
            }
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
                itemsLayout.removeView(v);
                updateItemsIndex();
            }
        });

        editItemView.setOnAddListener(new OnAddListener() {
            @Override
            public void onClick(int index, View v) {
                itemsLayout.addView(createEditView(index), index);
                updateItemsIndex();
            }
        });
        return editItemView;
    }

    public void onSaveButtonClick(View view) {
        Gson gson = new Gson();
        List<SoundInfo> list = new ArrayList<SoundInfo>();
        for (int i = 0; i < itemsLayout.getChildCount(); i++) {
            if (itemsLayout.getChildAt(i) instanceof EditItemView) {
                EditItemView item = (EditItemView) itemsLayout.getChildAt(i);
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
    }

}
