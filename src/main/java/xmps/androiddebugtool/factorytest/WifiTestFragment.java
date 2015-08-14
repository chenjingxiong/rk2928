package xmps.androiddebugtool.factorytest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.enjack.util.SwitchUtil;
import com.android.enjack.util.WifiAccess;
import com.android.enjack.util.WifiAutoConnectManager;
import com.enjack.diyviews.CircleButtonView;

import java.util.List;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.WifiScanListViewAdapter;


public class WifiTestFragment extends BaseTestItemFragment implements OnItemClickListener{
	
	private final String tag = "<WifiTestFragment>";
	private WifiAccess mWifiAccess = null;//new WifiAccess(this.getActivity());
	private List<ScanResult> mWifiScanResultList = null;
	private WifiScanListViewAdapter mAdapter = null;//new WifiScanListViewAdapter(context);
	private ListView mWifiListView = null;
	private WifiListener mWifiReceiver = null;
	private UIHandler handler = new UIHandler();
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_wifi, container, false);
		TextView title = (TextView)v.findViewById(R.id.title);
		SwitchUtil.wifi(getActivity(), true);
		registeWifiReceiver();
		title.setText("Wifi测试");
		mWifiAccess = new WifiAccess(this.getActivity());
		mAdapter = new WifiScanListViewAdapter(this.getActivity());
		//mWifiAccess.startScan();
		//mWifiScanResultList = mWifiAccess.getWifiList();
		mWifiListView = (ListView)v.findViewById(R.id.wifi_list);
		//mAdapter.refreshData(mWifiScanResultList);
		//mWifiListView.setAdapter(mAdapter);
		mWifiListView.setOnItemClickListener(this);
		CircleButtonView btn = (CircleButtonView)v.findViewById(R.id.wifi_refresh);
		btn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWifiAccess.startScan();
				mWifiScanResultList = mWifiAccess.getWifiList();
				mAdapter.refreshData(mWifiScanResultList);
				mWifiListView.setAdapter(mAdapter);
			}
			
		});
		return v;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null!=mWifiReceiver)
			this.getActivity().unregisterReceiver(mWifiReceiver);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView tv = (TextView)view.findViewById(R.id.idWifiSSID);
		String str = tv.getText().toString();
		
		popDialog(str);
	}
	
	
	private EditText input;
	private String wifiKey;
	private void popDialog(String ssid){
		input = new EditText(this.getActivity());
		wifiKey = ssid;
		new AlertDialog.Builder(this.getActivity())
		.setTitle("输入密码")
		.setIcon(android.R.drawable.ic_dialog_info) 
		.setView(input)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String str = input.getText().toString();
				Log.i(tag, "wifi key:"+str);
				WifiAutoConnectManager wacm = new WifiAutoConnectManager(WifiTestFragment.this.getActivity());
				wacm.start(wifiKey, str);
			}})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		})
		.show();
	}
	
	
	private void registeWifiReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		if(null==mWifiReceiver)
			mWifiReceiver = new WifiListener();
		this.getActivity().registerReceiver(mWifiReceiver, filter);
	}
	
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			WifiAccess wa = new WifiAccess(WifiTestFragment.this.getActivity());
			String ssid = wa.getSSID();
			View v = WifiTestFragment.this.getView();
			TextView title = (TextView)v.findViewById(R.id.title);
			title.setText(""+ssid+"已连接");
			title.setTextColor(Color.RED);
			title.setTextSize(20);
		}
	}
	
	
	private class WifiListener extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//��������״̬
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);  
				switch (wifiState) {  
	            case WifiManager.WIFI_STATE_DISABLED:
	            	Log.i(tag, "wifi disabled.");
	                break;  
	            case WifiManager.WIFI_STATE_DISABLING:  
	            	Log.i(tag, "wifi disabling...");
	                break;  
	            case WifiManager.WIFI_STATE_ENABLED:
	            	Log.i(tag, "wifi enabled.");
	            	break;
	            case WifiManager.WIFI_STATE_ENABLING:
	            	Log.i(tag, "wifi enabling...");
	            	break;
	            case WifiManager.WIFI_STATE_UNKNOWN:
	            	Log.i(tag, "wifi state unknown");
	            	break;
				}
			}
			
			 if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){//��������״̬
				 Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				 if (null != parcelableExtra){
					 NetworkInfo networkInfo = (NetworkInfo) parcelableExtra; 
					 State state = networkInfo.getState();
					 if(state == State.CONNECTED){
						 WifiAccess wa = new WifiAccess(WifiTestFragment.this.getActivity());
						 String ssid = wa.getSSID();
						 String str = "已连接:"+ssid;
						 Log.i(tag, str);
						 handler.sendEmptyMessage(0);
						 Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
					 }
				 }
			 }
		}}


	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "Wifi测试";
		item.board = "通用";
		item.desc = "Wifi连接测试";
		return item;
	}
}
