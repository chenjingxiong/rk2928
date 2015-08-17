package com.enjack.diyviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.enjack.util.LCDMetrixUtil;

import xmps.androiddebugtool.factorytest.R;


/**
 * 矩形view，类似button。注意：</br>
 * 1.对该view的UI更新操作都要调用postInvalidate执行才生效。
 * 		该view在更新UI后没有使自己重绘是为了防止一系列UI设置，
 * 		比如设置文字、背景等导致频繁无效的重绘操作。
 * </br>
 *
 * 2.如果custom:textSuggestSize="true"，那么不能将view的大小设置为wrap_content。
 *
 * </br>
 *
 * 3.view显示的内容，包括文字、背景等，需要用attrs.xml自定义的属性。
 *
 * </br>
 *
 * 4.如果作为ScrollView的子view，会有onTouchEvent的问题。有两种办法解决，一是把ACTION_CANCEL当成ACTION_UP处理，
 * 		这种方式无法检测到和ScrollView滚动方向一致(垂直方向)的滑动。二是采用TouchScrollView，建议采用TouchScrollView
 *
 * </br>
 *
 * xml写法例子：	</br>
 *
 *           android:id="@+id/start_factory_test"				</br>
 *           android:layout_width="180dip"						</br>
 *           android:layout_height="90dip"						</br>
 *           android:layout_centerInParent="true"				</br>
 *           custom:text="123"									</br>
 *           custom:textSize="34sp"								</br>
 *           custom:textColor="#ff000000"						</br>
 *           custom:textColorPressed="#ffffffff"				</br>
 *           custom:textColorInvalid="#ff909090"				</br>
 *           custom:textPadding="10dip"							</br>
 *           custom:textSuggestSize="false"						</br>
 *           custom:textGravity="center"						</br>
 *           custom:bkComm="#ffeeffee"							</br>
 *           custom:bkPressed="@drawable/comm_bk_press_shape"	</br>
 *           custom:bkInvalid="#ffffff00"						</br>
 *
 *
 * @author enjack.20150611
 *
 * @see com.enjack.diyviews.TouchScrollView
 * */
public class SimpleView extends View {
	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("SimpleView", 3);
	private final String tag = "<SimpleView>";
	private OnClickListener mClickListener = null;
	/**是否完成onMeasure*/
	private boolean mHasMeasured = false;
	/**额外的draw操作*/
	protected DrawExtraCallBack mDrawExtraCallBack = null;
	/**view的画布*/
	protected Canvas mCanvas = null;
	/**view写字的画笔*/
	protected Paint mPaint = null;
	protected Paint mPaintPressed = null;
	protected Paint mPaintInvalid = null;
	/**view的长宽*/
	protected int mWidth = 0;
	protected int mHeight = 0;
	/**view上显示的文字*/
	protected String mText = "";
	/**文字显示的位置*/
	protected int mTextLayout = TEXT_LAYOUT_CENTER;
	/**文字padding*/
	protected int mTextPadding = -1;
	protected int mTextPaddingTop = -1;
	protected int mTextPaddingLeft = -1;
	protected int mTextPaddingRight = -1;
	protected int mTextPaddingBottom = -1;
	/**文字颜色*/
	protected int mTextColor = Color.rgb(0, 0, 0);
	/**按下时的文字颜色*/
	protected int mTextColorPressed = INVALID_COLOR;
	/**view在disable状态下的文字颜色*/
	protected int mTextColorInvalid = INVALID_COLOR;
	/**文字大小*/
	protected int mTextSize = 35;
	/**是否采用自动计算出来的建议的字体大小*/
	protected boolean mSuggestTextSize = false;
	/**背景drawable*/
	protected Drawable mDrawable = null;
	/**按下时背景drawbale*/
	protected Drawable mDrawablePressed = null;
	/**view处于disable状态下的背景drawbale*/
	protected Drawable mDrawableInvalid = null;
	/**view的状态(正常/按下/不可用)*/
	protected int mViewState = VIEW_STATE_NORMAL;

	/**--------------文字的显示排列方式----------------*/
	public final static int TEXT_LAYOUT_CENTER = 1;
	public final static int TEXT_LAYOUT_CENTER_LEFT = 2;
	public final static int TEXT_LAYOUT_CENTER_RIGHT = 3;
	public final static int TEXT_LAYOUT_CENTER_TOP = 4;
	public final static int TEXT_LAYOUT_CENTER_BOTTOM = 5;
	public final static int TEXT_LAYOUT_LEFT_TOP = 6;
	public final static int TEXT_LAYOUT_RIGHT_TOP = 7;
	public final static int TEXT_LAYOUT_LEFT_BOTTOM = 8;
	public final static int TEXT_LAYOUT_RIGHT_BOTTOM = 9;
	public final static int TEXT_LAYOUT_DEFAULT = 1;

	/**默认文字大小*/
	protected final float DEFAULT_TEXT_SIZE = 35;//in pixel
	/**表示无效的颜色，color从-16777216(#FF000000)到16777216(#00FFFFFF)*/
	protected static final int INVALID_COLOR = Integer.MAX_VALUE;

	/**log switch*/
	private final boolean LOG_SWITCH = true;

	/**VIEW的状态*/
	protected static final int VIEW_STATE_NORMAL = 1;
	protected static final int VIEW_STATE_PRESSED = 2;
	protected static final int VIEW_STATE_INVALID = 3;


	/**
	 * SimpleView异常类。
	 * */
	protected static class SimpleViewException extends Exception{

		private static final long serialVersionUID = 1L;
		public static final String UNKNOW_VIEW_EXCEPTION = "unknow exception!";
		public static final String SUGGEST_FONT_SIZE_WITHOUT_SPECIFIED_VIEW_SIZE = "textSuggestSize was setted to true without a definitized LAYOUT_WIDTH & LAYOUT_HEIGHT!";

		public SimpleViewException(){
			super(UNKNOW_VIEW_EXCEPTION);
		}

		public SimpleViewException(String msg){
			super(msg);
		}
	}


	/**
	 * 额外画图的回调接口。
	 * */
	public static interface DrawExtraCallBack{
		public void drawExtra(Canvas canvas);
	}


	@SuppressLint("ClickableViewAccessibility")
	public SimpleView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);

		//get attributes--->
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyle, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++){
			int index = array.getIndex(i);
			switch (index){//如果xml没有指定某个属性，那么array中也不会有这个属性。
				case R.styleable.DiyViews_text:
					mText = array.getString(index);
					break;
				case R.styleable.DiyViews_textColor:
					mTextColor = array.getColor(index, Color.BLACK);
					break;
				case R.styleable.DiyViews_textColorPressed:
					mTextColorPressed = array.getColor(index, INVALID_COLOR);
					break;
				case R.styleable.DiyViews_textColorInvalid:
					mTextColorInvalid = array.getColor(index, INVALID_COLOR);
					break;
				case R.styleable.DiyViews_textSize:
					mTextSize = array.getDimensionPixelSize(index, (int)DEFAULT_TEXT_SIZE);
					break;
				case R.styleable.DiyViews_textSuggestSize:
					mSuggestTextSize = array.getBoolean(index, false);
					break;
				case R.styleable.DiyViews_textPadding:
					mTextPadding = array.getDimensionPixelSize(index, -1);
					break;
				case R.styleable.DiyViews_textPaddingTop:
					mTextPaddingTop = array.getDimensionPixelSize(index, -1);
					break;
				case R.styleable.DiyViews_textPaddingLeft:
					mTextPaddingLeft = array.getDimensionPixelSize(index, -1);
					break;
				case R.styleable.DiyViews_textPaddingRight:
					mTextPaddingRight = array.getDimensionPixelSize(index, -1);
					break;
				case R.styleable.DiyViews_textPaddingBottom:
					mTextPaddingBottom = array.getDimensionPixelSize(index, -1);
					break;
				case R.styleable.DiyViews_textGravity:
					int val = array.getInt(index, 1);
					switch(val){
						case 1:
							mTextLayout = TEXT_LAYOUT_CENTER;break;
						case 2:
							mTextLayout = TEXT_LAYOUT_CENTER_LEFT;break;
						case 3:
							mTextLayout = TEXT_LAYOUT_CENTER_RIGHT;break;
						case 4:
							mTextLayout = TEXT_LAYOUT_CENTER_TOP;break;
						case 5:
							mTextLayout = TEXT_LAYOUT_CENTER_BOTTOM;break;
						case 6:
							mTextLayout = TEXT_LAYOUT_LEFT_TOP;break;
						case 7:
							mTextLayout = TEXT_LAYOUT_RIGHT_TOP;break;
						case 8:
							mTextLayout = TEXT_LAYOUT_LEFT_BOTTOM;break;
						case 9:
							mTextLayout = TEXT_LAYOUT_RIGHT_BOTTOM;break;
						default:
							mTextLayout = TEXT_LAYOUT_DEFAULT;break;
					}
					break;
				case R.styleable.DiyViews_bkComm:
					mDrawable = array.getDrawable(index);
					break;
				case R.styleable.DiyViews_bkPressed:
					mDrawablePressed = array.getDrawable(index);
					break;
				case R.styleable.DiyViews_bkInvalid:
					mDrawableInvalid = array.getDrawable(index);
					break;
			}
		}

		//
		if(-1==mTextPadding)
			mTextPadding = LCDMetrixUtil.dip2px(this.getContext(), 6f);
		if(-1==mTextPaddingTop)
			mTextPaddingTop = mTextPadding;
		if(-1==mTextPaddingBottom)
			mTextPaddingBottom = mTextPadding;
		if(-1==mTextPaddingLeft)
			mTextPaddingLeft = mTextPadding;
		if(-1==mTextPaddingRight)
			mTextPaddingRight = mTextPadding;

		initPaint();

		array.recycle();
	}

	@SuppressLint("ClickableViewAccessibility")
	public SimpleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
//		super(context, attrs);
//		mTextPadding = LCDMetrixUtil.dip2px(this.getContext(), 6f); 
//		mTextPaddingTop = mTextPaddingBottom = mTextPaddingLeft = mTextPaddingRight = mTextPadding;
//		initPaint();
//		TouchListener listener = new TouchListener();
//		this.setOnTouchListener(listener);
	}

	@SuppressLint("ClickableViewAccessibility")
	public SimpleView(Context context){
		this(context, null);
//		super(context);
//		mTextPadding = LCDMetrixUtil.dip2px(this.getContext(), 6f);
//		mTextPaddingTop = mTextPaddingBottom = mTextPaddingLeft = mTextPaddingRight = mTextPadding;
//		initPaint();
//		TouchListener listener = new TouchListener();
//		this.setOnTouchListener(listener);
	}

	@Override
	protected void onDraw(Canvas canvas){
		mAnalysts.pre();
		this.mWidth = this.getWidth();
		this.mHeight = this.getHeight();
		this.mCanvas = canvas;
		if(mSuggestTextSize){
			mTextSize = getSuggestFontSize(mText);
			mPaint.setTextSize(mTextSize);
			mPaintPressed.setTextSize(mTextSize);
			mPaintInvalid.setTextSize(mTextSize);
		}
		//
		if(VIEW_STATE_PRESSED == mViewState){
			if(null!=mDrawablePressed){
				mDrawablePressed.setBounds(0, 0, mWidth, mHeight);
				mDrawablePressed.draw(mCanvas);
			}
			else
				drawBackground();

			if(INVALID_COLOR != mTextColorPressed)
				drawText(mText, mTextLayout, mPaintPressed);
			else
				drawText(mText, mTextLayout);
		}
		else if(VIEW_STATE_INVALID == mViewState){
			if(null!=mDrawableInvalid){
				mDrawableInvalid.setBounds(0, 0, mWidth, mHeight);
				mDrawableInvalid.draw(mCanvas);
			}
			else
				drawBackground();

			if(INVALID_COLOR != mTextColorInvalid)
				drawText(mText, mTextLayout, mPaintInvalid);
			else
				drawText(mText, mTextLayout);
		}
		else{
			drawBackground();
			drawText(this.mText, this.mTextLayout);
		}

		if(null!=mDrawExtraCallBack)
			mDrawExtraCallBack.drawExtra(canvas);
		mAnalysts.post();
	}

	/**
	 * 重写onMeasure测量view的大小，如果不重写，那么wrap_content就会变成fill_parent大小。
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
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


		if (widthMode == MeasureSpec.EXACTLY)//指定确定的值或者fill_parent
		{
			width = widthSize;
		} else//指定为wrap_content或者其他
		{
			int textWidth = getExactlyTextWidth(mPaint, mText);
			width = textWidth+mTextPaddingLeft+mTextPaddingRight;
		}

		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		} else
		{
			Rect rc = getTextDisplayRect(mPaint, mText);
			height = rc.height()+mTextPaddingTop+mTextPaddingBottom;
		}

		mWidth = width;
		mHeight = height;
		setMeasuredDimension(width, height);
		mHasMeasured = true;
	}


	/**
	 * 检查是否采用建议字体大小，但是又没有指定view的具体大小。
	 *
	 * @param	mode
	 * 	测量模式
	 *
	 * @return
	 * 	true表示没有冲突，false表示冲突。
	 * */
	protected boolean checkSuggestSizeConflict(int mode) throws SimpleViewException{
		boolean ret = true;
		if(mode != MeasureSpec.EXACTLY  &&
				mSuggestTextSize){
			ret = false;
			throw new SimpleViewException(SimpleViewException.SUGGEST_FONT_SIZE_WITHOUT_SPECIFIED_VIEW_SIZE);
		}

		return ret;
	}


	protected void initPaint(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mTextColor);
		//p.setStyle(Style.STROKE);//空心
		mPaint.setStrokeWidth(1);
		mPaint.setTextSize(mTextSize);
		//
		mPaintPressed = new Paint();
		mPaintPressed.setAntiAlias(true);
		mPaintPressed.setColor(mTextColorPressed);
		mPaintPressed.setStrokeWidth(1);
		mPaintPressed.setTextSize(mTextSize);
		//
		mPaintInvalid = new Paint();
		mPaintInvalid.setAntiAlias(true);
		mPaintInvalid.setColor(mTextColorInvalid);
		mPaintInvalid.setStrokeWidth(1);
		mPaintInvalid.setTextSize(mTextSize);
	}


	/**
	 * 设置写字的画笔。该方法属于UI更新操作，请postInvalidate重绘。
	 * */
	public void setPaint(Paint p){
		this.mPaint = p;
	}


	/**
	 * 设置文字的大小。该方法属于UI更新操作，请postInvalidate重绘。
	 * */
	public void setTextSize(int size){
		mPaint.setTextSize(size);
		mPaintPressed.setTextSize(size);
		mPaintInvalid.setTextSize(size);
	}


	/**
	 * 设置text是否根据view的大小自动给出建议的字体大小。
	 * 注意，如果设置为true，那么java/xml中设定的text size都将不起作用。
	 * 该方法属于UI更新操作，请手动使view重绘。
	 *
	 * @param	b
	 * 	true:yes.	false:no.
	 * */
	public void setTextSizeSuggested(boolean b){
		this.mSuggestTextSize = b;
	}

	/**
	 * 设置view内文字padding。该方法属于UI更新操作，请postInvalidate重绘。
	 *
	 * @param	left
	 * 	left padding
	 * @param	top
	 * 	top padding
	 * @param	right
	 * 	right padding
	 * @param	bottom
	 * 	bottom padding
	 * */
	public void setTextPadding(int left, int top, int right, int bottom){
		this.mTextPaddingLeft = left;
		this.mTextPaddingTop = top;
		this.mTextPaddingRight = right;
		this.mTextPaddingBottom = bottom;
	}

	/**
	 * 设置文字显示的位置。该方法属于UI更新操作，请postInvalidate重绘。
	 *
	 * @param layout
	 * 1--居中(TEXT_LAYOUT_CENTER)；
	 * 2--靠左居中(TEXT_LAYOUT_CENTER_LEFT)；
	 * 3--靠右居中(TEXT_LAYOUT_CENTER_RIGHT)；
	 * 4--靠上居中(TEXT_LAYOUT_CENTER_TOP)；
	 * 5--靠下居中(TEXT_LAYOUT_CENTER_BOTTOM)；
	 * 6--左上角(TEXT_LAYOUT_LEFT_TOP)；
	 * 7--右上角(TEXT_LAYOUT_RIGHT_TOP)；
	 * 8--左下角(TEXT_LAYOUT_LEFT_BOTTOM)；
	 * 9--右下角(TEXT_LAYOUT_RIGHT_BOTTOM)；
	 * */
	public void setTextLayout(int layout){
		mTextLayout = layout;
	}


	/**
	 * 设置显示的文字。该方法属于UI更新操作，请postInvalidate重绘。
	 * */
	public void setText(String text){
		mText = text;
	}


	/**
	 * 获取view上显示的问题。
	 * */
	public String getText(){
		return this.mText;
	}

	/**
	 * 设置背景图片。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。view的背景应当在xml事先指定好。
	 * */
	@SuppressWarnings("deprecation")
	public void setBackground(Bitmap bitmap){
		this.mDrawable = new BitmapDrawable(bitmap);
	}

	/**
	 * 设置背景图片。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。view的背景应当在xml事先指定好。
	 * */
	@Override
	public void setBackgroundDrawable(Drawable drawable){
		this.mDrawable = drawable;
	}

	/**
	 * 设置背景图片。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。view的背景应当在xml事先指定好。
	 * */
	@Override
	public void setBackgroundResource(int id){
		this.mDrawable = this.getResources().getDrawable(id);
	}


	/**
	 * 设置背景颜色。该方法属于UI更新操作，请postInvalidate重绘。</br>
	 * 注意：</br>
	 * 该方法设置的是VIEW_STATE_NORMAL的状态，在按下或者无效状态时候显示背景并没有改变。view的背景应当在xml事先指定好。
	 * */
	public void setBackgroundColor(int color){
		this.mDrawable = new ColorDrawable(color);
	}

	protected void drawBackground(){
//		if(null!=mBitmap){
//			int width = mBitmap.getWidth();
//			int height = mBitmap.getHeight();
//			float scaleW = (float)mWidth/(float)width;
//			float scaleH = (float)mHeight/(float)height;
//			Matrix matrix = new Matrix();
//			matrix.postScale(scaleW, scaleH);
//			Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);
//			mCanvas.drawBitmap(resizedBitmap, 0, 0, mPaint);
//		}
		if(null!=mDrawable){
			mDrawable.setBounds(0, 0, mWidth, mHeight);
			mDrawable.draw(mCanvas);
		}
	}



	/**
	 * 在view上写字。
	 *
	 * @param text
	 * 	显示的文字。
	 * @param layout
	 * 1--居中(TEXT_LAYOUT_CENTER)；
	 * 2--靠左居中(TEXT_LAYOUT_CENTER_LEFT)；
	 * 3--靠右居中(TEXT_LAYOUT_CENTER_RIGHT)；
	 * 4--靠上居中(TEXT_LAYOUT_CENTER_TOP)；
	 * 5--靠下居中(TEXT_LAYOUT_CENTER_BOTTOM)；
	 * 6--左上角(TEXT_LAYOUT_LEFT_TOP)；
	 * 7--右上角(TEXT_LAYOUT_RIGHT_TOP)；
	 * 8--左下角(TEXT_LAYOUT_LEFT_BOTTOM)；
	 * 9--右下角(TEXT_LAYOUT_RIGHT_BOTTOM)；
	 * @param	paint
	 * 	画笔
	 * */
	protected void drawText(String text, int layout, Paint paint){
		float x,y;
		Rect bounds = new Rect();
		bounds = getTextDisplayRect(paint, text);
		int width = getExactlyTextWidth(paint, text);
		FontMetrics fm = paint.getFontMetrics();
		switch(layout){
			case TEXT_LAYOUT_CENTER_LEFT:
				x = mTextPaddingLeft;
				y = mHeight/2+(-fm.ascent-fm.descent)/2;
				break;
			case TEXT_LAYOUT_CENTER_RIGHT:
				x = mWidth-bounds.width()-mTextPaddingRight;
				y = mHeight/2+(-fm.ascent-fm.descent)/2;
				break;
			case TEXT_LAYOUT_CENTER_TOP:
				x = (mWidth-width)/2;
				y = -fm.ascent+mTextPaddingTop;
				break;
			case TEXT_LAYOUT_CENTER_BOTTOM:
				x = (mWidth-width)/2;
				y = mHeight-fm.descent-mTextPaddingBottom;
				break;
			case TEXT_LAYOUT_LEFT_TOP:
				x = mTextPaddingLeft;
				y = -fm.ascent+mTextPaddingTop;
				break;
			case TEXT_LAYOUT_RIGHT_TOP:
				x = mWidth-bounds.width()-mTextPaddingRight;
				y = -fm.ascent+mTextPaddingTop;
				break;
			case TEXT_LAYOUT_LEFT_BOTTOM:
				x = mTextPaddingLeft;
				y = mHeight-fm.descent-mTextPaddingBottom;
				break;
			case TEXT_LAYOUT_RIGHT_BOTTOM:
				x = mWidth-bounds.width()-mTextPaddingRight;
				y = mHeight-fm.descent-mTextPaddingBottom;
				break;
			case TEXT_LAYOUT_CENTER:
			default:
				x = (mWidth-width)/2;
				y = mHeight/2+(-fm.ascent-fm.descent)/2;
		}
		mCanvas.drawText(text, x, y, paint);
	}


	/**
	 * 在view上写字。
	 *
	 * @param text
	 * 	显示的文字。
	 * @param layout
	 * 1--居中(TEXT_LAYOUT_CENTER)；
	 * 2--靠左居中(TEXT_LAYOUT_CENTER_LEFT)；
	 * 3--靠右居中(TEXT_LAYOUT_CENTER_RIGHT)；
	 * 4--靠上居中(TEXT_LAYOUT_CENTER_TOP)；
	 * 5--靠下居中(TEXT_LAYOUT_CENTER_BOTTOM)；
	 * 6--左上角(TEXT_LAYOUT_LEFT_TOP)；
	 * 7--右上角(TEXT_LAYOUT_RIGHT_TOP)；
	 * 8--左下角(TEXT_LAYOUT_LEFT_BOTTOM)；
	 * 9--右下角(TEXT_LAYOUT_RIGHT_BOTTOM)；
	 * */
	protected void drawText(String text, int layout){
		drawText(text, layout, mPaint);
	}

	/**居中显示文字*/
	protected void drawText(String text){
		drawText(text, TEXT_LAYOUT_CENTER);
	}


	/**
	 * 获取该view的画布。
	 * */
	public Canvas getCanvas(){
		return this.mCanvas;
	}


	@Override
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		if(!enabled)
			mViewState = VIEW_STATE_INVALID;
		else
			mViewState = VIEW_STATE_NORMAL;
	}


	protected void onTouchDown(int x, int y){
		//logout("onTouchDown("+x+","+y+")");
		this.mViewState = VIEW_STATE_PRESSED;
		this.invalidate();
	}

	protected void onTouchUp(int x, int y){
		//logout("onTouchUp("+x+","+y+")");
		this.mViewState = VIEW_STATE_NORMAL;
		this.invalidate();
	}

	protected void onClicked(){
		//logout("onClicked");
		if(null!=mClickListener)
			mClickListener.onClick(this);
	}

	protected void onDoubleClicked(){
		logout("onDoubleClicked");
	}

	protected void onTouchSlide(int x1, int y1, int x2, int y2){
		logout("onTouchSlide("+x1+","+y1+"-->"+x2+","+y2+")");
	}

	@Override
	public void setOnClickListener(OnClickListener listener){
		mClickListener = listener;
	}


	private long mTouchUpTime = 0;
	private int mPreTouchState = 1;// 0 for down, 1 for up.
	private Point mFirstPoint = new Point(0, 0);//store the start point of sliding. 
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//getParent().requestDisallowInterceptTouchEvent(true);//屏蔽父控件拦截onTouch事件
		if(MotionEvent.ACTION_DOWN == (event.getAction()&MotionEvent.ACTION_MASK)){
			int x = (int)event.getX();
			int y = (int)event.getY();
			onTouchDown(x, y);

			if(1==mPreTouchState){
				mFirstPoint.x = x;
				mFirstPoint.y = y;
			}
			mPreTouchState = 0;
		}
		else if(MotionEvent.ACTION_UP == (event.getAction()&MotionEvent.ACTION_MASK)){
			int x = (int)event.getX();
			int y = (int)event.getY();
			onTouchUp(x, y);
			if(System.currentTimeMillis() - mTouchUpTime < 300)
				onDoubleClicked();
			//else if(mFirstPoint.x==x && mFirstPoint.y==y)
			else if(Math.abs(mFirstPoint.x-x)<=10 && Math.abs(mFirstPoint.y-y)<=10)
				onClicked();
			mTouchUpTime = System.currentTimeMillis();
			if(0==mPreTouchState ){
				Point p = new Point(0, 0);
				p.x = x;
				p.y = y;
				if(Math.abs(x*x+y*y-mFirstPoint.x*mFirstPoint.x-mFirstPoint.y*mFirstPoint.y)>100)
					onTouchSlide(mFirstPoint.x, mFirstPoint.y, x, y);
			}
			mPreTouchState = 1;
		}
		/*如果是放在普通的ScrollView里，就打开下面这段注释代码
		 * 如果是放在TouchScrollView里，就不需要把ACTION_CANCEL当成ACTION_UP处理*/
//		else if(MotionEvent.ACTION_CANCEL == (event.getAction()&MotionEvent.ACTION_MASK)){
//			onTouchUp((int)event.getX(), (int)event.getY());
//		}
		return true;
	}


	/**判断是否完成onMeasure*/
	public boolean hasMeasured(){
		return this.mHasMeasured;
	}



	/**
	 * 设置额外的draw操作回调。
	 *
	 * @param cb
	 * 	回调接口
	 *
	 * @see DrawExtraCallBack
	 * */
	public void setDrawExtraCallBack(DrawExtraCallBack cb){
		this.mDrawExtraCallBack = cb;
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
		int permittedWidth = mWidth - mTextPaddingLeft - mTextPaddingRight;
		int permittedHeight = mHeight - mTextPaddingTop - mTextPaddingBottom;
		for(int i=0; i<MAX_FONT_SIZE; i++){
			Rect rc = new Rect();
			Paint p = mPaint;
			p.setTextSize(i);
			p.getTextBounds(text, 0, text.length(), rc);
			if(rc.width()>=permittedWidth || rc.height()>=permittedHeight){
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
		int size =0;
		int permittedWidth = mWidth - mTextPaddingLeft - mTextPaddingRight;
		int permittedHeight = (mHeight - mTextPaddingTop - mTextPaddingBottom)/2;
		for(int i=0; i<MAX_FONT_SIZE; i++){
			Rect rc = new Rect();
			Paint p = mPaint;
			p.setTextSize(i);
			p.getTextBounds(text, 0, text.length(), rc);
			if(rc.width()>=permittedWidth || rc.height()>=permittedHeight){
				size = i-1;
				break;
			}
		}

		if(size>MAX_FONT_SIZE)
			size = 0;

		return size;
	}


	/**
	 * 精确的计算字符串的显示长度。
	 * */
	public int getExactlyTextWidth(Paint paint, String str) {
		int iRet = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				iRet += (int) Math.ceil(widths[j]);
			}
		}
		return iRet;
	}


	/**
	 * 获取字符串显示的矩形区域。直接用getTextBounds会得到负数，这里转换成正数。
	 * */
	public Rect getTextDisplayRect(Paint p, String str){
		Rect rc = new Rect();
		p.getTextBounds(str, 0, str.length(), rc);
		if(rc.left<0){
			rc.left = Math.abs(rc.left);
			rc.right += 2*rc.left;
		}
		if(rc.top<0){
			rc.top = Math.abs(rc.top);
			rc.bottom += 2*rc.top;
		}
		return rc;
	}


	@Override
	public String toString(){
		StringBuilder str = new StringBuilder("SimpleView attrs:");
		str.append(" [width]:"+this.mWidth);
		str.append(" [height]:"+this.mHeight);
		str.append(" [text]:"+this.mText);
		str.append(" [text color]:"+this.mTextColor);
		str.append(" [text color pressed]:"+this.mTextColorPressed);
		str.append(" [text color invalid]:"+this.mTextColorInvalid);
		str.append(" [text size]:"+this.mTextSize);
		str.append(" [text suggest size]:"+this.mSuggestTextSize);
		str.append(" [padding]:"+this.mTextPadding);
		str.append(" [padding left]:"+this.mTextPaddingLeft);
		str.append(" [padding right]:"+this.mTextPaddingRight);
		str.append(" [padding top]:"+this.mTextPaddingTop);
		str.append(" [padding bottom]:"+this.mTextPaddingBottom);
		str.append(" [bk]:"+(null==mDrawable?"unspecfied":"specfied"));
		str.append(" [bk-pressed]:"+(null==mDrawablePressed?"unspecfied":"specfied"));
		str.append(" [bk-invalid]:"+(null==mDrawableInvalid?"unspecfied":"specfied"));
		switch(mTextLayout){
			case TEXT_LAYOUT_CENTER:
				str.append(" [text layout]:center");break;
			case TEXT_LAYOUT_CENTER_LEFT:
				str.append(" [text layout]:center_left");break;
			case TEXT_LAYOUT_CENTER_RIGHT:
				str.append(" [text layout]:center_right");break;
			case TEXT_LAYOUT_CENTER_TOP:
				str.append(" [text layout]:center_top");break;
			case TEXT_LAYOUT_CENTER_BOTTOM:
				str.append(" [text layout]:center_bottom");break;
			case TEXT_LAYOUT_LEFT_TOP:
				str.append(" [text layout]:left_top");break;
			case TEXT_LAYOUT_RIGHT_TOP:
				str.append(" [text layout]:right_top");break;
			case TEXT_LAYOUT_LEFT_BOTTOM:
				str.append(" [text layout]:left_bottom");break;
			case TEXT_LAYOUT_RIGHT_BOTTOM:
				str.append(" [text layout]:right_bottom");break;
			default:
				str.append(" [text layout]:unspecified");break;
		}
		return str.toString();
	}

	private void logout(String str){
		if(LOG_SWITCH)
			Log.i(tag, str);
	}
}
