package xmps.androiddebugtool.factorytest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.enjack.diyviews.CircleColorButtonView;
import com.enjack.diyviews.SwitchView;
import com.enjack.diyviews.ValueTextView;
import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.SdCardChecker;

public class SdCardFragment extends BaseTestItemFragment implements SdCardChecker.CheckingStatusListener{
	
	private final String tag = "<SdCardFragment>";
	private View rootview = null;
	private SdCardChecker checker = null;
	private int targetPath = SdCardChecker.TEST_EXTERNAL;
	private ValueTextView tvTotal = null;
	private ValueTextView tvAvailable = null;
	private ValueTextView tvWriteSpeed = null;
	private ValueTextView tvReadSpeed = null;
	private ValueTextView tvResult = null;
	private SwitchView svSwitch = null;
	private TextView tvTargetPath = null;
	private TextView tvInfo = null;
	private CircleColorButtonView btnStart = null;
	private UIHandler handler = new UIHandler();
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_sdcard, container, false);
		rootview = v;
		
		TextView title = (TextView)v.findViewById(R.id.title);
		title.setText("SD卡读写测试");
		tvTotal = (ValueTextView)v.findViewById(R.id.sdcard_total);
		tvAvailable = (ValueTextView)v.findViewById(R.id.sdcard_available);
		tvWriteSpeed = (ValueTextView)v.findViewById(R.id.sdcard_writespeed);
		tvReadSpeed = (ValueTextView)v.findViewById(R.id.sdcard_readspeed);
		tvResult = (ValueTextView)v.findViewById(R.id.sdcard_tvResult);
		tvTargetPath = (TextView)v.findViewById(R.id.sdcard_tvTargetPath);
		tvInfo = (TextView)v.findViewById(R.id.sdcard_tvInfo);
		btnStart = (CircleColorButtonView)v.findViewById(R.id.sdcard_start);
		svSwitch = (SwitchView)v.findViewById(R.id.sdcard_switch);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checker = new SdCardChecker(targetPath);
				checker.setCheckListener(SdCardFragment.this);
				checker.start();
			}
		});
		svSwitch.setStateChangedListener(new SwitchView.SwitchStateChangedListener() {
			
			@Override
			public void onSwitchStateChanged(boolean state) {
				if(state){
					targetPath = SdCardChecker.TEST_EXTERNAL;
					tvTargetPath.setText("外置SD卡");
				}
				else{
					targetPath = SdCardChecker.TEST_INTERNAL;
					tvTargetPath.setText("内置SD卡");
				}
			}
		});
		
		
		return rootview;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	private class UIHandler extends Handler{
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0://onTestStart
				tvInfo.setTextSize(20);
				tvInfo.setText("准备开始... ...");
				svSwitch.setVisibility(View.INVISIBLE);
				tvResult.setValue("测试中...");
				tvResult.postInvalidate();
				Log.i(tag, "sdcard test starting...");
				break;
			case 1://onTestStop
				svSwitch.setVisibility(View.VISIBLE);
				break;
			case 2://onTotalSpace
				tvTotal.setValue((String)msg.obj);
				tvTotal.postInvalidate();
				break;
			case 3://onAvailableSpace
				tvAvailable.setValue((String)msg.obj);
				tvAvailable.postInvalidate();
				break;
			case 4://onWriteSpeed
				tvWriteSpeed.setValue((String)msg.obj);
				tvWriteSpeed.postInvalidate();
				break;
			case 5://onReadSpeed
				tvReadSpeed.setValue((String)msg.obj);
				tvReadSpeed.postInvalidate();
				break;
			case 6://onMessage
				String str = (String)msg.obj;
				tvInfo.setText(str);
				break;
			case 7://onResult
				int result = msg.arg1;
				if(1==result){//success
					tvResult.setValue("成功");
					tvResult.setValueColor(Color.BLUE);
					tvResult.postInvalidate();
				}
				else{
					tvResult.setValue("失败");
					tvResult.setValueColor(Color.RED);
					tvResult.postInvalidate();
				}
				break;
			}
		}
	}
	
	
	@Override
	public void onTestStart() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(0);
	}
	
	@Override
	public void onTestStop() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(1);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void onTotalSpace(long size) {
		// TODO Auto-generated method stub
		float space = size/1024.0f/1024.0f/1024.0f;
		String str = String.format("%.2f", space);
		handler.obtainMessage(2, str).sendToTarget();
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void onAvailableSpace(long size) {
		// TODO Auto-generated method stub
		float space = size/1024.0f/1024.0f/1024.0f;
		String str = String.format("%.2f", space);
		handler.obtainMessage(3, str).sendToTarget();
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void onWriteSpeed(long speed) {
		// TODO Auto-generated method stub
		float a = speed/1014.00f/1024.00f;
		String obj = String.format("%.2f", a);
		handler.obtainMessage(4, obj).sendToTarget();
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public void onReadSpeed(long speed) {
		// TODO Auto-generated method stub
		float a = speed/1014.00f/1024.00f;
		String obj = String.format("%.2f", a);
		handler.obtainMessage(5, obj).sendToTarget();
	}
	
	@Override
	public void onMessage(String msg) {
		// TODO Auto-generated method stub
		handler.obtainMessage(6, msg).sendToTarget();
	}
	
	@Override
	public void onResult(boolean result) {
		// TODO Auto-generated method stub
		if(result)
			handler.obtainMessage(7, 1, 0).sendToTarget();
		else
			handler.obtainMessage(7, 0, 0).sendToTarget();
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "SD读写测试";
		item.board = "通用";
		item.desc = "SD读写测试";
		return item;
	}
}
