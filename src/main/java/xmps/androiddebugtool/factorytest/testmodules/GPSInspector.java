package xmps.androiddebugtool.factorytest.testmodules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * 获取位置以及GPS卫星信息
 *
 * @author enjack
 * */
public class GPSInspector{
	private final String tag = "GPSInspector";
	private Context mContext = null;
	private LocationListener mLocationListener = null;//位置信息
	private GpsStatus.Listener mGpsStatelliteListener = null;//卫星信息
	private GPSStatusListener mListener = null;// 回调
	private boolean mCallListener = false;

	public static interface GPSStatusListener{
		public void onTestError(int errCode);
		public void onTestStart();
		public void onTestStop();
		public void onSpeed(float speed);
		public void onLatitude(double latitude);//纬度
		public void onLongitude(double longitude);//经度
		public void onAltitude(double altitude);//海拔
		public void onUTC(long time);
		public void onSatellite(ArrayList<GpsSatellite> array);
		public void onTestMessage(String message);
	}

	public GPSInspector(Context context){
		this.mContext = context;
	}

	public void setListener(GPSStatusListener listener){
		mListener = listener;
	}

	public void start(){
		if(null == mListener){
			Log.e(tag, "listener didn't setted.");
			return;
		}
		mCallListener = true;
		mListener.onTestStart();
		if(!hasGpsProvider()){
			Log.e(tag, "Device has not GPS!!!");
			mListener.onTestError(2);
			return;
		}
		if(!isGpsEnable()){
			mListener.onTestError(1);
			mListener.onTestMessage("GPS未打开");
		}
		else
			mListener.onTestMessage("GPS已经打开");
		LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		if(null==location)
			Log.w(tag, "getLastKnownLocation return null");
		else
			Log.i(tag, "getLastKnownLocation return success.");

		//位置信息回调
		mLocationListener = new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if(mCallListener){
					mListener.onSpeed(location.getSpeed());
					mListener.onLatitude(location.getLatitude());
					mListener.onLongitude(location.getLongitude());
					mListener.onUTC(location.getTime());
					mListener.onAltitude(location.getAltitude());
				}
			}

			@Override
			public void onStatusChanged(String provider, int status,
										Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				Log.i(tag, "provider enable.");
				if(mCallListener)
					mListener.onTestMessage("GPS已经打开");
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Log.w(tag, "provider disable.");
				if(mCallListener){
					mListener.onTestError(1);
					mListener.onTestMessage("GPS未打开");
				}
			}};


		//卫星信息回调
		mGpsStatelliteListener = new GpsStatus.Listener(){

			@Override
			public void onGpsStatusChanged(int event) {
				// TODO Auto-generated method stub
				ArrayList<GpsSatellite> array = new ArrayList<GpsSatellite>();
				LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
				GpsStatus status = lm.getGpsStatus(null);
				int num = status.getMaxSatellites();
				Iterator<GpsSatellite> it = status.getSatellites().iterator();
				int count = 0;
				while (it.hasNext() && count <= num){
					array.add(it.next());
					count++;
				}
				if(mCallListener)
					mListener.onSatellite(array);
			}

		};
		locationManager.requestLocationUpdates(provider, 1000, 0.1f, mLocationListener);
		locationManager.addGpsStatusListener(mGpsStatelliteListener);
	}


	public void pause(){
		mCallListener = false;
	}

	/***gps是否打开*/
	public boolean isGpsEnable(){
		LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**是否有GPS功能*/
	public boolean hasGpsProvider(){
		LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		List<String> list;
		list = locationManager.getAllProviders();
		for(String tmp:list){
			if(tmp.equals(LocationManager.GPS_PROVIDER))
				return true;
		}

		return false;
	}
}
