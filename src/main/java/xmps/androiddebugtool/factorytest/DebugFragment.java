package xmps.androiddebugtool.factorytest;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.enjack.diyviews.MagnetView;


/**
 * do nothing.just for debug&test.
 * */
public class DebugFragment extends Fragment {
	
	private final String tag = "<DebugFragment>";
	private Button btn = null;
	private Button auto;
	private MagnetView magnet = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_debug, container, false);
		findElements(v);
		setElementsListener();
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
	}
	
	private class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.id_dbg_btn_1://off
				break;
			case R.id.id_dbg_btn:	//on
                Log.i(tag, magnet.toString());
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
