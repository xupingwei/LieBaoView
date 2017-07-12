package com.xaaolaf.liebaoview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xupingwei on 2017/7/7.
 */

public class RoundProgressbar extends View {

    private Paint mBackgroundPaint;
    private Paint mFrontPaint;
    private Paint mArcPaint;


    private int mWidth = 200;
    private int mHeight = 200;

    private int mProgress;
    private int mMaxProgress = 100;


    public RoundProgressbar(Context context) {
        super(context);
        init();
    }

    public RoundProgressbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public RoundProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //背景色
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);  //消除锯齿
        mBackgroundPaint.setColor(Color.BLUE);

        //前景色
        mFrontPaint = new Paint();
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setColor(Color.WHITE);

        //圆弧颜色
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(12);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(Color.RED);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth, mHeight, mHeight / 2, mBackgroundPaint);
        canvas.drawCircle(mWidth, mHeight, mHeight / 2 - 20, mFrontPaint);
        canvas.save();
        canvas.rotate(-90, mWidth, mHeight);
        RectF rectF = new RectF(mWidth / 2, mHeight / 2, mWidth, mHeight);
//        canvas.drawArc(rectF,0);

    }
}
