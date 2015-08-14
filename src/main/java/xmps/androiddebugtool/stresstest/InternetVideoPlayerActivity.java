package xmps.androiddebugtool.stresstest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.enjack.util.LCDMetrixUtil;

import xmps.androiddebugtool.factorytest.R;

public class InternetVideoPlayerActivity extends Activity {
	
	private final String tag = "<InternetVideoPlayerActivity>";
	private WebView web;
	private ProgressBar bar;
	private MainHandler handler = null;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int w = LCDMetrixUtil.width(this);
		int h = LCDMetrixUtil.height(this);
		if(w<h){
			if(LCDMetrixUtil.ORIENTATION_PORT==LCDMetrixUtil.getOrientation(this))
				LCDMetrixUtil.setOrientation(this, LCDMetrixUtil.ORIENTATION_LAND);
			else
				LCDMetrixUtil.setOrientation(this, LCDMetrixUtil.ORIENTATION_PORT);
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
		setContentView(R.layout.activity_internel_video_player);
		handler = new MainHandler();
		bar = (ProgressBar)findViewById(R.id.internet_video_progress);
		web = (WebView)findViewById(R.id.internet_video_webView);
		web.setWebViewClient(new WebViewClient(){
			 @Override
			 public boolean shouldOverrideUrlLoading(WebView view, String url){
				 view.loadUrl(url);
				 return true;
			 }
		});
		web.setWebChromeClient(new WebChromeClient(){
			
			@Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    handler.sendEmptyMessage(1);
                    Log.i(tag, "load finish");

                } else {
                	handler.obtainMessage(0, newProgress, 0).sendToTarget();
                }

            }
		});
		web.getSettings().setJavaScriptEnabled(true);//֧��javascript
		web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//����ʹ�û���
		web.loadUrl("http://www.youku.com");
	}
	
	@SuppressLint("HandlerLeak")
	private class MainHandler extends Handler{
		@Override 
        public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				bar.setProgress(msg.arg1);
				break;
			case 1:
				bar.setProgress(0);
				break;
			}
		}
	}
}
