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

import com.enjack.diyviews.CircleButtonView;
import com.enjack.diyviews.ValueTextView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.UsbStorageChecker;


@SuppressLint("DefaultLocale")
public class UsbStorageFragment extends BaseTestItemFragment implements UsbStorageChecker.CheckingStatusListener{

	private final String tag = "<UsbStorageFragment>";
	private UsbStorageChecker checker = null;
	private ValueTextView tvTotal = null;
	private ValueTextView tvAvailable = null;
	private ValueTextView tvWriteSpeed = null;
	private ValueTextView tvReadSpeed = null;
	private ValueTextView tvResult = null;
	private TextView tvInfo = null;
	private CircleButtonView btnStart = null;
	private UIHandler handler = new UIHandler();
	
	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title="U盘读写测试";
		item.board="rk2928-pad(np10)";
		item.desc="测试USB座子";
		return item;
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		View v = null;
        v = inflater.inflate(R.layout.fm_usbstorage, container, false);
        TextView title = (TextView)v.findViewById(R.id.title);
		title.setText("U盘读写测试");
		
		tvTotal = (ValueTextView)v.findViewById(R.id.usbstorage_total);
		tvAvailable = (ValueTextView)v.findViewById(R.id.usbstorage_available);
		tvWriteSpeed = (ValueTextView)v.findViewById(R.id.usbstorage_writespeed);
		tvReadSpeed = (ValueTextView)v.findViewById(R.id.usbstorage_readspeed);
		tvResult = (ValueTextView)v.findViewById(R.id.usbstorage_tvResult);
		tvInfo = (TextView)v.findViewById(R.id.usbstorage_tvInfo);
		btnStart = (CircleButtonView)v.findViewById(R.id.usbstorage_start);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checker = new UsbStorageChecker();
				checker.setCheckListener(UsbStorageFragment.this);
				checker.start();
			}
		});
        return v;
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
				tvResult.setValue("测试中");
				tvResult.postInvalidate();
				Log.i(tag, "sdcard test starting...");
				break;
			case 1://onTestStop
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

}
