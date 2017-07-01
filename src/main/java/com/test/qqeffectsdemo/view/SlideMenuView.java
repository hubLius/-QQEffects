package com.test.qqeffectsdemo.view;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Liu on 2017/4/1.
 */

public class SlideMenuView extends FrameLayout {

    private View menuView;
    private View mainView;
    private ViewDragHelper dragHelper;
    private boolean result;
    private FloatEvaluator floarEvaluator = new FloatEvaluator();
    private ArgbEvaluator argbevaluator = new ArgbEvaluator();

    public static final int MODE_OPEN = 1;
    public static final int MODE_CLOSE = 0;
    public int SlideMode;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(SlideMenuView.this, callback);
    }


    //当布局完成后执行，此时就可以获取子view了
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("参数传入有误，此view能切只能传入两个子view");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxleft = (int) (getMeasuredWidth() * 0.6);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return result;
    }

    private int maxleft;
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        /**
         * 是否监视view的触摸事件
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == menuView || child == mainView) {
                return true;
            }
            return false;
        }


        /**
         监视到view的触摸事件就执行
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 是否强制横向滑动
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            //返回值为0不强制横向滑动，返回值大于0则强制滑动
            return 1;
        }

        /**
         * 修正view的水平位置
         * @param child
         * @param left 移动后的left
         * @param dx 水平移动的距离
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                left = getMaxMoveSize(left);
            }
            return left;
        }

        /**
         * 当view的位置发生改变
         * @param changedView
         * @param left 改变后的left
         * @param top 改变后的top
         * @param dx 水平移动的距离
         * @param dy 垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView) {
                //手指在menu上移动时menu不动，main移动相应的距离
                menuView.layout(0, getTop(), menuView.getMeasuredWidth(), getBottom());
                int movesize = mainView.getLeft() + dx;
                movesize = getMaxMoveSize(movesize);
                mainView.layout(movesize, getTop(), movesize + mainView.getMeasuredWidth(), getBottom());
            }

            float percent = mainView.getLeft() * 1f / maxleft;
            execAnimal(percent);
            if (listener != null) {
                //将移动的百分比传递给调用者
                listener.onDragging(percent);
                if (percent == 1f) {
                    listener.onOpen();
                    SlideMode = MODE_OPEN;
                } else if (percent == 0f) {
                    listener.onClose();
                    SlideMode = MODE_CLOSE;
                }
            }

        }


        /**
         * 抬起手之后会执行的方法
         * @param releasedChild
         * @param xvel x轴移动的速率
         * @param yvel 沿y轴移动的速率
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mainView.getLeft() > maxleft / 2) {
                //打开
                openAnimal();
            } else {
                //关闭
                closeAnimal();
            }

        }
    };

    public void openAnimal() {
        dragHelper.smoothSlideViewTo(mainView, maxleft, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenuView.this);
    }

    public void closeAnimal() {
        dragHelper.smoothSlideViewTo(mainView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenuView.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenuView.this);
        }
    }

    private void execAnimal(float percent) {
        //main移动时缩放
        mainView.setScaleX(floarEvaluator.evaluate(percent, 1.0f, 0.8f));
        mainView.setScaleY(floarEvaluator.evaluate(percent, 1.0f, 0.8f));

        //menu移动时的缩放
        menuView.setScaleX(floarEvaluator.evaluate(percent, 0.3f, 1f));
        menuView.setScaleY(floarEvaluator.evaluate(percent, 0.3f, 1f));
        menuView.setTranslationX(floarEvaluator.evaluate(percent, -menuView.getMeasuredWidth() / 2, 0));

        //设置背景渐变色
        if (getBackground() != null) {
            int Color = (int) argbevaluator.evaluate(percent, android.graphics.Color.BLACK, android.graphics.Color.TRANSPARENT);
            getBackground().setColorFilter(Color, PorterDuff.Mode.SRC_OVER);

        }


    }

    //切换main页面状态的方法
    public void toogle() {
        //如果mian的状态为打开，就关闭它
        if (mainView.getLeft() == 0) {
            openAnimal();
        } else {
            closeAnimal();
        }
    }


    private int getMaxMoveSize(int left) {
        if (left < 0) {
            left = 0;
        } else if (left > maxleft) {
            left = maxleft;
        }
        return left;
    }

    private OnSlideChangeListener listener;

    public void setOnSlideChangeListener(OnSlideChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSlideChangeListener {
        void onDragging(float parcent);

        void onOpen();

        void onClose();
    }
}
