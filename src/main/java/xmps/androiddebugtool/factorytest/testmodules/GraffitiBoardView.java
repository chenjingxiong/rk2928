package xmps.androiddebugtool.factorytest.testmodules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GraffitiBoardView extends View {

	private final String tag = "GraffitiBoardView";
	private Timer timer = null;
	private TimerTask timerTask = null;
	private Paint paint = new Paint();
	private ArrayList<Path> pathList = new ArrayList<Path>();
	private ArrayList<Long> timeList = new ArrayList<Long>();
	private int deadIdx = -1;
	private final long STAY_TIME = 3000;//线条显示的时间
	//private final int ANIMATE_TIME = 500;//消失过程时间
	private final int lineColor = Color.argb(255, 100, 200, 10);


	public GraffitiBoardView(Context context, AttributeSet attrs,
							 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initTimer();
		initPaint();
		// TODO Auto-generated constructor stub
	}

	public GraffitiBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTimer();
		initPaint();
		// TODO Auto-generated constructor stub
	}

	public GraffitiBoardView(Context context) {
		super(context);
		initTimer();
		initPaint();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas){
		int removeIdx = -1;
		synchronized(this){
			for(int i=0; i<pathList.size(); i++){
				if(i==deadIdx){
					//Log.i(tag, "draw dei");
					removeIdx = i;
					deadIdx = -1;
//					Path p = pathList.get(i);
//					Paint ppp = new Paint();
//					ppp.setAntiAlias(true);
//					ppp.setStrokeWidth(10);
//					int alpha = Color.alpha(tmpColor);
//					alpha--;
//					if(alpha<=0){
//						deadIdx = -1;
//						tmpColor = lineColor;
//					}
//					else{
//						int a = alpha << 24;
//						int color = (lineColor & 0x00ffffff) | a;
//						canvas.drawPath(p, ppp);
//						this.invalidate();
//						delayms(ANIMATE_TIME/255);
//					}
				}
				else{
					Path p = pathList.get(i);
					canvas.drawPath(p, paint);
				}
			}
		}

		if(-1!=removeIdx && removeIdx<pathList.size() && removeIdx<timeList.size()){
			delTime(removeIdx);
			delPath(removeIdx);
		}
	}

	private void initPaint(){
		paint.setAntiAlias(true);
		paint.setColor(lineColor);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
	}

	private void initTimer(){
		timer = new Timer();
		timerTask = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(-1!=deadIdx)//只标识一个即将死掉的path
					return;
				long curTime = System.currentTimeMillis();
				synchronized(GraffitiBoardView.this){
					//if(timeList.size()!=pathList.size())//手指未抬起，时间未加入array
					//	return;
					for(int i=0; i<timeList.size(); i++){
						if(curTime-timeList.get(i)>=STAY_TIME){
							deadIdx = i;
							GraffitiBoardView.this.postInvalidate();
							//Log.i(tag, "find die:"+curTime+" "+timeList.get(i));
							return;
						}
					}
				}
			}

		};
		timer.schedule(timerTask, 1, 50);
	}

	public void stopTimer(){
		if(null!=timerTask)
			timerTask.cancel();
		if(null!=timer)
			timer.cancel();
	}

	private void addPath(Path p){
		synchronized(this){
			pathList.add(p);
		}
	}

	private void delPath(int idx){
		synchronized(this){
			pathList.remove(idx);
		}
	}

	private void addTime(long t){
		synchronized(this){
			timeList.add(t);
		}
	}

	private void delTime(int idx){
		synchronized(this){
			timeList.remove(idx);
		}
	}

//	private void delayms(int ms){
//		try {
//			Thread.sleep(ms);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	private Path mPath = null;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(MotionEvent.ACTION_DOWN == (event.getAction()&MotionEvent.ACTION_MASK)){
			int x = (int)event.getX();
			int y = (int)event.getY();
			mPath = new Path();
			mPath.moveTo(x, y);
			addPath(mPath);
			this.invalidate();
			Log.i(tag, "fist point x:"+x+" y:"+y);
		}
		else if(MotionEvent.ACTION_UP == (event.getAction()&MotionEvent.ACTION_MASK)){
			int x = (int)event.getX();
			int y = (int)event.getY();
			mPath.lineTo(x, y);
			this.invalidate();
			addTime(System.currentTimeMillis());
			Log.i(tag, "last point x:"+x+" y:"+y);
		}
		else if(MotionEvent.ACTION_MOVE == (event.getAction()&MotionEvent.ACTION_MASK)){
			int x = (int)event.getX();
			int y = (int)event.getY();
			mPath.lineTo(x, y);
			this.invalidate();
			//Log.i(tag, "x:"+x+" y:"+y);
		}
		return true;
	}
}
