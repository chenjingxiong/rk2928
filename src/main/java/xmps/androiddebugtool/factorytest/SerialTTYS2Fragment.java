package xmps.androiddebugtool.factorytest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.enjack.diyviews.SwitchView;
import com.enjack.tools.serialport.SerialPort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

/**
 * Created by enjack on 2015/8/16.
 */
public class SerialTTYS2Fragment extends BaseTestItemFragment {

    private String tag = "<SerialTTYS2Fragment>";
    private View view = null;
    private Spinner spinner = null;
    private Button btnClrReceive = null;
    private Button btnClrReceiveCnt = null;
    private Button btnClrSend = null;
    private Button btnClrSendCnt = null;
    private Button btnOpen = null;
    private Button btnClose = null;
    private Button btnSend = null;
    private SwitchView switchAutoSend = null;
    private TextView tvReceive = null;
    private EditText etSend = null;
    private TextView tvReceiveCnt = null;
    private TextView tvSendCnt = null;
    private EditText etAutoTime = null;

    private SerialPort port = null;
    private int baundrate = 0;
    private int receiveBytesCnt = 0;
    private int sendBytesCnt = 0;

    private UIHandler handler = new UIHandler();
    private Timer timer = null;
    private TimerTask task = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fm_ttys, container, false);
        TextView tv = (TextView)view.findViewById(R.id.title);
        tv.setText("ttyS2");
        findElements();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(hidden){
            Log.i(tag, "hide fragment. disable timer.");
            if(null!=task){
                task.cancel();
                task = null;
            }
            if(null!=timer){
                timer.cancel();
                timer = null;
            }
        }
        else{
            Log.i(tag, "show fragment. resume timer.");
            if(switchAutoSend.getState()) {
                String text = etAutoTime.getText().toString();
                int ms = Integer.parseInt(text);
                timer = new Timer();
                task = new AutoSendTask();
                timer.schedule(task, 0, ms);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(switchAutoSend.getState()) {
            String text = etAutoTime.getText().toString();
            int ms = Integer.parseInt(text);
            timer = new Timer();
            task = new AutoSendTask();
            timer.schedule(task, 0, ms);
        }
    }

    @Override
    public void onPause(){
        Log.i(tag, "onPause");
        super.onPause();
        if(null!=task){
            task.cancel();
            task = null;
        }
        if(null!=timer){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy(){
        if(null!=task){
            task.cancel();
            task = null;
        }
        if(null!=timer){
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }


    private void findElements(){
        spinner = (Spinner)view.findViewById(R.id.ttys_sp_baundrate);
        List<String> list = new ArrayList<>();
        list.add("115200");
        list.add("9600");
        list.add("4800");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Log.i(tag, "115200 selected.");
                        baundrate = 115200;
                        break;
                    case 1:
                        Log.i(tag, "9600 selected.");
                        baundrate = 9600;
                        break;
                    case 2:
                        Log.i(tag, "4800 selected.");
                        baundrate = 4800;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnClickedListener btnListener = new BtnClickedListener();
        btnClrReceive = (Button)view.findViewById(R.id.ttys_btn_clr_receive);
        btnClrReceive.setOnClickListener(btnListener);
        btnClrReceiveCnt = (Button)view.findViewById(R.id.ttys_btn_clr_receive_cnt);
        btnClrReceiveCnt.setOnClickListener(btnListener);
        btnClrSend = (Button)view.findViewById(R.id.ttys_btn_clr_send);
        btnClrSend.setOnClickListener(btnListener);
        btnClrSendCnt = (Button)view.findViewById(R.id.ttys_btn_clr_send_cnt);
        btnClrSendCnt.setOnClickListener(btnListener);
        btnOpen = (Button)view.findViewById(R.id.ttys_btn_open);
        btnOpen.setOnClickListener(btnListener);
        btnClose = (Button)view.findViewById(R.id.ttys_btn_close);
        btnClose.setOnClickListener(btnListener);
        btnSend = (Button)view.findViewById(R.id.ttys_btn_send);
        btnSend.setOnClickListener(btnListener);

        switchAutoSend = (SwitchView)view.findViewById(R.id.ttys_switch_auto_send);
        switchAutoSend.setStateChangedListener(new SwitchView.SwitchStateChangedListener() {
            @Override
            public void onSwitchStateChanged(boolean state) {
                if(state){
                    String text = etAutoTime.getText().toString();
                    int ms = Integer.parseInt(text);
                    timer = new Timer();
                    task = new AutoSendTask();
                    timer.schedule(task, 0, ms);
                }
                else{
                    if(null!=timer) {
                        task.cancel();
                        timer.cancel();
                    }
                    timer = null;
                    task = null;
                }
            }
        });

        tvReceive = (TextView)view.findViewById(R.id.ttys_tv_receive);
        tvReceiveCnt = (TextView)view.findViewById(R.id.ttys_tv_receive_cnt);
        etSend = (EditText)view.findViewById(R.id.ttys_et_send);
        tvSendCnt = (TextView)view.findViewById(R.id.ttys_tv_send_cnt);
        etAutoTime = (EditText)view.findViewById(R.id.ttys_et_time);
    }


    private class BtnClickedListener implements View.OnClickListener{
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ttys_btn_clr_receive:
                    tvReceive.setText("");
                    break;
                case R.id.ttys_btn_clr_receive_cnt:
                    receiveBytesCnt = 0;
                    tvReceiveCnt.setText("接收:0");
                    break;
                case R.id.ttys_btn_clr_send:
                    etSend.setText("");
                    break;
                case R.id.ttys_btn_clr_send_cnt:
                    sendBytesCnt = 0;
                    tvSendCnt.setText("发送:0");
                    break;
                case R.id.ttys_btn_open:{
                    if(null==port){
                        if(0==baundrate){
                            Log.e(tag, "invalid baundrate.");
                            Toast.makeText(getActivity(), "invalid baundrate", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        File file = new File("/dev/ttyS2");
                        if(!file.canRead() || !file.canWrite()){
                            Log.e(tag, "tty can't read or write.");
                            Toast.makeText(getActivity(), "tty can't read or write.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        port = new SerialPort(file, baundrate, 0);
                        try {
                            port.open();
                        } catch (IOException e) {
                            e.printStackTrace();
                            port = null;
                        }
                        if(null == port){
                            Log.e(tag, "open tty failed.");
                            Toast.makeText(getActivity(), "open tty failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        port.setReceiver(new SerialPort.SerialPortDataReceiver() {
                            @Override
                            public void onReceived(byte[] bytes, int i) {
                                receiveBytesCnt+=i;
                                byte[] buff = new byte[i];
                                System.arraycopy(bytes, 0, buff, 0, i);
                                String str = new String(buff);
                                handler.obtainMessage(0, str).sendToTarget();
                            }
                        });
                        Toast.makeText(getActivity(), "open tty success!", Toast.LENGTH_SHORT).show();
                        Log.i(tag, "open tty success! Baundrate:"+baundrate);
                    }
                    else{
                        Log.i(tag, "tty already opened.");
                        Toast.makeText(getActivity(), "tty already opened.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case R.id.ttys_btn_close:
                    if(port!=null){
                        port.close();
                        port = null;
                    }
                    switchAutoSend.setState(false);
                    break;
                case R.id.ttys_btn_send:
                    if(null==port){
                        Log.e(tag, "tty not opened yet.");
                        Toast.makeText(getActivity(), "tty not opened yet.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String data = etSend.getText().toString();
                        if(data!=null && data.length()!=0) {
                            byte[] buff = data.getBytes();
                            port.send(buff);
                            sendBytesCnt += data.length();
                            handler.sendEmptyMessage(1);
                        }
                    }
                    break;
            }
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:{//receive data
                    String str = (String)msg.obj;
                    String text = tvReceive.getText().toString();
                    StringBuilder sb = new StringBuilder();
                    sb.append(text);
                    sb.append(str);
                    if(sb.length()>200)
                        text = sb.substring(sb.length()-50);
                    else
                        text = sb.toString();
                    tvReceive.setText(text);
                    tvReceiveCnt.setText("接收:"+receiveBytesCnt);
                    break;
                }
                case 1:{//data send count
                    tvSendCnt.setText("发送:"+sendBytesCnt);
                    break;
                }
            }
            //super.handleMessage(msg);
        }
    }

    private class AutoSendTask extends TimerTask{

        /**
         * The task to run should be specified in the implementation of the {@code run()}
         * method.
         */
        @Override
        public void run() {
            if(null!=port){
                String str = etSend.getText().toString();
                if(null!= str && str.length()!=0) {
                    byte[] buff = str.getBytes();
                    port.send(buff);
                    sendBytesCnt += str.length();
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    @Override
    public ItemDescription getItemDescription() {
        ItemDescription item = new ItemDescription();
        item.board = "通用";
        item.title = "ttyS2";
        item.desc = "ttyS2收发测试";
        return item;
    }
}
