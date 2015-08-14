package xmps.androiddebugtool.factorytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.enjack.util.LCDMetrixUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.g;


public class AllTestItemActivity extends ActionBarActivity implements OnItemClickListener{
	private final String tag = "<AllTestItemActivity>";
	private ListView itemList = null;
	private ListAdapter adapter = null;
	private ArrayList<ListData> datas = new ArrayList<ListData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_test_item);
		LCDMetrixUtil.lockOrientation(this, this);
		initList();
        ImageButton btn = (ImageButton)findViewById(R.id.all_test_item_fb);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "scroll to top");
                itemList.smoothScrollToPosition(0);
            }
        });
	}
	
	private void initList(){
		itemList = (ListView)findViewById(R.id.all_test_item_list);
		
		LinkedHashMap<String, Class<?>> map = g.tp.getMap();
		//for(Class<?> c: map.values()){
		for(String str: map.keySet()){
			Class<?> c = map.get(str);
			Object obj = null;
			try {
				obj = c.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(/*obj instanceof ItemDescription*/true){
				Method method = null;
				try {
					method = c.getMethod("getItemDescription");
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ItemDescription description = null;
				try {
					if(null!=method)
						description = (ItemDescription) method.invoke(obj);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(null!=description){
					ListData data = new ListData(description.title,
								description.board,
								description.desc,
								str);
					datas.add(data);
				}
			}
		}
		adapter = new ListAdapter(this);
		itemList.setAdapter(adapter);
		itemList.setOnItemClickListener(this);
	}
	
	private class ListData{
		private String title;
		private String board;
		private String desc;
		private String fm;
		
		public ListData(String title, String board, String desc, String fm){
			this.title = title;
			this.board = board;
			this.desc = desc;
			this.fm = fm;
		}
		
		public String getTitle(){
			return this.title;
		}
		
		public String getBoard(){
			return this.board;
		}
		
		public String getDesc(){
			return this.desc;
		}
		
		public String getFragment(){
			return this.fm;
		}
	}
	
	private class ViewHolder{
		public TextView tvTitle;
		public TextView tvBoard;
		public TextView tvDesc;
	}
	
	private class ListAdapter extends BaseAdapter{
		
		private LayoutInflater inflater;
		
		public ListAdapter(Context c){
			this.inflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(null==convertView){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.test_item_list, null);
				holder.tvTitle = (TextView)convertView.findViewById(R.id.test_item_title);
				holder.tvBoard = (TextView)convertView.findViewById(R.id.test_item_board);
				holder.tvDesc = (TextView)convertView.findViewById(R.id.test_item_desc);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			ListData data;
			data = (ListData)datas.get(position);
			holder.tvTitle.setText("项目"+(position+1)+":"+data.getTitle());
			holder.tvBoard.setText("板型:"+data.getBoard());
			holder.tvDesc.setText(data.getDesc());
			return convertView;
		}}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_test_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.i(tag, "item "+position+" clicked.");
		Intent intent = new Intent(this, ShowOneItemActivity.class);
		ListData data = datas.get(position);
		Bundle bundle = new Bundle();
		bundle.putString("Target_fragment", data.getFragment());
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
