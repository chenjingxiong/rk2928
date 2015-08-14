package xmps.androiddebugtool.factorytest;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.enjack.util.SwitchUtil;


/**
 * do nothing.just for debug&test.
 * */
public class DebugFragment extends Fragment {
	
	private final String tag = "<DebugFragment>";
	private Button btn = null;
	private Button auto;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_debug, container, false);
		//fullScreen(true);
		//this.getActivity().getWindowManager().getDefaultDisplay().getRealSize(outSize);
		findElements(v);
		setElementsListener();
		//long val = RK2928_IO_Spec.LCDC.BASE + RK2928_IO_Spec.LCDC.OFFSET_LCDC_SYS_CONFIG;
		//Log.i(tag, "reg:" + toHexString(val));
		return v;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	public void findElements(View v){
		auto = (Button)v.findViewById(R.id.id_dbg_btn_1);
		btn = (Button)v.findViewById(R.id.id_dbg_btn);
	}
	
	public void setElementsListener(){
		ButtonListener listener = new ButtonListener();
		btn.setOnClickListener(listener);
		auto.setOnClickListener(listener);
		//normalscreen.setOnClickListener(listener);
	}
	
	private class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.id_dbg_btn_1://off
				//SwitchUtil.mute(getActivity(), 3);
				SwitchUtil.lockScreen(getActivity());
				break;
			case R.id.id_dbg_btn:	//on
				SwitchUtil.mute(getActivity(), 2);
				break;
			}
		}
	}
	
	private String toHexString(long val){
		return String.format("0x%08x", val);
	}
	
	
	private void fullScreen(boolean full){
		MainActivity activity = (MainActivity)this.getActivity();
		activity.requestFullScreen(full);
	}
}
