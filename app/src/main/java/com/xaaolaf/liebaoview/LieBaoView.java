package com.xaaolaf.liebaoview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by xupingwei on 2017/7/7.
 */

public class LieBaoView extends View {

    private static final String TAG = "LieBaoView";

    //绘制背景圆的画笔
    private Paint mBackgroundCirclePaint;
    //绘制旋转圆的画笔
    private Paint mFrontCirclePaint;
    //绘制文字的画笔
    private Paint mTextPaint;
    //绘制进度条的画笔
    private Paint mArcPaint;


    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;

    //旋转Bitmap与画布
    private Bitmap mOverturnBitmap;
    private Canvas mOverturnCanvas;

    private int mWidth = 400;
    private int mHeight = 400;
    private int mPadding = 20;
    private int mProgress = 0;
    private int mMaxProgress = 100;
    private int mRotateAngle = 0;

    private boolean isRotating;
    private boolean isInital = false;
    private boolean isDescending;
    private boolean isIncreasing;
    private boolean isCleaning;

    private Matrix mMatrix;
    private Camera mCamera;


    private Runnable mRotateRunnable;
    private Runnable mCleaningRunnable;

    public LieBaoView(Context context) {
        super(context);
        init();
    }


    public LieBaoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LieBaoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBackgroundCirclePaint = new Paint();
        mBackgroundCirclePaint.setAntiAlias(true);
        mBackgroundCirclePaint.setColor(Color.argb(0xff, 0x10, 0x53, 0xff));

        mFrontCirclePaint = new Paint();
        mFrontCirclePaint.setAntiAlias(true);
        mFrontCirclePaint.setColor(Color.argb(0xff, 0x5e, 0xae, 0xff));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(80);
        mTextPaint.setColor(Color.WHITE);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(Color.WHITE);
        mArcPaint.setStrokeWidth(12);
        mArcPaint.setStyle(Paint.Style.STROKE);

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);

        mOverturnBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mOverturnCanvas = new Canvas(mOverturnBitmap);

        mMatrix = new Matrix();
        mCamera = new Camera();

        mRotateRunnable = new Runnable() {
            @Override
            public void run() {
                //当前是正在增加过程
                if (isIncreasing) {
                    Log.d(TAG, "mProgress: " + mProgress);
                    if (mProgress >= 59) {
                        isIncreasing = false;
                    }

                    mProgress++;
                } else {
                    if (mRotateAngle > 90 && mRotateAngle < 180) {
                        mRotateAngle = mRotateAngle + 3 + 180;
                    } else if (mRotateAngle >= 180) {
                        isRotating = false;
                        isInital = true;
                        return;
                    } else {
                        mRotateAngle += 3;
                    }
                }
                invalidate();
                postDelayed(this, 25);
            }
        };

        mCleaningRunnable = new Runnable() {
            @Override
            public void run() {
                if (mProgress >= 60) {
                    isCleaning = false;
                    return;
                }

                if (isDescending) {
                    mProgress--;
                    if (mProgress <= 0) {
                        isDescending = false;
                    }
                } else {
                    mProgress++;
                }

                invalidate();
                postDelayed(this, 40);
            }
        };

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCleaning) {
                    return;
                }
                isDescending = true;
                isCleaning = true;
                mProgress--;
                postDelayed(mCleaningRunnable, 40);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆
        mBitmapCanvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2, mBackgroundCirclePaint);
        mBitmapCanvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - mPadding, mTextPaint);
        mBitmapCanvas.save();

        //实例化一个矩形，该矩形的左上角和右下角坐标与原Bitmap并不重合
        //这是因为要使进度条和最外面的圆有一定的间隙
        RectF rectF = new RectF(10, 10, mWidth - 10, mHeight - 10);
        //先将画笔逆时针旋转90度，这样的drawArc的起始角度就能从0度开始
        mBitmapCanvas.rotate(-90, mWidth / 2, mHeight / 2);
        mBitmapCanvas.drawArc(rectF, 0, ((float) mProgress / mMaxProgress) * 360, false, mArcPaint);
        mBitmapCanvas.restore();
        canvas.drawBitmap(mBitmap, 0, 0, null);

        mOverturnCanvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - mPadding, mFrontCirclePaint);
        String text = (int) (((float) mProgress / mMaxProgress) * 100) + "%";
        //获取文本的宽度
        float textWidth = mTextPaint.measureText(text);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float baseLine = mHeight / 2 - (metrics.ascent + metrics.descent) / 2;
        mOverturnCanvas.drawText(text, mWidth / 2 - textWidth / 2, baseLine, mTextPaint);
        canvas.drawBitmap(mOverturnBitmap, mMatrix, null);


        //当前正在翻转
        if (isRotating) {
            mCamera.save();
            mCamera.rotateY(mRotateAngle);
            if (mRotateAngle >= 180) {
                mRotateAngle -= 180;
            }
            mCamera.getMatrix(mMatrix);
            mCamera.restore();
            mMatrix.preTranslate(-mWidth / 2, -mHeight / 2);
            mMatrix.postTranslate(mWidth / 2, mHeight / 2);
        }
        canvas.drawBitmap(mOverturnBitmap, mMatrix, null);

        //如果当前空间尚未进行翻转过程
        if (!isRotating && !isInital) {
            isIncreasing = true;
            isRotating = true;
            postDelayed(mRotateRunnable, 10);
        }
    }
}
