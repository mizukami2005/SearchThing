package com.example.searchthing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class EntryImage extends View{
	ArrayList<Point>mPointList;
	Paint mPaint;
	Bitmap mBitmap;
	Bitmap mImage;
	Bitmap before_photo_bmp,after_photo_bmp,mutableBitmap;
	Matrix matrix;
	Context context;
	String filename;
	String x,y;
	 
	
	public EntryImage(Context context){
		super(context);
		Init();
	}
	
	public EntryImage(Context context, AttributeSet attrs){
		super(context, attrs);
		Init();
	}
	
	public EntryImage(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		Init();
	}
	
	public void Init(){
		mPointList = new ArrayList<Point>();
		mPaint = new Paint();
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//画像がセットされていれば描画
		if(mImage != null){
			canvas.drawBitmap(mImage, 0, 0, null);
		}else{
			canvas.drawColor(Color.BLUE);
		}
		for (Point point : mPointList){
			Log.v("test", "x:" + point.x + ", y:" + point.y);
			canvas.drawBitmap(mBitmap, point.x, point.y, null);
		}
	}//end onDraw
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			Point point = new Point();
			point.x = (int)event.getX();
			point.y = (int)event.getY();
			x = Integer.toString(point.x);
			y = Integer.toString(point.y);
			mPointList.add(point);
			invalidate();
			
			Intent intent_searchActivity = new Intent(getContext(),EntryForm.class);
			intent_searchActivity.putExtra("filename", filename);
			intent_searchActivity.putExtra("x", x);
			intent_searchActivity.putExtra("y", y);
			getContext().startActivity(intent_searchActivity);
			//showDialog();
			break;
		default:
			break;
		}//end switch
		return true;
	}//end onTouchEvent
	
	//Bitmapを保存
	public void saveBitmap(String filename){
		int width = getWidth();
		int height = getHeight();
		if(mImage != null){
			width = mImage.getWidth();
			height = mImage.getHeight();
		}
		
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(newBitmap);
		//画像がセットされていれば描画する
		if(mImage != null){
			canvas.drawBitmap(mImage, 0, 0, null);
		}else{
			canvas.drawColor(Color.BLUE);
		}
		for(Point point : mPointList){
			canvas.drawBitmap(mBitmap, point.x, point.y, null);
		}
		
		try{
//			FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "DCIM/cmr/test.png"));
//			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//			fos.close();
				FileOutputStream out = new FileOutputStream(filename);
				newBitmap.compress(CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
				//Toast.makeText(this, "保存されました", Toast.LENGTH_LONG).show();
				//Intent main_intent = new Intent(EntryActivity.this, MainActivity.class);
				//startActivity(main_intent);
		}catch (FileLockInterruptionException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}//saveBitmap
	
	public Bitmap getImage(){
		return mImage;
	}
	
	public void setImage(Bitmap mImage){
		this.mImage = mImage;
		invalidate();
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
//	public void setPoint(String x, String y){
//		this.x = x;
//		this.y = y;
//	}
	

	
	
	
	//Uriをリサイズしてビットマップに変換
	public Bitmap getBitmapFromUri(Uri imageUri, int sampleSize) throws IOException{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = sampleSize;
		Bitmap resizeBitmap = null;
		ContentResolver conReslv = context.getContentResolver();
		InputStream iStream = conReslv.openInputStream(imageUri);
		resizeBitmap = BitmapFactory.decodeStream(iStream, null, opts);
		iStream.close();
		return resizeBitmap;
	}
	
	public void photoSetImage(Uri getDataUri, String path){
		try{
			before_photo_bmp = getBitmapFromUri(getDataUri, 4);
		}catch(IOException e){
			e.printStackTrace();
		}
			after_photo_bmp = Bitmap.createBitmap(before_photo_bmp,0,0,before_photo_bmp.getWidth(), before_photo_bmp.getHeight(), matrix, true);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			options.inSampleSize = 2;
			mutableBitmap = BitmapFactory.decodeFile(path, options);
	}
	
	public void showDialog(){
		Log.d("alert", "alert準備");
	}
	
	
	
}//end EntryImage
