package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by kuson on 17/4/19.
 */

public abstract class BaseRefreshHeaderView extends LinearLayout implements BaseRefreshHeader {

    protected LinearLayout mContainer;

    public BaseRefreshHeaderView(Context context, int layoutId) {
        this(context, null, layoutId);
    }

    public BaseRefreshHeaderView(Context context, @Nullable AttributeSet attrs, int layoutId) {
        super(context, attrs);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(layoutId, null);
        initView();
    }

    public abstract void initView();

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

    public void setState(int state) {

    }

    public int getState() {
        return 0;
    }

    @Override
    public void onMove(float delta) {

    }

    @Override
    public boolean releaseAction() {
        return false;
    }

    @Override
    public void refreshComplete() {

    }
}
