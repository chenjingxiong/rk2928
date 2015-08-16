package xmps.androiddebugtool.factorytest.testmodules;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import xmps.androiddebugtool.factorytest.BackLightFragment;
import xmps.androiddebugtool.factorytest.BatteryFragment;
import xmps.androiddebugtool.factorytest.BlueToothFragment;
import xmps.androiddebugtool.factorytest.DebugFragment;
import xmps.androiddebugtool.factorytest.GPSFragment;
import xmps.androiddebugtool.factorytest.GraffitiBoardFragment;
import xmps.androiddebugtool.factorytest.KeypadFragment;
import xmps.androiddebugtool.factorytest.Rj45PortFragment;
import xmps.androiddebugtool.factorytest.ScreenTestFragment;
import xmps.androiddebugtool.factorytest.SdCardFragment;
import xmps.androiddebugtool.factorytest.SerialTTYS0Fragment;
import xmps.androiddebugtool.factorytest.SerialTTYS1Fragment;
import xmps.androiddebugtool.factorytest.SerialTTYS2Fragment;
import xmps.androiddebugtool.factorytest.SerialTestFragment;
import xmps.androiddebugtool.factorytest.SinglePointTouchFragment;
import xmps.androiddebugtool.factorytest.SoundFragment;
import xmps.androiddebugtool.factorytest.StartPageFragment;
import xmps.androiddebugtool.factorytest.UsbStorageFragment;
import xmps.androiddebugtool.factorytest.WifiTestFragment;


/**
 * 测试流程类。
 *
 *
 * @author enjack
 * */
public class TestProcedures {

	private final String tag = "<TestProcedures>";
	private final boolean USE_CONFIG_FILE = false;
	
	
	
	/*==========================================================================
	 * 				手动配置以下测试内容和流程
	 * ========================================================================*/
	/**测试流程顺序，反射查找class的根据，已弃用*/
	private String class_table[] = {
			"SoundFragment",
			"BackLightFragment"
	};

	/**测试流程顺序*/
	private String item_table[] = {

            ITEM_START_PAGE,
            ITEM_SERIAL_TTYS2,
            ITEM_SERIAL_TTYS1,
            ITEM_SERIAL_TTYS0,
            ITEM_SOUND,
			ITEM_WIFI,
			ITEM_RJ45PORT,
			ITEM_GPS,
			ITEM_GRAFFITI,
			ITEM_BATTERY,
			ITEM_SERIAL,
			ITEM_BT,
			ITEM_USB_STORAGE,
			ITEM_SDCARD,
			ITEM_BACKLIGHT,
			ITEM_KEYPAD,
			ITEM_SINGLE_POINT_TOUCH,
			ITEM_SCREEN_DISPLAY,
            ITEM_DEBUG,
	};


	/**按键板的layout选择*/
	private String keypadLayoutSel = KEYPAD_LAYOUT_TEST1;
	
	/*==========================================================================
	 * 				手动配置以上测试内容和流程
	 * ========================================================================*/
//	
//	
//	
//	
//	
//	
//	
//	
	
	/*==========================================================================
	 * 				当在源码中增加测试项目时候，需要手动修改下面内容
	 * ========================================================================*/

	/**测试流程顺序以及配置的字符串表示方式*/
	public static final String ITEM_START_PAGE = "start_page";
	public static final String ITEM_DEBUG = "debug_page";
	public static final String ITEM_BACKLIGHT = "BackLight";
	public static final String ITEM_SOUND = "Sound";
	public static final String ITEM_KEYPAD = "Keypad";
	public static final String ITEM_WIFI = "wifi";
	public static final String ITEM_SERIAL = "serial";
	public static final String ITEM_SERIAL_TTYS0 = "serial_ttyS0";
	public static final String ITEM_SERIAL_TTYS1 = "serial_ttyS1";
	public static final String ITEM_SERIAL_TTYS2 = "serial_ttyS2";
	public static final String ITEM_SDCARD = "sd_card";
	public static final String ITEM_USB_STORAGE = "usb_storage";
	public static final String ITEM_BT = "bt";
	public static final String ITEM_SCREEN_DISPLAY = "lcd_screen_display";
	public static final String ITEM_SINGLE_POINT_TOUCH = "single_point_touch";
	public static final String ITEM_GPS = "GPS";
	public static final String ITEM_BATTERY = "battery";
	public static final String ITEM_GRAFFITI = "graffiti";
	public static final String ITEM_RJ45PORT = "rj45_port";

	/**keypad lyout*/
	public static final String KEYPAD_LAYOUT_TEST1 = "LayoutTest1";
	public static final String KEYPAD_LAYOUT_TEST2 = "LayoutTest2";

	/**
	 * 添加字符串到class的映射。新增加的测试项fragment需要在这里手动添加映射。
	 * */
	private void mapItemInit(){
		itemClassMap.put(ITEM_START_PAGE, StartPageFragment.class);//起始页
		itemClassMap.put(ITEM_DEBUG, DebugFragment.class);//测试用的fragment
		itemClassMap.put(ITEM_BACKLIGHT, BackLightFragment.class);//背光
		itemClassMap.put(ITEM_SOUND, SoundFragment.class);//声音
		itemClassMap.put(ITEM_KEYPAD, KeypadFragment.class);//按键板
		itemClassMap.put(ITEM_WIFI, WifiTestFragment.class);//wifi
		itemClassMap.put(ITEM_SERIAL, SerialTestFragment.class);//串口
        itemClassMap.put(ITEM_SERIAL_TTYS0, SerialTTYS0Fragment.class);//ttyS0
		itemClassMap.put(ITEM_SERIAL_TTYS1, SerialTTYS1Fragment.class);//ttyS1
        itemClassMap.put(ITEM_SERIAL_TTYS2, SerialTTYS2Fragment.class);//ttyS2
		itemClassMap.put(ITEM_SDCARD, SdCardFragment.class);//sd卡
		itemClassMap.put(ITEM_USB_STORAGE, UsbStorageFragment.class);//u盘
		itemClassMap.put(ITEM_BT, BlueToothFragment.class);//蓝牙
		itemClassMap.put(ITEM_SCREEN_DISPLAY, ScreenTestFragment.class);//屏幕显示
		itemClassMap.put(ITEM_SINGLE_POINT_TOUCH, SinglePointTouchFragment.class);//单点触摸
		itemClassMap.put(ITEM_GPS, GPSFragment.class);//gps
		itemClassMap.put(ITEM_BATTERY, BatteryFragment.class);//电池
		itemClassMap.put(ITEM_GRAFFITI, GraffitiBoardFragment.class);//涂鸦
		itemClassMap.put(ITEM_RJ45PORT, Rj45PortFragment.class);//RJ45网口

		//layout class
		//itemClassMap.put(KEYPAD_LAYOUT_TEST1, Layout_test1.class);
		//itemClassMap.put(KEYPAD_LAYOUT_TEST2, Layout_test2.class);
	}
	/*==========================================================================
	 * 				当在源码中增加测试项目时候，需要手动修改上面内容
	 * ========================================================================*/
//	
//
//	
//	
//	
//	
//	
//	
//	
//	
	/**流程字符串到测试类的映射*/
	private LinkedHashMap<String, Class<?>> itemClassMap = new LinkedHashMap<String, Class<?>>();

	public TestProcedures(){
		mapItemInit();
	}


	/**
	 * 通过反射获取所有需要测试的项目的类。为了和测试配置文件更好兼容，弃用反射方式，采用字符串和class的映射方式。
	 *
	 * @param	pkg
	 * 	测试类所在包。
	 *
	 * @return
	 * 	ArrayList
	 * */
	@Deprecated
	public ArrayList<Class<?>> getTestClass(Package pkg){
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();

		for(int i=0; i<class_table.length; i++){
			String name = pkg.getName()+"."+class_table[i];
			list.add(getClassByName(name));
		}

		return list;
	}

	/**
	 * 获取测试流程中各个测试项对应的class，包括fragment、keypad layout等等。
	 *
	 * @return
	 * 	ArrayList<Class<?>>
	 * */
	public ArrayList<Class<?>> getTestClass(){
		ConfigurationFileFilter fileFilter = new ConfigurationFileFilter();
		if(USE_CONFIG_FILE && fileFilter.hasConfigFile()){
			ArrayList<String> item;
			String keypadLayout;
			fileFilter.parse();
			item = fileFilter.getTestItems();
			keypadLayout = fileFilter.getKeypadLayout();
			int size = item.size();
			if(size==0){
				Log.w(tag, "Configuration file exist but not item specified.Use default setting.");
			}
			else{
				item_table = new String[size];
				int idx = 0;
				for(String s:item){
					item_table[idx] = s;
					idx++;
					Log.i(tag, "item:"+s);
				}
				if(keypadLayout==null || keypadLayout.equals("")){
					keypadLayoutSel = KEYPAD_LAYOUT_TEST1;
					Log.w(tag, "Keypad layout didn't correctly specified.User default[KEYPAD_LAYOUT_TEST1]");
				}
				else
					keypadLayoutSel = keypadLayout;
			}
		}
		//
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();
		for(int i=0; i<item_table.length; i++)
			list.add(itemClassMap.get(item_table[i]));
		return list;
	}


	/**
	 * 获取具体实现keypad测试的类。
	 *
	 * @return
	 * 	keypad layout class defined in com.enjack.keypadlayout
	 * */
	public Class<?> getKeypadLayout(){
		return itemClassMap.get(keypadLayoutSel);
	}


	/**获取映射*/
	public LinkedHashMap<String, Class<?>> getMap(){
		return this.itemClassMap;
	}


	/**
	 * 根据名称获取Class object.
	 * */
	private Class<?> getClassByName(String name){
		Class<?> c = null;
		try {
			/*
			 * forName() will load class and initialize it, such as load static code block.
			 * But constructor would not be loaded.
			 * */
			c = Class.forName(name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c = null;
		}

		return c;
	}

}
