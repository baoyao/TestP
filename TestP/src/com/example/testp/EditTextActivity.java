package com.example.testp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
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

        String SPLIT_TEXT=" ";
        int SPLIT_INDEX = 4;
        
        Editable ea = edit.getText();
        int startSelection = edit.getSelectionStart();
        int currentSelection = startSelection;
        String text = edit.getText().toString();

        int startSpaces = countTextsInContent(text,SPLIT_TEXT);
        
        switch (view.getId()) {
        case R.id.btn_del:
            if (currentSelection == 0) {
                return;
            }
            text = edit.getText().toString();

            boolean bool = (text.length() > 0 && SPLIT_TEXT.equals(text.substring(
                    currentSelection - 1, currentSelection)));
            if (bool) {
                ea.delete(currentSelection - 1, currentSelection);
                ea.delete(currentSelection - 1-1, currentSelection-1);
                currentSelection -= 1;
            }else{
                ea.delete(currentSelection - 1, currentSelection);
                currentSelection -= 1;
            }

            break;
        default:
            ea.insert(currentSelection, ((Button) view).getText().toString());
            currentSelection += 1;
            break;
        }
        
        text = edit.getText().toString();
        text=splitString(text,SPLIT_TEXT,SPLIT_INDEX);
        edit.setText(text);

        int endSpaces = countTextsInContent(text,SPLIT_TEXT);
        int changeSpaces=(endSpaces - startSpaces);

        int insertSelection=0;
        
        if((currentSelection + changeSpaces)!=text.length()){//在中间插入数字时
            if(view.getId()==R.id.btn_del){
                if(changeSpaces==0){
                    if(startSelection%5==0){
                        insertSelection=-1;
                    }
                }else if(changeSpaces==-1){
                    if(startSelection%5!=0){
                        insertSelection=1;
                    }
                }
            }else{
                if(changeSpaces==0){
                    if(startSelection%5==4){
                        insertSelection=1;
                    }
                }else if(changeSpaces==1){
                    if(startSelection%5!=4){
                        insertSelection=-1;
                    }
                }
            }
        }
        //移动光标
        Selection.setSelection((Spannable) edit.getText(), insertSelection+currentSelection + changeSpaces);
    }
    

    private int countTextsInContent(String content, String text) {
        int count = 0;
        while (content.indexOf(text) != -1) {
            count++;
            content = content.substring(content.indexOf(text) + text.length());
        }
        return count;
    }
    
    private String splitString(String content,String splitText,int splitIndex){
        if (content.length() >= splitIndex) {
            content = content.replaceAll(splitText, "");
            for (int i = 0; i < content.length() / splitIndex; i++) {
                int subIndex = i * (splitIndex + 1);
                if (subIndex + splitIndex > content.length()) {
                    break;
                }
                if (i == 0) {
                    content = content.substring(0, splitIndex) + splitText
                            + content.substring(splitIndex);
                } else {
                    content = content.substring(0, subIndex + splitIndex)
                            + splitText + content.substring(subIndex + splitIndex);
                }
            }
        }
        return content;
    }
    

    public void onButtonClick2(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

}
