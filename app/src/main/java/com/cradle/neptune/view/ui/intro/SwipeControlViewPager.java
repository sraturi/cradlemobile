package com.cradle.neptune.view.ui.intro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.core.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeControlViewPager extends ViewPager {
    private boolean swipeEnabled = true;

    public SwipeControlViewPager(@NonNull Context context) {
        super(context);
    }

    public SwipeControlViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    public boolean isSwipeEnabled() {
        return swipeEnabled;
    }

    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }
}
