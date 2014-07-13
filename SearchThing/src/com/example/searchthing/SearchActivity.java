package com.example.searchthing;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class SearchActivity extends Activity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{

	//DB関係
    private EntryDB Edb;
    
    private String search_thing = "リンゴ";
    
    private ArrayList<String> get_db_finlename;	//画像名格納
    
    private String path;						//画像のファイルパス
    
    private ImageView search_photo_img;			//サーチした画像
    
    private GestureDetector gesposition;
   
    private int searchX,searchY;				//座標参照用
    
    private String message;
    
    private boolean dialog_flag = true;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.search);
		
		search_photo_img = (ImageView) findViewById(R.id.search_img);
		
		//SQLiteの準備
		MyDBHelper helper = new MyDBHelper(this, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		Edb = new EntryDB(db);
		
		List<MyDBEntity> entityList = Edb.findAll();
		
		for (MyDBEntity entity: entityList) {
			Log.e("DB", "DB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
					+ ":" + entity.getY() + ":" + entity.getThing());
		}
		
		get_db_finlename = new ArrayList<String>();
		
		List<MyDBEntity> entity1 =Edb.findByTthing(search_thing);
		
		for (MyDBEntity entity: entity1) {
			Log.e("DB", "DB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
					+ ":" + entity.getY() + ":" + entity.getThing());
			searchX = Integer.parseInt(entity.getX());
			searchY = Integer.parseInt(entity.getY());
			message = entity.getThing();
			//画像のファイル名を格納
			get_db_finlename.add(entity.getFilename());
		}
		
		for (int i = 0; i < get_db_finlename.size(); i++) {
			Log.d("filename", "filename:" + get_db_finlename.get(i));
		}
		
		//探してきた画像を作成
		ShowImage();
		
		
		
		gesposition = new GestureDetector(search_photo_img.getContext(), this);
		
	}//end onCreate 
	
	private void ShowDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("探し物");
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog_flag = true;
			}
		});
		
		alertDialogBuilder.setNegativeButton("編集", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog_flag = true;

			}
		});
		
		alertDialogBuilder.setCancelable(false);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	private void ShowImage(){
		path = Environment.getExternalStorageDirectory().getPath()+ "/DCIM/cmr/";
//		EntryActivity get = new EntryActivity();
//		get.photoSetImage();
		Bitmap bm = null;
		int len = 1024;
		byte[] buffer = new byte[len];

		try {
			File file = new File(path,get_db_finlename.get(0));
			FileInputStream fis = new FileInputStream(file);
			//BufferedInputStream binput = new BufferedInputStream(in);
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
			//out.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		search_photo_img.setImageBitmap(bm);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		gesposition.onTouchEvent(event);
		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
//		if(event.getX() == searchX || event.getX() >= searchX - 20 && event.getX() <= searchX + 20){
//			Log.e("hello", "------hello---------");
//		}
//		if(event.getY()  == searchY  + 90 || event.getY() >= searchY + 90  - 30 && event.getY()<= searchY + 90  + 30){
//			Log.e("hello", "------hello---------");
//		}
		if(event.getX() == searchX && event.getY()  == searchY  + 90|| event.getX() >= searchX - 20 && event.getX() <= searchX + 20 && event.getY() >= searchY + 90  - 30 && event.getY()<= searchY + 90  + 30){
			Log.e("hello", "------hello---------");
			//ダイアログ表示
			if (dialog_flag == true) {
				ShowDialog();
				dialog_flag = false;
			}
		}
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
