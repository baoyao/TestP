package com.example.testp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testp.EditItemView.OnSoundChangedListener;
import com.example.testp.EditItemView.OnTimeChangedListener;

/**
 * @author houen.bao
 * @date Sep 27, 2016 2:27:34 PM
 */
public class ItemPopupKeypad {

    private Context mContext;
    private String[] dataList;
    private Button mTagView;
    private PopupWindow mKeyPopupWindow;
    private EditItemView mItemView;
    private GridView mKeypadGridView;

    public ItemPopupKeypad(Context context) {
        this.mContext = context;
        dataList = mContext.getResources().getStringArray(R.array.sound_list);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.keypad, null);
        mKeypadGridView = (GridView) contentView.findViewById(R.id.keypad_gridview);
        mKeypadGridView.setAdapter(mKeypadAdapter);
        mKeypadGridView.setOnItemClickListener(mKeypadOnItemClickListener);
        mKeyPopupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mKeyPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.keypad_bg));
    }

    public void showSoundKeypad(EditItemView itemView,Button view, OnSoundChangedListener onSoundChangedListener) {
        mTagView = view;
        mItemView = itemView;
        mOnSoundChangedListener=onSoundChangedListener;
        mOnTimeChangedListener=null;
        dataList = mContext.getResources().getStringArray(R.array.sound_list);
        mKeyPopupWindow.showAsDropDown(view);
        mKeypadAdapter.notifyDataSetChanged();
        mScrollHandler.sendEmptyMessageDelayed(1, 100);
        mScrollHandler.sendEmptyMessageDelayed(1, 200);
    }
    
    private Handler mScrollHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mKeypadGridView.smoothScrollBy(250, 0);
        }
    };

    public void showTimeKeypad(EditItemView itemView,Button view, OnTimeChangedListener onTimeChangedListener) {
        mItemView = itemView;
        mTagView = view;
        mOnTimeChangedListener=onTimeChangedListener;
        mOnSoundChangedListener=null;
        if(view.getId()==R.id.time3){
            dataList = Utils.getMillTimeData();
        }else{
            dataList = Utils.getTimeData();
        }
        mKeypadAdapter.notifyDataSetChanged();
        mKeyPopupWindow.showAsDropDown(view);
    }
    
    private OnTimeChangedListener mOnTimeChangedListener;

    private OnSoundChangedListener mOnSoundChangedListener;

    private OnItemClickListener mKeypadOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            long startTime=0;
            if(mItemView!=null){
                startTime=Utils.parseTime(mItemView.getTime());
            }
            mTagView.setText(dataList[position]);
            mTagView.setTag(""+position);
            mKeyPopupWindow.dismiss();
            
            long endTime=0;
            if(mItemView!=null){
                endTime=Utils.parseTime(mItemView.getTime());
            }
            if(mOnTimeChangedListener != null){
                mOnTimeChangedListener.onChanged(mItemView,(endTime-startTime));
            }
            if(mOnSoundChangedListener != null){
                mOnSoundChangedListener.onChanged(mItemView);
            }
        }
    };

    private BaseAdapter mKeypadAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataList.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataList[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.keypad_item, null);
                holder.txt = (TextView) convertView.findViewById(R.id.txt);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.txt.setText(dataList[position]);
            return convertView;
        }

        class Holder {
            TextView txt;
        }
    };

}
