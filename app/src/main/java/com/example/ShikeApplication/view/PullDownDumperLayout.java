package com.example.ShikeApplication.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import static java.lang.Thread.sleep;

public class PullDownDumperLayout extends LinearLayout implements View.OnTouchListener {

    private View mHeadLayout;
    private int mHeadLayoutHeight;
    private MarginLayoutParams mHeadLayoutParams;
    private boolean mOnLayoutIsInit=false;  // 判断是否为第一次初始化，第一次初始化需要把headView移出界面外

    private float mMoveY;  // 移动时，前一个坐标
    private boolean mChangeHeadLayoutTopMargin; // 如果为false，会退出头部展开或隐藏动画
    private int mBoundary;  // 触发动画的分界线，由mRatio计算得到

    private int mHeadLayoutHideSpeed;
    private int mHeadLayoutUnfoldSpeed;
    private long mSleepTime;

    private double mRatio;  // 头部布局上半部分和整体高度的比例

    public PullDownDumperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeadLayoutHideSpeed=-20;  // 初始化参数，根据自己的需求调整
        mHeadLayoutUnfoldSpeed=20;
        mSleepTime=10;
        mRatio=0.5;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!mOnLayoutIsInit && changed) {
            mHeadLayout = this.getChildAt(0);
            mHeadLayoutHeight=-mHeadLayout.getHeight();
            mBoundary=(int)(mRatio*mHeadLayoutHeight);
            mHeadLayoutParams=(MarginLayoutParams) mHeadLayout.getLayoutParams();
            mHeadLayoutParams.topMargin=mHeadLayoutHeight;
            mHeadLayout.setLayoutParams(mHeadLayoutParams);
            getChildAt(1).setOnTouchListener(this);  // android:clickable="true"
            mHeadLayout.setOnTouchListener(this);
            mOnLayoutIsInit=true;  // 标记已被初始化
        }
    }

    /**
     * 屏幕触摸操作监听器
     * @return false则注册本监听器的控件将不会对事件做出响应，true则相反
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveY=event.getRawY();  // 捕获按下时的坐标，初始化mMoveY
                mChangeHeadLayoutTopMargin=false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currY=event.getRawY();
                int vector=(int)(currY-mMoveY);  // 向量，用于判断手势的上滑和下滑
                mMoveY=currY;  // 判断是否为滑动
                if(Math.abs(vector)==0){
                    return false;
                }
                if (vector < 0 && mHeadLayoutParams.topMargin <= mHeadLayoutHeight) {  // 头部完全隐藏时不再向上滑动
                    return false;
                }
                if (vector > 0 && mHeadLayoutParams.topMargin >= 0) {  // 头部完全展开时不再向下滑动
                    return false;
                }
                int topMargin = mHeadLayoutParams.topMargin + (vector/2);//阻尼值
                if(topMargin>0){
                    /* 瞬间拉动的距离超过了头部高度，因为这一瞬间很短，这里采用直接赋值的方式
                     如需平滑过渡，要另开线程，并且监听到ACTION_DOWN时线程可被打断*/
                    topMargin = 0;
                }
                else if(topMargin<mHeadLayoutHeight){
                    /* 瞬间拉动的距离超过了头部高度，因为这一瞬间很短，这里采用直接赋值的方式
                     如需平滑过渡，要另开线程，并且监听ACTION_DOWN时线程可被打断*/
                    topMargin = mHeadLayoutHeight;
                }
                //用户对屏幕的滑动将会改变控件的TopMargin
                mHeadLayoutParams.topMargin = topMargin ;
                mHeadLayout.setLayoutParams(mHeadLayoutParams);
                break;
            default:
                mChangeHeadLayoutTopMargin=true;
                /*if(mHeadLayoutParams.topMargin<=mBoundary){
                    //隐藏
                    new MoveHeaderTask().execute(true);
                }
                else{
                    //展开
                    new MoveHeaderTask().execute(false);
                }*/
                new MoveHeaderTask().execute(true);
                break;
        }
        return false;
    }

    /**
     * 新线程，隐藏或者展开头部布局，线程可被ACTION_DOWN打断
     */
    class MoveHeaderTask extends AsyncTask<Boolean, Integer, Integer> {
        /**
         * @param opt true为隐藏动画，false为展开动画
         * @return
         */
        @Override
        protected Integer doInBackground(Boolean... opt) {
            int topMargin=mHeadLayoutParams.topMargin;
            int speed=(opt[0])?mHeadLayoutHideSpeed:mHeadLayoutUnfoldSpeed; //true为隐藏，false为展开
            while(mChangeHeadLayoutTopMargin){
                topMargin += speed;
                if (topMargin <= mHeadLayoutHeight||topMargin>=0) {
                    topMargin=(opt[0])?mHeadLayoutHeight:0;
                    publishProgress(topMargin);
                    break;
                }
                publishProgress(topMargin);
                try {
                    sleep(mSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            mHeadLayoutParams.topMargin=topMargin[0];
            mHeadLayout.setLayoutParams(mHeadLayoutParams);
        }
    }

    public void setHeadLayoutHideSpeed(int speed) {  // 调整参数
        this.mHeadLayoutHideSpeed=speed;
    }
    public void setHeadLayoutUnfoldSpeed(int speed){
        this.mHeadLayoutUnfoldSpeed=speed;
    }
    public void setSleepTime(long time){
        this.mSleepTime=time;
    }
    public void setRatio(double ratio){
        this.mRatio=ratio;
    }
}

/*
 * https://www.jb51.net/article/163124.htm
 */
