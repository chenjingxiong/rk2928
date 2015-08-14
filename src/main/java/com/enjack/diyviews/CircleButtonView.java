package com.enjack.diyviews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import xmps.androiddebugtool.factorytest.R;

/**
 * 圆形button。
 * </br></br>
 * xml示例：											</br>
 * 			android:id="@+id/start_factory_test"	</br>
 android:layout_width="0dip"				</br>
 android:layout_height="0dip"			</br>
 android:layout_centerInParent="true"	</br>
 custom:radius="@dimen/hdiv10"			</br>
 custom:text="Start"						</br>
 custom:textSize="20dip"					</br>
 custom:textSuggestSize="true"			</br>
 custom:textColor="#ffeeeeee"			</br>
 custom:textColorPressed="#ffffffff"		</br>
 custom:textPadding="20dip"				</br>
 custom:frameColor="#ff000000"			</br>
 custom:frameWidth="2px"					</br>
 custom:bkComm="#ff8855aa"				</br>
 custom:bkPressed="#ff9966bb"			</br>
 *
 *
 * @author enjack
 *
 * @see com.enjack.diyviews.SimpleView
 * */
public class CircleButtonView extends SimpleView{
	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("CircleButtonView", 3);
	private final String tag = "<CircleButtonView>";
	private final boolean LOG_SWITCH = true;
	private boolean mHasMeasured = false;
	private boolean mHasDrawed = false;
	protected int mRadius = -1;
	private Bitmap mBitmapComm = null;
	private Bitmap mBitmapPressed = null;
	private Bitmap mBitmapInvalid = null;
	protected int mFrameColor = INVALID_COLOR;
	protected int mFrameWidth = -1;

	/**
	 * CircleButtonView异常类。
	 * */
	protected static class CircleButtonViewException extends SimpleViewException{

		private static final long serialVersionUID = 1L;
		public static final String RADIUS_UNSPECIFIED = "Radius unspecified!!!";

		public CircleButtonViewException(){
			super(UNKNOW_VIEW_EXCEPTION);
		}

		public CircleButtonViewException(String msg){
			super(msg);
		}
	}

	/**
	 * 圆形类。
	 * */
	protected class Circle{

		private int x;
		private int y;
		private int radius;

		/**
		 * @param x
		 * 	圆心x坐标
		 * @param y
		 * 	圆心y坐标
		 * @param	radius
		 * 	半径
		 * */
		public Circle(int x, int y, int radius){
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		public Point getCenterPoint(){
			return new Point(this.x, this.y);
		}

		public int getRadius(){
			return this.radius;
		}

		/**
		 * 判断点是否在圆内。
		 * */
		public boolean contains(int x, int y){
			return ((x-this.x)*(x-this.x)+(y-this.y)*(y-this.y)<radius*radius);
		}

		/**
		 * 判断矩形是否在圆内。
		 * */
		public boolean contains(Rect rect){
			if(contains(rect.left, rect.top)&&
					contains(rect.left, rect.bottom)&&
					contains(rect.right, rect.top)&&
					contains(rect.right, rect.bottom))
				return true;
			else
				return false;
		}

		@Override
		public String toString(){
			String str = "x:"+x+" y:"+y+" radius:"+radius;
			return str;
		}
	}

	public CircleButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyle, 0);
		int n = array.getIndexCount();
		Drawable drawable;
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
				case R.styleable.DiyViews_bkComm:
					drawable = array.getDrawable(index);
					//从getIntrinsicWidth判断Drawable的类型，-1判断为ColorDrawable(不仅仅color，其他类型的drawable也有可能是-1)
					//如果能够出去width则判断为BitmapDrawable.
					//所有只能支持ColorDrawable和BitmapDrawbale.无法解析ShapeDrawable.
					if(-1==drawable.getIntrinsicWidth()){
						ColorDrawable colordrawable = (ColorDrawable)drawable;
						mBitmapComm = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(mBitmapComm);
						canvas.drawColor(colordrawable.getColor());
					}
					else{
						BitmapDrawable bitmapdrawable = (BitmapDrawable)drawable;
						mBitmapComm = bitmapdrawable.getBitmap();
					}

					break;
				case R.styleable.DiyViews_bkPressed:
					drawable = array.getDrawable(index);
					if(-1==drawable.getIntrinsicWidth()){
						ColorDrawable colordrawable = (ColorDrawable)drawable;
						mBitmapPressed = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(mBitmapPressed);
						canvas.drawColor(colordrawable.getColor());
					}
					else{
						BitmapDrawable bitmapdrawable = (BitmapDrawable)drawable;
						mBitmapPressed = bitmapdrawable.getBitmap();
					}
					break;
				case R.styleable.DiyViews_bkInvalid:
					drawable = array.getDrawable(index);
					if(-1==drawable.getIntrinsicWidth()){
						ColorDrawable colordrawable = (ColorDrawable)drawable;
						mBitmapInvalid = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(mBitmapInvalid);
						canvas.drawColor(colordrawable.getColor());
					}
					else{
						BitmapDrawable bitmapdrawable = (BitmapDrawable)drawable;
						mBitmapInvalid = bitmapdrawable.getBitmap();
					}
					break;
			}
		}

		array.recycle();
		initPaint();
		mTextPadding = mTextPaddingLeft = mTextPaddingRight = mTextPaddingTop = mTextPaddingBottom = 0;
	}

	public CircleButtonView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public CircleButtonView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onDraw(Canvas canvas){
		mAnalysts.pre();
		mHasDrawed = false;
		mCanvas = canvas;
		if(mSuggestTextSize){
			mTextSize = getSuggestFontSize(mText);
			mPaint.setTextSize(mTextSize);
			mPaintPressed.setTextSize(mTextSize);
			mPaintInvalid.setTextSize(mTextSize);
		}

		if(VIEW_STATE_PRESSED == mViewState){
			if(null!=mBitmapPressed)
				canvas.drawBitmap(mBitmapPressed, 0, 0, null);
			else
				canvas.drawBitmap(mBitmapComm, 0, 0, null);

			if(INVALID_COLOR != mTextColorPressed)
				super.drawText(mText, TEXT_LAYOUT_CENTER, mPaintPressed);
			else
				super.drawText(mText, TEXT_LAYOUT_CENTER, mPaint);
		}
		else if(VIEW_STATE_INVALID == mViewState){
			if(null!=mBitmapInvalid)
				canvas.drawBitmap(mBitmapInvalid, 0, 0, null);
			else
				canvas.drawBitmap(mBitmapComm, 0, 0, null);

			if(INVALID_COLOR != mTextColorInvalid)
				super.drawText(mText, TEXT_LAYOUT_CENTER, mPaintInvalid);
			else
				super.drawText(mText, TEXT_LAYOUT_CENTER, mPaint);
		}
		else{
			canvas.drawBitmap(mBitmapComm, 0, 0, null);
			super.drawText(mText, TEXT_LAYOUT_CENTER, mPaint);
		}

		drawFrame();
		//
		if(null!=mDrawExtraCallBack)
			mDrawExtraCallBack.drawExtra(canvas);

		mHasDrawed = true;
		mAnalysts.post();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		checkLayoutException();
		mWidth = mHeight = mRadius*2;
		setMeasuredDimension(mWidth, mHeight);

		if(null!=mBitmapComm){
			mBitmapComm = resize(mBitmapComm, mWidth, mWidth);
			mBitmapComm = clipCircle(mBitmapComm, mRadius, mRadius, mRadius);
		}
		if(null!=mBitmapPressed){
			mBitmapPressed = resize(mBitmapPressed, mWidth, mWidth);
			mBitmapPressed = clipCircle(mBitmapPressed, mRadius, mRadius, mRadius);
		}
		if(null!=mBitmapInvalid){
			mBitmapInvalid = resize(mBitmapInvalid, mWidth, mWidth);
			mBitmapInvalid = clipCircle(mBitmapInvalid, mRadius, mRadius, mRadius);
		}

		mHasMeasured = true;
	}


	/**
	 * 设置背景图片。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。view的背景应当在xml事先指定好。
	 * */
	public void setBackground(Bitmap bitmap){
		class Delay implements Runnable{
			private Bitmap bitmap = null;
			{logout("waitting for onMeasure finish-->");}
			public Delay(Bitmap bitmap){
				this.bitmap = bitmap;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					delayms(50);
					if(mHasMeasured){
						bitmap = resize(bitmap, mWidth, mHeight);
						bitmap = clipCircle(bitmap, mRadius, mRadius, mRadius);
						mBitmapComm = bitmap;
						CircleButtonView.this.postInvalidate();
						return;
					}
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

		if(mHasMeasured){
			bitmap = resize(bitmap, mWidth, mHeight);
			bitmap = clipCircle(bitmap, mRadius, mRadius, mRadius);
			mBitmapComm = bitmap;
		}
		else{
			new Thread(new Delay(bitmap)).start();
		}
	}

	/**
	 * 不支持Drawable，已经弃用。
	 * */
	@Override
	@Deprecated
	public void setBackgroundDrawable(Drawable drawable){
		//don't support Drawable, do nothing.
	}

	/**
	 * 已弃用。
	 * */
	@Override
	@Deprecated
	public void setBackgroundResource(int id){
		//do nothing.
	}

	protected void drawFrame(){
		if(INVALID_COLOR!=mFrameColor && -1!=mFrameWidth){
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setColor(mFrameColor);
			p.setStyle(Paint.Style.STROKE);
			p.setStrokeWidth(mFrameWidth);
			mCanvas.drawCircle(mRadius, mRadius, (float)(mRadius-(float)mFrameWidth/2f), p);
		}
	}

	/**
	 * 设置背景颜色。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 不建议在onCreate中使用这个函数，虽然也可以设置，但是会有延时，
	 * 因为必须等onMeasure和onDraw都完成后createBitmap才能顺利执行，并
	 * 更新到mBitmapComm。背景应当在xml中事先指定好。</br>
	 *
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。
	 *
	 *
	 * @param	color
	 * 	背景颜色。
	 * */
	@Override
	@Deprecated
	public void setBackgroundColor(int color){
		class Delay implements Runnable{
			private int color = INVALID_COLOR;
			{logout("waitting for onMeasure finish-->");}
			public Delay(int color){
				this.color = color;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					delayms(50);
					if(mHasDrawed){
						mBitmapComm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
						Canvas c = new Canvas(mBitmapComm);
						c.drawColor(this.color);
						mBitmapComm = clipCircle(mBitmapComm, mRadius, mRadius, mRadius);
						CircleButtonView.this.postInvalidate();
						return;
					}
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

		if(mHasDrawed){
			mBitmapComm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(mBitmapComm);
			//c.drawColor(color);
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setColor(color);
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			c.drawCircle(mRadius, mRadius, mRadius, p);
		}
		else{
			new Thread(new Delay(color)).start();
		}
	}

	/**
	 * 设置边框
	 * */
	public void setFrame(int color, int width){
		this.mFrameColor = color;
		this.mFrameWidth = width;
	}


	/**
	 * 检查xml layout是否有问题。
	 *
	 * @return	true表示layout有异常
	 * @throws CircleButtonViewException
	 * */
	private boolean checkLayoutException(){
		boolean ret = true;
		if(-1==mRadius){
			ret = false;
			try {
				throw new CircleButtonViewException(CircleButtonViewException.RADIUS_UNSPECIFIED);
			} catch (CircleButtonViewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}


	private Bitmap resize(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int newWidth = w;
			int newHeight = h;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}


	/**
	 * 裁剪出一个圆形的位图。边角透明。
	 *
	 * @param	bitmap
	 * 	要裁剪的位图。
	 * @param	x
	 * 	圆心的x坐标。
	 * @param	y
	 * 	圆心的y坐标。
	 *
	 * @return
	 * 	圆形位图。
	 * */
	private Bitmap clipCircle(Bitmap bitmap, int x, int y, int radius){
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if(x<=0 || y<=0 || x>=w || y>=h ||
				x-radius<0 || y-radius<0 ||
				x+radius>w || y+radius>h)
			return null;
		Bitmap bm = Bitmap.createBitmap(radius*2, radius*2, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint();
		p.setAntiAlias(true);
		c.drawARGB(0, 0, 0, 0);
		c.drawCircle(radius, radius, radius, p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		c.translate(-(x-radius), -(y-radius));
		c.drawBitmap(bitmap, 0, 0, p);
		return bm;
	}


	private int MAX_FONT_SIZE = 1000;
	/**
	 * 根据给定的字符串，获取view所能容纳的最大的字体大小，包括padding边距也计算在内。
	 * PS:该方法是根据mPaint的设置来计算的，mPaint必须正确设置初始化。
	 *
	 * @param	text
	 * 	要计算的字符串。
	 *
	 * @return
	 * 	最大的字体大小。
	 * */
	public int getInBoundMaxFontSize(String text){
		int size = 0;
		for(int i=0; i<MAX_FONT_SIZE; i++){
			Rect rc = new Rect();
			Paint p = mPaint;
			p.setTextSize(i);
			rc = getTextDisplayRect(p, mText);
			int centerX = (int)((rc.left+rc.right)/2);
			int centerY = (int)((rc.top+rc.bottom)/2);
			int offsetX = mRadius - centerX;
			int offsetY = mRadius - centerY;
			rc.left += offsetX;
			rc.right += offsetX;
			rc.top += offsetY;
			rc.bottom += offsetY;
			Circle circle = new Circle(mRadius, mRadius, mRadius);
			if(!circle.contains(rc)){
				size = i-1;
				break;
			}
		}

		if(size>MAX_FONT_SIZE)
			size = 0;

		return size;
	}


	/**
	 * 根据给定的字符串，获取view建议的字体大小，包括padding边距也计算在内。
	 * PS:该方法是根据mPaint的设置来计算的，mPaint必须正确设置初始化。
	 *
	 * @param	text
	 * 	要计算的字符串。
	 *
	 * @return
	 * 	字体大小。
	 * */
	public int getSuggestFontSize(String text){
		float suggestScale = 0.9f;
		int size = 0;
		for(int i=0; i<MAX_FONT_SIZE; i++){
			Rect rc = new Rect();
			Paint p = mPaint;
			p.setTextSize(i);
			rc = getTextDisplayRect(p, mText);
			int centerX = (int)((rc.left+rc.right)/2);
			int centerY = (int)((rc.top+rc.bottom)/2);
			int offsetX = mRadius - centerX;
			int offsetY = mRadius - centerY;
			rc.left += offsetX;
			rc.right += offsetX;
			rc.top += offsetY;
			rc.bottom += offsetY;
			Circle circle = new Circle((int)(mRadius*suggestScale),
					(int)(mRadius*suggestScale),
					(int)(mRadius*suggestScale));
			if(!circle.contains(rc)){
				size = i-1;
				break;
			}
		}

		if(size>MAX_FONT_SIZE)
			size = 0;

		return size;
	}

	public int getRadius(){
		return this.mRadius;
	}


	@Override
	public String toString(){
		StringBuilder str = new StringBuilder("CircleButtonView attrs:");
		str.append(" radisu="+mRadius);
		str.append(" frame color="+mFrameColor);
		str.append(" frame width="+mFrameWidth);
		str.append(" text color="+mTextColor);
		str.append(" text size="+mTextSize);
		str.append(" text="+mText);
		return str.toString();
	}

	private void logout(String str){
		if(LOG_SWITCH)
			Log.i(tag, str);
	}
}

