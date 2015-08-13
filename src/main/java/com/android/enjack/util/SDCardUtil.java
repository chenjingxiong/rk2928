package com.android.enjack.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;



/**
 * SD卡工具。
 * @author enjack
 * */
public class SDCardUtil {


	/**
	 * 判断外部sd卡是否存在。
	 *
	 * @return
	 * true表示存在。
	 * */
	public static boolean isExist(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}


	/**获取外部sd卡的路径*/
	public static String getPath(){
		return Environment.getExternalStorageDirectory().toString();
	}


	/**
	 * 得到真实的外部SD卡的路径。有些机型把flash设置为sd卡，同时也支持外置sd。该方法获取的是真实的外部sd路径。
	 * */
	public static String getRealSDCardPath() {
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();
		BufferedInputStream in=null;
		BufferedReader inBr=null;
		try {
			Process p = run.exec(cmd);
			in = new BufferedInputStream(p.getInputStream());
			inBr = new BufferedReader(new InputStreamReader(in));


			String lineStr;
			String tmp;
			while ((lineStr = inBr.readLine()) != null) {
				//Log.i("CommonUtil:getSDCardPath", lineStr);
				tmp = lineStr.toLowerCase(Locale.getDefault());
				if (tmp.contains("sdcard")&& tmp.contains(".android_secure")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String result = strArray[1].replace("/.android_secure","");
						return result;
					}
				}
				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					Log.e("CommonUtil:getSDCardPath", "命令执行失败!");
				}
			}
		} catch (Exception e) {
			Log.e("CommonUtil:getSDCardPath", e.toString());
			//return Environment.getExternalStorageDirectory().getPath();
		}finally{
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(inBr!=null){
					inBr.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Environment.getExternalStorageDirectory().getPath();
	}


	/**
	 * 获取指定路径的分区的大小。单位字节。
	 * */
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public static long getTotalSize(String path){
		long size = 0;
		StatFs stat = new StatFs(path);
		if(android.os.Build.VERSION.SDK_INT<18)
			size = stat.getBlockSize() * ((long) stat.getBlockCount());
		else
			size = stat.getBlockSizeLong() * ((long) stat.getBlockCountLong());
		return size;
	}


	/**获取可用空间。单位字节。*/
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public static long getAvailableSize(String path){
		long size = 0;
		StatFs stat = new StatFs(path);
		if(android.os.Build.VERSION.SDK_INT>=18)
			size = stat.getAvailableBytes();
		else
			size = stat.getBlockSize() * ((long) stat.getAvailableBlocks());
		return size;
	}
}

