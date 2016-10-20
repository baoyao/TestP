package com.example.testp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author houen.bao
 * @date Sep 27, 2016 2:27:34 PM
 */
public class GeneralPopupKeypad {

    private Context mContext;
    private String[] dataList;
    private View mTagView;
    private PopupWindow mKeyPopupWindow;
    private OnKeypadItemClickListener mOnKeypadItemClickListener;
    private GridView mKeypadGridView;

    public GeneralPopupKeypad(Context context) {
        this.mContext = context;
        dataList = mContext.getResources().getStringArray(R.array.sound_list);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.keypad, null);
        mKeypadGridView = (GridView) contentView.findViewById(R.id.keypad_gridview);
        mKeypadGridView.setAdapter(mKeypadAdapter);
        mKeypadGridView.setOnItemClickListener(mKeypadOnItemClickListener);
        mKeyPopupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mKeyPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.keypad_bg));
    }

    public void showKeypad(View view, String[] data, OnKeypadItemClickListener onKeypadItemClickListener) {
        mTagView = view;
        dataList = data;
        setmOnKeypadItemClickListener(onKeypadItemClickListener);
        mKeypadAdapter.notifyDataSetChanged();
        mKeyPopupWindow.showAsDropDown(view);
    }

    private OnItemClickListener mKeypadOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
//            if (mTagView instanceof Button) {
//                ((Button) mTagView).setText(dataList[position]);
//            }
//            mTagView.setTag("" + position);
            mKeyPopupWindow.dismiss();
            if (mOnKeypadItemClickListener != null) {
                mOnKeypadItemClickListener.onItemClick(position, view, mTagView);
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

    public void setmOnKeypadItemClickListener(OnKeypadItemClickListener mOnKeypadItemClickListener) {
        this.mOnKeypadItemClickListener = mOnKeypadItemClickListener;
    }

    public interface OnKeypadItemClickListener {
        void onItemClick(int position, View clickItemView, View tagView);
    }

}
