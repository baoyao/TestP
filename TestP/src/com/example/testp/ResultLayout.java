package com.example.testp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author houen.bao
 * @date Oct 17, 2016 11:02:20 AM
 */
public class ResultLayout extends RelativeLayout {

    public static final int RESULT_1 = 1;
    public static final int RESULT_2 = 2;
    public static final int RESULT_3 = 3;

    private Context mContext;

    public ResultLayout(Context context) {
        this(context, null, 0);
    }

    public ResultLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResultLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void showResult(int result) {
        createResultView(result);
        refreshView();
    }

    private void createResultView(int result) {
        ImageView img = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                30);
        int res=0;
        if(result==RESULT_1){
            res=R.drawable.cool_img;
        }
        if(result==RESULT_2){
            res=R.drawable.bad_img;
        }
        if(result==RESULT_3){
            res=R.drawable.miss_img;
        }
        img.setImageResource(res);
        params.topMargin=this.getHeight()-img.getHeight();
        img.setLayoutParams(params);
        this.addView(img, this.getChildCount());
    }
    
    private void refreshView(){
        if(this.getChildCount()==1){
            mHandler.sendEmptyMessage(1);
        }
    }
    
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            for(int i=0;i<ResultLayout.this.getChildCount();i++){
                RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)ResultLayout.this.getChildAt(i).getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin-10, params.rightMargin, params.bottomMargin);
                ResultLayout.this.getChildAt(i).setLayoutParams(params);
                if(params.topMargin<0){
                    ResultLayout.this.removeViewAt(i);
                }
            }
            if(ResultLayout.this.getChildCount()>0){
                mHandler.sendEmptyMessageDelayed(1, 20);
            }
        }
    };

}
