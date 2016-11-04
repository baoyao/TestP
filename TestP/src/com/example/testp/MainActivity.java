package com.example.testp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.winplus.serial.utils.SerialPortActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.example.testp.KeyButton.OnTouchDownListener;
import com.example.testp.RhythmController.TimeRecord;

public class MainActivity extends SerialPortActivity implements OnTouchDownListener {

    private final static int START_LOAD_INDEX = 0;
    private final static int MAX_SOUNDS = PublicConfig.MAX_SOUNDS;

    private SoundPool mSoundPool;
    private SoundPlayer mSoundPlayer;
    
    private String mSongJson;
    private int[] mSoundId;
    private ResultLayout mResult;
    private SeekBar mSpeedController;
    private TextView mSpeedValaue;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mWhiteLayout, mBlackLayout, mKeyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent it=this.getIntent();
        final Uri uri=it.getData();
        if(uri!=null){
            mSongJson=Utils.isSongFile(uri.getPath());
            if(mSongJson==null){
                Toast.makeText(this, "您选择的不是歌曲文件", Toast.LENGTH_LONG).show();
                this.finish();
                return;
            }
        }
        mWhiteLayout = (LinearLayout) this.findViewById(R.id.white_layout);
        mBlackLayout = (LinearLayout) this.findViewById(R.id.black_layout);
        mKeyLayout = (LinearLayout) this.findViewById(R.id.key_layout);
        mHorizontalScrollView = (HorizontalScrollView) this.findViewById(R.id.scrollview);
        
        mResult=(ResultLayout) this.findViewById(R.id.result_layout);
        ((Button)this.findViewById(R.id.mute)).setText(Utils.getConfiguration(this,Constants.KEY_MUTE, false)?"打开弹奏音":"关闭弹奏音");
        mSpeedController=(SeekBar) this.findViewById(R.id.speed_controller);
        mSpeedValaue=(TextView) this.findViewById(R.id.speed_vlaue);
        mSpeedController.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        
        int[] screenSize=Utils.getScreenSize(this);
        ((TextView)this.findViewById(R.id.pf)).setText(screenSize[0]+"x"+screenSize[1]+" | "+screenSize[2]);
        
        Button switchKeyButton=(Button) this.findViewById(R.id.switch_sounds);
        if(Utils.getConfiguration(this, Constants.KEY_IS_JP, false)){
            switchKeyButton.setText("极品钢琴键音");
        }else{
            switchKeyButton.setText("真实钢琴键音");
        }
        
        loadSoundPool();
    }
    
    private void loadSoundPool(){
        mWhiteLayout.removeAllViews();
        mBlackLayout.removeAllViews();
        createDialog();
        setDialogMaxProgress(MAX_SOUNDS);

        final AssetManager mAssetManager = this.getAssets();
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

        new AsyncTask<Object, Integer, Integer>() {
            String soundDir = "sound";
            
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(Utils.getConfiguration(MainActivity.this, Constants.KEY_IS_JP, false)){
                    soundDir = "sound_jp";
                }
            }
            
            @Override
            protected Integer doInBackground(Object... params) {
                // TODO Auto-generated method stub
                try {
                    loadAssetSounds();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v(this.toString(), "Exception " + e);
                }
                return mSoundId.length;
            }

            private void loadAssetSounds() throws IOException {
                
                String[] mSounds = mAssetManager.list(soundDir);
                int sCount = mSounds.length;
                // sort
                String temp;
                String piano = "piano";
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
                    mSoundId[i] = mSoundPool.load(mAssetManager.openFd(soundDir + "/" + mSounds[i]), 1);
                    this.publishProgress(count);
                    if (count >= MAX_SOUNDS) {
                        break;
                    }
                }
            }

            @Override
            protected void onPostExecute(Integer soundsLength) {
                // TODO Auto-generated method stub
                super.onPostExecute(soundsLength);
                mWhiteLayout.removeAllViews();
                mBlackLayout.removeAllViews();
                calcKeyWidth();
                
                List<KeyButton> keyButtonList=new ArrayList<KeyButton>();
                int count = 0;
                for (int i = START_LOAD_INDEX; i < soundsLength; i++) {
                    count++;
                    KeyButton keyButton=null;
                    if (isBlackKey(count)) {
                        keyButton=buildBlackKey(count, (i + 1));
                        mBlackLayout.addView(keyButton);
                    } else {
                        keyButton=buildWhiteKey(count, (i + 1));
                        mWhiteLayout.addView(keyButton);
                    }
                    keyButtonList.add(keyButton);
                    if (count >= MAX_SOUNDS) {
                        break;
                    }
                }

                ViewGroup.LayoutParams bParams=(ViewGroup.LayoutParams)mBlackLayout.getLayoutParams();
                bParams.height=mWhiteLayout.getHeight()/2;
                mBlackLayout.setLayoutParams(bParams);

                mSoundPlayer = new SoundPlayer(MainActivity.this,mSoundPool,keyButtonList);
                dismissDialog();
                if(mSongJson != null){
                    play();
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
    
    private OnSeekBarChangeListener mOnSeekBarChangeListener=new OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int p=0;
            if(progress!=(seekBar.getMax()/2)){
                p=progress-(seekBar.getMax()/2);
            }
            mSpeedValaue.setText(""+p);
            mSoundPlayer.setSpeed(p);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            
        }
    };
    
    private KeyButton buildBlackKey(int keyIndex, int soundId) {
        KeyButton view = new KeyButton(this);
        view.setKeyId(keyIndex);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(BLACK_KEY_WIDTH,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(calcBlackKeyLeftMargin(keyIndex), 0, 0, 0);
        view.setLayoutParams(params);
        view.setOnTouchDownListener(this);
        view.setSoundId(soundId);
        view.setBackgroundResource(R.drawable.black_key_selector);
        return view;
    }
    
    private void calcKeyWidth(){
        int[] size=Utils.getScreenSize(MainActivity.this);
        WHITE_KEY_WIDTH=size[0]/52-WHITE_KEY_LEFT_MARGIN;//52w 36b
        BLACK_KEY_WIDTH=(WHITE_KEY_WIDTH/3)*2;
        
        PublicConfig.BLACK_KEY_WIDTH = BLACK_KEY_WIDTH;
        PublicConfig.WHITE_KEY_WIDTH = WHITE_KEY_WIDTH;
        
        FrameLayout.LayoutParams sParams=(FrameLayout.LayoutParams)mHorizontalScrollView.getLayoutParams();
        sParams.leftMargin=(size[0]%52)/2;
        mHorizontalScrollView.setLayoutParams(sParams);
    }

    private int BLACK_KEY_WIDTH = PublicConfig.BLACK_KEY_WIDTH;
    private int WHITE_KEY_WIDTH = PublicConfig.WHITE_KEY_WIDTH;
    private final int WHITE_KEY_LEFT_MARGIN = PublicConfig.WHITE_KEY_LEFT_MARGIN;

    private final int KEY_START_INDEX = 2;
    private final int TOTAL_CELL_KEYS = 12;

    private int calcBlackKeyLeftMargin(int id) {
        int marginLeft = 0;
        switch (getEndKeyNum(id - 3)) {
        case KEY_START_INDEX - 3:// 第一个黑键
            marginLeft = (WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX:// 两个黑键组的第一个黑键
            marginLeft = ((WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) * 2) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX + 2:// 两个黑键组的第二个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        case KEY_START_INDEX + 5:// 三个黑键组的第一个黑键
            marginLeft = ((WHITE_KEY_WIDTH - (BLACK_KEY_WIDTH / 2)) * 2) + (WHITE_KEY_LEFT_MARGIN * 2);
            break;
        case KEY_START_INDEX + 7:// 三个黑键组的第二个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        case KEY_START_INDEX + 9:// 三个黑键组的第三个黑键
            marginLeft = (WHITE_KEY_WIDTH - BLACK_KEY_WIDTH) + WHITE_KEY_LEFT_MARGIN;
            break;
        }

        return marginLeft;
    }

    private KeyButton buildWhiteKey(int keyIndex, int soundId) {
        KeyButton view = new KeyButton(this);
        view.setKeyId(keyIndex);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WHITE_KEY_WIDTH,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(WHITE_KEY_LEFT_MARGIN, 0, 0, 5);
        view.setLayoutParams(params);
        view.setOnTouchDownListener(this);
        view.setSoundId(soundId);
        view.setBackgroundResource(R.drawable.white_key_selector);
        return view;
    }

    private boolean isBlackKey(int count) {
        if (count <= TOTAL_CELL_KEYS + 3) {// 第一组黑键特殊处理(因为多了第一个黑键)
            return count == KEY_START_INDEX || count == KEY_START_INDEX + 3 || count == KEY_START_INDEX + 5
                    || count == KEY_START_INDEX + 8 || count == KEY_START_INDEX + 10 || count == KEY_START_INDEX + 12;
        } else {
            int endResult = getEndKeyNum(count - 3);
            return endResult == KEY_START_INDEX || endResult == KEY_START_INDEX + 2 || endResult == KEY_START_INDEX + 5
                    || endResult == KEY_START_INDEX + 7 || endResult == KEY_START_INDEX + 9;
        }
    }

    private int getEndKeyNum(int count) {
        if (count <= TOTAL_CELL_KEYS) {
            return count;
        }
        return getEndKeyNum(count - TOTAL_CELL_KEYS);
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
    public void onTouchDown(View v) {
        int sId = ((KeyButton) v).getSoundId();
        mSoundPool.play(sId, 1, 1, 0, 0, 1);
        showResult(sId);
    }
    
    private void showResult(int soundId){
        List<TimeRecord> record=mSoundPlayer.getRecord();
        long currentTime=System.currentTimeMillis();
        boolean isFind=false;
        for(int i=0;i<record.size();i++){
            if(record.get(i).soundIndex==soundId){
                isFind=true;
                if(isMiddleOfTheGiveTime(currentTime,record.get(i).endTime)){
                    //great
                    mResult.showResult(ResultLayout.RESULT_1);
                    return;
                }
                if(isMiddleOfTheGiveTime(currentTime,record.get(i).preTime)){
                    mResult.showResult(ResultLayout.RESULT_2);
                    return;
                }
                mResult.showResult(ResultLayout.RESULT_3);
                break;
            }
        }
        if(mSoundPlayer!=null && mSoundPlayer.isPlaying() && !isFind){
            mResult.showResult(ResultLayout.RESULT_3);
        }
    }
    
    private boolean isMiddleOfTheGiveTime(long currentTime,long tagTime){
        if(tagTime==0){
            return false;
        }
        if(currentTime>=(tagTime-500)&&currentTime<=(tagTime+500)){
            return true;
        }
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            mSongJson=data.getStringExtra(Constants.EXT_SOUND_DATA);
            if(mSongJson!=null){
                play();
            }
        }
    }
    
    private void play(){
        if(mSoundPlayer != null){
            mSoundPlayer.play(mSongJson);
            ((Button)this.findViewById(R.id.pause_resume)).setText("暂停播放");
        }
    }

    public void onControlButtonClick(View view) {
        switch (view.getId()) {
        case R.id.jump_to_edit:
            Intent intent=new Intent(MainActivity.this, EditSongActivity.class);
            intent.putExtra(Constants.EXT_REQUEST_CODE, Constants.MAIN_ACTIVITY_REQUEST_CODE);
            startActivityForResult(intent, Constants.MAIN_ACTIVITY_REQUEST_CODE);
            break;
        case R.id.play:
            if(mSoundPlayer!=null && mSongJson!=null){
                play();
            }
            break;
        case R.id.pause_resume:
            if(mSoundPlayer!=null){
                if(((Button)view).getText().toString().equals("暂停播放")){
                    mSoundPlayer.pause();
                    ((Button)view).setText("继续播放");
                }else{
                    mSoundPlayer.resume();
                    ((Button)view).setText("暂停播放");
                }
            }
            break;
        case R.id.stop:
            if(mSoundPlayer!=null){
                mSoundPlayer.stop();
            }
            break;
        case R.id.songlist:
            startActivityForResult(new Intent(MainActivity.this, SongListActivity.class), Constants.MAIN_ACTIVITY_REQUEST_CODE);
            break;
        case R.id.mute:
            if(Utils.getConfiguration(this,Constants.KEY_MUTE, false)){
                ((Button)view).setText("关闭弹奏音");
                Utils.saveConfiguration(this,Constants.KEY_MUTE, false);
            }else{
                ((Button)view).setText("打开弹奏音");
                Utils.saveConfiguration(this,Constants.KEY_MUTE, true);
            }
            break;
        case R.id.switch_sounds:
            if(Utils.getConfiguration(this, Constants.KEY_IS_JP, false)){
                ((Button)view).setText("真实钢琴键音");
                Utils.saveConfiguration(this, Constants.KEY_IS_JP, false);
            }else{
                ((Button)view).setText("极品钢琴键音");
                Utils.saveConfiguration(this, Constants.KEY_IS_JP, true);
            }

            if(mSoundPlayer!=null){
                mSoundPlayer.stop();
            }
            
            if(mSoundPool != null){
                mSoundPool.release();
            }
            
            loadSoundPool();
            break;
        default:
            break;
        }
    }
    
    public void onMoreButtonClick(View view){
        View controllerView=this.findViewById(R.id.controller_layout);
        if(controllerView.getVisibility()==View.INVISIBLE){
            controllerView.setVisibility(View.VISIBLE);
        }else{
            controllerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSoundPlayer!=null){
            mSoundPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSoundPlayer!=null){
            mSoundPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mSoundPool!=null){
            mSoundPool.release();
        }
    }

    @Override
    public void writeDataToSerialPort(String data) {
        super.writeDataToSerialPort(data);
    }

    @Override
    public void onSerialPortCallback(String data) {
        super.onSerialPortCallback(data);
    }

}
