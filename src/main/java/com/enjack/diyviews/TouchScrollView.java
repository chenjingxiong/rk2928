package com.enjack.diyviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;


/**
 * 继承ScrollView解决onTouchEvent拦截的问题。通过判断点击位置决定调用子view和父view的各个touch event方法。
 * </br>
 * 注意：子view的onTouchEvent必须返回true。
 * */
public class TouchScrollView extends ScrollView {

	private final String tag = "TouchScrollView";
	private final boolean LOG_SWITCH = false;
	private int mCurrentPos = 0;

	private void logout(String str){
		if(LOG_SWITCH)
			Log.i(tag, str);
	}

	public TouchScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TouchScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TouchScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private boolean isChildViewTouched(MotionEvent event){
		float x = event.getX();
		float y = event.getY();
		ViewGroup group = (ViewGroup)this.getChildAt(0);
		int n = group.getChildCount();
		logout("child count:"+n);
		Rect[] rc = new Rect[n];
		for(int i=0; i<n; i++){
			rc[i] = new Rect();
			View v = group.getChildAt(i);
			v.getHitRect(rc[i]);
			logout("child["+i+"]: left:"+rc[i].left+" top:"+rc[i].top+" right:"+rc[i].right+" bottom:"+rc[i].bottom);
		}
		y += mCurrentPos;
		for(int j=0; j<n; j++){
			if(rc[j].contains((int)x, (int)y)){
				logout("touch child at:"+j);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event){
		logout("<dispatchTouchEvent>");
		if(isChildViewTouched(event)){
			onTouchEvent(event);
			return super.dispatchTouchEvent(event);
		}
		else{
			super.dispatchTouchEvent(event);
			return true;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		super.onInterceptTouchEvent(event);
		return false;
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		logout("<onTouchEvent>");
		super.onTouchEvent(event);
		return false;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt){
		mCurrentPos = t;
//		if(t + getHeight() >=  computeVerticalScrollRange()){//滑动到底部
//		}  
	}

}
