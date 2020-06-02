package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class CameraGridView extends View {

	public CameraGridView(Context context) {
		super(context);
	}

	public CameraGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
//	private boolean port = false;
//	public void setPortFlag( boolean b ){
//		port = b;
//	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		
		int width  = getWidth();
		int height = getHeight();
		
		canvas.save();

		//横线
		float yy = (float)(height*0.33);
		canvas.drawLine( 0, yy, width, yy, paint);
		yy = (float)(height*0.67);
		canvas.drawLine( 0, yy, width, yy, paint);

		//竖线
		float xx = (float)(width*0.33);
		canvas.drawLine( xx, 0, xx, height, paint);
		xx = (float)(width*0.67);
		canvas.drawLine( xx, 0, xx, height, paint);
		
		canvas.restore();
	}
	
	
	
}
