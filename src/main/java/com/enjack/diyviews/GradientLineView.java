package com.enjack.diyviews;

import xmps.androiddebugtool.factorytest.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


/**
 *  颜色渐变的线条，可用于layout控件之间的分隔线。
 *  起点和终点颜色如果没有设置，则默认黑色。中间点颜色可以不设置。
 *  </br></br>
 *
 *  xml写法1(水平渐变)：</br>
 *  	android:layout_width="fill_parent"		</br>
 android:layout_height="1dp"				</br>
 custom:gradientStartColor="#00000000"	</br>
 custom:gradientEndColor="#00000000"		</br>
 custom:gradientCenterColor="#ff000000"	</br>

 </br></br>
 xml写法2(垂直渐变)：</br>
 android:layout_width="1dp"				</br>
 android:layout_height="100dip"			</br>
 custom:gradientStartColor="#ffff0000"	</br>
 custom:gradientEndColor="#ff00ff00"		</br>
 custom:gradientCenterColor="#ff0000ff"	</br>

 @author enjack
  * */
public class GradientLineView extends View {
	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("GradientLineView", 3);
	/**��ʾ��Ч����ɫ��color��-16777216(#FF000000)��16777216(#00FFFFFF)*/
	protected static final int INVALID_COLOR = Integer.MAX_VALUE;
	private final int DIR_HORIZONTAL = 0;
	private final int DIR_VERTICAL = 1;
	private final int DIR_UNKNOWN = 2;
	
	private int[] mColorArray;;
	private int startColor = INVALID_COLOR;
	private int centerColor = INVALID_COLOR;
	private int endColor = INVALID_COLOR;
	private int dir = DIR_UNKNOWN;
	private int mWidth = 0;
	private int mHeight = 0;
	private Rect mRect = new Rect();
	private Shader mShader = null;
	private Paint mPaint = null;

	public GradientLineView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyleAttr, 0);  
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++){
			int index = array.getIndex(i);
			switch (index){
			case R.styleable.DiyViews_gradientStartColor:
				startColor = array.getColor(index, Color.BLACK);
			case R.styleable.DiyViews_gradientCenterColor:
				centerColor = array.getColor(index, Color.BLACK);
				break;
			case R.styleable.DiyViews_gradientEndColor:
				endColor = array.getColor(index, Color.BLACK);
				break;
			}
		}
		
		array.recycle();
		
		if(INVALID_COLOR == startColor)
			startColor = Color.BLACK;
		if(INVALID_COLOR == endColor)
			endColor = Color.BLACK;
		if(INVALID_COLOR==centerColor){
			mColorArray = new int[2];
			mColorArray[0] = startColor;
			mColorArray[1] = endColor;
		}
		else{
			mColorArray = new int[3];
			mColorArray[0] = startColor;
			mColorArray[1] = centerColor;
			mColorArray[2] = endColor;
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	public GradientLineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GradientLineView(Context context) {
		this(context, null);
	}
	
	@Override
	public void onDraw(Canvas c){
		mAnalysts.setTag("GradientLineView");
		mAnalysts.pre();
		c.drawRect(mRect.left, mRect.top, mRect.right, mRect.bottom, mPaint);
		mAnalysts.post();
	}
	
	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		mAnalysts.setTag("GradientLineView-->onMeasure()");
		mAnalysts.pre();
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
	    int width = 0;  
	    int height = 0;  
	    
	    if (widthMode == MeasureSpec.EXACTLY)//ָ��ȷ����ֵ����fill_parent  
	        width = widthSize;  
	    else//ָ��Ϊwrap_content��������  
	    	width = 1;
	  
	    if (heightMode == MeasureSpec.EXACTLY)
	    	height = heightSize;
	    else
	    	height = 1;
	    
	    mWidth = width;
	    mHeight = height;
	    setMeasuredDimension(width, height);
	    
	    setupResource();
	    mAnalysts.post();
	}
	
	private void setupResource(){
		if(mWidth>=mHeight)
	    	dir = DIR_HORIZONTAL;
	    else
	    	dir = DIR_VERTICAL;
	    
	    mRect.top = 0;
	    mRect.bottom = mHeight;
	    mRect.left = 0;
	    mRect.right = mWidth;
	    switch(dir){
		case DIR_HORIZONTAL:
			mShader = new LinearGradient(mRect.left, mRect.top, mRect.right, mRect.top, 
					mColorArray, null, Shader.TileMode.CLAMP);
			break;
		case DIR_VERTICAL:
			mShader = new LinearGradient(mRect.left, mRect.top, mRect.left, mRect.bottom, 
					mColorArray, null, Shader.TileMode.CLAMP);
			break;
		case DIR_UNKNOWN:
			mShader = new LinearGradient(mRect.left, mRect.top, mRect.right, mRect.top, 
					mColorArray, null, Shader.TileMode.CLAMP);
			break;
		}
	    mPaint.setShader(mShader);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("GradientLineView:");
		sb.append(" width:"+mWidth);
		sb.append(" height:"+mHeight);
		return sb.toString();
	}

}
