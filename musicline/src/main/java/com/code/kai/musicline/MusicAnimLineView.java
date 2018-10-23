package com.code.kai.musicline;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

/**
 * author       : caikai
 * email        :
 * date         : 19/10/2018
 * description  :
 */
public class MusicAnimLineView extends View {

    private Paint mPaint;
    private ValueAnimator mAnimator;
    private float mLineWidth; //线宽
    private int mLineColor; //线的颜色
    private int mLineRadius;    //圆角
    private int mAnimatorDuration;  //动画时长
    private int mLineSpace;       //间距
    private int mLineHeight;        //线的高度
    private int mWidth, mHeight;
    private float mLineRealHeigh; //线的实际高度，有圆角的话需要减掉圆角部分
    private int mLine1Height, mLine2Height, mLine3Height, mLine4Height;
    private float mLine1X, mLine2X, mLine3X, mLine4X;
    private int mAnimatorValue;

    public MusicAnimLineView(Context context) {
        this(context, null);
    }

    public MusicAnimLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicAnimLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MusicAnimLineView);

        mLineWidth = ta.getDimension(R.styleable.MusicAnimLineView_line_width, 0);
        mLineRadius = (int) ta.getDimension(R.styleable.MusicAnimLineView_line_radius, 0);
        mLineSpace = (int) ta.getDimension(R.styleable.MusicAnimLineView_line_space, 0);
        mAnimatorDuration = ta.getInteger(R.styleable.MusicAnimLineView_animDuration, 0);
        mLineColor = ta.getColor(R.styleable.MusicAnimLineView_line_color, Color.WHITE);
        mLineHeight = (int) ta.getDimension(R.styleable.MusicAnimLineView_line_height, 0);

        mWidth = (int) (mLineWidth * 4 + 3 * mLineSpace);
        mHeight = (int) (mLineHeight + mLineWidth);
        mLineRealHeigh = mHeight - mLineWidth;
        ta.recycle();
        initAnim(mAnimatorDuration, Animation.INFINITE);
        init();
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        //init line height
        mLine1Height = (int) (3f * mLineHeight / 5);
        mLine2Height = (int) (1f * mLineHeight / 5);
        mLine3Height = (int) (1f * mLineHeight / 2);
        mLine4Height = (int) (mLineWidth / 2);

        mLine1X = mLineWidth / 2;
        mLine2X = 3 * mLineWidth / 2 + mLineSpace;
        mLine3X = (mLineWidth + mLineSpace) * 2 + mLineWidth / 2;
        mLine4X = (mLineWidth + mLineSpace) * 3 + mLineWidth / 2;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.translate(0, mLineWidth / 2);
        canvas.drawLine(mLine1X, mLine1Height, mLine1X, mHeight - mLineWidth, mPaint);
        canvas.drawLine(mLine2X, mLine2Height, mLine2X, mHeight - mLineWidth, mPaint);
        canvas.drawLine(mLine3X, mLine3Height, mLine3X, mHeight - mLineWidth, mPaint);
        canvas.drawLine(mLine4X, mLine4Height, mLine4X, mHeight - mLineWidth, mPaint);
        if (!mIsAnim) {
            startPlay();
        }
    }

    private void initAnim(int duration, int repeatCount) {
        mAnimator = ValueAnimator.ofInt(0, duration);
        mAnimator.setDuration(mAnimatorDuration);
        mAnimator.setRepeatCount(repeatCount);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                updateValueAnimator(animation);
            }
        });
    }

    private boolean mIsAnim;
    private boolean mIsPaused;

    public void startPlay() {
        mAnimator.start();
        mIsAnim = true;
    }

    public void pausePlay() {

        if (mIsPaused) {
            return;
        }

        mIsPaused = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAnimator.pause();
        } else {
            mAnimator.cancel();
        }
    }

    public void resumePlay() {

        if (!mIsPaused) {
            return;
        }

        mIsPaused = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAnimator.resume();
        } else {
            ValueAnimator animator = ValueAnimator.ofInt(mAnimatorValue, mAnimatorDuration);
            animator.setDuration(
                (long) ((1 - mAnimatorValue * 1.0f / mAnimatorDuration) * mAnimatorDuration));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    updateValueAnimator(animation);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {

                }

                @Override public void onAnimationEnd(Animator animation) {
                    mAnimator.start();
                }

                @Override public void onAnimationCancel(Animator animation) {

                }

                @Override public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }
    }

    private void updateValueAnimator(ValueAnimator animation) {
        mAnimatorValue = (int) animation.getAnimatedValue();
        float f = mAnimatorValue * 1.0f / (mAnimatorDuration / 2);
        mLine1Height = calculateHeight(f, 3f * mLineHeight / 5 - mLineWidth / 2);
        mLine3Height = calculateHeight(f, 1f * mLineHeight / 2 + mLineWidth / 2);

        mLine2Height = (int) (3f * mLineHeight / 5 - Math.abs((1 - f) * 2f * mLineHeight / 5));
        mLine4Height = (int) (1f * mLineHeight / 2 - Math.abs((1 - f) * 1f * mLineHeight / 2));
        invalidate();
    }

    public void cancel() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private int calculateHeight(float f, float maxHeight) {
        return (int) Math.abs((1 - f) * maxHeight);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }
}
