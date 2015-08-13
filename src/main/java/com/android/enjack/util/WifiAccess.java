package com.android.enjack.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiAccess {
	
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	private List<WifiConfiguration> mWifiConfigurations;
	
	
	//
	public WifiAccess(Context context){
		
		mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo=mWifiManager.getConnectionInfo(); 
	}
	
	//
	public void openWifi(){
		
		if(!mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(true);  
        }
	}
	
	//
	public void closeWifi(){
		
		if(!mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(false);  
        } 
	}
	
	//
	public int checkState() {    
        return mWifiManager.getWifiState();    
    }
	
	//
	public boolean isEnable(){
		return mWifiManager.isWifiEnabled();
	}

    //得到配置好的网络
	public List<WifiConfiguration> getConfiguration(){  
        return mWifiConfigurations;  
    }


    //指定配置好的网络进行连接
    public void connetionConfiguration(int index){  
        if(index>mWifiConfigurations.size()){  
            return ;  
        }
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);  
    }	
    
    //
    public void startScan(){  
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList=mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();  
    }


    //得到网络列表
    public List<ScanResult> getWifiList(){  
        return mWifiList;  
    }


    //查看扫描结果
    public StringBuffer lookUpScan(){  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<mWifiList.size();i++){  
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");  
        }  
        return sb;    
    }
    
    
    public String getMacAddress(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();  
    }
    
    
    public String getBSSID(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
    }
    
    public String getSSID(){
    	return (mWifiInfo==null)?"NULL":mWifiInfo.getSSID();
    }
    
    public int getIpAddress(){  
        return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();  
    }
    
    public String intToIp(int i)  {
    	return (i&0xff)+"."+((i>>8)&0xff)+"."+((i>>16)&0xff)+"."+((i>>24)&0xff);
    	}

    //得到连接的ID
    public int getNetWordId(){  
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();  
    }

    //得到wifiInfo的所有信息
    public String getWifiInfo(){  
        return (mWifiInfo==null)?"NULL":mWifiInfo.toString();  
    }

    //添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration){  
        int wcgId=mWifiManager.addNetwork(configuration);  
        mWifiManager.enableNetwork(wcgId, true);  
    }

    //断开指定ID的网络
    public void disConnectionWifi(int netId){  
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }

    /**
     * 得到wifi信号强度
     * */
    public int getIntensity(int range){
    	return WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), range);
    }

}//class end
