package com.example.searchthing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Screen extends View{
	float x,y;
	Paint p = new Paint();
	Bitmap img;
	
	public Screen(Context context){
		super(context);
		Resources res = this.getContext().getResources();
		img = BitmapFactory.decodeResource(res, R.drawable.searchimg);
	}
	
	public boolean onTouchEvent(MotionEvent ev){
		Log.e("hello", "hello");
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			x = ev.getX();
			y = ev.getY();
			Log.e("touch", "touch");
			Screen.this.invalidate();
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas c){
		super.onDraw(c);
		c.drawBitmap(img, x, y, p);
	}
	
}
