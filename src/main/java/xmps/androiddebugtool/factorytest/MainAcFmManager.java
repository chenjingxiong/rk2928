package xmps.androiddebugtool.factorytest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import xmps.androiddebugtool.factorytest.chain.FragmentChainManager;
import xmps.androiddebugtool.factorytest.testmodules.TestProcedures;
import xmps.androiddebugtool.factorytest.testmodules.g;


public class MainAcFmManager extends FragmentChainManager {
	
	private String tag = "<MainAcFmManager>";
	private Context context = null;
	private int idContent = -1;
	private Activity activity = null;
	private ArrayList<Class<?>> list = new ArrayList<Class<?>>();

	public MainAcFmManager(int idContent, Context c, Activity activity) {
		super(idContent, c, activity);
		// TODO Auto-generated constructor stub
		this.context = c;
		this.idContent = idContent;
		this.activity = activity;
		g.tp = new TestProcedures();
	}
	
	
	/**add all fragments to chian and then show it.*/
	public boolean init(){
		//Package pkg = this.getClass().getPackage();
		//list = tp.getTestClass(pkg);
		list = g.tp.getTestClass();
		//
		int cnt = list.size();
		Log.i(tag, cnt+" classes will be loaded.");
		for(int i=0; i<cnt; i++){
			Object obj = null;
			try {
				obj = list.get(i).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(tag, "load class failed.");
				return false;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(tag, "load class failed.");
				return false;
			}
			//
			addTail((Fragment)obj);
		}
		
		Log.i(tag, "all class loaded. show first fragment--->");
		showFirst();
		return true;
	}
	
	

}
