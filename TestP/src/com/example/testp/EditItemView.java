package com.example.testp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author houen.bao
 * @date Sep 26, 2016 1:47:17 PM
 */
public class EditItemView extends LinearLayout implements OnClickListener {

    private Context mContext;
    private int mIndex;

    private Button mDelete, mAdd;
    private Button mSound, mTime1, mTime2, mTime3;
    private OnDeleteListener mOnDeleteListener;
    private OnAddListener mOnAddListener;

    private TextView mIndexView;
    private ItemPopupKeypad mKeypad;
    private String[] mTimeList,mMillTimeList,mSoundList;
    
    private OnTimeChangedListener mOnTimeChangedListener;

    public EditItemView(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    public EditItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public EditItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        mTimeList = Utils.getTimeData();
        mMillTimeList=Utils.getMillTimeData();
        mSoundList = mContext.getResources().getStringArray(R.array.sound_list);
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener){
        mOnTimeChangedListener=onTimeChangedListener;
    }

    public void setIndex(int index) {
        mIndex = index;
        mIndexView.setText("" + index);
    }

    public int getIndex() {
        return mIndex;
    }
    
    public void setSound(int soundIndex){
        mSound.setText(mSoundList[soundIndex-1]);
        mSound.setTag((soundIndex-1)+"");
    }

    public int getSoundIndex() {
        return mSound.getTag() == null ? 1 : Integer.parseInt(mSound.getTag().toString())+1;
    }

    public int[] getTime() {
        int t1 = mTime1.getTag() == null ? 0 : Integer.parseInt(mTime1.getTag().toString());
        int t2 = mTime2.getTag() == null ? 0 : Integer.parseInt(mTime2.getTag().toString());
        int t3 = mTime3.getTag() == null ? 0 : Integer.parseInt(mTime3.getTag().toString());
        return new int[] { t1, t2, t3 };
    }
    
    public void setTime(int[] time){
        mTime1.setText(mTimeList[time[0]]);
        mTime2.setText(mTimeList[time[1]]);
        mTime3.setText(mMillTimeList[time[2]]);
        mTime1.setTag(""+time[0]);
        mTime2.setTag(""+time[1]);
        mTime3.setTag(""+time[2]);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mDelete = (Button) this.findViewById(R.id.delete);
        mDelete.setOnClickListener(this);

        mAdd = (Button) this.findViewById(R.id.add);
        mAdd.setOnClickListener(this);

        mIndexView = (TextView) this.findViewById(R.id.index);

        mKeypad = new ItemPopupKeypad(mContext);

        mSound = (Button) this.findViewById(R.id.sound);
        mSound.setOnClickListener(this);

        mTime1 = (Button) this.findViewById(R.id.time1);
        mTime2 = (Button) this.findViewById(R.id.time2);
        mTime3 = (Button) this.findViewById(R.id.time3);
        mTime1.setOnClickListener(this);
        mTime2.setOnClickListener(this);
        mTime3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.delete:
            if (mOnDeleteListener != null) {
                mOnDeleteListener.onClick(this);
            }
            break;
        case R.id.add:
            if (mOnAddListener != null) {
                mOnAddListener.onClick(getIndex(), this);
            }
            break;
        case R.id.sound:
            mKeypad.showSoundKeypad(mSound);
            break;
        case R.id.time1:
            mKeypad.showTimeKeypad(this,mTime1,mOnTimeChangedListener);
            break;
        case R.id.time2:
            mKeypad.showTimeKeypad(this,mTime2,mOnTimeChangedListener);
            break;
        case R.id.time3:
            mKeypad.showTimeKeypad(this,mTime3,mOnTimeChangedListener);
            break;
        default:
            break;
        }
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        mOnDeleteListener = onDeleteListener;
    }

    public void setOnAddListener(OnAddListener onAddListener) {
        mOnAddListener = onAddListener;
    }

    public interface OnDeleteListener {
        public void onClick(View v);
    }

    public interface OnAddListener {
        public void onClick(int index, View v);
    }
    
    public interface OnTimeChangedListener {
        public void onChanged(EditItemView itemView,long changedTime);
    }

}
