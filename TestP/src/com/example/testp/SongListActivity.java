package com.example.testp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

    private final String SONG_PATH = PublicConfig.SONG_PATH;
    private List<SongInfo> mSongList = new ArrayList<SongInfo>();
    private SongAdapter mSongAdapter;
    private TextView mFilePathTextView;
    private Button comfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.song_list);

        mFilePathTextView = (TextView) this.findViewById(R.id.file_path);
        comfirmButton = (Button) this.findViewById(R.id.comfirm);

        File file = new File(SONG_PATH);
        if (!file.exists()) {
            file.mkdirs();
            return;
        }

        String[] songs = file.list();
        if (songs == null || songs.length == 0) {
            return;
        } else {
            fileToObject(songs);
        }
        ListView listView = (ListView) this.findViewById(R.id.list);
        mSongAdapter = new SongAdapter();
        listView.setAdapter(mSongAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String soundJson = Utils.soundObjectParseToJson(mSongList.get(position).getSounds());
                jumpToMainActivityAndPlay(soundJson);
            }
        });
    }

    private void jumpToMainActivityAndPlay(String soundJson) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXT_SOUND_DATA, soundJson);
        intent.setClass(SongListActivity.this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onChooseButtonClick(View view) {
        switch (view.getId()) {
        case R.id.choose_file: {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 0);
            break;
        }
        case R.id.comfirm: {
            if (mFilePathTextView.getTag() != null) {
                jumpToMainActivityAndPlay(mFilePathTextView.getTag().toString());
            } else {
                Toast.makeText(this, "请选择歌曲文件", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String soundJson = data.getStringExtra(Constants.EXT_SOUND_DATA);
            if (soundJson != null) {
                reload();
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            soundJson = Utils.isSongFile(uri.getPath());
            if (soundJson != null) {
                comfirmButton.setVisibility(View.VISIBLE);
                String path = uri.getPath();
                mFilePathTextView.setText(path.length() > 40 ? path.substring(0, 40) : path);
                mFilePathTextView.setTag(soundJson);
            } else {
                Toast.makeText(this, "您选择的不是歌曲文件", Toast.LENGTH_LONG).show();
                mFilePathTextView.setText("");
                mFilePathTextView.setTag(null);
                comfirmButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        File file = new File(SONG_PATH);
        if (!file.exists()) {
            file.mkdirs();
            return;
        }

        String[] songs = file.list();
        if (songs == null || songs.length == 0) {
            return;
        }
        reload();
    }

    private void reload() {
        mSongList.clear();
        File file = new File(SONG_PATH);
        String[] songs = file.list();
        if (songs == null || songs.length == 0) {
            mSongAdapter.notifyDataSetChanged();
        } else {
            fileToObject(songs);
            mSongAdapter.notifyDataSetChanged();
        }
    }

    class SongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSongList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mSongList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(SongListActivity.this).inflate(R.layout.song_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.count = (TextView) convertView.findViewById(R.id.count);
                holder.delete = (Button) convertView.findViewById(R.id.delete);
                holder.edit = (Button) convertView.findViewById(R.id.edit);
                holder.share = (Button) convertView.findViewById(R.id.share);
                holder.delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(mSongList.get(position).getPath());
                        file.delete();
                        Toast.makeText(SongListActivity.this,
                                "Delete " + mSongList.get(position).getName() + " success", Toast.LENGTH_SHORT).show();
                        reload();
                        SongListActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                                .fromFile(file)));
                    }
                });
                holder.edit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SongListActivity.this, EditSongActivity.class);
                        intent.putExtra(Constants.EXT_SOUND_DATA,
                                Utils.soundObjectParseToJson(mSongList.get(position).getSounds()));
                        intent.putExtra(Constants.EXT_REQUEST_CODE, Constants.SONG_LIST_ACTIVITY_REQUEST_CODE);
                        intent.putExtra(Constants.EXT_SONG_NAME, mSongList.get(position).getName());
                        startActivityForResult(intent, Constants.SONG_LIST_ACTIVITY_REQUEST_CODE);
                    }
                });
                holder.share.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse("file:/" + mSongList.get(position).getPath());
                        intent.setDataAndType(uri, "text/plain");
                        startActivity(intent);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.name.setText(mSongList.get(position).getName());
            holder.count.setText(mSongList.get(position).getSounds().size() + "");
            return convertView;
        }

        class Holder {
            TextView name, count;
            Button delete, edit, share;
        }

    }

    private void fileToObject(String[] filelist) {
        try {
            for (int i = 0; i < filelist.length; i++) {
                SongInfo info = new SongInfo();
                info.setPath(SONG_PATH + "/" + filelist[i]);
                File file = new File(info.getPath());
                String fileContent = Utils.readTxtFile(file);
                List<SoundInfo> list = Utils.jsonParseToSoundObject(fileContent);
                info.setSounds(list);
                info.setName(filelist[i].substring(0, filelist[i].lastIndexOf(".")));
                mSongList.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class SongInfo {
        private String name;
        private String path;
        private List<SoundInfo> sounds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<SoundInfo> getSounds() {
            return sounds;
        }

        public void setSounds(List<SoundInfo> sounds) {
            this.sounds = sounds;
        }
    }

}
