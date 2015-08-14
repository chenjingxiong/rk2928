package xmps.androiddebugtool.factorytest.chain;

import android.app.Fragment;
import android.view.KeyEvent;

public abstract class BaseTestItemFragment extends Fragment {
	public BaseTestItemFragment(){
		super();
	}
	
	public abstract ItemDescription getItemDescription();
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}
}
