package com.xiangxue.myapplication.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.xiangxue.myapplication.Util;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewConfigurationCompat;

/**
 * Date:2019-12-01
 * author:lwb
 * Desc:
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";

    private List<View> lineViews;  // 每一行的子View
    private List<List<View>> views; //所有的行  ，把每一行的view放到这个数组中
    private List<Integer> heights;   // 每一行的高度

    private OverScroller mScroller;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);// 获取最小滑动距离
        mScroller = new OverScroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int currX = 0;
        int currY = 0;

        for (int i = 0; i < views.size(); i++) {  // 大循环 ，所有的子view ，一行一行的布局
            List<View> lineViews = this.views.get(i);  //  取出这一行
            Integer lineHeight = heights.get(i);  //  取出这一行的高度值
            //遍历当前行的子View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                int left = currX;
                int top = currY;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                //确定下一个view的left值
                currX += child.getMeasuredWidth();
            }
            currY += lineHeight;
            currX = 0;
        }

    }


    private void init() {
        lineViews = new ArrayList<>();
        views = new ArrayList<>();
        heights = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureHeight = heightSize;

        init();

        // 记录当前行的高度
        int lineWidth = 0;  // 宽度是当前行子View的宽度之和
        int lineHeight = 0; // 高度是当前行所有子View中高度最大值

        //整个流式布局的
        int flowlayoutWidth = 0; //所有行中宽度的最大值
        int flowlayoutHeight = 0; // 所有行的高度的累加

        // 遍历所有的子view，对子View进行测量，分配到具体的行
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            //测量子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);/****这里用measureChild方法有讲究****/
            //获取当前子View的测量的高度/宽度
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            // 看下当前的行剩余的宽度是否可以容纳下一个子View，
            // 如果放不下，换行，  保存当前行的所有的子view，累加高度，当前的宽度/高度置0
            if ((lineWidth + childWidth) > widthSize) {  // 换行
                // 处理一行只有一个子view并且view的高度是match_parent
                if (lineViews.size() == 1 && lineViews.get(0).getLayoutParams().height == LayoutParams.MATCH_PARENT) {
                    lineHeight = Util.dp2px(150);
                }

                views.add(lineViews);
                lineViews = new ArrayList<>();  // 创建新的一行
                flowlayoutWidth = Math.max(flowlayoutWidth, lineWidth);
                flowlayoutHeight += lineHeight;
                heights.add(lineHeight);
                lineHeight = 0;
                lineWidth = 0;
            }
            /***不换行***/
            lineViews.add(child);
            lineWidth += childWidth;
            if (lp.height != LayoutParams.MATCH_PARENT) {
                lineHeight = Math.max(lineHeight, childHeight);
            }

            //最后一行的宽高计算
            if (i == childCount - 1) {
                flowlayoutWidth = Math.max(flowlayoutWidth, lineWidth);
                flowlayoutHeight += lineHeight;
                heights.add(lineHeight);
                views.add(lineViews);
            }

        }

        realHeight = flowlayoutHeight;
        scrollable = realHeight > measureHeight;

        //FlowLayout最终的宽高
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : flowlayoutWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : realHeight);


        // 重新测量一次Layout_heigth = match_parent
//        reMeasureChild(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 处理layoutheigth = match_parent
     */
    private void reMeasureChild(int widthMeasureSpec, int heightMeasureSpec) {
        int lineSize = views.size();
        for (int i = 0; i < lineSize; i++) {
            int lineHeight = heights.get(i);  // 每一行的行高
            List<View> lineViews = views.get(i);  // 每一行的子View

            // 遍历子View
            int size = lineViews.size();
            for (int j = 0; j < size; j++) {
                View child = lineViews.get(j);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.height == LayoutParams.MATCH_PARENT) {
                    int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, params.width);
                    int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lineHeight);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }
        }

    }

    private int mTouchSlop; // 用来判断是不是一次滑动(滑动小于这个值就)
    private float mLastInterceptX = 0;
    private float mLastInterceptY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;
        float curX = event.getX();
        float curY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptX = curX;
                mLastInterceptY = curY;
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent  -  move");
                float dx = curX - mLastInterceptX;
                float dy = curY - mLastInterceptY;

                if (Math.abs(dy) > Math.abs(dx) && Math.abs(dy) > mTouchSlop) {
                    intercept = true;
                } else {
                    intercept = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        mLastInterceptX = curX;
        mLastInterceptY = curY;
        return intercept;
    }


    private boolean scrollable = false;
    private int measureHeight; // 代表本身测量高度
    private int realHeight; // 表示内容的高度
    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) { // 处理滑动
        if (!scrollable) {
            return super.onTouchEvent(event);
        }

        float curY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = curY;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent  -  move");

                float dy = mLastY - curY;  // 本次手势滑动了多大距离
//                int oldScrollY = getScrollY();  // 已经偏移了的距离
//                int scrolY = (int) (oldScrollY + dy);  //这是本次需要偏移的距离 = 之前偏移的距离 + 本次手势滑动的距离
//                if (scrolY < 0) {
//                    scrolY = 0;
//                }
//                if (scrolY > realHeight - measureHeight) {
//                    scrolY = realHeight - measureHeight;
//                }
//                scrollTo(0, scrolY);

                mScroller.startScroll(0, mScroller.getFinalY(), 0, (int)dy);
                invalidate();
                mLastY = curY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

            int currY = mScroller.getCurrY();
            if (currY < 0) {
                currY = 0;
            }
            if (currY > realHeight - measureHeight) {
                currY = realHeight - measureHeight;
            }

            scrollTo(0, currY);
            postInvalidate();
        }
    }
}
