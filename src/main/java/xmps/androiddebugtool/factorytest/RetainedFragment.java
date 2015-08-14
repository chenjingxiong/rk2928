package xmps.androiddebugtool.factorytest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

/**�����������ݵ�fragment*/
public class RetainedFragment extends Fragment {
	
	private final String tag = "<RetainedFragment>";
	private int mFmChainPos = -1;

	@Override
    public void onCreate(Bundle savedInstanceState){
		Log.i(tag, "RetainedFragment--onCreate");
		super.onCreate(savedInstanceState);
		// Tell the framework to try to keep this fragment around
		// during a configuration change.
		setRetainInstance(true);
	}
	
	public void setChainPos(int pos){
		this.mFmChainPos = pos;
	}
	
	public int getChainPos(){
		return this.mFmChainPos;
	}
}
