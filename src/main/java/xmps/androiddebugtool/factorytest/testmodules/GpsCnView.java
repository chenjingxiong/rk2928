package xmps.androiddebugtool.factorytest.testmodules;

import java.util.ArrayList;

import com.enjack.diyviews.DrawEfficiencyAnalysts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class GpsCnView extends View {
	private DrawEfficiencyAnalysts mAnalysts = new DrawEfficiencyAnalysts("GpsCnView", 3);
	private boolean layoutLand = false;//是否是横屏 
	private ArrayList<Integer> data = new ArrayList<Integer>();
	private int width;
	private int height;
	private float line0_x1;
	private float line0_x2;
	private float line0_y;
	private float line37_x1;
	private float line37_x2;
	private float line37_y;
	private float line50_x1;
	private float line50_x2;
	private float line50_y;
	private float padding;
	private float sub_width;


	public GpsCnView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public GpsCnView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GpsCnView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected synchronized void onDraw(Canvas canvas){
		//data debug
//		data.clear();
//		data.add(50);
//		data.add(40);
//		data.add(37);
//		data.add(20);
//		data.add(10);
//		data.add(49);
//		data.add(40);
//		data.add(36);
//		data.add(20);
//		data.add(10);
//		data.add(50);
//		data.add(40);
//		data.add(38);
//		data.add(20);
//		data.add(10);

		mAnalysts.pre();
		width = this.getWidth();
		height = this.getHeight();
		line0_x1 = (float)width*0.1f;
		line0_x2 = (float)width;
		line0_y = (float)height*0.9f;
		line50_x1 = line0_x1;
		line50_x2 = line0_x2;
		line50_y = (float)height*0.1f;
		line37_x1 = line0_x1;
		line37_x2 = line0_x2;
		line37_y = (float)height*0.8f*(50-37)/50.0f+line50_y;
		if(layoutLand){//show all item
			int num = data.size();
			if(0==num){
				padding = 0;
				sub_width = 0;
			}
			else{
				if(num<10)
					num = 10;
				padding = (float)width*0.9f*0.1f/(num+1);
				sub_width = (float)width*0.9f*0.9f/num;
			}
		}
		else{//only show 10 item
			padding = (float)width*0.9f*0.1f/11;
			sub_width = (float)width*0.9f*0.9f/10;
		}
		drawBackground(canvas, width, height);
		drawValues(canvas);
		mAnalysts.post();
	}


	private void drawValues(Canvas c){
		if(data.size()<=0)
			return;
		if(layoutLand){
			for(int i=0; i<data.size(); i++){
				float startX = (i+1)*padding+i*sub_width+line0_x1;
				int value = data.get(i);
				drawOneSatellite(c, startX, value);
			}
		}
		else{
			int cnt = data.size()>10?10:data.size();
			for(int i=0; i<cnt; i++){
				float startX = (i+1)*padding+i*sub_width+line0_x1;
				int value = data.get(i);
				drawOneSatellite(c, startX, value);
			}
		}
	}


	@SuppressLint("DefaultLocale")
	private void drawOneSatellite(Canvas c, float startX, int value){
		if(value<=0 || value>50)
			return;
		String str = String.format("%02d", value);
		Paint p = new Paint();
		float y = (float)height*0.8f*(50-value)/50.0f+line50_y;
		p.setAntiAlias(true);
		if(value<37){
			p.setTextSize(20);
			p.setColor(Color.BLACK);
			p.setStyle(Paint.Style.STROKE);
			Path path = new Path();
			path.moveTo(startX, line0_y);
			path.lineTo(startX, y);
			path.lineTo(startX+sub_width, y);
			path.lineTo(startX+sub_width, line0_y);
			c.drawPath(path, p);
			Rect rc = getTextDisplayRect(p, str);
			c.drawText(str, startX+sub_width/2-rc.width()/2, y-rc.height()/2, p);
		}
		else{
			p.setTextSize(25);
			p.setColor(Color.rgb(100, 150, 100));
			c.drawRect(startX, y, startX+sub_width, line0_y, p);
			Rect rc = getTextDisplayRect(p, str);
			c.drawText(str, startX+sub_width/2-rc.width()/2, y-rc.height()/2, p);
		}
	}


	private void drawBackground(Canvas c, int w, int h){
		//c.drawColor(Color.rgb(222, 0, 0));
		int[] colors = new int[3];
		colors[0] = Color.argb(255, 255, 255, 255);
		colors[1] = Color.argb(255, 220, 220, 220);
		colors[2] = Color.argb(255, 255, 255, 255);
		Shader shader = new LinearGradient(0, 0, w, 0,
				colors, null, Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setShader(shader);
		c.drawRect(0, 0, w, h, p);
		//
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
		p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.rgb(22, 22, 22));
		p.setStyle(Paint.Style.STROKE);
		p.setPathEffect(effects);
		Path path = new Path();
		path.moveTo(line0_x1, line0_y);
		path.lineTo(line0_x2,line0_y);
		c.drawPath(path, p);
		path.reset();
		path.moveTo(line50_x1, line50_y);
		path.lineTo(line50_x2,line50_y);
		c.drawPath(path, p);
		path.reset();
		path.moveTo(line37_x1, line37_y);
		path.lineTo(line37_x2,line37_y);
		c.drawPath(path, p);
		//
		p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.BLACK);
		p.setTextSize(25);
		String str = "0";
		Rect rc = getTextDisplayRect(p, str);
		c.drawText(str, (line0_x1-rc.width())/2.0f, line0_y+rc.height()/2.0f, p);
		str = "37";
		rc = getTextDisplayRect(p, str);
		c.drawText(str, (line37_x1-rc.width())/2.0f, line37_y+rc.height()/2.0f, p);
		str = "50";
		rc = getTextDisplayRect(p, str);
		c.drawText(str, (line50_x1-rc.width())/2.0f, line50_y+rc.height()/2.0f, p);
	}


	private Rect getTextDisplayRect(Paint p, String str){
		Rect rc = new Rect();
		p.getTextBounds(str, 0, str.length(), rc);
		if(rc.left<0){
			rc.left = Math.abs(rc.left);
			rc.right += 2*rc.left;
		}
		if(rc.top<0){
			rc.top = Math.abs(rc.top);
			rc.bottom += 2*rc.top;
		}
		return rc;
	}

	public void tellMeOrientation(boolean land){
		this.layoutLand = land;
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<Integer> list){
		data.clear();
		this.data = (ArrayList<Integer>)list.clone();
	}

}
