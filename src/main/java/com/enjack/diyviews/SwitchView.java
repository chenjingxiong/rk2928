package com.enjack.diyviews;

import xmps.androiddebugtool.factorytest.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 开关控件。如果设置wrap_content，控件的大小则为100x500.</br>
 *
 * </br>
 * xml写法1：</br>
 * 		android:id="@+id/id_dbg_toggle"			</br>
 android:layout_below="@id/id_dbg_btn_1"	</br>
 android:layout_width="wrap_content"		</br>
 android:layout_height="wrap_content"	</br>
 custom:switchState="true"				</br>
 custom:onColor="#ff70ee70"				</br>
 custom:offColor="#ff909090"				</br>

 </br></br>

 xml写法2：</br>
 android:layout_width="60dip"	</br>
 android:layout_height="25dip"	</br>

 @author enjack
  * */
public class SwitchView extends View {
	private final int COLOR1 = Color.argb(255, 200, 200, 200);
	private final int COLOR2 = Color.argb(255, 700, 70, 255);

	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("SwitchView", 3);
	private SwitchStateChangedListener mListener = null;
	private Paint mPaint = new Paint();
	private PorterDuffXfermode mXfermode = new PorterDuffXfermode(Mode.SRC);
	private int mWidth = 100;
	private int mHeight = 50;
	/**off color*/
	private int mColor1 = COLOR1;
	/**on color*/
	private int mColor2= COLOR2;
	/***/
	private Rect mRect = new Rect();
	/***/
	private int mRadius1;
	/***/
	private int mRadius2;
	/***/
	private boolean mState = false;
	/**显示滑动动画*/
	private boolean mShowAnimate = false;
	/***/
	private int mCirclePos = 0;


	public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyleAttr, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++){
			int index = array.getIndex(i);
			switch (index){
				case R.styleable.DiyViews_switchState:
					mState = array.getBoolean(index, false);
					break;
				case R.styleable.DiyViews_offColor:
					mColor1 = array.getColor(index, COLOR1);
					break;
				case R.styleable.DiyViews_onColor:
					mColor2 = array.getColor(index, COLOR2);
					break;
			}
		}

		array.recycle();
	}

	public SwitchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchView(Context context) {
		this(context, null);
	}

	@Override
	public void onDraw(Canvas canvas){
		mAnalysts.pre();

		if(mState)
			mPaint.setColor(mColor2);
		else
			mPaint.setColor(mColor1);
		canvas.save();
		canvas.translate(mRadius1, mRadius1);
		canvas.drawCircle(0, 0, mRadius1, mPaint);
		canvas.restore();
		canvas.save();
		canvas.translate(mWidth-mRadius1, mRadius1);
		canvas.drawCircle(0, 0, mRadius1, mPaint);
		canvas.restore();
		canvas.drawRect(mRect, mPaint);
		canvas.save();
		if(mShowAnimate){
			canvas.translate(mCirclePos, mRadius1);
		}
		else{
			if(mState)
				canvas.translate(mWidth-mRadius1, mRadius1);
			else
				canvas.translate(mRadius1, mRadius1);
		}
		mPaint.setColor(Color.argb(255, 240, 240, 240));
		canvas.drawCircle(0, 0, mRadius2, mPaint);
		canvas.restore();

		mAnalysts.post();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0;
		int height = 0;

		if (widthMode == MeasureSpec.EXACTLY)
			width = widthSize;
		else
			width = 100;

		if (heightMode == MeasureSpec.EXACTLY)
			height = heightSize;
		else
			height = 50;

		mWidth = width;
		mHeight = height;
		mRadius1 = mHeight/2;
		mRadius2 = (int)(mRadius1*0.85f);
		mRect.left = mRadius1;
		mRect.right = mWidth - mRadius1;
		mRect.top = 0;
		mRect.bottom = mHeight;

		mPaint.setAntiAlias(true);
		/*
		 * 先画左右两个圆，在画中间矩形区域，矩形区域和圆有重叠的地方，如果颜色带alpha透明，
		 * 重叠的半圆和其他地方的颜色在模拟器上会深浅不一，所以设置Xfermode为SRC
		 * 在真机上不会，不需要设置.如果在真机上设置，会导致黑边。
		 * */
		//mPaint.setXfermode(mXfermode);

		setMeasuredDimension(width, height);
	}

	private void change(boolean state){
		boolean tmp = mState;
		mState = state;
		if(mState!=tmp && mListener!=null)
			mListener.onSwitchStateChanged(mState);
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(MotionEvent.ACTION_UP == (event.getAction()&MotionEvent.ACTION_MASK)){
			change(!getState());
			new Thread(new PostAnimateDrawThread()).start();
		}

		return true;
	}

	public static interface SwitchStateChangedListener{
		public void onSwitchStateChanged(boolean state);
	}

	/**设置状态监听。*/
	public void setStateChangedListener(SwitchStateChangedListener listener){
		this.mListener = listener;
	}

	/**获取当前状态。*/
	public boolean getState(){
		return mState;
	}

	/**设置状态。*/
	public void setState(boolean state){
		this.postInvalidate();
		change(state);
	}

	private class PostAnimateDrawThread implements Runnable{

		@Override
		public void run() {

			if(mShowAnimate)
				return;
			int start = mRadius1;
			int end = mWidth - mRadius1;
			if(mState)
				mCirclePos = start;
			else
				mCirclePos = end;
			while(true){
				mShowAnimate = true;
				if(mState)
					mCirclePos++;
				else
					mCirclePos--;
				if(mCirclePos>end || mCirclePos<start){
					mShowAnimate = false;
					break;
				}
				SwitchView.this.postInvalidate();

				delayms(1);
			}
		}


		private void delayms(int ms){
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
