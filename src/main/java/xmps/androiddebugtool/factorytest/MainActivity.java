package xmps.androiddebugtool.factorytest;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.android.enjack.util.LCDMetrixUtil;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;

public class MainActivity extends Activity implements FragmentChainManager.FragmentChainChangeListener {

    private final String tag = "<MainActivity>";
    private final String RETAINED_FM_TAG = "fm_tag:RetainedFragment";
    private MainAcFmManager mafmm = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //LCDMetrixUtil.lockOrientation(this, this);
        //LCDMetrixUtil.lockOrientation(this, LCDMetrixUtil.ORIENTATION_LAND);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        //reserved. save data.
        RetainedFragment retainedfm = null;
        FragmentManager fmm = getFragmentManager();
        retainedfm = (RetainedFragment)fmm.findFragmentByTag(RETAINED_FM_TAG);
        if(null==retainedfm){
            retainedfm = new RetainedFragment();
            fmm.beginTransaction().add(retainedfm, RETAINED_FM_TAG).commit();
        }
        //
        mafmm = new MainAcFmManager(R.id.content, MainActivity.this, this);
        mafmm.addChainChangeListener(this);
        mafmm.init();
        findViewById(R.id.next).setOnClickListener(new BtnListener());
        findViewById(R.id.pre).setOnClickListener(new BtnListener());

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mafmm.removeChainChangeListener(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
		/*
		 * 关于配置信息改变导致fragment重叠的问题，可尝试以下几种方法：
		 * 1.禁止旋转;
		 * 2.禁止旋转后activity重建;
		 * 3.onCreate判断屏幕方向并锁定;
		 * 4.onCreate判断instance是否为null，获取存在的fragment(建议用此种方式);
		 * 5.onSaveInstanceState不调用super保存state;
		 *
		 * 这里用方式5
		 * */
        //super.onSaveInstanceState(outState);
    }



    private class BtnListener implements View.OnClickListener{


        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.next:
                    mafmm.next();
                    break;
                case R.id.pre:
                    mafmm.pre();
                    break;
            }
        }

    }

    public MainAcFmManager getActivityFmManager(){
        return this.mafmm;
    }

    /**
     * request fragment's view full parent view.
     * */
    public void requestFullScreen(boolean full){
        if(Configuration.ORIENTATION_LANDSCAPE ==
                getResources().getConfiguration().orientation){
            RelativeLayout.LayoutParams lp;
            FrameLayout frame = (FrameLayout)findViewById(R.id.content);
            Button next = (Button)findViewById(R.id.next);
            Button pre = (Button)findViewById(R.id.pre);
            if(full){
                frame.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
                next.setVisibility(View.INVISIBLE);
                pre.setVisibility(View.INVISIBLE);
            }
            else{
                lp= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                next.setVisibility(View.VISIBLE);
                pre.setVisibility(View.VISIBLE);
                lp.addRule(RelativeLayout.LEFT_OF, R.id.next);
                int marginPixel = LCDMetrixUtil.dip2px(MainActivity.this, 1f);
                lp.setMargins(0, marginPixel, 0, marginPixel);
                frame.setLayoutParams(lp);
            }
            return;

        }
        else{
            RelativeLayout.LayoutParams lp;
            FrameLayout frame = (FrameLayout)findViewById(R.id.content);
            Button next = (Button)findViewById(R.id.next);
            Button pre = (Button)findViewById(R.id.pre);
            if(full){
                frame.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
                next.setVisibility(View.INVISIBLE);
                pre.setVisibility(View.INVISIBLE);
            }
            else{
                lp= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                next.setVisibility(View.VISIBLE);
                pre.setVisibility(View.VISIBLE);
                lp.addRule(RelativeLayout.ABOVE, R.id.next);
                int marginPixel = LCDMetrixUtil.dip2px(MainActivity.this, 1f);
                lp.setMargins(0, marginPixel, 0, marginPixel);
                frame.setLayoutParams(lp);
            }
        }

    }


    @Override
    public void onChainChanged(FragmentChainManager chain) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onCurrentPosChanged(FragmentChainManager chain, int pos) {
        // TODO Auto-generated method stub
        Log.i(tag, "current pos:"+pos);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mafmm!=null){
                FragmentChainManager.FragmentInfo info = (FragmentChainManager.FragmentInfo)mafmm.findAt(mafmm.getCurrent());
                if(info.fm instanceof BaseTestItemFragment){
                    BaseTestItemFragment base = (BaseTestItemFragment)info.fm;
                    if(base.onKeyDown(keyCode, event)){
                        return true;
                    }
                }
            }
            //System.exit(0);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
