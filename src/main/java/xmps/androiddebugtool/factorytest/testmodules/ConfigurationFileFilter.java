package xmps.androiddebugtool.factorytest.testmodules;

import android.os.Environment;
import android.util.Log;

import com.android.enjack.util.LineReader;

import java.io.File;
import java.util.ArrayList;

/**
 * 读取测试配置文件
 * */
public class ConfigurationFileFilter {
	private final String tag = "<ConfigurationFileFilter>";
	private final String FILE_NAME = "rk2928_ft_cfg.dat";
	private final String FILE_PATH = "/mnt/xmps";
	private final String FILE_EXTERNAL_SD_PATH = "/mnt/external_sd";
	private final String FILE_INTERNAL_SD_PATH = Environment.getExternalStorageDirectory().toString();
	private final boolean dataDBG = false;
	private ArrayList<String> mTestItems = new ArrayList<String>();
	private String mKeypadLayout = null;

	public ConfigurationFileFilter(){
	}

	/**获取所有测试项*/
	public ArrayList<String> getTestItems(){
		return mTestItems;
	}

	/**获取按键板测试项*/
	public String getKeypadLayout(){
		return mKeypadLayout;
	}

	/**是否存在配置文件*/
	public boolean hasConfigFile(){
		File file = new File(FILE_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return true;
		file = new File(FILE_EXTERNAL_SD_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return true;
		file = new File(FILE_INTERNAL_SD_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return true;

		Log.i(tag, "Config file not found.");
		return false;
	}

	//
	@SuppressWarnings("unchecked")
	public void parse(){
		ArrayList<String> data;
		ArrayList<String> tmp = new ArrayList<String>();
		File file = getConfigFile();
		LineReader reader = new LineReader(file);
		data = reader.readAllLines();

		printData("all--->", data);

		//过滤空行
		tmp.clear();
		for(int i=0; i<data.size(); i++){
			String str = data.get(i);
			if(str!=null && !str.equals(""))
				tmp.add(str);
		}
		data = (ArrayList<String>) tmp.clone();
		printData("remove null line--->", data);

		//过滤首字符是#的行
		tmp.clear();
		for(int i=0; i<data.size(); i++){
			String str = data.get(i);
			String first = str.substring(0, 1);
			if(!first.equals("#"))
				tmp.add(str);
		}
		data = (ArrayList<String>) tmp.clone();
		printData("remove first #--->", data);

		//过滤#及其之后的注释
		tmp.clear();
		for(int i=0; i<data.size(); i++){
			String str = data.get(i);
			int idx = str.indexOf('#');
			if(-1!=idx)
				str = str.substring(0, idx);
			if(null!=str && !str.equals(""))
				tmp.add(str);
		}
		data = (ArrayList<String>) tmp.clone();
		printData("remove all #--->", data);

		//过滤空格、tab
		tmp.clear();
		for(int i=0; i<data.size(); i++){
			String str = data.get(i);
			str = str.replaceAll(" ", "");
			str = str.replaceAll("\t", "");
			if(str!=null && !str.equals(""))
				tmp.add(str);
		}
		data = (ArrayList<String>) tmp.clone();
		printData("remove 空格和tab--->", data);

		//分割等于号
		tmp.clear();
		mTestItems.clear();
		mKeypadLayout = null;
		for(int i=0; i<data.size(); i++){
			String str = data.get(i);
			String sp[] = str.split("=");
			if(sp.length==2){
				if(sp[0].equals("TEST_ITEM"))
					mTestItems.add(sp[1]);
				if(sp[0].equals("keypad_layout"))
					mKeypadLayout = sp[1];
			}
		}
	}



	/**获取配置文件*/
	private File getConfigFile(){

		File file = new File(FILE_EXTERNAL_SD_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return file;
		file = new File(FILE_INTERNAL_SD_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return file;
		file = new File(FILE_PATH+File.separator+FILE_NAME);
		if(file.exists() && file.canRead())
			return file;

		return null;

	}


	private void printData(String msg, ArrayList<String> data){
		if(!dataDBG)
			return;
		Log.i(tag, msg);
		for(String str:data)
			Log.i(tag, "***<"+str+">***");
	}
}
