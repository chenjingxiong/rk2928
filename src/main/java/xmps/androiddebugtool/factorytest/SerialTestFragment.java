package xmps.androiddebugtool.factorytest;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.enjack.diyviews.SwitchView;
import com.enjack.tools.serialport.SerialPort;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;


/**
 * ���ڲ���
 * 
 * @author enjack
 * */
public class SerialTestFragment extends BaseTestItemFragment {
	private SerialPort port0 = null;
	private SerialPort port1 = null;
	private SerialPort port2 = null;
	private View view = null;
	private MyHandler handler = new MyHandler();
	StringBuilder sb0 = new StringBuilder("");
	StringBuilder sb1 = new StringBuilder("");
	StringBuilder sb2 = new StringBuilder("");
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		view = inflater.inflate(R.layout.fm_serial, container, false);
		TextView tv = (TextView)view.findViewById(R.id.title);
		tv.setText("���ڲ���");
		
		if(!checkPermission()){
			Toast.makeText(getActivity(), "���ڶ�дȨ��������!", Toast.LENGTH_LONG).show();
			return view;
		}
		
		File file = new File("/dev/ttyS0");
		port0 = new  SerialPort(file, 115200, 0);
		file = new File("/dev/ttyS1");
		port1 = new  SerialPort(file, 115200, 0);
		file = new File("/dev/ttyS2");
		port2 = new  SerialPort(file, 115200, 0);
		
		
		try {
			port0.open();
			port1.open();
			port2.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		port0.setReceiver(new DataReceiver(0));
		port1.setReceiver(new DataReceiver(1));
		port2.setReceiver(new DataReceiver(2));
		
		view.findViewById(R.id.serial_btnSend).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = "RK2928 serial port test!   ";
				byte[] data = str.getBytes();
				port0.send(data);
				port1.send(data);
				port2.send(data);
			}
		});
		
		view.findViewById(R.id.serial_btnClear).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sb0.setLength(0);
				sb1.setLength(0);
				sb2.setLength(0);
				TextView tv = (TextView)view.findViewById(R.id.serial_ttyS0);
				tv.setText("");
				tv = (TextView)view.findViewById(R.id.serial_ttyS1);
				tv.setText("");
				tv = (TextView)view.findViewById(R.id.serial_ttyS2);
				tv.setText("");
			}
		});
		
		
		SwitchView switchview = (SwitchView)view.findViewById(R.id.serial_sending_loop);
		switchview.setStateChangedListener(new SwitchView.SwitchStateChangedListener() {
			
			private boolean bSend = true;
			@Override
			public void onSwitchStateChanged(boolean state) {
				// TODO Auto-generated method stub
				if(state){
					bSend = true;
					new Thread(new Runnable(){

						@Override
						public void run() {
							while(bSend){
								String str = "RK2928 serial port test!   "+getRandomString(5);
								byte[] data = str.getBytes();
								sb0.setLength(0);
								sb1.setLength(0);
								sb2.setLength(0);
								port0.send(data);
								port1.send(data);
								port2.send(data);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}).start();
				}
				else
					bSend = false;
			}
		});
        return  view;
    } 
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null!=port0 && null!=port1 && null!=port2){
			port0.close();
			port1.close();
			port2.close();
		}
	}
	
	private boolean checkPermission(){
		File file1 = new File("/dev/ttyS0");
		File file2 = new File("/dev/ttyS1");
		File file3 = new File("/dev/ttyS2");
		
		if(file1.canRead() && file1.canWrite() &&
				file2.canRead() && file2.canWrite() &&
				file3.canRead() && file3.canWrite())
			return true;
		else
			return false;
	}
	
private class MyHandler extends Handler{
		
		@SuppressLint("HandlerLeak")
		public MyHandler(){
			super();
		}
		@Override 
        public void handleMessage(Message msg){
			TextView v;
			StringBuilder sb;
			switch(msg.what){
			case 0:
				v = (TextView)view.findViewById(R.id.serial_ttyS0);
				sb = (StringBuilder)msg.obj;
				v.setText(sb.toString());
				break;
			case 1:
				v = (TextView)view.findViewById(R.id.serial_ttyS1);
				sb = (StringBuilder)msg.obj;
				v.setText(sb.toString());
				break;
			case 2:
				v = (TextView)view.findViewById(R.id.serial_ttyS2);
				sb = (StringBuilder)msg.obj;
				v.setText(sb.toString());
				break;
			}
		}
	}
	
	private class DataReceiver implements SerialPort.SerialPortDataReceiver{
		
		private int index = -1;
		public DataReceiver(int index){
			this.index = index;
		}

		@Override
		public void onReceived(byte[] arg0, int arg1) {
			// TODO Auto-generated method stub
			if(0==index){
				byte[] buff = new byte[arg1];
				System.arraycopy(arg0, 0, buff, 0, arg1);
				String str = new String(buff);
				sb0.append(str);
				handler.obtainMessage(0, sb0).sendToTarget();
			}
			else if(1==index){
				byte[] buff = new byte[arg1];
				System.arraycopy(arg0, 0, buff, 0, arg1);
				String str = new String(buff);
				sb1.append(str);
				handler.obtainMessage(1, sb1).sendToTarget();
			}
			else if(2==index){
				byte[] buff = new byte[arg1];
				System.arraycopy(arg0, 0, buff, 0, arg1);
				String str = new String(buff);
				sb2.append(str);
				handler.obtainMessage(2, sb2).sendToTarget();
			}
		}}
	
	/** 
     * ����һ���������������ĸ�ַ���(ֻ������Сд��ĸ) 
     *  
     * @param length 
     *            ����ַ������� 
     * @return ����ַ��� 
     */  
	private String getRandomString(int length) { 
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 }

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "���ڲ���";
		item.board = "rk2928";
		item.desc = "����/dev/ttyS0��/dev/ttyS1��/dev/ttyS2�����շ�����";
		return item;
	} 
}
