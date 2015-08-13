package com.android.enjack.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * 逐行读取文件。
 *
 * @author enjack
 * */
public class LineReader {
	
	private File file = null;
	private long current = 0;
	
	public LineReader(File file){
		this.file = file;
		this.current = 0;
	}


	/**
	 * 读取所有的行，保存到ArrayList中。
	 * */
	public ArrayList<String> readAllLines(){
		ArrayList<String> lineStr = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			for (String line = br.readLine(); line != null; line = br.readLine()) {  
				//System.out.println(line);  
				lineStr.add(line);
			}  
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(null!=br)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lineStr;
	}


	/**
	 * 获取行数。
	 * */
	public long getLineCount(){
		long count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			for (String line = br.readLine(); line != null; line = br.readLine()) 
				count++;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(null!=br)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		return count;
	}


	/**
	 * 设置当前行数计数。
	 * @see #getNext()
	 * */
	public void setCurrent(long index){
		this.current = index;
	}

	/**
	 * 调用一次读取一行。
	 *
	 * @return
	 * 	null 表示已经读完所有行。
	 *
	 * @see #setCurrent(long)
	 * */
	public String getNext(){
		String str = null;
		long cnt = getLineCount();
		
		if(this.current>=cnt)
			return null;
		str = getAt(this.current);
		this.current++;
		
		return str;
	}


	/**
	 * 获取指定行。
	 *
	 * @return
	 * null表示没有获取到。
	 * */
	public String getAt(long index){
		int count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			for (String line = br.readLine(); line != null; line = br.readLine()) {  
				//System.out.println(line);  
				if(index == count){
					return line;
				}
				count++;
			}  
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(null!=br)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	

}
