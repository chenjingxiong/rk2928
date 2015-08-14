package xmps.androiddebugtool.factorytest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.enjack.util.BackLightUtil;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

public class BackLightFragment extends BaseTestItemFragment {
	
	private final String tag = "<BackLightFragment>";
	private SeekBar seekbar = null;
	private TextView tvValue = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		View v = null;
        v = inflater.inflate(R.layout.fm_backlight, container, false); 
        //
        v.findViewById(R.id.bl_add).setOnClickListener(new ButtonListener());
        v.findViewById(R.id.bl_dec).setOnClickListener(new ButtonListener());
        TextView tv = (TextView)v.findViewById(R.id.title);
        tv.setText("LCD背光测试");
        tvValue = (TextView)v.findViewById(R.id.bl_value);
        seekbar = (SeekBar)v.findViewById(R.id.bl_seekbar);
        seekbar.setOnSeekBarChangeListener(new onSeekBarChanged());
        setUIVaule(getBrightness());
        return v;
    }  
	
	
	private void stopAutoAdjust(){
		Activity activity = this.getActivity();
		if(!BackLightUtil.isAutoBrightness(activity))
			BackLightUtil.stopAutoBrightness(activity);
	}
	
	
	private void setBrightness(int bri){
		BackLightUtil.setBrightness(getActivity(), bri);
		BackLightUtil.saveBrightness(getActivity(), bri);
	}
	
	private int getBrightness(){
		return BackLightUtil.getScreenBrightness(getActivity());
	}
	
	private void setUIVaule(int brightness){
		if(null!=seekbar)
			seekbar.setProgress(brightness);
		if(null!=tvValue)
			tvValue.setText(String.valueOf(brightness));
	}
	
	private void logout(){
		int bri = BackLightUtil.getScreenBrightness(getActivity());
		Log.i(tag, "current brightness is:"+bri);
	}
	
	
	private void add(){
		stopAutoAdjust();
		int brightness = getBrightness();
		brightness+=20;
		if(brightness>BackLightUtil.MAX_BRIGHTNESS)
			brightness = BackLightUtil.MAX_BRIGHTNESS;
		if(brightness<BackLightUtil.MIN_BRIGHTNESS)
			brightness = BackLightUtil.MIN_BRIGHTNESS;
		setBrightness(brightness);
		setUIVaule(brightness);
		logout();
	}
	
	private void dec(){
		stopAutoAdjust();
		int brightness = getBrightness();
		brightness -= 20;
		if(brightness>BackLightUtil.MAX_BRIGHTNESS)
			brightness = BackLightUtil.MAX_BRIGHTNESS;
		if(brightness < BackLightUtil.MIN_BRIGHTNESS)
			brightness = BackLightUtil.MIN_BRIGHTNESS;
		setBrightness(brightness);
		setUIVaule(brightness);
		logout();
	}
	
	
	private class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.bl_add:add();break;
			case R.id.bl_dec:dec();break;
			}
		}
	}
	
	
	private class onSeekBarChanged implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			int brightness = progress;
			if(brightness>BackLightUtil.MAX_BRIGHTNESS)
				brightness = BackLightUtil.MAX_BRIGHTNESS;
			if(brightness<BackLightUtil.MIN_BRIGHTNESS)
				brightness = BackLightUtil.MIN_BRIGHTNESS;
			setBrightness(brightness);
			tvValue.setText(String.valueOf(brightness));
			logout();
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
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "背光测试";
		item.board = "通用";
		item.desc = "LCD背光调节测试";
		return item;
	}
}
