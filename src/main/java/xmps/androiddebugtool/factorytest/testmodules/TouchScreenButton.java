package xmps.androiddebugtool.factorytest.testmodules;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;


/**
 * @author toddler
 * */
public class TouchScreenButton extends Button{

	public TouchScreenButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public TouchScreenButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		doDrawButton(canvas);
	}
	private synchronized void doDrawButton(Canvas canvas){
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setColorFilter(null);
		p.setStrokeWidth((float)2.0);
		//this.getWidth()
		//this.getHeight()
		canvas.drawLine(0, this.getHeight()/2,this.getWidth(), this.getHeight()/2, p);// x 轴
		canvas.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight(), p);//y  轴
		//canvas.drawLine(0, 25,50, 25, p);// x 轴
		// canvas.drawLine(25, 0, 25, 50, p);//y  轴
	}
}