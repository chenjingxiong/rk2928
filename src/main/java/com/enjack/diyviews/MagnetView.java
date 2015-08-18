package com.enjack.diyviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import xmps.androiddebugtool.factorytest.R;

/**
 * Created by enjack on 2015/8/17.
 */
public class MagnetView extends SimpleView{
    private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("MagnetView", 1);
    private DrawEfficiencyAnalysts mAnalystsMeasure = new DrawEfficiencyAnalysts("MagnetView-onMeasure", 1);
    private final String tag = "<MagnetView>";
    protected int mColorBackground = Color.argb(255, 90, 90, 200);
    protected int mColorBackgroundPressed = mColorBackground;
    protected Paint mPaintBackground = null;
    protected Paint mPaintText = null;
    protected Rect mViewArea = null;

    public MagnetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyle, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++){
            int index = array.getIndex(i);
            switch (index){
                case R.styleable.DiyViews_textColor:
                    mTextColor = array.getColor(index, Color.argb(255, 0, 0, 0));
                    break;
                case R.styleable.DiyViews_textColorPressed:
                    mTextColorPressed = array.getColor(index, Color.argb(255, 0, 0, 0));
                case R.styleable.DiyViews_bkColor:
                    mColorBackground = array.getColor(index, mColorBackground);
                    break;
                case R.styleable.DiyViews_bkColorPressed:
                    mColorBackgroundPressed = array.getColor(index, mColorBackgroundPressed);
                    break;
            }
        }

        array.recycle();
        initPaint();
    }

    public MagnetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagnetView(Context context) {
        this(context, null);
    }

    @Override
    public void draw(Canvas canvas) {
        mAnalysts.pre();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
        mCanvas = canvas;
        //draw background color
        canvas.drawRect(mViewArea, mPaintBackground);
        //draw text
        drawText(mText, TEXT_LAYOUT_CENTER, mPaintText);
        mAnalysts.post();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mAnalystsMeasure.pre();
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        boolean conflict = false;

        try {
            checkSuggestSizeConflict(widthMode);
        } catch (SimpleViewException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            conflict = true;
        }finally{
            if(conflict){
                setMeasuredDimension(0, 0);
                return;
            }
        }


        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            int textWidth = getExactlyTextWidth(mPaintText, mText);
            width = textWidth+mTextPaddingLeft+mTextPaddingRight;
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            Rect rc = getTextDisplayRect(mPaintText, mText);
            height = rc.height()+mTextPaddingTop+mTextPaddingBottom;
        }

        mWidth = width;
        mHeight = height;
        setMeasuredDimension(width, height);

        if(null==mViewArea)
            mViewArea = new Rect();
        mViewArea.left = mViewArea.top = 0;
        mViewArea.right = mWidth;
        mViewArea.bottom = mHeight;

        //
        if(mSuggestTextSize)
            mTextSize = getSuggestFontSize(mText);
        mPaintText.setTextSize(mTextSize);
        mAnalystsMeasure.post();
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
        if(mSuggestTextSize)
            mTextSize = getSuggestFontSize(mText);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(mTextColor);
    }

    @Override
    public int getSuggestFontSize(String text){
        int size =0;
        int permittedWidth = mWidth - mTextPaddingLeft - mTextPaddingRight;
        int permittedHeight = (mHeight - mTextPaddingTop - mTextPaddingBottom)/2;
        Rect rc = new Rect();
        for(int i=0; i<1000; i++){
            mPaintText.setTextSize(i);
            mPaintText.getTextBounds(text, 0, text.length(), rc);
            if(rc.width()>=permittedWidth || rc.height()>=permittedHeight){
                size = i-1;
                break;
            }
        }

        if(size>1000)
            size = 0;

        return size;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(MotionEvent.ACTION_DOWN == (event.getAction()&MotionEvent.ACTION_MASK)){
            mPaintText.setColor(mTextColorPressed);
            mPaintBackground.setColor(mColorBackgroundPressed);
            this.invalidate();
        }
        else if(MotionEvent.ACTION_UP == (event.getAction()&MotionEvent.ACTION_MASK)){
            mPaintText.setColor(mTextColor);
            mPaintBackground.setColor(mColorBackground);
            this.invalidate();
        }
        return true;
    }

    /**
     * Set text color.
     * @param commColor common status color
     * @param pressedColor color of text when pressed
     * */
    public void setTextColor(int commColor, int pressedColor){
        mTextColor = commColor;
        mTextColorPressed = pressedColor;
        mPaintText.setColor(mTextColor);
    }

    @Override
    public void setTextSize(int size){
        mTextSize = size;
        mPaintText.setTextSize(mTextSize);
    }

    @Override
    public void setTextSizeSuggested(boolean b){
        super.setTextSizeSuggested(b);
        if(b)
            mTextSize = getSuggestFontSize(mText);
    }

    /**Set background color, common status and pressed.*/
    public void setBackgroundColor(int commColor, int pressedColor){
        mColorBackground = commColor;
        mColorBackgroundPressed = pressedColor;
        mPaintBackground.setColor(mColorBackground);
    }
}
