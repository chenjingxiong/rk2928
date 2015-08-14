package com.enjack.diyviews;


import xmps.androiddebugtool.factorytest.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;


/**
 * 上下边分别显示数值(value)和名称单位(name)的TextView。
 * 有关UI的更新操作都要用postInvalidate重绘。
 * 一般应用里很少设置背景，所以不提供背景接口。
 * 如果需要绘制背景，请参考preDraw/postDraw。
 *
 * </br></br>
 * xml写法1:		</br>
 *  	android:id="@+id/id_dbg_text"	</br>
 android:layout_width="100dip"	</br>
 android:layout_height="80dp"	</br>

 </br>
 xml写法2：</br>
 android:id="@+id/id_dbg_text"		</br>
 android:layout_width="100dip"		</br>
 android:layout_height="80dp"		</br>
 custom:text="3.14"					</br>
 custom:textSize="40dip"				</br>
 custom:textColor="#ff000011"		</br>
 custom:textStrokeWidth="2dip"		</br>
 custom:textName="速度(mp/h)"			</br>
 custom:textNameColor="#aa303030"	</br>
 custom:textNameStrokeWidth="0dp"	</br>
 custom:upper="false"				</br>

 </br>

 xml写法3：</br>
 android:id="@+id/id_dbg_text"	</br>
 android:layout_width="100dip"	</br>
 android:layout_height="80dp"	</br>
 custom:text="3.14"				</br>
 custom:textName="速度(mp/h)"		</br>
 custom:textSuggestSize="true"	</br>

 </br>

 xml写法4：</br>
 android:id="@+id/id_dbg_text"			</br>
 android:layout_width="wrap_content"		</br>
 android:layout_height="wrap_content"	</br>
 custom:text="3.14"						</br>
 custom:textName="速度(mp/h)"				</br>
 custom:textSuggestSize="false"			</br>
 custom:upper="false"					</br>
 custom:textSize="40dip"					</br>
 custom:textNameSize="15dip"				</br>

 @author enjack
  * */
public class ValueTextView extends View {
	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("ValueTextView", 3);
	private int mWidth = -1;
	private int mHeight = -1;
	private Canvas mCanvas = null;
	/**value占这个view的空间比例*/
	private float mRatio = 0.7f;
	/**显示的数值*/
	private String mValue = "127";
	/**数值的单位名称*/
	private String mName = "公里(km)";
	/**value的字体颜色*/
	private int mValueColor = Color.BLACK;
	/**name的字体颜色*/
	private int mNameColor = Color.BLACK;
	/**value的字体大小*/
	private float mValueSize = 85;
	/**name的字体大小*/
	private float mNameSize = 25;
	/**value的字体粗细*/
	private float mValueStrokeWidth = 3f;
	/**name的字体粗细*/
	private float mNameStrokeWidth = 0.0f;
	/**是否采用自动计算适应的字体大小*/
	private boolean mSuggestedFontSize = false;
	/**value画笔*/
	private Paint mValuePaint = null;
	/**name画笔*/
	private Paint mNamePaint = null;
	/**value显示区域*/
	private Rect mValueRect = new Rect();
	/**name显示区域*/
	private Rect mNameRect = new Rect();
	/**value在name上面*/
	private boolean mUpper = true;
	/**额外绘图的call back*/
	private ValueTextViewExtraDraw mExtraDrawCb;

	/**表示无效的颜色，color从-16777216(#FF000000)到16777216(#00FFFFFF)*/
	protected static final int INVALID_COLOR = Integer.MAX_VALUE;

	/**额外的绘图接口。		</br>
	 * 注意：				</br>
	 * 1.由于在onDraw回调相应接口，所以应避免在preDraw、postDraw中有new Paint()/new Canvas/new Bitmap
	 * 		等操作，会影响绘图效率。绘图资源应当事先完成初始化。	</br>
	 * 2.如果更改了参入的Canvas，记得save/restore
	 * */
	public static interface  ValueTextViewExtraDraw{
		/**
		 * 在onDraw开始时
		 *
		 * @param c
		 * 	当前view使用的画布
		 * @param w
		 * 	view的width
		 * @param h
		 * 	view的height
		 * @param r1
		 * 	value的rect区域
		 * @param r2
		 * 	name的rect区域
		 * */
		public void preDraw(Canvas c, int w, int h, Rect r1, Rect r2);
		/**在onDraw结束时。
		 * @see #preDraw(Canvas, int, int, Rect, Rect)*/
		public void postDraw(Canvas c, int w, int h, Rect r1, Rect r2);
	}

	/**异常处理类*/
	private class ValueTextViewLayoutException extends Exception{
		private static final long serialVersionUID = 1L;
		public final static String EX_WRAP_CONTENT_SUGGEST_SIZE_CONFLICT =
				"<ValueTextViewLayoutException> 'mSuggestedFontSize=true' without a specified view width/height.";

		public ValueTextViewLayoutException(String msg){
			super(msg);
		}
	}

	public ValueTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DiyViews, defStyleAttr, 0);
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++){
			int index = array.getIndex(i);
			switch (index){
				case R.styleable.DiyViews_ratio://value占用view空间的比例
					mRatio = array.getFloat(index, 0.7f);
					break;
				case R.styleable.DiyViews_text://value显示的字符串
					mValue = array.getString(index);
					break;
				case R.styleable.DiyViews_textColor://value字体颜色
					mValueColor = array.getColor(index, Color.BLACK);
					break;
				case R.styleable.DiyViews_textSize://value字体大小
					mValueSize = array.getDimensionPixelSize(index, 85);
					break;
				case R.styleable.DiyViews_textStrokeWidth://value字体粗细
					mValueStrokeWidth = array.getDimensionPixelSize(index, 3);
					break;
				case R.styleable.DiyViews_textName://name显示的字符串
					mName = array.getString(index);
					break;
				case R.styleable.DiyViews_textNameColor://name的字体颜色
					mNameColor = array.getColor(index, Color.BLACK);
					break;
				case R.styleable.DiyViews_textNameSize://name字体大小
					mNameSize = array.getDimensionPixelSize(index, 25);
					break;
				case R.styleable.DiyViews_textNameStrokeWidth://name字体粗细
					mNameStrokeWidth = array.getDimensionPixelSize(index, 0);
					break;
				case R.styleable.DiyViews_textSuggestSize://是否采用自动字体大小
					mSuggestedFontSize = array.getBoolean(index, false);
					break;
				case R.styleable.DiyViews_upper://value在上还是在下
					mUpper = array.getBoolean(index, true);
					break;
			}
		}

		array.recycle();
		setupPaint();
	}

	public ValueTextView(Context context, AttributeSet attrs) {
		//super(context, attrs);
		// TODO Auto-generated constructor stub
		this(context, attrs, 0);
	}

	public ValueTextView(Context context) {
		//super(context);
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	@Override
	protected void onDraw(Canvas canvas){
		mAnalysts.pre();
		mCanvas = canvas;

		//for test
//		mCanvas.drawColor(Color.BLUE);
//		Paint p = new Paint();
//		p.setColor(Color.rgb(20, 200, 30));
//		mCanvas.drawRect(mValueRect, p);
//		p.setColor(Color.rgb(255, 60, 30));
//		mCanvas.drawRect(mNameRect, p);

		if(null!=mExtraDrawCb)
			mExtraDrawCb.preDraw(canvas, mWidth, mHeight, mValueRect, mNameRect);
		drawText();
		if(null!=mExtraDrawCb)
			mExtraDrawCb.postDraw(canvas, mWidth, mHeight, mValueRect, mNameRect);

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

		try {
			if(MeasureSpec.EXACTLY==widthMode &&
					MeasureSpec.EXACTLY==heightMode)
				checkSuggestSizeConflict(MeasureSpec.EXACTLY);
			else
				checkSuggestSizeConflict(widthMode);
		} catch (ValueTextViewLayoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mWidth = mHeight = 0;
			setMeasuredDimension(0, 0);
			return;
		}

		if (widthMode == MeasureSpec.EXACTLY)//指定确定的值或者fill_parent
		{
			width = widthSize;
		} else//指定为wrap_content或者其他
		{
			int w1 = getExactlyTextWidth(mValuePaint, mValue);
			int w2 = getExactlyTextWidth(mNamePaint, mName);
			width = Math.max(w1, w2)+1;
			width *= 1.1;//变大一点用来控制width padding
		}

		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		} else
		{
			Rect rc1 = getTextDisplayRect(mValuePaint, mValue);
			Rect rc2 = getTextDisplayRect(mNamePaint, mName);
			int h1 = rc1.height();
			int h2 = rc2.height();
			height = (int)((h1+h2+1)*1.4);//变大一点用来控制height padding
		}

		mWidth = width;
		mHeight = height;
		setMeasuredDimension(width, height);
		setupRect();

		//自动计算字体大小需要获取view的width/height，只有在onMeasure测量后才能进行计算
		if(mSuggestedFontSize)
			setupPaint();
	}

	private void setupPaint(){
		if(null==mValuePaint)
			mValuePaint = new Paint();
		if(null==mNamePaint)
			mNamePaint = new Paint();

		if(mSuggestedFontSize){
			Rect rc = new Rect();
			rc.left = (int)(mWidth*0.1f);
			rc.right = (int)(mWidth*0.9f);
			rc.top = (int)(mValueRect.height()*0.1f);
			rc.bottom = (int)(mValueRect.height()*0.9f);
			mValueSize = getSuggestedFontSize(mValue, mValuePaint, rc);

			rc.left = (int)(mWidth*0.1f);
			rc.right = (int)(mWidth*0.9f);
			rc.top = (int)(mNameRect.height()*0.1f);
			rc.bottom = (int)(mNameRect.height()*0.9f);
			mNameSize = getSuggestedFontSize(mName, mNamePaint, rc);
		}

		mValuePaint.setAntiAlias(true);
		mValuePaint.setStyle(Style.FILL_AND_STROKE);
		mValuePaint.setColor(mValueColor);
		mValuePaint.setTextSize(mValueSize);
		mValuePaint.setStrokeWidth(mValueStrokeWidth);

		mNamePaint.setAntiAlias(true);
		mNamePaint.setStyle(Style.FILL_AND_STROKE);
		mNamePaint.setColor(mNameColor);
		mNamePaint.setTextSize(mNameSize);
		mNamePaint.setStrokeWidth(mNameStrokeWidth);
	}

	private void setupRect(){
		if(mUpper){
			mValueRect.left = 0;
			mValueRect.top = 0;
			mValueRect.right = mWidth;
			mValueRect.bottom = (int)(mHeight*mRatio);

			mNameRect.left = 0;
			mNameRect.top = mValueRect.bottom+1;
			mNameRect.right = mWidth;
			mNameRect.bottom = mHeight;
		}
		else{
			mNameRect.left = 0;
			mNameRect.top = 0;
			mNameRect.right = mWidth;
			mNameRect.bottom = (int)(mHeight*(1-mRatio));

			mValueRect.left = 0;
			mValueRect.top = mNameRect.bottom+1;
			mValueRect.right = mWidth;
			mValueRect.bottom = mHeight;
		}
	}


	private void drawText(){
		float x,y;
		int width = getExactlyTextWidth(mValuePaint, mValue);
		FontMetrics fm = mValuePaint.getFontMetrics();
		x = (mWidth-width)/2;
//		if(mUpper)
//			y = mHeight-fm.descent+mValueRect.top;
//		else
//			y = -fm.ascent+mValueRect.top;
		y = mValueRect.height()/2+(-fm.ascent-fm.descent)/2+mValueRect.top;
		mCanvas.drawText(mValue, x, y, mValuePaint);

		width = getExactlyTextWidth(mNamePaint, mName);
		fm = mNamePaint.getFontMetrics();
		x = (mWidth-width)/2;
		y = mNameRect.height()/2+(-fm.ascent-fm.descent)/2+mNameRect.top;
		mCanvas.drawText(mName, x, y, mNamePaint);
	}


	private int MAX_FONT_SIZE = 1000;
	/**
	 * 给定字符串str和画笔p，以及区域rc，计算能容纳的字体大小
	 * */
	private int getSuggestedFontSize(String str, Paint p, Rect rc){
		int size = 0;
		int permittedWidth = rc.width();
		int permittedHeight = rc.height();
		for(int i=1; i<MAX_FONT_SIZE; i++){
			Rect r = new Rect();
			p.setTextSize(i);
			p.getTextBounds(str, 0, str.length(), r);
			if(r.width()>=permittedWidth || r.height()>=permittedHeight){
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
	private int getExactlyTextWidth(Paint paint, String str) {
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
	private Rect getTextDisplayRect(Paint p, String str){
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

	/**
	 * 检查是否采用建议字体大小，但是又没有指定view的具体大小。
	 * @return
	 * 	true表示没有冲突，false表示冲突。
	 * */
	protected boolean checkSuggestSizeConflict(int mode) throws ValueTextViewLayoutException{
		boolean ret = true;
		if(mode != MeasureSpec.EXACTLY  &&
				mSuggestedFontSize){
			ret = false;
			throw new ValueTextViewLayoutException(ValueTextViewLayoutException.EX_WRAP_CONTENT_SUGGEST_SIZE_CONFLICT);
		}

		return ret;
	}

	/**设置显示的数值*/
	public void setValue(int value){
		this.mValue = String.valueOf(value);
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置显示的数值*/
	public void setValue(float value){
		this.mValue = String.valueOf(value);
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置显示的数值*/
	public void setValue(double value){
		this.mValue = String.valueOf(value);
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置显示的数值*/
	public void setValue(boolean value){
		this.mValue = String.valueOf(value);
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置显示的数值*/
	public void setValue(String value){
		this.mValue = value;
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置value的字体颜色*/
	public void setValueColor(int color){
		this.mValueColor = color;
		setupPaint();
	}

	/**设置value字体大小*/
	public void setValueSize(float size){
		this.mValueSize = size;
		setupPaint();
	}

	/**设置value的字体粗细*/
	public void setValueStrokeWidth(float width){
		this.mValueStrokeWidth = width;
		setupPaint();
	}

	/**设置name*/
	public void setName(String name){
		this.mName = name;
		setupPaint();
	}

	/**设置name字体颜色*/
	public void setNameColor(int color){
		this.mNameColor = color;
		setupPaint();
	}

	/**设置name字体大小*/
	public void setNameSize(float size){
		this.mNameSize = size;
		setupPaint();
	}

	/**设置name字体粗细*/
	public void setNameStrokeWidth(float width){
		this.mNameStrokeWidth = width;
		setupPaint();
	}

	/**设置value占父view空间比例大小*/
	public void setRatio(float ratio){
		this.mRatio = ratio;
		setupRect();
		if(mSuggestedFontSize)
			setupPaint();
	}

	/**设置是否采用自动计算适应view的字体大小*/
	public void setSuggestedFontSize(boolean suggest){
		this.mSuggestedFontSize = suggest;
		setupPaint();
	}

	/**
	 * 设置value是否在name之上
	 *
	 * @param	up
	 * true:value在上 ; flase:name在上
	 * */
	public void setUpper(boolean up){
		this.mUpper = up;
	}

	//
	public void setExtraDrawCallBack(ValueTextViewExtraDraw cb){
		if(cb!=null && cb instanceof ValueTextViewExtraDraw)
			mExtraDrawCb = cb;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("<ValueTextView> ");
		sb.append(" width:"+mWidth);
		sb.append(" height:"+mHeight);
		sb.append(" ratio:"+mRatio);
		sb.append(" value-rect:"+mValueRect.left+","+mValueRect.right+","+mValueRect.top+","+mValueRect.bottom);
		sb.append(" name-rect:"+mNameRect.left+","+mNameRect.right+","+mNameRect.top+","+mNameRect.bottom);
		sb.append(" upper:"+mUpper);
		sb.append(" value-text:"+mValue);
		sb.append(" value-text-size:"+mValueSize);
		sb.append(" value-text-stroke:"+mValueStrokeWidth);
		sb.append(" name:"+mName);
		sb.append(" name-text-size:"+mNameSize);
		sb.append(" name-text-stroke:"+mNameStrokeWidth);
		return sb.toString();
	}
}
