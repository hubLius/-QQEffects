package com.test.qqeffectsdemo.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Created by Liu on 2017/4/1.
 */

public class Swipedelete extends FrameLayout {

    private ViewDragHelper dragHelper;
    private View main;
    private View menu;
    private float downX;
    private float downY;
    private boolean result;
    private long dowmTime;
    private float downsX;

    public Swipedelete(Context context) {
        this(context, null);
    }

    public Swipedelete(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Swipedelete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(Swipedelete.this, callback);
    }

    /**
     * 排版完成
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        main = getChildAt(0);
        menu = getChildAt(1);
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("你的程序炸啦！");
        }
    }

    /**
     * 排版
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        main.layout(0, 0, main.getMeasuredWidth(), main.getMeasuredHeight());
        menu.layout(main.getRight(), 0, main.getRight() + menu.getMeasuredWidth(), menu.getMeasuredHeight());
    }

    /**
     * 将拦截事件dragHelper去处理,根据他的返回值设置是否拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        result = dragHelper.shouldInterceptTouchEvent(ev);

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                dowmTime = System.currentTimeMillis();
                downsX=downX;
                break;
            case MotionEvent.ACTION_MOVE:
                float dX = event.getX() - downX;
                float dY = event.getY() - downY;
                //如果x轴的移动距离大于y轴的移动距离则请求父容器不要拦截自己的触摸事件
                if (dX-downsX<0&&Math.abs(dX)>Math.abs(dY)) {
                    requestDisallowInterceptTouchEvent(true);
                }if(Math.abs(dX)<Math.abs(dY)){
                requestDisallowInterceptTouchEvent(false);
            }
                downsX =dX;
                break;
            case MotionEvent.ACTION_UP:
                long moveTime = System.currentTimeMillis() - dowmTime;

                float upX = event.getX() - downX;
                float upY = event.getY() - downY;
                //根据勾股定理求取第三边的长度
                float transUp = (float) Math.sqrt(Math.pow(upX, 2) + Math.pow(upY, 2));
//                Log.e(TAG, "onTouchEvent: " + transUp);
                //如果用户的触摸时间小于500毫秒且移动距离小于8则认为这是一次点击事件
                if (moveTime < ViewConfiguration.getLongPressTimeout() && transUp < ViewConfiguration.getTouchSlop()) {
                    performClick();
//                    Log.e(TAG, "onTouchEvent: " + transUp);
                }
                break;
        }
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == main) {
                if (left > 0) {
                    left = 0;
                } else if (left < -menu.getMeasuredWidth()) {
                    left = -menu.getMeasuredWidth();
                }
            } else if (child == menu) {
                if (left > main.getRight() + menu.getMeasuredWidth()) {
                    left = main.getRight() + menu.getMeasuredWidth();
                } else if (left < main.getMeasuredWidth() - menu.getMeasuredWidth()) {
                    left = main.getMeasuredWidth() - menu.getMeasuredWidth();
                }
            }

            return left;
        }

        /**
         * 当view的位置该生改变
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == main) {
//                main.layout(left,top,left+main.getMeasuredWidth(),top+main.getMeasuredHeight());
//                menu.layout(main.getRight(), top, main.getRight() + menu.getMeasuredWidth(), top + menu.getMeasuredHeight());
                ViewCompat.offsetLeftAndRight(menu, dx);
            } else if (changedView == menu) {
                ViewCompat.offsetLeftAndRight(main, dx);
            }

            if (main.getLeft() < 0 && dx > 0) {
                requestDisallowInterceptTouchEvent(true);
            }
            if(main.getLeft()==0&&dx>0){
                requestDisallowInterceptTouchEvent(false);
            }
            if (listener != null) {
                if (main.getLeft() == 0) {
                    listener.onClose(Swipedelete.this);
                } else if (main.getLeft() == -menu.getMeasuredWidth()) {
                    listener.onOpen(Swipedelete.this);
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (main.getLeft() > -menu.getMeasuredWidth() / 2) {
                close();
            } else {
                open();
            }
        }
    };

    public void open() {
        dragHelper.smoothSlideViewTo(main, -menu.getMeasuredWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(Swipedelete.this);
    }

    public void close() {
        dragHelper.smoothSlideViewTo(main, 0, 0);
        ViewCompat.postInvalidateOnAnimation(Swipedelete.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(Swipedelete.this);
        }
    }


    private OnSwipeListener listener;

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeListener {
        void onOpen(Swipedelete swipedelete);

        void onClose(Swipedelete swipedelete);
    }
}
