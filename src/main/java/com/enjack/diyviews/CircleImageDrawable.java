package com.enjack.diyviews;

import android.graphics.Bitmap;  
import android.graphics.BitmapShader;  
import android.graphics.Canvas;  
import android.graphics.ColorFilter;  
import android.graphics.Paint;  
import android.graphics.PixelFormat;  
import android.graphics.Shader.TileMode;  
import android.graphics.drawable.Drawable;


/**
 * 圆形drawable。
 *
 * @author zhy.http://blog.csdn.net/lmj623565791/article/details/43752383
 * 	enjack
 * */
public class CircleImageDrawable extends Drawable  
{  
  
    private Paint mPaint;  
    private int mWidth;  
    private Bitmap mBitmap ;   
  
    public CircleImageDrawable(Bitmap bitmap)  
    {  
        mBitmap = bitmap ;   
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,  
                TileMode.CLAMP);  
        mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
        mPaint.setShader(bitmapShader);  
        mWidth = Math.min(mBitmap.getWidth(), mBitmap.getHeight());  
    }  
    
    
    public CircleImageDrawable(int color, int radius){
    	mWidth = radius*2;
    	mPaint = new Paint();  
        mPaint.setAntiAlias(true); 
        mPaint.setColor(color);
    } 
    
  
    @Override  
    public void draw(Canvas canvas)  
    {  
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);  
    }  
  
    @Override  
    public int getIntrinsicWidth()  
    {  
        return mWidth;  
    }  
  
    @Override  
    public int getIntrinsicHeight()  
    {  
        return mWidth;  
    }  
  
    @Override  
    public void setAlpha(int alpha)  
    {  
        mPaint.setAlpha(alpha);  
    }  
  
    @Override  
    public void setColorFilter(ColorFilter cf)  
    {  
        mPaint.setColorFilter(cf);  
    }  
  
    @Override  
    public int getOpacity()  
    {  
        return PixelFormat.TRANSLUCENT;  
    }  
  
}  
