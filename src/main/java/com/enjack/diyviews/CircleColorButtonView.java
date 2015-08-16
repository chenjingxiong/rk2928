package com.enjack.diyviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import xmps.androiddebugtool.factorytest.R;

/**
 * Created by enjack on 2015/8/16.
 */
public class CircleColorButtonView extends View{

    protected static final int INVALID_COLOR = Integer.MAX_VALUE;
    private final String tag = "<CircleColorButtonView>";
    private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("CircleButtonView", 1);
    private int mRadius = -1;
    private int mHeight = 0;
    private int mWidth = 0;
    protected int mFrameColor = INVALID_COLOR;
    protected int mFrameWidth = -1;

    public CircleColorButtonView(Context context) {
        super(context);
    }

    public CircleColorButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public CircleColorButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public CircleColorButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++){
            int index = array.getIndex(i);
            switch (index){
                case R.styleable.DiyViews_frameWidth:
                    mFrameWidth = array.getDimensionPixelSize(index, -1);
                    break;
                case R.styleable.DiyViews_frameColor:
                    mFrameColor = array.getColor(index, INVALID_COLOR);
                    break;
                case R.styleable.DiyViews_radius:
                    mRadius = array.getDimensionPixelSize(index, -1);
                    break;
            }
        }

        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas){
        mAnalysts.pre();

        mAnalysts.post();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        mWidth = mHeight = mRadius*2;
        setMeasuredDimension(mWidth, mHeight);
    }
}
