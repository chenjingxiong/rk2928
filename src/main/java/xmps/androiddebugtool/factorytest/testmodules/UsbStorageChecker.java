package xmps.androiddebugtool.factorytest.testmodules;

import android.util.Log;

import com.android.enjack.util.SDCardUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class UsbStorageChecker {
	private final String tag = "<UsbStorageChecker>";
	private CheckingStatusListener mListener = null;

	private final String RK2928_USB_STORAGE_PATH = "/mnt/usb_storage";

	public static interface CheckingStatusListener{

		public void onTestStart();
		public void onTestStop();
		public void onTotalSpace(long size);
		public void onAvailableSpace(long size);
		public void onWriteSpeed(long speed);
		public void onReadSpeed(long speed);
		public void onMessage(String msg);
		public void onResult(boolean result);
	}

	public UsbStorageChecker(){
	}


	/**开始检查测试sd卡*/
	public void start(){
		if(null==mListener){
			Log.e(tag, "Listener didn't setted!!!");
			return;
		}

		new Thread(new UsbStorageRWThread()).start();
	}

	public void setCheckListener(CheckingStatusListener listener){
		this.mListener = listener;
	}

	private class UsbStorageRWThread implements Runnable{
		private boolean result = true;
		private final String tmpFile = "usbtmp.tmp";
		private final int chCnt = 900000;
		private char[] chData = new char[chCnt];
		private final int tryCnt = 2;//重复写同一个文件次数 
		private String targetFile = RK2928_USB_STORAGE_PATH+File.separator+tmpFile;

		public UsbStorageRWThread(){
			super();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(null==mListener)
				return;
			Arrays.fill(chData, (char)0x89);
			mListener.onTestStart();
			delayms(300);//wait for ui updated.
			mainLoop();
			mListener.onTestStop();
		}

		private void mainLoop(){
			//
			if(isStorageExist()){
				Log.i(tag, "USB Storage found!");
				String storagePath = RK2928_USB_STORAGE_PATH;
				mListener.onTotalSpace(SDCardUtil.getTotalSize(storagePath));
				mListener.onAvailableSpace((SDCardUtil.getAvailableSize(storagePath)));
				mListener.onMessage("发现SD卡");
				if(createTmpFile()){
					for(int i=0; i<tryCnt; i++){
						createTmpFile();
						mListener.onMessage("正在写入...");
						long cost = writeOnce();
						mListener.onWriteSpeed(cost);
						mListener.onMessage("正在读取验证...");
						cost  = readOnce();
						mListener.onReadSpeed(cost);
						if(confirm())
							mListener.onMessage("验证成功!");
						else{
							mListener.onMessage("验证失败");
							result = false;
							removeTmpFile();
							break;
						}
						removeTmpFile();
						delayms(300);
					}
				}
				else{
					Log.e(tag, "can't not create file");
					mListener.onMessage("无法创建文件");
					result = false;
				}
			}
			else{
				Log.e(tag, "USB Storage not found.");
				mListener.onMessage("无法读写或U盘不存在");
				result = false;
			}

			//
			mListener.onResult(result);
		}

		/**
		 * @return	write speed(byte/s)
		 * */
		private long writeOnce(){
			long time  = System.currentTimeMillis();
			try {
				FileWriter fw = new FileWriter(targetFile);
				fw.write(chData);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1000*chCnt/(System.currentTimeMillis()-time);
		}

		/**@return read speed.*/
		private long readOnce(){
			int cnt = 0;
			int len = 0;
			long time1 = System.currentTimeMillis();
			long time2 = 0;
			char[] ch = new char[4096];
			try {
				FileReader fr = new FileReader(targetFile);
				while(-1!=(len=fr.read(ch))){
					cnt+=len;
					//System.out.print(ch);
				}
				time2 = System.currentTimeMillis();
				fr.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}

			long cost = time2-time1;

			return 1000*cnt/cost;
		}

		private boolean confirm(){
			int ch;
			int cnt = 0;
			try {
				FileReader fr = new FileReader(targetFile);
				while((ch=fr.read())!=-1){
					if(ch!=0x89){
						fr.close();
						return false;
					}
					if(++cnt>1000)//just comfirm 1000 bytes
						break;
				}
				fr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			return true;
		}

		private boolean isStorageExist(){
			File file = new File(RK2928_USB_STORAGE_PATH);
			if(file.exists()){
				File f = new File(RK2928_USB_STORAGE_PATH+File.separator+tmpFile);
				if(!f.exists()){
					try {
						boolean ret = f.createNewFile();
						return ret;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				else
					return true;
			}
			else
				return false;

		}

		private boolean createTmpFile(){
			File file = new File(targetFile);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}

			if(file.exists())
				return true;
			else
				return false;
		}

		private void removeTmpFile(){
			File file = new File(targetFile);
			if(file.exists())
				file.delete();
		}

		private void delayms(int ms){
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
