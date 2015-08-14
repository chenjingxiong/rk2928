package xmps.androiddebugtool.factorytest.testmodules;


import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.android.enjack.util.SwitchUtil;

public class BlueToothInspector {

	private final String tag = "<BlueToothInspector>";
	private BlueToothTestListener listener = null;

	public static interface BlueToothTestListener{
		public void onTestStart();
		public void onTestStop();
		public void onTestResult(boolean result);
		public void onTestMessage(String msg);
	}

	public BlueToothInspector(){

	}

	public void setListener(BlueToothTestListener listener){
		this.listener = listener;
	}

	/**start BT test.*/
	public void start(){
		Log.i(tag, "start BT test thread...");
		new Thread(new BlueToothTestRunnable()).start();
	}

	private class BlueToothTestRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(null==listener)
				return;
			listener.onTestStart();
			mainLoop();
			listener.onTestStop();
		}

		private void mainLoop(){
			BluetoothAdapter adapter =BluetoothAdapter.getDefaultAdapter();
			if(null==adapter){
				Log.e(tag, "Device doesn't support BlueTooth.");
				listener.onTestMessage("设备不支持蓝牙");
				listener.onTestResult(false);
			}
			else{
				if(adapter.isEnabled()){
					Log.i(tag, "BT is turned on and ready for us.");
					listener.onTestMessage("蓝牙已经打开");
					listener.onTestResult(true);
				}
				else{
					Log.i(tag, "BT off.Try turn it on.");
					listener.onTestMessage("尝试打开蓝牙...");
					SwitchUtil.bt(true);
					if(delayDetect(adapter)){
						Log.i(tag, "turn on BT success!");
						listener.onTestMessage("成功");
						listener.onTestResult(true);
					}
					else{
						Log.e(tag, "BT can't turn on.");
						listener.onTestMessage("失败");
						listener.onTestResult(false);
					}
				}
			}
		}

		/**检测bt是否打开*/
		private boolean delayDetect(BluetoothAdapter adapter){
			int cnt = 50;
			while(cnt-->=0){
				delayms(100);
				if(adapter.isEnabled())
					return true;
			}
			return false;
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
