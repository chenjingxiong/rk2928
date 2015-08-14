package xmps.androiddebugtool.factorytest;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

/**
 * ��Ļ��ʾ����.
 * 
 * @author toddler
 * */
public class ScreenTestFragment extends BaseTestItemFragment implements FragmentChainManager.FragmentChainChangeListener {
	protected static final String TAG = "ScreenTestFragment";
	View rootView;
	MainActivity activity;
	private Button screen_btnStart = null;
	private Button btnClick = null;
	private TextView tv = null;
	private char changeColor = 0;
	private MainAcFmManager mafmm = null;
	public  ScreenTestFragment(){
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fm_screen, container,
				false);
		if(this.getActivity() instanceof MainActivity){
			activity = (MainActivity)this.getActivity();
			mafmm = activity.getActivityFmManager();
			mafmm.addChainChangeListener(this);
		}
		tv = (TextView)rootView.findViewById(R.id.title);
		tv.setText("LCD显示测试");
		btnClick = (Button)rootView.findViewById(R.id.screen_LcdTouch);
		screen_btnStart = (Button)rootView.findViewById(R.id.screen_btnStart);
		btnClick.setVisibility(View.INVISIBLE);
		
		screen_btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				screen_btnStart.setVisibility(View.INVISIBLE);	
				tv.setVisibility(View.INVISIBLE);
				if(getActivity() instanceof MainActivity)
					activity.requestFullScreen(true);
				btnClick.setVisibility(View.VISIBLE);
			}	
		});
		btnClick.setOnClickListener(new View.OnClickListener() {
			@Override
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(changeColor){
				case 0:
					btnClick.setBackgroundColor(Color.RED);
					changeColor = 1;
					break;
				case 1: 
					btnClick.setBackgroundColor(Color.GREEN);
					changeColor = 2;
					break;
				case 2:
					btnClick.setBackgroundColor(Color.BLUE);
					changeColor = 3;
					break;	
				case 3:
					btnClick.setBackgroundColor(Color.BLACK);
					changeColor = 4;
					break;
				case 4:
					btnClick.setBackgroundColor(Color.WHITE);
					changeColor = 0;
					if(null!=mafmm)
						mafmm.next();
					//NEXT
					break;
				}
			}
		});
		return rootView;
		
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null!=mafmm)
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
		}
		else{
			MainActivity activity = (MainActivity)this.getActivity();
			activity.requestFullScreen(false);
			screen_btnStart.setVisibility(View.VISIBLE);	
			tv.setVisibility(View.VISIBLE);
			btnClick.setVisibility(View.INVISIBLE);
		}
			
	}
	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.board = "通用";
		item.title = "LCD屏幕显示测试";
		item.desc = "用RGB颜色测试LCD";
		return item;
	}
}