package com.jcodecraeer.xrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by kuson on 17/4/11.
 */

public class RunningBoyHeader extends LinearLayout implements BaseRefreshHeader {

    private LinearLayout mContainer;

    private RelativeLayout refreshIconLayout;

    private ImageView mRefreshBoy;

    private ImageView mRefreshBox;

    private ImageView mRunningBoy;

    private TextView mShowText;

    private AnimationDrawable runAnim;

    private int mState = STATE_NORMAL;

    public int mMeasuredHeight;

    private int iconWidth = 0;

    private int iconHeight = 0;

    private int iconTopMax = 0;

    public RunningBoyHeader(Context context) {
        super(context);
        initView();
    }

    public RunningBoyHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.refresh_header_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        refreshIconLayout = (RelativeLayout) mContainer.findViewById(R.id.refresh_icon_layout);
        mRefreshBoy = (ImageView) mContainer.findViewById(R.id.refresh_boy);
        mRefreshBox = (ImageView) mContainer.findViewById(R.id.refresh_box);
        mRunningBoy = (ImageView) mContainer.findViewById(R.id.running_boy);
        mShowText = (TextView) mContainer.findViewById(R.id.show_text_view);

        mRunningBoy.setImageResource(R.drawable.runningman);
        runAnim = (AnimationDrawable) mRunningBoy.getDrawable();

        measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();

        iconWidth = mRefreshBoy.getMeasuredWidth();
        iconHeight = mRefreshBoy.getMeasuredHeight();
        ViewGroup.LayoutParams params = refreshIconLayout.getLayoutParams();
        params.width = iconWidth;
        refreshIconLayout.setLayoutParams(params);
        iconTopMax = DeviceUtil.dipToPx(getContext(), 72) - iconHeight;
    }

    public void setState(int state) {
        if (state == mState) return ;

        if (state == STATE_REFRESHING) {
            onRefreshing();
            smoothScrollTo(mMeasuredHeight);
        } else if (state == STATE_DONE) {
            onFinish();
        } else if (state == STATE_NORMAL) {
            mRefreshBoy.setVisibility(View.VISIBLE);
            mRefreshBox.setVisibility(View.VISIBLE);
            mRunningBoy.setVisibility(View.GONE);
            mShowText.setText("下拉可刷新");
        } else if (state == STATE_RELEASE_TO_REFRESH) {
            mShowText.setText("松手后刷新");
        }

        mState = state;
    }

    public int getState() {
        return mState;
    }

    public void onRefreshing() {
        mRefreshBoy.setVisibility(View.GONE);
        mRefreshBox.setVisibility(View.GONE);
        mRunningBoy.setVisibility(View.VISIBLE);
        mShowText.setText("正在刷新...");
        runAnim.start();
    }

    public void onFinish() {
        mRefreshBoy.setVisibility(View.VISIBLE);
        mRefreshBox.setVisibility(View.VISIBLE);
        mRunningBoy.setVisibility(View.GONE);
        mShowText.setText("刷新完成");
        runAnim.stop();
    }

    public void setBoyBoxParams(float scrollY) {
        Log.e("params", "scrollY is:" + scrollY);
        Log.e("params", "mMeasuredHeight is:" + mMeasuredHeight);
        ViewGroup.MarginLayoutParams params1 = (ViewGroup.MarginLayoutParams) mRefreshBoy.getLayoutParams();
        ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mRefreshBox.getLayoutParams();
        float scale = scrollY / mMeasuredHeight;
        Log.e("params", "scale is:" + scale);
        Log.e("params", "iconWidth is:" + iconWidth + ", iconTopMax is:" + iconTopMax);
        int width = (int) Math.abs(iconWidth * scale);
        int top = (int) Math.abs(iconTopMax * scale);
        Log.e("params", "before----> width is:" + width + ", top is:" + top);
        if (width > iconWidth) {
            width = iconWidth;
            top = Math.abs(iconTopMax);
        }
        Log.e("params", "after----> width is:" + width + ", top is:" + top);
        params1.width = width;
        params2.width = width;
        params2.topMargin = top;
        mRefreshBoy.setLayoutParams(params1);
        mRefreshBox.setLayoutParams(params2);
        Log.e("params", "mRefreshBoy is visiable:" + mRefreshBoy.getVisibility());
    }

    @Override
    public void onMove(float delta, float scrollY) {
        setBoyBoxParams(scrollY);
        if(getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <=  mMeasuredHeight) {
            //return;
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    @Override
    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                reset();
            }
        }, 200);
    }

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer .getLayoutParams();
        lp.height = height;
        Log.e("tag", "header set visible height is:" + lp.height);
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        Log.e("tag", "header visible height is:" + lp.height);
        return lp.height;
    }

    private void smoothScrollTo(int destHeight) {
        Log.e("tag", "smooth scroll header is running");
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}
