package com.jcodecraeer.xrecyclerview;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by kuson on 17/4/14.
 */

public class TranslateAnimationLoading extends Animation {

    /**
     * @hide
     */
    protected float mFromXDelta;
    /**
     * @hide
     */
    protected float mToXDelta;
    /**
     * @hide
     */
    protected float mFromYDelta;
    /**
     * @hide
     */
    protected float mToYDelta;

    public TranslateAnimationLoading(){
        this.setFillAfter(true);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float dx = mFromXDelta;
        float dy = mFromYDelta;
        if (mFromXDelta != mToXDelta) {
            dx = mFromXDelta + ((mToXDelta - mFromXDelta) * interpolatedTime);
        }
        if (mFromYDelta != mToYDelta) {
            dy = mFromYDelta + ((mToYDelta - mFromYDelta) * interpolatedTime);
        }
        t.getMatrix().setTranslate(dx, dy);
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mFromXDelta = 0;
        mToXDelta = 0;
        mFromYDelta = 0;
        mToYDelta = -30;
    }
}
