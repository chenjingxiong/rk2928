package xmps.androiddebugtool.factorytest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.TouchScreenButton;

/**
 * ���㴥�����ԡ�
 * 
 * @author toddler
 * */
public class SinglePointTouchFragment extends BaseTestItemFragment implements FragmentChainManager.FragmentChainChangeListener {
	protected static final String TAG = "SinglePointTouchFragment";
	View rootView;
	MainActivity activity;
	private Button btnStart = null;
	private TouchScreenButton btnLeftUp = null;
	private TouchScreenButton btnLeftDown = null;
	private TouchScreenButton btnRightUp = null;
	private TouchScreenButton btnRightDown = null;
	private TouchScreenButton btnMiddle = null;
	
	private TextView tv = null;
	private MainAcFmManager mafmm = null;
	public  SinglePointTouchFragment(){
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fm_single_point_touch, container,
				false);
		if(getActivity() instanceof MainActivity){
			activity = (MainActivity)this.getActivity();
			mafmm = activity.getActivityFmManager();
			mafmm.addChainChangeListener(this);
		}
		tv = (TextView)rootView.findViewById(R.id.title);
		tv.setText("单点触摸测试");
		btnStart = (Button)rootView.findViewById(R.id.SinglePointTouch_btnStart);
		btnLeftUp = (TouchScreenButton)rootView.findViewById(R.id.SinglePointTouch_btnLeftUp);
		btnLeftDown = (TouchScreenButton)rootView.findViewById(R.id.SinglePointTouch_btnLeftDown);
		btnRightUp = (TouchScreenButton)rootView.findViewById(R.id.SinglePointTouch_btnRightUp);
		btnRightDown = (TouchScreenButton)rootView.findViewById(R.id.SinglePointTouch_btnRightDown);
		btnMiddle = (TouchScreenButton)rootView.findViewById(R.id.SinglePointTouch_btnMiddle);
		
		btnLeftUp.setVisibility(View.INVISIBLE);
		btnLeftDown.setVisibility(View.INVISIBLE);
		btnRightUp.setVisibility(View.INVISIBLE);
		btnRightDown.setVisibility(View.INVISIBLE);
		btnMiddle.setVisibility(View.INVISIBLE);	
		onTouchStart();
		onTouchLeftUp();
		onTouchLeftDown();
		onTouchRightUp();
		onTouchRightDown();
		onTouchMiddle();
		return rootView;
		
	}
	public void onTouchStart(){
		btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnStart.setVisibility(View.INVISIBLE);	
				tv.setVisibility(View.INVISIBLE);
				if(getActivity() instanceof MainActivity)
					activity.requestFullScreen(true);
				btnLeftUp.setVisibility(View.VISIBLE);
			}	
		});
	}
	public void onTouchLeftUp(){
		btnLeftUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnLeftUp.setVisibility(View.INVISIBLE);
				btnLeftDown.setVisibility(View.VISIBLE);
			}	
		});
	}
	public void onTouchLeftDown(){
		btnLeftDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnLeftDown.setVisibility(View.INVISIBLE);
				btnRightUp.setVisibility(View.VISIBLE);
			}	
		});
	}

	public void onTouchRightUp(){
		btnRightUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRightUp.setVisibility(View.INVISIBLE);
				btnRightDown.setVisibility(View.VISIBLE);
			}	
		});
	}
	public void onTouchRightDown(){
		btnRightDown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRightDown.setVisibility(View.INVISIBLE);
				btnMiddle.setVisibility(View.VISIBLE);
			}	
		});
	}
	public void onTouchMiddle(){
		btnMiddle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMiddle.setVisibility(View.INVISIBLE);
				if(getActivity() instanceof MainActivity)
					mafmm.next();
			}	
		});
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(getActivity() instanceof MainActivity)
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
			btnStart.setVisibility(View.VISIBLE);	
			tv.setVisibility(View.VISIBLE);
		}
			
	}
	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "单点触摸测试";
		item.board = "通用";
		item.desc = "测试触摸屏单点触摸";
		return item;
	}
}