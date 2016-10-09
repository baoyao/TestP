package com.example.testp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author houen.bao
 * @date Oct 9, 2016 10:56:42 AM
 */
public class SongListActivity extends Activity {

    private final String SONG_PATH=PublicConfig.SONG_PATH;
    private List<List<SoundInfo>> mSoundList=new ArrayList<List<SoundInfo>>();
    private String[] songs;
    private Gson mGson;
    private SongAdapter mSongAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.song_list);
        mGson=new Gson();
        File file=new File(SONG_PATH);
        if(!file.exists()){
            file.mkdirs();
            return;
        }
        
        songs=file.list();
        if(songs==null||songs.length==0){
            return;
        }else{
            fileToObject(songs);
        }
        ListView listView=(ListView) this.findViewById(R.id.list);
        mSongAdapter=new SongAdapter();
        listView.setAdapter(mSongAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String soundJson=mGson.toJson(mSoundList.get(position), new TypeToken<List<SoundInfo>>() {
                }.getType());
                Intent intent=new Intent();
                intent.putExtra(Constants.EXT_SOUND_DATA, soundJson);
                intent.setClass(SongListActivity.this, MainActivity.class);
                SongListActivity.this.setResult(RESULT_OK, intent);
                SongListActivity.this.finish();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        File file=new File(SONG_PATH);
        if(!file.exists()){
            file.mkdirs();
            return;
        }
        
        songs=file.list();
        if(songs==null||songs.length==0){
            return;
        }
        reload();
    }

    private void reload(){
        mSoundList.clear();
        File file=new File(SONG_PATH);
        songs=file.list();
        if(songs==null||songs.length==0){
            mSongAdapter.notifyDataSetChanged();
        }else{
            fileToObject(songs);
            mSongAdapter.notifyDataSetChanged();
        }
    }
    
    class SongAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSoundList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mSoundList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=null;
            if(convertView==null){
                holder=new Holder();
                convertView=LayoutInflater.from(SongListActivity.this).inflate(R.layout.song_item, null);
                holder.name=(TextView) convertView.findViewById(R.id.name);
                holder.count=(TextView) convertView.findViewById(R.id.count);
                holder.delete=(Button) convertView.findViewById(R.id.delete);
                holder.delete.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        File file=new File(SONG_PATH+"/"+songs[position]);
                        file.delete();
                        Toast.makeText(SongListActivity.this, "Delete "+songs[position]+" success", Toast.LENGTH_SHORT).show();
                        reload();
                    }
                });
                convertView.setTag(holder);
            }else{
                holder=(Holder) convertView.getTag();
            }
            holder.name.setText(songs[position]);
            holder.count.setText(mSoundList.get(position).size()+"");
            return convertView;
        }
        
        class Holder{
            TextView name,count;
            Button delete;
        }
        
    }
    
    private void fileToObject(String[] filelist){
        for(int i=0;i<filelist.length;i++){
            File file=new File(SONG_PATH+"/"+filelist[i]);
            String fileContent=readTxtFile(file);
            List<SoundInfo> list=mGson.fromJson(fileContent,new TypeToken<List<SoundInfo>>(){}.getType());
            mSoundList.add(list);
        }
    }

    private String readTxtFile(File fileName) {
        String result = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            String read = "";
            while ((read = bufferedReader.readLine()) != null) {
                result = result + read + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
