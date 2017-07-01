package com.test.qqeffectsdemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.test.qqeffectsdemo.R;


/**
 * Created by Liu on 2017/4/2.
 */

public class ParallaxLayout extends ListView {
    public ParallaxLayout(Context context) {
        this(context, null);
    }

    public ParallaxLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ImageView imageView;
    //最大高度
    private int maxHeight;
    //原始高度
    private int primiviteHeight;

    /**
     * 设置图片,获取头部图片
     *
     * @param imageView
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
        maxHeight = imageView.getDrawable().getIntrinsicHeight();
        primiviteHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
    }

    /**
     * 当listview滑动到头后执行
     *
     * @param deltaY         滑动到头后继续滑动的距离,向上到头后是负值,向下到头后是政治正直
     * @param scrollY
     * @param scrollRangeY
     * @param maxOverScrollY 滑动到头后可以继续滑动的最大距离
     * @param isTouchEvent   是否是滑动到头 true是用手拖动到头,false是滑翔到头
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //判断是否是向上滑动到头且是由手拖动到头
        if (deltaY < 0 && isTouchEvent) {
            int newHeight = imageView.getHeight() - deltaY / 3;
            newHeight = Math.min(newHeight, maxHeight);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.height = newHeight;
            imageView.setLayoutParams(params);
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(imageView.getHeight(), primiviteHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animalValue = (int) animation.getAnimatedValue();

                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = animalValue;
                    imageView.setLayoutParams(params);
                }
            });
            valueAnimator.setDuration(300).start();

        }
        return super.onTouchEvent(ev);
    }
}
