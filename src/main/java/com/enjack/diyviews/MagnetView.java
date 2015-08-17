package com.enjack.diyviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by enjack on 2015/8/17.
 */
public class MagnetView extends SimpleView{
    private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("MagnetView", 1);
    private final String tag = "<MagnetView>";
    protected int mColorBackground = Color.argb(255, 200, 200, 200);
    protected int mColorBackgroundPressed = mColorBackground;
    protected Paint mPaintBackground = null;
    protected Paint mPaintText = null;
    protected Rect mViewArea = null;

    public MagnetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    public MagnetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagnetView(Context context) {
        super(context);
    }

    @Override
    public void draw(Canvas canvas) {
        mAnalysts.pre();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
        //draw background color
        canvas.drawColor(mColorBackground);
        //draw text
        drawText(mText);
        mAnalysts.post();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(null==mViewArea)
            mViewArea = new Rect();
        mViewArea.left = mViewArea.top = 0;
        mViewArea.right = mWidth;
        mViewArea.bottom = mHeight;
    }

    @Override
    protected void initPaint(){
        if(null==mPaintBackground)
            mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(mColorBackground);

        if(null==mPaintText)
            mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(mTextColor);
    }
}
