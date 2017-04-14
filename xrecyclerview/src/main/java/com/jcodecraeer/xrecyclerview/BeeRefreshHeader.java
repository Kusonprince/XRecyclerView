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
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by kuson on 17/4/13.
 */

public class BeeRefreshHeader extends LinearLayout implements BaseRefreshHeader {

    private LinearLayout mContainer;
    private ImageView pullBeeView;
    private ImageView pullBeeCView;
    private TextView pullTextView;
    TranslateAnimationLoading mBeeViewAnimationJump;
    private int mState = STATE_NORMAL;
    public int mMeasuredHeight;
    int windowsWidth = 0;
    int beeWidth = 0;
    int beeHeight = 0;

    public BeeRefreshHeader(Context context) {
        super(context);
        initView();
    }

    public BeeRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.refresh_header_bee_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        pullBeeView = (ImageView) mContainer.findViewById(R.id.pull_bee);
        pullBeeCView = (ImageView) mContainer.findViewById(R.id.pull_bee_c);
        pullTextView = (TextView) mContainer.findViewById(R.id.pull_text);

        mBeeViewAnimationJump = new TranslateAnimationLoading();
        mBeeViewAnimationJump.setDuration(380);
        mBeeViewAnimationJump.setRepeatCount(Animation.INFINITE);
        mBeeViewAnimationJump.setRepeatMode(Animation.REVERSE);

        ((AnimationDrawable)pullBeeView.getDrawable()).start();

        measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        beeWidth = pullBeeView.getMeasuredWidth();
        beeHeight = pullBeeView.getMeasuredHeight();

        windowsWidth = DeviceUtil.getScreenWidth(getContext()) / 2;

    }

    private void setBeeparams(boolean isFinish) {
        if (!isFinish && pullBeeCView.getVisibility() == View.VISIBLE) {
            pullBeeCView.clearAnimation();
            pullBeeCView.setVisibility(View.GONE);
        }
        if (beeWidth <= 0) {
            beeWidth = pullBeeView.getWidth();
            beeHeight = pullBeeView.getHeight();
        }
        float scale = getVisibleHeight() / (float)mMeasuredHeight;
        scale = scale > 1 ? 1 : scale;
        if (isFinish) {
            pullBeeCView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * scale), (int) ((beeHeight * 1.6) * scale - beeHeight), 0, 0);
        } else {
            pullBeeView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * scale), (int) ((beeHeight * 1.6) * scale - beeHeight), 0, 0);
        }
    }

    @Override
    public void onMove(float delta) {
        setBeeparams(false);
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

    public void setState(int state) {
        if (state == mState) return ;

        if (state == STATE_REFRESHING) {
            onRefreshing();
            smoothScrollTo(mMeasuredHeight);
        } else if (state == STATE_DONE) {
            onFinish();
        } else if (state == STATE_NORMAL) {
            onStartPull();
        } else if (state == STATE_RELEASE_TO_REFRESH) {
            releaseRefresh();
        }

        mState = state;
    }

    public int getState() {
        return mState;
    }

    private void onStartPull() {
        pullBeeView.clearAnimation();
        pullTextView.setText("下拉更新...");
        pullBeeCView.setVisibility(View.GONE);
        pullBeeCView.clearAnimation();
        pullBeeView.setVisibility(View.VISIBLE);
    }

    private void releaseRefresh() {
        pullTextView.setText("松手更新...");
        pullBeeCView.setVisibility(View.GONE);
        pullBeeCView.clearAnimation();
        pullBeeView.setVisibility(View.VISIBLE);
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
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer .getLayoutParams();
        lp.height = height;
        Log.e("tag", "header set visible height is:" + lp.height);
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LinearLayout.LayoutParams lp =  (LinearLayout.LayoutParams)mContainer.getLayoutParams();
        Log.e("tag", "header visible height is:" + lp.height);
        return lp.height;
    }

    private void onRefreshing() {
        if (beeWidth <= 0) {
            pullBeeView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    beeWidth = pullBeeView.getWidth();
                    beeHeight = pullBeeView.getHeight();
                    pullBeeView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * 1), (int) ((beeHeight * 1.6) * 1 - beeHeight), 0, 0);
                    pullBeeCView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * 1), (int) ((beeHeight * 1.6) * 1 - beeHeight), 0, 0);
                    pullBeeView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            pullBeeView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * 1), (int) ((beeHeight * 1.6) * 1 - beeHeight), 0, 0);
            pullBeeCView.setPadding((int) ((windowsWidth - beeWidth * 1.2) * 1), (int) ((beeHeight * 1.6) * 1 - beeHeight), 0, 0);
        }

        pullBeeView.startAnimation(mBeeViewAnimationJump);
        pullTextView.setText("正在更新...");
        pullBeeCView.setVisibility(View.GONE);
        pullBeeCView.clearAnimation();
        pullBeeView.setVisibility(View.VISIBLE);
    }

    private void onFinish() {
        pullBeeView.clearAnimation();
        pullBeeView.setVisibility(View.GONE);
        pullBeeCView.setVisibility(View.VISIBLE);
        pullTextView.setText("更新完成...");
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
                setBeeparams(true);
            }
        });
        animator.start();
    }
}
