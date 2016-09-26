package com.example.testp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author houen.bao
 * @date Sep 26, 2016 1:47:17 PM
 */
public class EditItemView extends LinearLayout {

    private Context mContext;
    private String[] soundList = null;

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

        soundList = this.getResources().getStringArray(R.array.sound_list);

        AutoCompleteTextView actv = null;
    }

    private Button mDelete;
    private AutoCompleteTextView mSound;
    private OnDeleteListener mOnDeleteListener;

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mDelete = (Button) this.findViewById(R.id.delete);
        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mOnDeleteListener != null) {
                    mOnDeleteListener.onClick(EditItemView.this);
                }
            }
        });

        mSound = (AutoCompleteTextView) this.findViewById(R.id.sound);
        mSound.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, soundList));

    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        mOnDeleteListener = onDeleteListener;
    }

    public interface OnDeleteListener {
        public void onClick(View v);
    }

}
