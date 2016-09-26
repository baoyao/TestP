package com.example.testp;

import com.example.testp.EditItemView.OnDeleteListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

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
        EditItemView editItemView = (EditItemView) LayoutInflater.from(this).inflate(R.layout.edit_item, null);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(400,500);
        editItemView.setLayoutParams(params);
        editItemView.setPadding(5, 5, 5, 5);
        editItemView.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onClick(View v) {
                itemsLayout.removeView(v);
            }
        });
        itemsLayout.addView(editItemView, itemsLayout.getChildCount() - 1);
    }

    public void onSaveButtonClick(View view) {

    }

}
