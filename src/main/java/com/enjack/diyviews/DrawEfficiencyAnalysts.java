package com.enjack.diyviews;

import android.util.Log;

/**
 * @author enjack
 * */
public class DrawEfficiencyAnalysts {
	private long pre = 0;
	private long post = 0;
	private String tag = "";
	private int level = 4;
	private final long FAST = 16;//60 frames per second.
	private final long NORMAL = 33;//30 frames per second.
	private final long SLOW = 100;//10 frames per second.
	private final long WARNNING = 1000;// one frame per second.
	
	/**
	 * @param tag
	 * 	tag to print out.
	 * @param level
	 * 	print out level.</br>
	 * 	0---never print.</br>
	 * 	1---print fast/normal/slow/warnning/dangerout.</br>
	 * 	2---print normal/slow/warnning/dangerout.</br>
	 * 	3---slow/warnning/dangerout.</br>
	 * 	4---print warnning/dangerout.</br>
	 * 	5---only print dangerous.</br>
	 * */
	public DrawEfficiencyAnalysts(String tag, int level){
		this.tag = tag;
		this.level = level;
	}
	
	public void setTag(String tag){
		this.tag = tag;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public void pre(){
		if(0==level)
			return;
		pre = System.currentTimeMillis();
	}
	
	public void post(){
		if(0==level)
			return;
		post = System.currentTimeMillis();
		long take = post - pre;
		
		if(1==level)//print fast/normal/slow/warnning/dangerout.
			Log.i("[Analysts]", "onDraw() takes "+take+" ms.-->"+tag);
		else if(2==level){//print normal/slow/warnning/dangerout.
			if(take>FAST)
				Log.i("[Analysts]", "onDraw() takes "+take+" ms.-->"+tag);
		}
		else if(3==level){//print slow/warnning/dangerout.
			if(take>NORMAL)
				Log.w("[Analysts]", "<Slow>! onDraw() takes "+take+" ms.-->"+tag);
		}
		else if(4==level){//print warnning/dangerout.
			if(take>SLOW)
				Log.w("[Analysts]", "<Warnning>!!! onDraw() takes "+take+" ms. -->"+tag);
		}
		else if(5==level){//print dangerout.
			if(take > WARNNING)
				Log.e("[Analysts]", "!!!<DANGEROUS>!!! onDraw() takes "+take+" ms.-->"+tag);
		}
	}
}
