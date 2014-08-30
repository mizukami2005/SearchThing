package com.example.searchthing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class SearchResultActivity extends Activity{
	private String resultFilename,resultFilepath;
	private String x,y;
	private String thing;
	private ImageView searchResultImage;
	private Matrix matrix;
	private Bitmap thingImage,mutableBitmap;
	private Canvas canvas;	
	private float fx, fy;
	
	protected void onCreate(Bundle saveeInstanceState){
		super.onCreate(saveeInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		
		searchResultImage = (ImageView) findViewById(R.id.resultimg);
		
		thingImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		
		
		
		//Resources r = getContentResolver().getResources();
//		thingImage = BitmapFactory.decodeResource(r, R.drawable.ic_launcher);
		
		getData();
		
		resultFilepath= Environment.getExternalStorageDirectory().getPath() + "/DCIM/cmr/";
		
		
		Log.e("resultFilename", resultFilepath);
		Log.e("x", x);
		Log.e("y", y);
		Log.e("thing", thing);
		
		fx = Float.valueOf(x);
		fy = Float.valueOf(y);

		
		showImage();
		
		
	}//end onCreate
	
		
	private void getData(){
		Intent get_data = getIntent();
		resultFilename = get_data.getStringExtra("filename");
		x = get_data.getStringExtra("x");
		y = get_data.getStringExtra("y");
		thing = get_data.getStringExtra("thing");
	}//end getData
	
	private void showImage(){
		Bitmap bm = null;
		int len = 1024;
		byte[] buffer = new byte[len];
		try{
			File file = new File(resultFilepath,resultFilename);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nrb = fis.read(buffer, 0, len);
			while(nrb != -1){
				baos.write(buffer,0,nrb);
				nrb = fis.read(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
			fis.close();
			baos.close();
			matrix = new Matrix();
			matrix.postScale((float)0.3, (float)0.3);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			options.inSampleSize = 2;
			mutableBitmap = BitmapFactory.decodeFile(resultFilepath + resultFilename, options);
			//Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		canvas = new Canvas(mutableBitmap);
		searchResultImage.setImageBitmap(mutableBitmap);
		canvas.drawBitmap(thingImage, fx , fy, null);
		//searchResultImage.setImageBitmap(Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true));
	}//
	
}
