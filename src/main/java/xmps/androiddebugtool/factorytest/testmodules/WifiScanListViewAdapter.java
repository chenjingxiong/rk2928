package xmps.androiddebugtool.factorytest.testmodules;

import java.util.ArrayList;
import java.util.List;






import xmps.androiddebugtool.factorytest.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiScanListViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	List<ScanResult> mResult = null;

	public WifiScanListViewAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}


	/*
	 * 
	 * */
	public void refreshData(List<ScanResult> list){
		mResult = list;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mResult.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null==convertView){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.wifi_scan_list_item, null);
			holder.tvSSID = (TextView)convertView.findViewById(R.id.idWifiSSID);
			holder.tvSecurity = (TextView)convertView.findViewById(R.id.idWifiSecurity);
			holder.imgIntensity = (ImageView)convertView.findViewById(R.id.idWifiSignalIntensity);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder)convertView.getTag();

		//
		String str = mResult.get(position).toString();
		holder.tvSSID.setText(mResult.get(position).SSID);
		holder.tvSecurity.setText(mResult.get(position).capabilities);
		int level = getIntensity(str);
		int resid = R.drawable.wifi_capa1;
		switch(level){
			case 4:resid = R.drawable.wifi_capa4;break;
			case 3:resid = R.drawable.wifi_capa3;break;
			case 2:resid = R.drawable.wifi_capa2;break;
			case 1:resid = R.drawable.wifi_capa1;break;
			case 0:resid = R.drawable.wifi_capa0;break;
			default:resid = R.drawable.wifi_capa0;
				break;
		}
		holder.imgIntensity.setBackgroundResource(resid);
		return convertView;
	}


	@SuppressWarnings("unused")
	private String getSSIDString(String str){
		ArrayList<String> list = getStringBlocks(str, ',');
		String ssid = "";
		String tag = "SSID:";
		int prefixLen = tag.length();
		for(int i=0; i<list.size(); i++){
			ssid = list.get(i);
			if(ssid.contains(tag))
				return ssid.substring(prefixLen+1);
		}
		return ssid;
	}

	@SuppressWarnings("unused")
	private String getSecrityString(String str){
		ArrayList<String> list = getStringBlocks(str, ',');
		String capabilities = "";
		String tag = "capabilities:";
		int prefixLen = tag.length();
		for(int i=0; i<list.size(); i++){
			capabilities = list.get(i);
			if(capabilities.contains(tag))
				return capabilities.substring(prefixLen+1);
		}
		return capabilities;
	}

	private int getIntensity(String str){
		ArrayList<String> list = getStringBlocks(str, ',');
		String intensity = "";
		String tag = "level:";
		int prefixLen = tag.length();
		for(int i=0; i<list.size(); i++){
			intensity = list.get(i);
			if(intensity.contains(tag)){
				intensity = intensity.substring(prefixLen+1);
				break;
			}
		}
		int level = getNumberFromString(intensity);
		if(intensity.contains("-"))//负数
			level = 0-level;
		//Log.i("","intensity:"+intensity);
		if(level>=-50 && level<=0)
			return 4;
		else if(level>=-70 && level<-50)
			return 3;
		else if(level>=-100 && level<-70)
			return 2;
		else if(level>=200 && level<-100)
			return 1;
		else return 0;
	}

	/**
	 * 从字符串中提取数字
	 * **/
	private int getNumberFromString(String str){
		String data = "";
		for(int i=0; i<str.length(); i++){
			for(int j=0; j<10; j++)
				if(String.valueOf(str.charAt(i)).equals(String.valueOf(j)))
					data+=str.charAt(i);
		}

		if(0==data.length())
			return 0;
		else
			return Integer.parseInt(data);
	}

	/**
	 * 把一个字符串分段，根据传入的分段符
	 * @param str
	 * 		要提取段的字符串
	 * @param	ch
	 * 		分隔符
	 * */
	private ArrayList<String> getStringBlocks(String str, char ch){
		ArrayList<String> list = new ArrayList<String>();
		int start = 0;
		if(str.contains(String.valueOf(ch))){
			for(int i=0; i<str.length(); i++){
				if(str.charAt(i)==ch){
					list.add(str.substring(start, i));
					start = i+1;
				}
				if(i==str.length()-1)
					list.add(str.substring(start,i+1));
			}
		}
		else
			list.add(str);
		return list;
	}

	public class ViewHolder{
		TextView tvSSID;
		TextView tvSecurity;
		ImageView imgIntensity;
	}

}
