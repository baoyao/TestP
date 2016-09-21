package com.example.testp;

import java.io.IOException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener {

    private final static int MAX_SOUNDS=77;
    
    private SoundPool mSoundPool;
    private int[] mSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout mWhiteLayout = (LinearLayout) this.findViewById(R.id.white_layout);
        final LinearLayout mBlackLayout = (LinearLayout) this.findViewById(R.id.black_layout);
        
        createDialog();
        setDialogMaxProgress(MAX_SOUNDS);
        
        final AssetManager mAssetManager = this.getAssets();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        
        new AsyncTask<Object, Integer, Integer>(){
            String[] mSounds;
            @Override
            protected Integer doInBackground(Object... params) {
                // TODO Auto-generated method stub
                try {
                    mSounds = mAssetManager.list("sound");
                    int sCount = mSounds.length;
                    //sort
                    String temp;
                    for (int i = 0; i < sCount - 1; i++) {
                        for (int j = i + 1; j < sCount; j++) {
                            String str1 = mSounds[i].substring(0, mSounds[i].indexOf("."));
                            int num1 = Integer.parseInt(str1);
                            String str2 = mSounds[j].substring(0, mSounds[j].indexOf("."));
                            int num2 = Integer.parseInt(str2);
                            if (num1 > num2) {
                                temp = mSounds[i];
                                mSounds[i] = mSounds[j];
                                mSounds[j] = temp;
                            }
                        }
                    }

                    int startIndex=0;
                    
                    mSoundId = new int[sCount];
                    int count1=0;
                    for (int i = startIndex; i < sCount; i++) {
                        count1++;
                        mSoundId[i] = mSoundPool.load(mAssetManager.openFd("sound/" + mSounds[i]), 1);
                        this.publishProgress(count1);
                        if (count1 >= MAX_SOUNDS) {
                            break;
                        }
                    }
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.v("tt","Exception "+e);
                }
                return mSoundId.length;
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Integer sCount) {
                // TODO Auto-generated method stub
                super.onPostExecute(sCount);
                dismissDialog();
                mWhiteLayout.removeAllViews();
                
                int startIndex=0;
                int count2=0;
                for (int i = startIndex; i < sCount; i++) {
                    count2++;
                    if(isBlackKey(count2)){
                        mBlackLayout.addView(buildBlackKey(count2, mSounds[i]));
                    }else{
                        mWhiteLayout.addView(buildWhiteKey(count2, mSounds[i]));
                    }
                    if (count2 >= MAX_SOUNDS) {
                        break;
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
                setDialogProgress(values[0]);
            }
        }.execute(null,null);
        
    }
    
    private View buildBlackKey(int id, String text) {
        Button view = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                70,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(calcBlackKeyLeftMargin(id), 0, 0, 0);
        view.setLayoutParams(params);
        view.setOnClickListener(this);
        view.setTag(id);
        view.setBackgroundResource(R.drawable.key_selector_b);
        return view;
    }

    private int calcBlackKeyLeftMargin(int id){
        int marginLeft=0;
        switch(getEndResult(id)){
        case 2:
            if((id/12)>0){
                marginLeft=130+1+1;
            }else{
                marginLeft=65+1+1;
            }
            break;
        case 4:
            marginLeft=30+1;
            break;
        case 7:
            marginLeft=130+1+1;
            break;
        case 9:
            marginLeft=30+1;
            break;
        case 11:
            marginLeft=30+1;
            break;
        }
        return marginLeft;
    }

    private View buildWhiteKey(int id, String text) {
        Button view = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(1, 0, 0, 5);
        view.setLayoutParams(params);
        view.setOnClickListener(this);
        view.setTag(id);
        
        if(getEndResult(id)==1||getEndResult(id)==6){
            view.setBackgroundResource(R.drawable.key_selector_r);
        }else if(getEndResult(id)==5||getEndResult(id)==12){
            view.setBackgroundResource(R.drawable.key_selector_l);
        }else{
            view.setBackgroundResource(R.drawable.key_selector_m);
        }
        
        return view;
    }
    
    private boolean isBlackKey(int id){
        return getEndResult(id)==2||getEndResult(id)==4
                ||getEndResult(id)==7||getEndResult(id)==9||getEndResult(id)==11;
    }

    private int getEndResult(int id){
        if(id<=12){
            return id;
        }
        return getEndResult(id-12);
    }
    
    private ProgressDialog mDialog;
    
    private void createDialog(){
        mDialog=new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setMax(100);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setTitle("正在加载...");
        mDialog.show();
    }
    
    private void setDialogMaxProgress(int progress){
        if(mDialog!=null){
            mDialog.setMax(progress);
            mDialog.setMessage("0/"+mDialog.getMax());
        }
    }
    
    private void setDialogProgress(int progress){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.setProgress(progress);
            mDialog.setMessage(progress+"/"+mDialog.getMax());
        }
    }
    
    private void dismissDialog(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int sId = (int) v.getTag();
        mSoundPool.play(sId, 1, 1, 0, 0, 1);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mSoundPool.release();
    }
    
}
