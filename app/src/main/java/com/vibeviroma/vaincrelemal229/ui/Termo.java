package com.vibeviroma.vaincrelemal229.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vibeviroma.vaincrelemal229.R;


public class Termo extends View {

    private float mCr1Top, mRcTop;


    private Paint mPaintTop;
    private float mCr1Middle, mRcMiddle ;
    private Paint mPaintMiddle;
    private float mCr1Bottom, mRcBottom;
    private Paint mPaintBottom;


    private static final int  GRADUATION_TEXT_SIZE=16;
    private static float DEGREE_WIDTH=30;
    private static final int MAX_TEMP=100, MIN_TEMP=0;
    private static final float RANG_TEMP=100;
    private static final int nbGraduations=10;
    private int mMinTemp= MIN_TEMP;
    private int mMaxTemp=MAX_TEMP;
    private float rangeTemp=RANG_TEMP;
    private int mCurrentTemp=MIN_TEMP;
    private Rect rect= new Rect();


    private Paint degreePaint, graduationPaint;


    public Termo(Context context) {
        super(context);
        init(context, null);
    }

    public Termo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Termo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray type= context.obtainStyledAttributes(attrs, R.styleable.Termo);
        int BottomColor= type.getColor(R.styleable.Termo_bottomColor, Color.GRAY);
        int MiddleColor= type.getColor(R.styleable.Termo_middleColor, Color.WHITE);
        int TopColor= type.getColor(R.styleable.Termo_topColor, Color.RED);

        mCr1Top=type.getDimension(R.styleable.Termo_radius,20f);

        type.recycle();
        mRcTop=mCr1Top/2;
        mPaintTop = new Paint();
        mPaintTop.setColor(TopColor);
        mPaintTop.setStyle(Paint.Style.FILL);

        mCr1Middle = mCr1Top - 5;
        mRcMiddle = mRcTop - 5;
        mPaintMiddle = new Paint();
        mPaintMiddle.setColor(MiddleColor);
        mPaintMiddle.setStyle(Paint.Style.FILL);

        mCr1Bottom = mCr1Middle - mCr1Middle / 6;
        mRcBottom = mRcMiddle - mRcMiddle / 6;
        mPaintBottom = new Paint();
        mPaintBottom.setColor(BottomColor);
        mPaintBottom.setStyle(Paint.Style.FILL);

        DEGREE_WIDTH = mCr1Middle / 8;

        degreePaint = new Paint();
        degreePaint.setStrokeWidth(mCr1Middle / 16);
        degreePaint.setColor(TopColor);
        degreePaint.setStyle(Paint.Style.FILL);

        graduationPaint = new Paint();
        graduationPaint.setColor(TopColor);
        graduationPaint.setStyle(Paint.Style.FILL);
        graduationPaint.setAntiAlias(true);
        graduationPaint.setTextSize(/*convertDpToPixel(GRADUATION_TEXT_SIZE, getContext())*/15);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();

        int circleCenterX = width / 2;
        float circleCenterY = height - mCr1Top;
        float outerStartY = 0;
        float middleStartY = outerStartY + 5;


        float innerEffectStartY = middleStartY + mRcMiddle + 10;
        float innerEffectEndY = circleCenterY - mCr1Top - 10;
        float innerRectHeight = innerEffectEndY - innerEffectStartY;
        float innerStartY = innerEffectStartY + (/*mMaxTemp -*/ mCurrentTemp) / rangeTemp * innerRectHeight;

        RectF outerRect = new RectF();
        outerRect.left = circleCenterX - mRcTop;
        outerRect.top = outerStartY; 
        outerRect.right = circleCenterX + mRcTop;
        outerRect.bottom = circleCenterY;

        canvas.drawRoundRect(outerRect, mRcTop, mRcTop, mPaintTop);
        canvas.drawCircle(circleCenterX, circleCenterY, mCr1Top, mPaintTop);

        RectF middleRect = new RectF();
        middleRect.left = circleCenterX - mRcMiddle;
        middleRect.top = middleStartY;
        middleRect.right = circleCenterX + mRcMiddle;
        middleRect.bottom = circleCenterY;

        canvas.drawRoundRect(middleRect, mRcMiddle, mRcMiddle, mPaintMiddle);
        canvas.drawCircle(circleCenterX, circleCenterY, mCr1Middle, mPaintMiddle);

        canvas.drawRect(circleCenterX - mRcBottom, innerStartY, circleCenterX + mRcBottom, circleCenterY, mPaintBottom);
        canvas.drawCircle(circleCenterX, circleCenterY, mCr1Bottom, mPaintBottom);

        float tmp = innerEffectStartY;
        float startGraduation = mMaxTemp;
        float inc = rangeTemp / nbGraduations;

        //Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show();

        while (tmp <= innerEffectEndY) {
            canvas.drawLine(circleCenterX - mRcTop - DEGREE_WIDTH, tmp, circleCenterX - mRcTop, tmp, degreePaint);
            String txt = ((int) startGraduation) + "%" ;
            graduationPaint.getTextBounds(txt, 0, txt.length(), rect);
            float textWidth = rect.width();
            float textHeight = rect.height();

            canvas.drawText(((int) startGraduation) + "%" , circleCenterX - mRcTop - DEGREE_WIDTH - textWidth - DEGREE_WIDTH * 1.5f,
                    tmp + textHeight / 2, graduationPaint);
            tmp += (innerEffectEndY - innerEffectStartY) / nbGraduations;
            startGraduation -= inc;
        }
    }

    public void setmCurrentTemp(int mCurrentTemp) {
        if(mCurrentTemp>MAX_TEMP)
            this.mCurrentTemp = MAX_TEMP;
        else if (mCurrentTemp<MIN_TEMP)
            this.mCurrentTemp = MIN_TEMP;
        else
            this.mCurrentTemp = mCurrentTemp;
        invalidate();
    }

    public int getmMinTemp() {
        return mMinTemp;
    }
}
