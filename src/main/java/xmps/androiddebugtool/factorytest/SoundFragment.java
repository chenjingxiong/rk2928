package xmps.androiddebugtool.factorytest;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.SoundTest;

public class SoundFragment extends BaseTestItemFragment implements FragmentChainManager.FragmentChainChangeListener {
	
	private final String tag = "<SoundFragment>";
	private SoundTest test = null;
	private SeekBar seekbar = null;
	private Button play = null;
	private Button stop = null;
	private Button add = null;
	private Button dec = null;
	private TextView value = null;
	private MainAcFmManager mafmm = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		View v = inflater.inflate(R.layout.fm_sound, container, false);
		if(this.getActivity() instanceof MainActivity){
			MainActivity activity = (MainActivity)this.getActivity();
			mafmm = activity.getActivityFmManager();
			mafmm.addChainChangeListener(this);
		}
		
		TextView tv = (TextView)v.findViewById(R.id.title);
		tv.setText("喇叭声音测试");
		seekbar = (SeekBar)v.findViewById(R.id.sound_seekbar);
		play = (Button)v.findViewById(R.id.play);
		stop = (Button)v.findViewById(R.id.stop);
		add = (Button)v.findViewById(R.id.sound_add);
		dec = (Button)v.findViewById(R.id.sound_dec);
		value = (TextView)v.findViewById(R.id.soundd_val);
		//
		test = new SoundTest(this.getActivity(), this.getActivity(), R.raw.nokia);
		seekbar.setMax(test.getMax());
		seekbar.setProgress(test.get());
		value.setText(String.valueOf(test.get()));
		//
		play.setOnClickListener(new ButtonListener());
		stop.setOnClickListener(new ButtonListener());
		add.setOnClickListener(new ButtonListener());
		dec.setOnClickListener(new ButtonListener());
		seekbar.setOnSeekBarChangeListener(new SeekBarListener());
        return  v;
    } 
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null!=mafmm)
			mafmm.removeChainChangeListener(this);
		test.beforeExit();
	}
	
	
	private void logout(){
		Log.i(tag, "current vol:"+test.get());
	}
	
	
	private class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.sound_add:
				test.raise();logout();updateUI();break;
			case R.id.sound_dec:
				test.lower();logout();updateUI();break;
			case R.id.play:
				test.play();break;
			case R.id.stop:
				test.pause();break;
			}
		}
		
	}
	
	
	private void updateUI(){
		seekbar.setProgress(test.get());
		value.setText(String.valueOf(test.get()));
	}
	
	
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			Log.i("", "vol"+progress);
			test.set(progress);
			value.setText(String.valueOf(progress));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}


	@Override
	public void onChainChanged(FragmentChainManager chain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCurrentPosChanged(FragmentChainManager chain, int pos) {
		// TODO Auto-generated method stub
		boolean showing = false;
		//Log.i(tag, "current pos:"+pos);
		showing = mafmm.isShowing(this);
		if(!showing)
			test.pause();
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "喇叭声音测试";
		item.board = "通用";
		item.desc = "播放音频测试喇叭";
		return item;
	}
}
