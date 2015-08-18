package xmps.androiddebugtool.factorytest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enjack.diyviews.CircleColorButtonView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.BlueToothInspector;


public class BlueToothFragment extends BaseTestItemFragment implements BlueToothInspector.BlueToothTestListener{
	
	private CircleColorButtonView btnStart = null;
	private TextView tvMessage = null;
	private BlueToothInspector inspector = new BlueToothInspector();
	private UIHandler handler = new UIHandler();

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "蓝牙测试";
		item.board = "通用";
		item.desc = "蓝牙连接测试";
		return item;
	}

	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		View v = null;
        v = inflater.inflate(R.layout.fm_bt, container, false); 
        TextView title = (TextView)v.findViewById(R.id.title);
		title.setText("蓝牙测试");
		inspector.setListener(this);
		btnStart = (CircleColorButtonView)v.findViewById(R.id.bluetooth_start);
		tvMessage = (TextView)v.findViewById(R.id.bluetooth_message);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				inspector.start();
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
				btnStart.setVisibility(View.INVISIBLE);
				break;
			case 1://onTestStop
				btnStart.setVisibility(View.VISIBLE);
				break;
			case 2://onTestResult
				if(1==msg.arg1)
					tvMessage.setText("成功");
				else
					tvMessage.setText("失败");
				break;
			case 3:{//onTestMessage
				String str = (String)msg.obj;
				tvMessage.setText(str);
			}
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


	@Override
	public void onTestResult(boolean result) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result)
			handler.obtainMessage(2, 1, 0).sendToTarget();
		else
			handler.obtainMessage(2, 0, 0).sendToTarget();
	}


	@Override
	public void onTestMessage(String msg) {
		// TODO Auto-generated method stub
		handler.obtainMessage(3, msg).sendToTarget();
	}
}
