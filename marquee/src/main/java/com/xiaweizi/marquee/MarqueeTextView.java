package com.xiaweizi.marquee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * <pre>
 *     author : xiaweizi
 *     class  : com.xiaweizi.marquee.MarqueeTextView
 *     e-mail : 1012126908@qq.com
 *     time   : 2017/12/26
 *     desc   : 自定义跑马灯
 * </pre>
 */

@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {

    /** 默认滚动时间 */
    private static final int ROLLING_INTERVAL_DEFAULT = 10000;
    /** 第一次滚动默认延迟 */
    private static final int FIRST_SCROLL_DELAY_DEFAULT = 1000;
    /** 滚动模式-一直滚动 */
    public static final int SCROLL_FOREVER = 100;
    /** 滚动模式-只滚动一次 */
    public static final int SCROLL_ONCE = 101;

    /** 滚动器 */
    private Scroller mScroller;
    /** 滚动一次的时间 */
    private int mRollingInterval;
    /** 滚动的初始 X 位置 */
    private int mXPaused = 0;
    /** 是否暂停 */
    private boolean mPaused = true;
    /** 是否第一次 */
    private boolean mFirst = true;
    /** 滚动模式 */
    private int mScrollMode;
    /** 初次滚动时间间隔 */
    private int mFirstScrollDelay;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        mRollingInterval = typedArray.getInt(R.styleable.MarqueeTextView_scroll_interval, ROLLING_INTERVAL_DEFAULT);
        mScrollMode = typedArray.getInt(R.styleable.MarqueeTextView_scroll_mode, SCROLL_FOREVER);
        mFirstScrollDelay = typedArray.getInt(R.styleable.MarqueeTextView_scroll_first_delay, FIRST_SCROLL_DELAY_DEFAULT);
        typedArray.recycle();
        setSingleLine();
        setEllipsize(null);
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        mXPaused = 0;
        mPaused = true;
        mFirst = true;
        resumeScroll();
    }

    /**
     * 继续滚动
     */
    public void resumeScroll() {
        if (!mPaused)
            return;
        // 设置水平滚动
        setHorizontallyScrolling(true);

        // 使用 LinearInterpolator 进行滚动
        if (mScroller == null) {
            mScroller = new Scroller(this.getContext(), new LinearInterpolator());
            setScroller(mScroller);
        }
        int scrollingLen = calculateScrollingLen();
        final int distance = scrollingLen -  mXPaused;
        final int duration = (Double.valueOf(mRollingInterval * distance * 1.00000
                / scrollingLen)).intValue();
        if (mFirst) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScroller.startScroll(mXPaused, 0, distance, 0, duration);
                    invalidate();
                    mPaused = false;
                }
            }, mFirstScrollDelay);
        } else {
            mScroller.startScroll(mXPaused, 0, distance, 0, duration);
            invalidate();
            mPaused = false;
        }
    }

    /**
     * 暂停滚动
     */
    public void pauseScroll() {
        if (null == mScroller)
            return;

        if (mPaused)
            return;

        mPaused = true;

        mXPaused = mScroller.getCurrX();

        mScroller.abortAnimation();
    }

    /**
     * 停止滚动，并回到初始位置
     */
    public void stopScroll() {
        if (null == mScroller) {
            return;
        }
        mPaused = true;
        mScroller.startScroll(0, 0, 0, 0, 0);
    }

    /**
     * 计算滚动的距离
     *
     * @return 滚动的距离
     */
    private int calculateScrollingLen() {
        TextPaint tp = getPaint();
        Rect rect = new Rect();
        String strTxt = getText().toString();
        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);
        return rect.width();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (null == mScroller) return;
        if (mScroller.isFinished() && (!mPaused)) {
            if (mScrollMode == SCROLL_ONCE) {
                stopScroll();
                return;
            }
            mPaused = true;
            mXPaused = -1 * getWidth();
            mFirst = false;
            this.resumeScroll();
        }
    }

    /** 获取滚动一次的时间 */
    public int getRndDuration() {return mRollingInterval;}
    /** 设置滚动一次的时间 */
    public void setRndDuration(int duration) {this.mRollingInterval = duration;}
    /** 设置滚动模式 */
    public void setScrollMode(int mode) {this.mScrollMode = mode;}
    /** 获取滚动模式 */
    public int getScrollMode() {return this.mScrollMode;}
    /** 设置第一次滚动延迟 */
    public void setScrollFirstDelay(int delay) {this.mFirstScrollDelay = delay;}
    /** 获取第一次滚动延迟 */
    public int getScrollFirstDelay() {return mFirstScrollDelay;}

    public boolean isPaused() {
        return mPaused;
    }
}