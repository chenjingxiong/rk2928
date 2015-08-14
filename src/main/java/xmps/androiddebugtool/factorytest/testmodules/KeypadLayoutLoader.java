package xmps.androiddebugtool.factorytest.testmodules;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Load keypad layout class.
 * 
 * @author enjack
 * 
 * */
public class KeypadLayoutLoader {
	
	
	static{
		Log.i(g.tag, "keypad loader start.");
	}
	
	private Class<?> keypadLayout = null;
	private Object layoutObj = null;
	
	public KeypadLayoutLoader(){
		keypadLayout = g.tp.getKeypadLayout();
	}
	
	
	/***/
	public void load(Fragment fm, LayoutInflater inflater, ViewGroup container){
		newLayoutInstance();
		invokeStart(fm, inflater, container);
	}
	
	
	/***/
	public void deLoad(){
		invokeStop();
	}
	
	/***/
	public Class<?> getLayoutClass(){
		return keypadLayout;
	}
	
	
	
	/**return the instance of layout class.*/
	public Object getLayoutObject(){
		return this.layoutObj;
	}
	
	
	/**new a instance of layout class.*/
	private void newLayoutInstance(){
		try {
			layoutObj = keypadLayout.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**invoke layout class's onStart()*/
	private void invokeStart(Fragment fm, LayoutInflater inflater, ViewGroup container){
		Method method = null;
		try {
			method = keypadLayout.getMethod("onStart", Fragment.class, LayoutInflater.class, ViewGroup.class);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			method.invoke(layoutObj, fm, inflater, container);
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
	}
	
	private void invokeStop(){
		Method method = null;
		try {
			method = keypadLayout.getMethod("onStop");
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			method.invoke(layoutObj);
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
	}
	

	public View invokeInflate(){
		Method method = null;
		Object obj = null;
		try {
			method = keypadLayout.getMethod("onInflate");
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			obj = method.invoke(layoutObj);
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
		
		return (View)obj;
	}
}
