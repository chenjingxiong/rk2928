package xmps.androiddebugtool.factorytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enjack.diyviews.CircleColorButtonView;

import xmps.androiddebugtool.factorytest.chain.BaseTestItemFragment;
import xmps.androiddebugtool.factorytest.chain.ItemDescription;

/**
 * 电池充电测试
 *
 * @author enjack
 * */
public class BatteryFragment extends BaseTestItemFragment {
	private final String tag = "<BatteryFragment>";
	private CircleColorButtonView circleView = null;
	private TextView chargeView = null;
	private BatteryReceiver batteryReceiver = null;
	private Paint paint = new Paint();
	private RectF oval = new RectF();
	private int batteryLevel = 0;
	private boolean isCharging = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
					ViewGroup container,
					Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_battery, container,false);
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryReceiver = new BatteryReceiver();
		this.getActivity().registerReceiver(batteryReceiver, intentFilter);
		
		TextView tv = (TextView)v.findViewById(R.id.title);
		tv.setText("电池充电测试");
		circleView = (CircleColorButtonView)v.findViewById(R.id.battery_cap);
		chargeView = (TextView)v.findViewById(R.id.battery_charge);
		circleView.enableClickedDraw(false);
		circleView.setDrawExtraCallBack(new CircleColorButtonView.DrawExtraCallBack(){

			@Override
			public void drawExtra(Canvas canvas) {
				// TODO Auto-generated method stub
				int radius = circleView.getRadius();
				float strokeWidth = radius/10;
				oval.left = strokeWidth/2;
				oval.right = radius*2 - strokeWidth/2;
				oval.top = strokeWidth/2;
				oval.bottom = radius*2 - strokeWidth/2;
				float angle = batteryLevel*360/100;
				paint.setAntiAlias(true);
				paint.setColor(Color.argb(0xee, 0xff, 0x99, 0x22));
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(strokeWidth);
				canvas.drawArc(oval, 0, angle, false, paint);
			}
			
		});
		circleView.postInvalidate();
		return v;
	}
	
	
	@Override
	public void onDestroy(){
		this.getActivity().unregisterReceiver(batteryReceiver);
		super.onDestroy();
	}
	
	class BatteryReceiver extends BroadcastReceiver{

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				int status = intent.getIntExtra("status", -1);
				batteryLevel = level*100/scale;
				Log.i(tag, "battery:"+batteryLevel);
				switch(status){
				case BatteryManager.BATTERY_STATUS_CHARGING:
					isCharging = true;
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					isCharging = false;
					break;
				}
				if(isCharging){
					chargeView.setText("正在充电");
					circleView.setBackgroundColor(Color.argb(0xff, 0xee, 0x44, 0x44));
					circleView.setFrame(Color.argb(0xff, 0xee, 0x44, 0x44), 10);
				}
				else{
					chargeView.setText(" ");
					circleView.setBackgroundColor(Color.argb(0xff, 0x71, 0xfe, 0x59));
					circleView.setFrame(Color.argb(0xff, 0x71, 0xfe, 0x59), 10);
				}
				circleView.setText(String.valueOf(batteryLevel)+"%");
				circleView.postInvalidate();
			}
		}
		
	}

	@Override
	public ItemDescription getItemDescription() {
		// TODO Auto-generated method stub
		ItemDescription item = new ItemDescription();
		item.title = "电池充电测试";
		item.board = "通用";
		item.desc = "电池充电";
		return item;
	}

}
