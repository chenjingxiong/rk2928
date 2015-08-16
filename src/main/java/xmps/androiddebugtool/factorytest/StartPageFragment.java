package xmps.androiddebugtool.factorytest;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enjack.diyviews.CircleButtonView;
import com.enjack.diyviews.SimpleView;

import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.stresstest.StressTestActivity;
import xmps.androiddebugtool.tools.CommToolsActivity;

public class StartPageFragment extends Fragment implements FragmentChainManager.FragmentChainChangeListener {
	
	private MainAcFmManager mafmm = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_startpage, container, false);
		//
		CircleButtonView start = (CircleButtonView)v.findViewById(R.id.start_factory_test);
		start.setOnClickListener(new ButtonListener());
		SimpleView allTestItem = (SimpleView)v.findViewById(R.id.all_test_item);
		allTestItem.setOnClickListener(new ButtonListener());
		SimpleView stressTools = (SimpleView)v.findViewById(R.id.stress_test_tools);
		stressTools.setOnClickListener(new ButtonListener());
        SimpleView commTools = (SimpleView)v.findViewById(R.id.comm_tools);
        commTools.setOnClickListener(new ButtonListener());
		//
		MainActivity activity = (MainActivity)this.getActivity();
		activity.requestFullScreen(true);
		mafmm = activity.getActivityFmManager();
		mafmm.addChainChangeListener(this);
		
		return v;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mafmm.removeChainChangeListener(this);
	}

	@Override
	public void onChainChanged(FragmentChainManager chain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCurrentPosChanged(FragmentChainManager chain, int pos) {
		// TODO Auto-generated method stub
		if(!mafmm.isShowing(this)){
			MainActivity activity = (MainActivity)this.getActivity();
			activity.requestFullScreen(false);
		}
		else{
			MainActivity activity = (MainActivity)this.getActivity();
			activity.requestFullScreen(true);
		}
			
	}
	
	
	private class ButtonListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.stress_test_tools:{
				Intent intent = new Intent(getActivity(), StressTestActivity.class);
				getActivity().startActivity(intent);
			}
				break;
			case R.id.all_test_item:{
				Intent intent = new Intent(getActivity(), AllTestItemActivity.class);
				getActivity().startActivity(intent);
				break;
			}
            case R.id.comm_tools:{
                Intent intent = new Intent(getActivity(), CommToolsActivity.class);
                startActivity(intent);
                break;
            }
			case R.id.start_factory_test:
				mafmm.next();
				//MainActivity activity = (MainActivity)getActivity();
				//activity.requestFullScreen(false);
				break;
			}
		}}
	
}
