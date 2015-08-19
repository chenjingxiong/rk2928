package com.enjack.diyviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import xmps.androiddebugtool.factorytest.R;

/**
 * 圆形的button。不支持图片。有关UI更新操作要invalid view。
 *
 * </br></br>
 * xml:</br>
         android:id="@+id/id_dbg_ccbv"  </br>
         android:layout_width="0dip"    </br>
         android:layout_height="0dip"   </br>
         custom:bkColor="#ff990000"     </br>
         custom:bkColorPressed="#ff000099"      </br>
         custom:radius="@dimen/wdiv25"  </br>
         custom:frameColor="#ff00ff00"  </br>
         custom:frameWidth="6px"        </br>
         custom:text="Circle"           </br>
         custom:textSize="12sp"         </br>
         custom:textSuggestSize="true"  </br>
         custom:textColor="#ffeeffee"   </br>
         custom:textColorPressed="#84480d98"    </br>
 @author enjack
 * Created by enjack on 2015/8/16.
 */
public class CircleColorButtonView extends View{

    protected final int INVALID_COLOR = Integer.MAX_VALUE;
    protected final int MAX_FONT_SIZE = 1000;
    private final String tag = "<CircleColorButtonView>";
    private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("CircleColorButtonView", 2);
    protected DrawExtraCallBack mCallBackDraw = null;
    private boolean mClickAble = true;
    protected int mRadius = -1;
    protected int mHeight = 0;
    protected int mWidth = 0;
    protected boolean mSuggestTextSize = false;
    protected String mText = "";
    protected int mTextSize = 0;
    protected int mFrameColor = INVALID_COLOR;
    protected int mFrameWidth = -1;
    protected int mColorBackground = Color.argb(0, 0, 0, 99);
    protected int mColorBackgroundPressed = Color.argb(0, 0, 0, 0);
    protected int mColorText = Color.argb(255, 0, 0, 0);
    protected int mColorTextPressed = Color.argb(255, 0, 0, 0);
    protected Paint mPaintBackround = null;
    protected Paint mPaintFrame = null;
    protected Paint mPaintText = null;

    public CircleColorButtonView(Context context) {
        //super(context);
        this(context, null);
    }

    public CircleColorButtonView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
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
                case R.styleable.DiyViews_textSuggestSize:
                    mSuggestTextSize = array.getBoolean(index, false);
                    break;
                case R.styleable.DiyViews_text:
                    mText = array.getString(index);
                    break;
                case R.styleable.DiyViews_textSize:
                    mTextSize = array.getDimensionPixelSize(index, 35);
                    break;
                case R.styleable.DiyViews_textColor:
                    mColorText = array.getColor(index, mColorText);
                    break;
                case R.styleable.DiyViews_textColorPressed:
                    mColorTextPressed = array.getColor(index, mColorTextPressed);
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

    @Override
    protected void onDraw(Canvas canvas){
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
        int width = getExactlyTextWidth(mPaintText, mText);
        Paint.FontMetrics fm = mPaintText.getFontMetrics();
        x = (mWidth-width)/2;
        y = mHeight/2+(-fm.ascent-fm.descent)/2;
        canvas.drawText(mText, x, y, mPaintText);

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

    protected void initPaint(){
        mPaintBackround = new Paint();
        mPaintBackround.setAntiAlias(true);
        mPaintBackround.setStyle(Paint.Style.FILL);
        mPaintBackround.setColor(mColorBackground);

        mPaintFrame = new Paint();
        mPaintFrame.setAntiAlias(true);
        mPaintFrame.setColor(mFrameColor);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setStrokeWidth(mFrameWidth);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintText.setColor(mColorText);
        if(mSuggestTextSize)
            mTextSize = getSuggestFontSize(mText);
        mPaintText.setTextSize(mTextSize);
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
        Circle circle = new Circle(0, 0, 0);
        Rect rc = new Rect();
        for(int i=10; i<MAX_FONT_SIZE; i+=10){
            mPaintText.setTextSize(i);
            rc = getTextDisplayRect(mPaintText, text);
            int centerX = (int)((rc.left+rc.right)/2);
            int centerY = (int)((rc.top+rc.bottom)/2);
            int offsetX = mRadius - centerX;
            int offsetY = mRadius - centerY;
            rc.left += offsetX;
            rc.right += offsetX;
            rc.top += offsetY;
            rc.bottom += offsetY;
            circle.setPoint((int)(mRadius*suggestScale),
                    (int)(mRadius*suggestScale),
                    (int)(mRadius*suggestScale));
            if(!circle.contains(rc)){
                for(int j=i-1; j>=i-10; j--){
                    mPaintText.setTextSize(j);
                    rc = getTextDisplayRect(mPaintText, text);
                    centerX = (int)((rc.left+rc.right)/2);
                    centerY = (int)((rc.top+rc.bottom)/2);
                    offsetX = mRadius - centerX;
                    offsetY = mRadius - centerY;
                    rc.left += offsetX;
                    rc.right += offsetX;
                    rc.top += offsetY;
                    rc.bottom += offsetY;
                    circle.setPoint((int)(mRadius*suggestScale),
                            (int)(mRadius*suggestScale),
                            (int)(mRadius*suggestScale));
                    if(circle.contains(rc))
                        return j;
                    else if(!circle.contains(rc) && j==i-10)
                        return 0;
                }
            }
        }

        if(size>MAX_FONT_SIZE)
            size = 0;
        return size;
    }

    /**
     * 获取字符串显示的矩形区域。直接用getTextBounds会得到负数，这里转换成正数。
     * */
    protected Rect getTextDisplayRect(Paint p, String str){
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
     * 精确的计算字符串的显示长度。
     * */
    protected int getExactlyTextWidth(Paint paint, String str) {
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

    /**Get radius of the circle view*/
    public int getRadius(){
        return mRadius;
    }

    /**Get text of view*/
    public String getText(){
        return mText;
    }

    /**Set view's text.Should invalid view.*/
    public void setText(String text){
        mText = text;
        if(mSuggestTextSize)
            mTextSize = getSuggestFontSize(mText);
    }

    /**Set text color.Should invalid view.*/
    public void setTextColor(int color){
        mColorText = color;
        mPaintText.setColor(color);
    }

    /**Set text size.Should invalid view.*/
    public void setTextSize(int size){
        mTextSize = size;
        mPaintText.setTextSize(size);
    }

    /**If true, the text size is automatically calculated by view.*/
    public void setSuggestTextSize(boolean suggeste){
        mSuggestTextSize = suggeste;
    }

    /**Set text color when view pressed.*/
    public void setTextColorPressed(int color){
        mColorTextPressed = color;
    }

    /**Set background color.*/
    public void setBackgroundColor(int color){
        mColorBackground = color;
        mPaintBackround.setColor(color);
    }

    /**Set background color when view pressed.*/
    public void setBackgroundColorPressed(int color){
        mColorBackgroundPressed = color;
    }

    /**Set frame color.*/
    public void setFrameColor(int color){
        mFrameColor = color;
        mPaintFrame.setColor(color);
    }

    /**Set frame width.*/
    public void setFrameWidth(int width){
        mFrameWidth = width;
        mPaintFrame.setStrokeWidth(width);
    }

    /**Set frame color and width.*/
    public void setFrame(int color, int width){
        mFrameColor = color;
        mFrameWidth = width;
        mPaintFrame.setColor(color);
        mPaintFrame.setStrokeWidth(width);
    }

    /**Set extra draw call back.*/
    public void setDrawExtraCallBack(DrawExtraCallBack cb){
        mCallBackDraw = cb;
    }

    /**If false, the pressed colors would not been draw.Default is true.*/
    public void enableClickedDraw(boolean click){
        mClickAble = click;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(!mClickAble)
            return true;
        if(MotionEvent.ACTION_DOWN == (event.getAction()&MotionEvent.ACTION_MASK)){
            mPaintText.setColor(mColorTextPressed);
            mPaintBackround.setColor(mColorBackgroundPressed);
            this.invalidate();
        }
        else if(MotionEvent.ACTION_UP == (event.getAction()&MotionEvent.ACTION_MASK)){
            mPaintText.setColor(mColorText);
            mPaintBackround.setColor(mColorBackground);
            this.invalidate();
        }
        return super.onTouchEvent(event);
    }

    public static interface DrawExtraCallBack{
        public void drawExtra(Canvas canvas);
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
         * 设置圆的属性
         *
         * @param x
         *  圆心x坐标
         *  @param y
         *  圆心y坐标
         *
         *  @param radius
         *  半径
         * */
        public void setPoint(int x, int y, int radius){
            this.x = x;
            this.y =y;
            this.radius = radius;
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
}
