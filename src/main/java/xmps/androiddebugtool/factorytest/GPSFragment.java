package xmps.androiddebugtool.factorytest;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.location.GpsSatellite;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enjack.diyviews.ValueTextView;

import java.util.ArrayList;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.GPSInspector;
import xmps.androiddebugtool.factorytest.testmodules.GpsCnView;


/**
 * gps测试
 * 
 * @author enjack
 * */
public class GPSFragment extends BaseTestItemFragment
							implements FragmentChainManager.FragmentChainChangeListener
							,GPSInspector.GPSStatusListener{
	
	private final String tag = "<GPSFragment>";
	private MainAcFmManager mafmm = null;
	private GPSInspector gps = null;
	private ArrayList<GpsSatellite> satellite = new ArrayList<GpsSatellite>();
	private UIHandler handler = new UIHandler();
	
	private ValueTextView gpsSatNum = null;
	private ValueTextView gpsLarge37 = null;
	private ValueTextView gpsLongitude = null;
	private ValueTextView gpsLatitude = null;
	private ValueTextView gpsAltitude = null;
	private ValueTextView gpsSpeed = null;
	private ValueTextView gpsUTC = null;
	private TextView gpsMessage = null;
	private GpsCnView gpsCn = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_gps, container,false);
		//
		if(getActivity() instanceof MainActivity){
			MainActivity activity = (MainActivity)this.getActivity();
			mafmm = activity.getActivityFmManager();
			mafmm.addChainChangeListener(this);
		}
		//
		TextView title = (TextView)v.findViewById(R.id.title);
		title.setText("GPS搜星测试");
		gpsSatNum = (ValueTextView)v.findViewById(R.id.gps_sat_num);
		gpsLarge37 = (ValueTextView)v.findViewById(R.id.gps_large37);
		gpsLongitude = (ValueTextView)v.findViewById(R.id.gps_longitude);
		gpsLatitude = (ValueTextView)v.findViewById(R.id.gps_latitude);
		gpsAltitude = (ValueTextView)v.findViewById(R.id.gps_altitude);
		gpsSpeed = (ValueTextView)v.findViewById(R.id.gps_speed);
		gpsUTC = (ValueTextView)v.findViewById(R.id.gps_utc);
		gpsMessage = (TextView)v.findViewById(R.id.gps_test_info);
		gpsCn = (GpsCnView)v.findViewById(R.id.gps_cn);
		if(Configuration.ORIENTATION_LANDSCAPE == 
				getActivity().getResources().getConfiguration().orientation)
			gpsCn.tellMeOrientation(true);
		else
			gpsCn.tellMeOrientation(false);
		//
		gps = new GPSInspector(this.getActivity());
		gps.setListener(this);
		gps.start();
		return v;
	}
	
	@Override
	public void onDestroy(){
		if(getActivity() instanceof MainActivity)
			mafmm.removeChainChangeListener(this);
		if(null!=gps)
			gps.pause();
		super.onDestroy();
	}
	
	private class UIHandler extends Handler{
		@SuppressLint("DefaultLocale")
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:{//onTestError
				if(1==msg.arg1){//gps off
					//Intent intent = new Intent(Settings.ACTION_SETTINGS);
					//GPSFragment.this.getActivity().startActivityForResult(intent, 0);
					gpsMessage.setText("[错误]GPS未打开!!!");
				}
				else if(2==msg.arg1){//device didn't support gps
					gpsMessage.setText("[错误]该设备没有GPS功能!!!");
				}
				break;
			}
			case 1:{//onTestStart
				gpsMessage.setText("准备开始测试...");
				break;
			}
			case 2:{//onTestStop
				break;
			}
			case 3:{//onSpeed
				String str = (String)msg.obj;
				float speed = Float.parseFloat(str);
				if(speed<1000){
					String strSpeed = String.format("%.2f", speed);
					gpsSpeed.setValue(strSpeed);
					gpsSpeed.setName("速度(m/s)");
					gpsSpeed.postInvalidate();
				}
				else{
					speed = speed*3.6f;
					String strSpeed = String.format("%.2f", speed);
					gpsSpeed.setValue(strSpeed);
					gpsSpeed.setName("速度(Km/h)");
					gpsSpeed.postInvalidate();
				}
				break;
			}
			case 4:{//onLatitude
				String str = (String)msg.obj;
				float latitude = Float.parseFloat(str);
				String strLat = String.format("%.2f", latitude);
				gpsLatitude.setValue(strLat);
				gpsLatitude.postInvalidate();
				break;
			}
			case 5:{//onLongitude
				String str = (String)msg.obj;
				float longitude = Float.parseFloat(str);
				String strLong = String.format("%.2f", longitude);
				gpsLongitude.setValue(strLong);
				gpsLongitude.postInvalidate();
				gpsMessage.setText("[成功]GPS已定位!!!");
				break;
			}
			case 6:{//onAltitude
				String str = (String)msg.obj;
				float altitude = Float.parseFloat(str);
				String strAl = String.format("%.1f", altitude);
				gpsAltitude.setValue(strAl);
				gpsAltitude.postInvalidate();
				break;
			}
			case 7:{//onUTC
				String str = (String)msg.obj;
				long time = Long.parseLong(str);
				long dayTime = (time/1000)%86400;
				long hour = dayTime/3600;
				long min = (dayTime-hour*3600)/60;
				long sec = dayTime-hour*3600-min*60;
				String formatTime = String.format("%02d:%02d:%02d", hour, min, sec);
				gpsUTC.setValue(formatTime);
				gpsUTC.postInvalidate();
				//gpsMessage.setText(formatTime);
				break;
			}
			case 8:{//onSatellite
				ArrayList<Integer> list = new ArrayList<Integer>();
				for(GpsSatellite sat:satellite){
					int snr = (int)sat.getSnr();
					if(snr>0)
						list.add(snr);
				}
				if(list.size()!=0){
					gpsCn.setData(list);
					gpsCn.postInvalidate();
				}
				int cnt = 0;
				for(int i=0; i<list.size(); i++){
					if(list.get(i)>0)
						cnt++;
				}
				gpsSatNum.setValue(cnt);
				gpsSatNum.postInvalidate();
				int cnt37 = 0;
				for(int i=0; i<list.size(); i++){
					if(list.get(i)>=37)
						cnt37++;
				}
				gpsLarge37.setValue(cnt37);
				gpsLarge37.postInvalidate();
				break;
			}
			case 9:{//onTestMessage
				String info = (String)msg.obj;
				gpsMessage.setText(info);
				break;
			}
			}
		}
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.board = "通用";
		item.desc = "测试gps搜星及获取位置信息";
		item.title = "GPS测试";
		return item;
	}

	@Override
	public void onChainChanged(FragmentChainManager chain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCurrentPosChanged(FragmentChainManager chain, int pos) {
		// TODO Auto-generated method stub
		if(mafmm.isShowing(this)){
			Log.i(tag, "Fragment showing. start receive gps information.");
			if(null!=gps)
				gps.start();
		}
		else{
			if(null!=gps)
				gps.pause();
		}
	}

	@Override
	public void onTestError(int errCode) {
		// TODO Auto-generated method stub
		Log.w(tag, "err:"+errCode);
		handler.obtainMessage(0, errCode, 0).sendToTarget();
	}

	@Override
	public void onTestStart() {
		// TODO Auto-generated method stub
		Log.i(tag, "GPS test start.");
		handler.sendEmptyMessage(1);
	}

	@Override
	public void onTestStop() {
		// TODO Auto-generated method stub
		Log.i(tag, "GPS test stop");
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onSpeed(float speed) {
		// TODO Auto-generated method stub
		Log.i(tag, "speed:"+speed);
		String str = String.format("%.2f", speed);
		handler.obtainMessage(3, str).sendToTarget();
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onLatitude(double latitude) {
		// TODO Auto-generated method stub
		Log.i(tag, "Latitude:"+latitude);
		String str = String.format("%.2f", latitude);
		handler.obtainMessage(4, str).sendToTarget();
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onLongitude(double longitude) {
		// TODO Auto-generated method stub
		Log.i(tag, "Longitude:"+longitude);
		String str = String.format("%.2f", longitude);
		handler.obtainMessage(5, str).sendToTarget();
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onAltitude(double altitude) {
		// TODO Auto-generated method stub
		Log.i(tag, "Altitude:"+altitude);
		String str = String.format("%.2f", altitude);
		handler.obtainMessage(6, str).sendToTarget();
	}

	@Override
	public void onUTC(long time) {
		// TODO Auto-generated method stub
		Log.i(tag, "UTC:"+time);
		handler.obtainMessage(7, String.valueOf(time)).sendToTarget();;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSatellite(ArrayList<GpsSatellite> array) {
		// TODO Auto-generated method stub
		satellite.clear();
		int num = array.size();
		ArrayList<GpsSatellite> tmp = (ArrayList<GpsSatellite>)array.clone();
		//sort
		for(int i=0; i<array.size(); i++){
			int max = 0;
			int idx = 0;
			for(int j=0; j<tmp.size(); j++){
				GpsSatellite sat = tmp.get(j);
				int val = (int)sat.getSnr();
				if(val>max){
					idx = j;
					max = val;
				}
			}
			satellite.add(tmp.get(idx));
			tmp.remove(idx);
		}
		//print out all satellite
//		Log.i(tag, "sat num:"+num);
//		for(GpsSatellite s:satellite){
//			Log.i(tag, "snr:"+s.getSnr());
//		}
		
		handler.sendEmptyMessage(8);
	}

	@Override
	public void onTestMessage(String message) {
		// TODO Auto-generated method stub
		handler.obtainMessage(9, message).sendToTarget();
	}
	
}
