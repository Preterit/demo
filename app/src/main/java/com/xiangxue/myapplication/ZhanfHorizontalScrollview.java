package com.xiangxue.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Date:2019-12-03
 * author:lwb
 * Desc:
 */
public class ZhanfHorizontalScrollview extends ViewGroup {
    private int childCount;//子View数量
    private int childIndex;//子View索引
    private int measuredHeight;//子View的高度
    private int measuredWidth;//子View的宽度
    private Scroller scroller;//弹性滑动对象，用于实现View的弹性滑动
    private VelocityTracker velocityTracker;//速度追踪，

    public ZhanfHorizontalScrollview(Context context) {
        this(context, null);
    }

    public ZhanfHorizontalScrollview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhanfHorizontalScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        scroller = new Scroller(getContext());
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得它的父容器为它设置的测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 1.计算自定义的ViewGroup中所有子控件的大小
        // measureChildren(widthMeasureSpec, heightMeasureSpec);
        int height = 0;
        int width = 0;

        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != GONE) {
                    // 2.计算自定义的ViewGroup中所有子控件的大小
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    measuredHeight = child.getMeasuredHeight();
                    measuredWidth = child.getMeasuredWidth();
                    height = Math.max(height, measuredHeight);
                    width += measuredWidth;
                }
            }
        }
        // 设置自定义的控件MyViewGroup的大小，如果是MeasureSpec.EXACTLY则直接使用父ViewGroup传入的宽和高，否则设置为自己计算的宽和高
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        childCount = getChildCount();
        int height = 0;
        int width = 0;
        if (childCount > 0) {
            for (int index = 0; index < childCount; index++) {
                View child = getChildAt(index);
                if (child.getVisibility() != GONE && child != null) {
                    measuredHeight = getMeasuredHeight();
                    measuredWidth = getMeasuredWidth();
                    height = Math.max(height, measuredHeight);
                    child.layout(width, 0, width + measuredWidth, height);
                }
                width += measuredWidth;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //如果动画还没有结束，再次点击时结束上次动画，即开启这次新的ACTION_DOWN的动画
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();//View的左边缘 - View内容的左边缘 位置的像素点
                // int scrollToChildIndex = scrollX / measuredWidth;
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();//获取X方向手指滑动的速度，之前必须调用computeCurrentVelocity（）方法
                if (Math.abs(xVelocity) > 200) {//当滑动速度>200Px/S时
                    childIndex = xVelocity > 0 ? childIndex - 1 : childIndex + 1;
                } else {
                    childIndex = (scrollX + measuredWidth / 2) / measuredWidth;
                }
                childIndex = Math.max(0, Math.min(childIndex, childCount - 1));//限定childIndex在0到childCount之间
                int dx = childIndex * measuredWidth - scrollX;
                scroller.startScroll(getScrollX(), 0, dx, 0, 500);//up 时自动滚动到
                invalidate();
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    //分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    //分别记录上次滑动的坐标（onINterceptTouchEvent）
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("ZhanfHorizontalScroll","move");
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        mLastY = y;
        mLastXIntercept = x;
        mLastYIntercept = y;

        return super.onInterceptTouchEvent(ev);
    }

}


