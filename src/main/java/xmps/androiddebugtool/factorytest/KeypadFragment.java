package xmps.androiddebugtool.factorytest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xmps.androiddebugtool.factorytest.testmodules.KeypadLayoutLoader;

public class KeypadFragment extends Fragment{
	
	
	KeypadLayoutLoader loader = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		loader = new KeypadLayoutLoader();
		loader.load(this, inflater, container);
		View v = loader.invokeInflate();
		
		return v;
    } 
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		loader.deLoad();
	}

}
