package com.example.testp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author houen.bao
 * @date Sep 22, 2016 4:19:49 PM
 */
public class EditTextActivity extends Activity {

    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edittext_main);
        edit = (EditText) this.findViewById(R.id.txt);

    }

    public void onButtonClick(View view) {

        int spaceIndex = 4;
        Editable ea = edit.getText();
        int nowSelection = edit.getSelectionStart();
        String text = edit.getText().toString();

        switch (view.getId()) {
        case R.id.btn_del:
            if (nowSelection == 0) {
                return;
            }
            text = edit.getText().toString();

            boolean bool = (text.length() > 0 && " ".equals(text.substring(
                    nowSelection - 1, nowSelection)));
            if (bool) {
                ea.delete(nowSelection - 1, nowSelection);
                nowSelection -= 1;
            }

            if (nowSelection == 0) {
                return;
            }

            ea.delete(nowSelection - 1, nowSelection);
            nowSelection -= 1;
            break;
        default:
            ea.insert(nowSelection, ((Button) view).getText().toString());
            nowSelection += 1;
            break;
        }
        text = edit.getText().toString();

        int startSpace = text.length() / (spaceIndex + 1);

        text = text.replaceAll(" ", "");
        // 添加空格 begin
        if (text.length() >= spaceIndex) {
            for (int i = 0; i < text.length() / spaceIndex; i++) {
                int subIndex = i * (spaceIndex + 1);
                if (subIndex + spaceIndex > text.length()) {
                    break;
                }
                if (i == 0) {
                    text = text.substring(0, spaceIndex) + " "
                            + text.substring(spaceIndex);
                } else {
                    text = text.substring(0, subIndex + spaceIndex)
                            + " " + text.substring(subIndex + spaceIndex);
                }
            }
        }
        edit.setText(text);
        // 添加空格 end

        int endSpace = text.length() / (spaceIndex + 1);
        
        //移动光标
        Selection.setSelection((Spannable) edit.getText(), nowSelection + (endSpace - startSpace));
    }
    

    public void onButtonClick2(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

}
