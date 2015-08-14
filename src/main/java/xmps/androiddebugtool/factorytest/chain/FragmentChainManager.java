package xmps.androiddebugtool.factorytest.chain;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;


/**
 * Manager the fragments attached to activity.
 * 
 * @author enjack
 * 
 * 
 * */
public class FragmentChainManager extends ObjectList{
	
	private String tag = "<FragmentChainManager>";
	private int current = -1;
	private int idContent = 0;
	private Context context = null;
	private Activity activity = null;
	private ArrayList<FragmentChainChangeListener> chainChangeListener = null;
	
	
	public FragmentChainManager(int idContent, Context c, Activity activity){
		super();
		this.idContent = idContent;
		this.context = c;
		this.activity = activity;
		this.chainChangeListener = new ArrayList<FragmentChainChangeListener>();
	}
	
	
	/***/
	public class FragmentInfo{
		public Fragment fm;
		public int flag;//has been added to activity or not.
		public Class<?> cls;
		public FragmentInfo(Fragment fm){
			this.fm = fm;
			this.flag = 0;
			this.cls = fm.getClass();
		}
	}
	
	
	/**call back method.*/
	public interface FragmentChainChangeListener{
		/**when remove/add operated.*/
		public void onChainChanged(FragmentChainManager chain);
		/**when current changed.*/
		public void onCurrentPosChanged(FragmentChainManager chain, int pos);
	}
	
	
	/**add listener of chain change.*/
	public void addChainChangeListener(FragmentChainChangeListener listener){
		Log.i(tag, "listener added!");
		chainChangeListener.add(listener);
	}
	
	
	/**
	 * remove listener.
	 * 
	 * @param	obj
	 * 	the object who add listener.
	 * */
	public void removeChainChangeListener(Object obj){
		/*
		 * ���һ��������˼�����������ʵ������null�����������ͷ�ʱ�����໹����������һ�����ã���ô���ʵ�����޷���GC���ա�
		 * ������destory֮ǰ���ɾ��������
		 * */
		for(int i=0; i<chainChangeListener.size(); i++){
			if(obj.getClass().getName().equals(chainChangeListener.get(i).getClass().getName())){
				chainChangeListener.remove(i);
				Log.i(tag, obj.getClass().getName().toString()+" remove its listener.");
			}
		}
	}
	
	
	private void onChange(){
		for(int i=0; i<chainChangeListener.size(); i++){
			chainChangeListener.get(i).onChainChanged(this);
			chainChangeListener.get(i).onCurrentPosChanged(this, current);
		}
	}
	
	
	private void setCurrent(int idx){
		this.current = idx;
		onChange();
	}
	
	
	@Override
	public String toString(){
		String info = "cnt:"+length()+" current is:"+current+". ";
		for(int i=0; i<length(); i++){
//			info +=(findAt(i).getClass().getName()+"("+
//					findAt(i).getClass().hashCode()+")->");
			FragmentInfo fminfo = (FragmentInfo)findAt(i);
			info = fminfo.cls.getName()+"("+fminfo.fm.hashCode()+")->";
		}
		return info;
	}
	
	
	/**get current showing fragment's index in list.*/
	public int getCurrent(){
		return current;
	}
	
	
	/**get show state of fragment*/
	public boolean isShowing(Fragment fm){
		String name = fm.getClass().getName();
		FragmentInfo info = (FragmentInfo)findAt(current);
		String showing = info.cls.getName();
		if(name.equals(showing))
			return true;
		else
			return false;
	}
	
	
	/**show first fragment in chain.*/
	public void showFirst(){
		FragmentInfo info = (FragmentInfo)getHead();
		if(info!=null){
			FragmentManager fm = activity.getFragmentManager();
			FragmentTransaction trans = fm.beginTransaction();
			hideCurrent();
			if(0==info.flag){
				trans.add(idContent, info.fm);
				info.flag = 1;
			}
			else{
				trans.show(info.fm);
				info.flag = 1;
			}
			
			trans.commit();
			setCurrent(0);
		}
	}
	
	
	/**show last fragment in list.*/
	public void showLast(){
		FragmentInfo info = (FragmentInfo)getTail();
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			hideCurrent();
			if(0==info.flag){
				trans.add(idContent, info.fm);
				info.flag = 1;
			}
			else{
				trans.show(info.fm);
				info.flag = 1;
			}
			
			trans.commit();
			setCurrent(length()-1);
		}
	}
	
	
	/**show fragment in list as specified index.*/
	public void show(int index){
		FragmentInfo info = (FragmentInfo)findAt(index);
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			hideCurrent();
			if(0==info.flag)
				trans.add(idContent, info.fm);
			else
				trans.show(info.fm);
			trans.commit();
			setCurrent(index);
		}
	}
	
	
	/**just add fragment object into list but not display on activity.*/
	public void addAt(Fragment fm, int index){
		FragmentInfo info = new FragmentInfo(fm);
		super.addAt(index, info);
		int i = current;
		if(index<=i)
			setCurrent(i+1);
	}
	
	
	/***/
	public void addHead(Fragment fm){
		FragmentInfo info = new FragmentInfo(fm);
		super.addHead(fm);
		int i = current;
		if(0<=i)
			setCurrent(i+1);
	}
	
	
	/***/
	public void addTail(Fragment fm){
		FragmentInfo info = new FragmentInfo(fm);
		super.addTail(info);
	}
	
	/**remove from both list and activity*/
	@Override
	public void removeAll(){
		for(int i=0; i<length(); i++){
			FragmentInfo info = (FragmentInfo)findAt(i);
			if(info!=null){
				FragmentManager fmm = activity.getFragmentManager();
				FragmentTransaction trans = fmm.beginTransaction();
				trans.remove(info.fm);
				trans.commit();
			}
		}
		super.removeAll();
		setCurrent(-1);
	}
	
	/**remove from both list and activity*/
	@Override
	public void removeAt(int index){
		FragmentInfo info = (FragmentInfo)findAt(index);
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			trans.remove(info.fm);
			trans.commit();
		}
		super.removeAt(index);
		//
		if(current == index)
			setCurrent(-1);
		else if(index<current)
			setCurrent(current-1);
	}
	
	
	/***/
	@Override
	public void removeHead(){
		FragmentInfo info = (FragmentInfo)getHead();
		if(info!=null){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			trans.remove(info.fm);
			trans.commit();
		}
		super.removeHead();
		//
		if(0==current)
			setCurrent(-1);
		else if(current>0)
			setCurrent(current-1);
	}
	
	
	/***/
	@Override
	public void removeTail(){
		FragmentInfo info = (FragmentInfo)getTail();
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			trans.remove(info.fm);
			trans.commit();
		}
		super.removeTail();
		//
		if(length()-1==current)
			setCurrent(-1);
	}
	
	
	/**hide current showing fragment*/
	public void hideCurrent(){
		FragmentInfo info = (FragmentInfo)findAt(current);
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			trans.hide(info.fm);
			trans.commit();
		}
	}
	
	
	/**hide fragment at the specified index in chain*/
	public void hide(int index){
		Fragment fm = (Fragment)findAt(index);
		if(null!=fm){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			trans.hide(fm);
			trans.commit();
		}
	}
	
	
	/**show next fragment in chain.*/
	public void next(){
		int len = length();
		if(-1==current || len-1==current)
			return;
		//
		FragmentInfo info = (FragmentInfo)findAt(current+1);
		if(info!=null){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			hideCurrent();
			if(0==info.flag)
				trans.add(idContent, info.fm);
			else
				trans.show(info.fm);
			info.flag = 1;
			trans.commit();
			setCurrent(current+1);
		}
	}
	
	
	/**show pre fragment in chain.*/
	public void pre(){
		if(current<=0)	return;
		//
		FragmentInfo info = (FragmentInfo)findAt(current-1);
		if(null!=info){
			FragmentManager fmm = activity.getFragmentManager();
			FragmentTransaction trans = fmm.beginTransaction();
			hideCurrent();
			if(0==info.flag)
				trans.add(idContent, info.fm);
			else
				trans.show(info.fm);
			info.flag = 1;
			trans.commit();
			setCurrent(current-1);
		}
			
	}
}
