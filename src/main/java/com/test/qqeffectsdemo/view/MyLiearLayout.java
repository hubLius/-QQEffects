package com.test.qqeffectsdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Liu on 2017/4/2.
 */

public class MyLiearLayout extends LinearLayout {
    public MyLiearLayout(Context context) {
        this(context, null);
    }

    public MyLiearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLiearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenuView slideMenuView;

    public void setSlideMenu(SlideMenuView slideMenuView) {
        this.slideMenuView = slideMenuView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenuView != null && slideMenuView.SlideMode == SlideMenuView.MODE_OPEN) {
            slideMenuView.closeAnimal();
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
