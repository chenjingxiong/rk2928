package xmps.androiddebugtool.factorytest;


import android.app.Fragment;
import android.graphics.Color;
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
        magnet = (MagnetView)v.findViewById(R.id.magnet);
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
                magnet.setText("enjack");
                magnet.setTextSize(24);
                magnet.setTextColor(Color.BLUE, Color.YELLOW);
                magnet.setBackgroundColor(Color.GRAY, Color.BLUE);
                magnet.postInvalidate();
                Log.i(tag, "text:"+magnet.getText());
				break;
			case R.id.id_dbg_btn:	//on
                //magnet.setTextSizeSuggested(true);
                //magnet.postInvalidate();
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
