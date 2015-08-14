package xmps.androiddebugtool.factorytest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.GraffitiBoardView;


/**
 * Ϳѻ�壬�������Ի����ɱ�
 * 
 * @author enjack
 * */
public class GraffitiBoardFragment extends BaseTestItemFragment {
	
	private GraffitiBoardView graffiti;
	@Override
	public View onCreateView(LayoutInflater inflater, 
					ViewGroup container,
					Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_graffiti_board, container,false);
		TextView tv = (TextView)v.findViewById(R.id.title);
		tv.setText("Ϳѻ��");
		graffiti = (GraffitiBoardView)v.findViewById(R.id.graffiti_board);
		return v;
	}
	
	@Override
	public void onDestroy(){
		if(null!=graffiti)
			graffiti.stopTimer();
		super.onDestroy();
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "Ϳѻ��";
		item.board = "ͨ��";
		item.desc = "�����Ƿ�ɱ�";
		return item;
	}

}
