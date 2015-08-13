package com.android.enjack.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;

import java.lang.reflect.Method;

/**
 * 系统功能开关。
 *
 * @author enjack
 * */
public class SwitchUtil {

	/**Wifi开关</br>
	 * 权限:</br>
	 * android.permission.ACCESS_WIFI_STATE		</br>
	 * android.permission.CHANGE_WIFI_STATE
	 * */
	public static void wifi(Context context, boolean on){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(on && !wifi.isWifiEnabled())
			wifi.setWifiEnabled(true);
		else if(!on && wifi.isWifiEnabled())
			wifi.setWifiEnabled(false);
	}

	/**GPS开关。需要system app才可以调用，否则会抛出SecurityException异常，导致
	 * 程序FC。
	 *
	 * </br>
	 *
	 * 权限:</br>
	 * android.permission.ACCESS_FINE_LOCATION		</br>
	 * android.permission.WRITE_SETTINGS			</br>
	 * android.permission.WRITE_SECURE_SETTINGS
	 * */
	public static void gps(Context context, boolean on){
		boolean status = Secure.isLocationProviderEnabled(context.getContentResolver(),   
                LocationManager.GPS_PROVIDER);
		if(on!=status){
			Secure.setLocationProviderEnabled(context.getContentResolver(),  
	                LocationManager.GPS_PROVIDER, on); 
		}
	}

	/**蓝牙开关</br>
	 * 权限：</br>
	 * android.permission.BLUETOOTH		</br>
	 * android.permission.BLUETOOTH_ADMIN
	 * */
	public static void bt(boolean on){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter!=null){
			if(on && !adapter.isEnabled())
				adapter.enable();
			else if(!on && adapter.isEnabled())
				adapter.disable();
		}
	}

	/**蜂窝数据流量开关</br>
	 * 权限：</br>
	 * android.permission.ACCESS_NETWORK_STATE	</br>
	 * android.permission.CHANGE_NETWORK_STATE
	 * */
	public static void cellular(Context context, boolean on){
		ConnectivityManager connectivityManager = null;
		Class<?> connectivityManagerClz = null;
		try {
            connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
            connectivityManagerClz = connectivityManager.getClass();
            Method method = connectivityManagerClz.getMethod(
                    "setMobileDataEnabled", new Class[] { boolean.class });
            method.invoke(connectivityManager, on);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**屏幕背光自动调节开关</br>
	 * 权限:</br>
	 * android.permission.WRITE_SETTINGS
	 * */
	public static void autoBL(Context context, boolean on){
		
		if(on){
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,  
	                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  
		}
		else{
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,  
	                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
	}

	/**锁屏。需要system app才能调用。否则会抛出SecurityException异常。
	 *
	 * </br>
	 * 权限：</br>
	 * android.permission.USES_POLICY_FORCE_LOCK		</br>
	 * android.permission.DEVICE_POWER
	 * */
	public static void lockScreen(Context context){
		//PowerManager power = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		//power.goToSleep(SystemClock.uptimeMillis());
	}

	/**自动旋转屏幕开关</br>
	 * 权限：</br>
	 * android.permission.WRITE_SETTINGS
	 * */
	public static void rotation(Context context, boolean on){
		int status = 0;  
        try  
        {  
            status = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        }
        catch (SettingNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(on && status==0){
        	Uri uri = Settings.System.getUriFor("accelerometer_rotation");
        	Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 1);
        	context.getContentResolver().notifyChange(uri, null);
        }
        else if(!on && status==1){
        	Uri uri = Settings.System.getUriFor("accelerometer_rotation");
        	Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", 0);
        	context.getContentResolver().notifyChange(uri, null);
        }
	}

	/**
	 * 闪光灯开关。在某些机型上可能需要一个SurfaceView用来开启预览才能使用闪光。</br>
	 * 权限：</br>
	 * android.permission.CAMERA
	 *
	 * @param camera
	 * 	Camera instance return by Camera.open()
	 * */
	public static void flash(Camera camera, boolean on){
		Parameters parameters;
		if(null == camera)
			return;
		
		if(on){
			parameters = camera.getParameters();
			parameters.setFlashMode("torch");
			camera.setParameters(parameters);
		}
		else{
			parameters = camera.getParameters();
			parameters.setFlashMode("off");
			camera.setParameters(parameters);
		}
	}

	/**静音功能。
	 *
	 * @param context
	 * 	Context
	 *
	 * @param mode
	 * 1表示静音，2表示震动，3表示正常模式
	 * */
	public static void mute(Context context, int mode){
		AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		switch(mode){
		case 1:
			audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case 2:
			audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case 3:
			audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		}
	}
}
