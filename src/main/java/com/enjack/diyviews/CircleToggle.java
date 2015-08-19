package com.enjack.diyviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;

import xmps.androiddebugtool.factorytest.R;

/**
 * Circle toggle.
     android:id="@+id/id_debug_circle_tttt"
     android:layout_width="0dp"
     android:layout_height="0dp"
     android:layout_below="@id/id_dbg_ccbv"
     android:layout_marginTop="10dp"
     android:layout_centerHorizontal="true"
     custom:radius="100dp"
     custom:textSuggestSize="true"
     custom:text="abc"
     custom:textPressed="123"
     custom:textColor="#84480d98"
     custom:textColorPressed="#ffedc649"
     custom:frameWidth="2px"
     custom:frameColor="#84480d98"
     custom:frameColorPressed="#ffedc649"
     custom:bkColor="#00000000"
     custom:bkColorPressed="#ffff150e"

 * Created by enjack on 2015/8/19.
 */
public class CircleToggle extends CircleColorButtonView {
    private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("CircleToggle", 2);
    protected String mTextPressed = "";
    protected int mFrameColorPressed = mFrameColor;
    private boolean mSelected = false;
    private ToggleStateWatcher mWatcher = null;

    public CircleToggle(Context context) {
        //super(context);
        this(context, null);
    }

    public CircleToggle(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public CircleToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++){
            int index = array.getIndex(i);
            switch (index){
                case R.styleable.DiyViews_frameColorPressed:
                    mFrameColorPressed = array.getColor(index, INVALID_COLOR);
                    break;
                case R.styleable.DiyViews_textPressed:
                    mTextPressed = array.getString(index);
                    break;
            }
        }

        array.recycle();
        initResources();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mAnalysts.pre();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
        //draw background color
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaintBackround);

        //draw frame
        if(INVALID_COLOR!=mFrameColor && -1!=mFrameWidth){
            canvas.drawCircle(mRadius, mRadius, (float)(mRadius-(float)mFrameWidth/2f), mPaintFrame);
        }

        //draw text
        float x,y;
        String text = mText;
        if(mSelected)
            text = mTextPressed;
        int width = getExactlyTextWidth(mPaintText, text);
        Paint.FontMetrics fm = mPaintText.getFontMetrics();
        x = (mWidth-width)/2;
        y = mHeight/2+(-fm.ascent-fm.descent)/2;
        canvas.drawText(text, x, y, mPaintText);

        //draw extras
        if(mCallBackDraw!=null)
            mCallBackDraw.drawExtra(canvas);
        mAnalysts.post();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        mWidth = mHeight = mRadius*2;
        setMeasuredDimension(mWidth, mHeight);
    }

    private void initResources(){
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        if(mSuggestTextSize)
            mTextSize = getSuggestFontSize(mText);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(mColorText);
        mPaintText.setStyle(Paint.Style.FILL);

        mPaintFrame = new Paint();
        mPaintFrame.setAntiAlias(true);
        mPaintFrame.setColor(mFrameColor);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setStrokeWidth(mFrameWidth);

        mPaintBackround = new Paint();
        mPaintBackround.setAntiAlias(true);
        mPaintBackround.setStyle(Paint.Style.FILL);
        mPaintBackround.setColor(mColorBackground);
    }

    private void changeResources(){
        int textColor = mColorText;
        int frameColor = mFrameColor;
        int bkColor = mColorBackground;
        if(mSelected){
            textColor = mColorTextPressed;
            frameColor = mFrameColorPressed;
            bkColor = mColorBackgroundPressed;
        }

        if(mSuggestTextSize && mSelected)
            mTextSize = getSuggestFontSize(mTextPressed);
        else if(mSuggestTextSize && !mSelected)
            mTextSize = getSuggestFontSize(mText);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(textColor);
        mPaintFrame.setColor(frameColor);
        mPaintFrame.setStrokeWidth(mFrameWidth);
        mPaintBackround.setColor(bkColor);
    }

    @Override
    /**Get current showing text of view*/
    public String getText(){
        if(mSelected)
            return mTextPressed;
        else
            return mText;
    }

    /**Get text.
     * @param sel
     * If true,return selected text, else return non-selected text.
     * */
    public String getText(boolean sel){
        if(sel)
            return mTextPressed;
        else
            return mText;
    }

    @Override
    /**Set current showing text color.*/
    public void setTextColor(int color){
        if(mSelected)
            mColorTextPressed = color;
        else
            mColorText = color;
        changeResources();
    }

    /**Set text color(Selected and non-selected).*/
    public void setTextColor(int color, int colorPressed){
        mColorText = color;
        mColorTextPressed = colorPressed;
        changeResources();
    }

    @Override
    @Deprecated
    /**Deprecated.Do nothing.*/
    public void setTextColorPressed(int color){}

    @Override
    /**Set current showing text(selected or non-selected).*/
    public void setText(String text){
        if(mSelected)
            mTextPressed = text;
        else
            mText = text;
        changeResources();
    }

    /**Set text(Selected and non-selected).*/
    public void setText(String text, String textPressed){
        mText = text;
        mTextPressed = textPressed;
        changeResources();
    }

    @Override
    public void setTextSize(int size){
        mTextSize = size;
        mPaintText.setTextSize(size);
        changeResources();
    }

    @Override
    public void setSuggestTextSize(boolean suggeste){
        mSuggestTextSize = suggeste;
        changeResources();
    }

    @Override
    /**Set current showing frame color.*/
    public void setFrameColor(int color){
        if(mSelected)
            mFrameColorPressed = color;
        else
            mFrameColor = color;
        changeResources();
    }

    /**Set color of frame when pressed*/
    public void setFrameColor(int color, int colorPressed){
        mFrameColor = color;
        mFrameColorPressed = colorPressed;
        changeResources();
    }

    /**Deprecated. Do nothing*/
    @Override
    @Deprecated
    public void setFrame(int color, int width){}

    /**Set frame color and width.*/
    public void setFrame(int color, int colorPressed, int width){
        mFrameColor = color;
        mFrameColorPressed = colorPressed;
        mFrameWidth = width;
        changeResources();
    }

    @Override
    /**Set current showing background color.*/
    public void setBackgroundColor(int color){
        if(mSelected)
            mColorBackgroundPressed = color;
        else
            mColorBackground = color;
        changeResources();
    }

    /**Set background color(Selected and non-selected).*/
    public void setBackgroundColor(int color, int colorPressed){
        mColorBackground = color;
        mColorBackgroundPressed = colorPressed;
        changeResources();
    }

    @Override
    @Deprecated
    /**Deprecated. Do nothing.*/
    public void setBackgroundColorPressed(int color){}

    /**Set selected status of toggle view.*/
    public void setSelected(boolean sel){
        mSelected = sel;
        changeResources();
        if(mWatcher!=null)
            mWatcher.onSelectChanged(mSelected);
    }

    /**Get current selected status.*/
    public boolean getSelected(){
        return mSelected;
    }

    /**Set listener of view when selecte status changed.*/
    public void setWatcher(ToggleStateWatcher watcher){
        mWatcher = watcher;
    }

    public interface ToggleStateWatcher{
        public void onSelectChanged(boolean sel);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(MotionEvent.ACTION_DOWN == (event.getAction()&MotionEvent.ACTION_MASK)){
            setSelected(!mSelected);
            changeResources();
            invalidate();
        }
        //return super.onTouchEvent(event);
        return true;
    }
}
