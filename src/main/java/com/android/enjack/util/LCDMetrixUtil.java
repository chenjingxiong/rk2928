package com.android.enjack.util;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;


/**
 * @author enjack
 * */
public class LCDMetrixUtil {
	
	public final static int ORIENTATION_LAND = 1;
	public final static int ORIENTATION_PORT = 2;
	
	public static int width(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	public static int height(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	
	public static float density(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}
	
	public static int desityDpi(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.densityDpi;
	}
	
	public static float scaledDensity(Activity activity){
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.scaledDensity;
	}
	
	public static int dip2px(Context context, float dipValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	}
	
	public static int px2dip(Context context, float pxValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale + 0.5f); 
	}
	
	public static int px2sp(Context context, float pxValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
		return (int) (pxValue / fontScale + 0.5f); 
    }
	
	public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    }

	/**
	 * 获取当前activity的方向。注意，activity的方向不一定跟系统的方向是一致的。
	 * */
	public static int getOrientation(Context c){
		if(Configuration.ORIENTATION_LANDSCAPE == 
				c.getResources().getConfiguration().orientation)
			return ORIENTATION_LAND;
		else
			return ORIENTATION_PORT;
	}

	/**设置指定activity的方向。*/
	public static void setOrientation(Activity a, int ori){
		if(ORIENTATION_LAND == ori)
			a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else if(ORIENTATION_PORT == ori)
			a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**锁定当前的方向为activity的方向，当设备转动时候，activity方向不跟随改变。*/
	public static void lockOrientation(Activity a, Context c){
		if(android.os.Build.VERSION.SDK_INT>=18)
			a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		else{
			if(ORIENTATION_LAND == getOrientation(c))
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else
				a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}


	/**锁定activity的方向为指定的方向。不跟随设备转动改变。*/
	public static void lockOrientation(Activity a, int ori){
		if(ORIENTATION_PORT == ori)
			a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else if(ORIENTATION_LAND == ori)
			a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
}
