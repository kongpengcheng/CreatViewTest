package com.example.centling.creatviewtest;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Harry.kong on 2016/8/28.
 */
public class MyCustomView extends View {

    private Paint mPaint;
    private Paint Paint1;
    private Rect mBound;
    private int mMax = 100;//设置的最大值
    private int mProgress = 0;//设置的进度
    private static final boolean DEBUG = false;
    private String titleText = "Hello world";
    private int titleColor = Color.BLACK;
    private int titleBackgroundColor = Color.WHITE;
    private int titleSize = 16;
    private RectF oval;
    private float mStartAngle = 180;//开始的角度
    private float mSweepAngle = 0;//划过的角度
    private OnProgressListener mOnProgressListener;
    //分段颜色 外环
    private int[] OUT_SECTION_COLORS = {
            0xFFE5BD7D, 0xFFFAAA64,
            0xFFFFFFFF, 0xFF6AE2FD,
            0xFF8CD0E5, 0xFFA3CBCB,
            0xFFBDC7B3, 0xFFD1C299,
            0xFFE5BD7D};
    private Rect bounds;

    public MyCustomView(Context context) {
        this(context, null);

    }

    public MyCustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    /**
     * 设置进度监听
     *
     * @param mOnProgressListener
     */
    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    public MyCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                R.styleable.MyCustomView, defStyleAttr, 0);
        if (null != a) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.MyCustomView_titleColor:
                        titleColor = a.getColor(attr, Color.BLACK);
                        break;
                    case R.styleable.MyCustomView_titleSize:
                        titleSize = a.getDimensionPixelSize(attr, titleSize);
                        break;
                    case R.styleable.MyCustomView_titleText:
                        titleText = a.getString(attr);
                        break;
                    case R.styleable.MyCustomView_titleBackgroundColor:
                        titleBackgroundColor = a.getColor(attr, Color.WHITE);
                        break;
                }
            }
            //进行回收
            a.recycle();
            init();
        }


    }

    private void init() {
        //扇形
        oval = new RectF();
        //矩形
        bounds = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(titleSize);
        Paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);//消除锯齿
        Paint1.setStyle(Paint.Style.STROKE);
        Paint1.setColor(Color.BLACK);
        Paint1.setStrokeWidth(10);
        Paint1.setStyle(Paint.Style.STROKE);
        Paint1.setStrokeCap(Paint.Cap.ROUND);

        /**
         * 得到自定义View的titleText内容的宽和高
         */
        mBound = new Rect();
        mPaint.getTextBounds(titleText, 0, titleText.length(), mBound);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(titleBackgroundColor);
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, getWidth() / 2f, mPaint);
        mPaint.setColor(titleColor);
        if (mOnProgressListener != null) {//如果不为空  则为接口返回的值
            titleText = mOnProgressListener.OnProgress(mMax, mProgress);
        } else {
            titleText = mProgress + "/" + mMax;
        }
        mPaint.getTextBounds(titleText, 0, titleText.length(), bounds);
        canvas.drawText(titleText, oval.centerX() - bounds.width() / 2, oval.centerY() + bounds.height() / 2, mPaint);
        canvas.drawArc(oval, mStartAngle, mSweepAngle, false, Paint1);

    }

    /**
     * 设置开始的角度  可以控制开始的位置 默认为180  即从左边开始
     *
     * @param mStartAngle
     */
    public void setStartAngle(float mStartAngle) {
        this.mStartAngle = mStartAngle;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int radiu = 0;
        if (oval.bottom <= 0) {
            //   radiu = (int) ((Math.min(getWidth(), getHeight())) / 2+Paint1.getStrokeWidth());
            radiu = (int) (getWidth() / 2f - Paint1.getStrokeWidth() / 2f);
            oval.left = getWidth() / 2 - radiu;
            oval.top = getHeight() / 2 - radiu;
            oval.right = getWidth() / 2 + radiu;
            oval.bottom = getHeight() / 2 + radiu;
        }
        //设置渐变色
        //  setSweepShader(OUT_SECTION_COLORS, Paint1);
        //  setSweepShader(INNER_SECTION_COLORS, mRollDrawPaint);
    }

    /**
     * 设置渐变颜色
     */
    private void setSweepShader(int[] colors, Paint p) {
        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, colors, null);
        p.setShader(sweepGradient);
    }

    /**
     * 设置层叠颜色
     */
    public void setOutColors(int[] colors) {
        OUT_SECTION_COLORS = colors;
        setSweepShader(OUT_SECTION_COLORS, mPaint);
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        if (mMax == 0) {
            throw new IllegalArgumentException("Max不能为0!");
        }
        mSweepAngle = 360f * mProgress / mMax;
        //post的可以在子线程刷新ui
        postInvalidate();
    }

    /**
     * 用于外部判断当前进度状态
     */
    public interface OnProgressListener {
        /**
         * 返回中间部分文字内容
         *
         * @param max
         * @param progress
         * @return
         */
        String OnProgress(int max, int progress);
    }
}
