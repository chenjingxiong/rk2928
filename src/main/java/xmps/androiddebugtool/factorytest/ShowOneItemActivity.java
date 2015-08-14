package xmps.androiddebugtool.factorytest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import xmps.androiddebugtool.factorytest.chain.ItemDescription;
import xmps.androiddebugtool.factorytest.testmodules.g;

public class ShowOneItemActivity extends ActionBarActivity {
	
	private final String tag = "<ShowOneItemActivity>";
	private final String FRAGMENT_TAG = "fm_tag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_one_item);
		//LCDMetrixUtil.lockOrientation(this, this);
		ActionBar bar = this.getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String target = bundle.getString("Target_fragment");
		Log.i(tag, "target fragment:"+target);
		LinkedHashMap<String, Class<?>> map = g.tp.getMap();
		Class<?> cls = map.get(target);
		FragmentManager manager = this.getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		Fragment fm = null;
		fm = manager.findFragmentByTag(FRAGMENT_TAG);
		if(null==fm){
			try {
				fm = (Fragment)cls.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			trans.replace(R.id.show_one_item_fm_content, fm, FRAGMENT_TAG);
			trans.commit();
		}
		if(null!=fm){
			Method method = null;
			try {
				method = cls.getMethod("getItemDescription");
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ItemDescription desc = null;
			try {
				desc = (ItemDescription)method.invoke(fm);
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
			if(null!=desc) {
				bar.setTitle(desc.title);
			}
		}
		else
			Log.e(tag, "can't not find reflect target fragment");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_one_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == android.R.id.home){
			finish();
		}
		else if(id == R.id.show_one_item_back){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
