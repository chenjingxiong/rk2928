package xmps.androiddebugtool.factorytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.enjack.util.SwitchUtil;

import java.util.Timer;
import java.util.TimerTask;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

/**
 * RJ45网口测试
 * */
public class Rj45PortFragment extends BaseTestItemFragment {
	private final String tag = "<Rj45PortFragment>";
	private WebView web = null;
	private EditText address = null;
	private Button go = null;
	private ProgressBar progressbar = null;
	private boolean debugMode = false;
	private Timer timer = null;
	private TimerTask timerTask = null;
	private MainHandler handler = null;

	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_rj45port, container, false);
		TextView title = (TextView)v.findViewById(R.id.title);
		title.setText("RJ45网口测试");
		handler = new MainHandler();
		closeOtherNetwork();
		checkingNetWorkStatus();
		
		address = (EditText)v.findViewById(R.id.rj45_address);
		address.setInputType(InputType.TYPE_NULL);//�����������뷨
		address.setOnClickListener(new ViewClickedListener());
		web = (WebView)v.findViewById(R.id.rj45_webView);
		go = (Button)v.findViewById(R.id.rj45_go);
		go.setOnClickListener(new ViewClickedListener());
		progressbar = (ProgressBar)v.findViewById(R.id.rj45_progress_horizontal);
		return v;
	}
	
	@Override
	public void onDestroy(){
		if(timerTask!=null)
			timerTask.cancel();
		if(timer!=null)
			timer.cancel();
		timer = null;
		timerTask = null;
		super.onDestroy();
	}
	
	@SuppressLint("HandlerLeak")
	private class MainHandler extends Handler{
		@Override 
        public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				if(timer!=null)
					timer.cancel();
				if(timerTask!=null)
					timerTask.cancel();
				initWebView();
				Toast.makeText(getActivity(), "已经关闭其他网络连接", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if(timer!=null)
					timer.cancel();
				if(timerTask!=null)
					timerTask.cancel();
				initWebView();
				SwitchUtil.wifi(getActivity(), true);
				Toast.makeText(getActivity(), "进入调试模式，正在打开wifi...", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				progressbar.setProgress(0);
				break;
			case 3:
				progressbar.setProgress(msg.arg1);
				break;
			case 4:
				String url = (String)msg.obj;
				address.setText(url);
				break;
			}
		}
	}
	
	private class ViewClickedListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.rj45_address:
				String text = address.getText().toString();
				if(text==null || text.equals(""))
					address.setText("http://www.baidu.com");
				address.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
				//address.requestFocus();
				InputMethodManager imm = (InputMethodManager)getActivity().
						getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);//强制显示输入法
				break;
			case R.id.rj45_go:
				String str = address.getText().toString();
				if(str.equals("Wifi") || str.equals("wifi")){
					debugMode = true;
					address.setText("http://www.baidu.com");
					handler.sendEmptyMessage(1);
				}
				else{
					if(str==null || str.equals(""))
						str = "http://www.baidu.com";
					if(!str.contains("http://"))
						str = "http://"+str;
					web.loadUrl(str);
				}
//				InputMethodManager im = (InputMethodManager)getActivity().
//						getSystemService(Context.INPUT_METHOD_SERVICE);
//				im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 
//						InputMethodManager.HIDE_NOT_ALWAYS);
				break;
			}
		}}

	/**关闭wifi和数据连接，只用有线网络上网*/
	private void closeOtherNetwork(){
		//close wifi
		SwitchUtil.wifi(getActivity(), false);
		//关闭数据连接
		
	}
	
	
	private void checkingNetWorkStatus(){
		timer = new Timer();
		timerTask = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(debugMode){
					handler.sendEmptyMessage(1);
					return;
				}
				WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
				if(!wifi.isWifiEnabled()){
					Log.i(tag, "Other network has been closed. Use cable now.");
					handler.sendEmptyMessage(0);
					return;
				}
			}};
		timer.schedule(timerTask, 1, 100);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView(){
		if(web==null)
			return;
		web.setWebViewClient(new WebViewClient(){
			 @Override
			 public boolean shouldOverrideUrlLoading(WebView view, String url){
				 view.loadUrl(url);
				 return true;
			 }
		});
		
		web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    handler.sendEmptyMessage(2);

                } else {
                	if(0==newProgress){//a new page
                		String url = web.getUrl();
                		handler.obtainMessage(4, url).sendToTarget();
                	}
                	handler.obtainMessage(3, newProgress, 0).sendToTarget();
                }

            }
        });
		web.getSettings().setJavaScriptEnabled(true);//支持javascript
		//web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
		web.loadUrl("http://www.baidu.com");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(web!=null && web.canGoBack()){
				Log.i(tag, "WebView go back");
				web.goBack();
				return true;
			}
			else{
				Log.w(tag, "WebView can't go back.Parents process key event.");
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "网口测试";
		item.board = "rk2928-pad";
		item.desc = "pad(np10)开票机网口上网测试";
		return item;
	}
}
