package com.example.testp;

import java.io.IOException;
import java.util.Arrays;

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

    private final static int START_LOAD_INDEX = 0;
    private final static int MAX_SOUNDS = 88;

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

        new AsyncTask<Object, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Object... params) {
                // TODO Auto-generated method stub
                try {
                     loadAssetSounds();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("tt", "Exception " + e);
                }
                return mSoundId.length;
            }

            private void loadAssetSounds() throws IOException {
                String soundDir="sound";
                String[] mSounds = mAssetManager.list(soundDir);
                int sCount = mSounds.length;
                // sort
                String temp;
                String piano="piano";
                for (int i = 0; i < sCount - 1; i++) {
                    for (int j = i + 1; j < sCount; j++) {
                        String str1 = mSounds[i].substring(piano.length(), mSounds[i].indexOf("."));
                        int num1 = Integer.parseInt(str1);
                        String str2 = mSounds[j].substring(piano.length(), mSounds[j].indexOf("."));
                        int num2 = Integer.parseInt(str2);
                        if (num1 > num2) {
                            temp = mSounds[i];
                            mSounds[i] = mSounds[j];
                            mSounds[j] = temp;
                        }
                    }
                }
                
                mSoundId = new int[sCount];
                int count = 0;
                for (int i = START_LOAD_INDEX; i < sCount; i++) {
                    count++;
                    mSoundId[i] = mSoundPool.load(mAssetManager.openFd(soundDir+"/" + mSounds[i]), 1);
                    this.publishProgress(count);
                    if (count >= MAX_SOUNDS) {
                        break;
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Integer soundsLength) {
                // TODO Auto-generated method stub
                super.onPostExecute(soundsLength);
                dismissDialog();
                mWhiteLayout.removeAllViews();
                mBlackLayout.removeAllViews();

                int count = 0;
                for (int i = START_LOAD_INDEX; i < soundsLength; i++) {
                    count++;
                    if (isBlackKey(count)) {
                        mBlackLayout.addView(buildBlackKey(count, (i+1)));
                    } else {
                        mWhiteLayout.addView(buildWhiteKey(count, (i+1)));
                    }
                    if (count >= MAX_SOUNDS) {
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
        }.execute(null, null);

    }

    private View buildBlackKey(int keyIndex, int soundId) {
        Button view = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(BLACK_KEY_WIDTH,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(calcBlackKeyLeftMargin(keyIndex), 0, 0, 0);
        view.setLayoutParams(params);
        view.setOnClickListener(this);
        view.setTag(soundId);
        view.setBackgroundResource(R.drawable.black_key_selector);
        return view;
    }

    private final int BLACK_KEY_WIDTH = 2 * 40;
    private final int WHITE_KEY_WIDTH = 2 * 60;
    private final int WHITE_KEY_LEFT_MARGIN = 1;

    private final int KEY_START_INDEX = 2;
    private final int TOTAL_CELL_KEYS = 12;

    private int calcBlackKeyLeftMargin(int id) {
        int marginLeft = 0;
        switch (getEndResult(id - 3)) {
        case KEY_START_INDEX - 3://第一个黑键
            marginLeft = (WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX://两个黑键组的第一个黑键
            marginLeft = ((WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) * 2) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX + 2://两个黑键组的第二个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        case KEY_START_INDEX + 5://三个黑键组的第一个黑键
            marginLeft = ((WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) * 2) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX + 7://三个黑键组的第二个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        case KEY_START_INDEX + 9://三个黑键组的第三个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        }

        return marginLeft;
    }

    private View buildWhiteKey(int keyIndex, int soundId) {
        Button view = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WHITE_KEY_WIDTH,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(WHITE_KEY_LEFT_MARGIN, 0, 0, 5);
        view.setLayoutParams(params);
        view.setOnClickListener(this);
        view.setTag(soundId);
        view.setBackgroundResource(R.drawable.white_key_selector);
        return view;
    }

    private boolean isBlackKey(int count) {
        if (count <= TOTAL_CELL_KEYS + 3) {//第一组黑键特殊处理(因为多了第一个黑键)
            return count == KEY_START_INDEX || count == KEY_START_INDEX + 3 || count == KEY_START_INDEX + 5
                    || count == KEY_START_INDEX + 8 || count == KEY_START_INDEX + 10 || count == KEY_START_INDEX + 12;
        } else {
            int endResult = getEndResult(count - 3);
            return endResult == KEY_START_INDEX || endResult == KEY_START_INDEX + 2 || endResult == KEY_START_INDEX + 5
                    || endResult == KEY_START_INDEX + 7 || endResult == KEY_START_INDEX + 9;
        }
    }

    private int getEndResult(int count) {
        if (count <= TOTAL_CELL_KEYS) {
            return count;
        }
        return getEndResult(count - TOTAL_CELL_KEYS);
    }

    private ProgressDialog mDialog;

    private void createDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setMax(100);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setTitle("正在加载...");
        mDialog.show();
    }

    private void setDialogMaxProgress(int progress) {
        if (mDialog != null) {
            mDialog.setMax(progress);
            mDialog.setMessage("0/" + mDialog.getMax());
        }
    }

    private void setDialogProgress(int progress) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.setProgress(progress);
            mDialog.setMessage(progress + "/" + mDialog.getMax());
        }
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int sId = (Integer) v.getTag();
        mSoundPool.play(sId, 1, 1, 0, 0, 1);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mSoundPool.release();
    }

}
