package xmps.androiddebugtool.factorytest.testmodules;

import android.util.Log;

import com.android.enjack.util.LineReader;
import com.android.enjack.util.SDCardUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SdCardChecker {
	private final String tag = "<SdCardChecker>";
	private CheckingStatusListener mListener = null;
	private boolean mTargetExternal = true;

	private final String RK2928_EXTERNAL_SD_PATH = "/mnt/external_sd";
	public final static int TEST_EXTERNAL = 0;
	public final static int TEST_INTERNAL = 1;

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

	public SdCardChecker(int target){
		if(TEST_INTERNAL == target)
			mTargetExternal = false;
		else
			mTargetExternal = true;
	}


	/**开始检查测试sd卡*/
	public void start(){
		if(null==mListener){
			Log.e(tag, "Listener didn't setted!!!");
			return;
		}

		new Thread(new SdCardRWThread()).start();
	}

	public void setCheckListener(CheckingStatusListener listener){
		this.mListener = listener;
	}

	private class SdCardRWThread implements Runnable{
		private boolean result = true;
		private final String tmpFile = "sdtmp.tmp";
		private final String strData = "01234567890abcdefghijklmnopqrstuvwxyz~!@#$%%ABCD\n";
		private final int lineCnt = 10000;//文件写入的行数
		private final int tryCnt = 2;//重复写同一个文件次数 
		private String targetFile = null;

		public SdCardRWThread(){
			super();
			if(mTargetExternal)
				targetFile = RK2928_EXTERNAL_SD_PATH+File.separator+tmpFile;
			else
				targetFile = SDCardUtil.getPath()+File.separator+tmpFile;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(null==mListener)
				return;
			mListener.onTestStart();
			delayms(300);//wait for ui updated.
			mainLoop();
			mListener.onTestStop();
		}

		private void mainLoop(){
			//
			if(isStorageExist()){
				Log.i(tag, "Storage Card found!");
				String storagePath = "";
				if(mTargetExternal)
					storagePath = RK2928_EXTERNAL_SD_PATH;
				else
					storagePath = SDCardUtil.getPath();
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
				Log.e(tag, "Storage Card not found.");
				mListener.onMessage("无法读写或SD卡不存在");
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
			int cnt = 0;
			int length = strData.length();
			try {
				FileWriter fw = new FileWriter(targetFile);
				if(null!=fw){
					for(int i=0; i<lineCnt; i++){
						fw.write(strData);
						fw.flush();
						cnt+=length;
					}
					fw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				cnt = 0;
				e.printStackTrace();
			}
			if(0==cnt)
				return 0;
			else
				return 1000*cnt/(System.currentTimeMillis()-time);

		}

		/**@return read speed.*/
		private long readOnce(){
			int cnt = 0;
			int len = 0;
			long time1 = System.currentTimeMillis();
			long time2 = 0;
			char[] ch = new char[1024];
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
			LineReader lr = new LineReader(new File(targetFile));
			String str = "";
			for(int i=0; i<5; i++){
				str = lr.getAt(i);
				if(!str.equals(strData.substring(0, strData.length()-1)))
					return false;
			}
			return true;
		}

		private boolean isStorageExist(){
			if(mTargetExternal){
				File file = new File(RK2928_EXTERNAL_SD_PATH);
				if(file.exists()){
					File f = new File(RK2928_EXTERNAL_SD_PATH+File.separator+tmpFile);
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
			else{
				return SDCardUtil.isExist();
			}

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
